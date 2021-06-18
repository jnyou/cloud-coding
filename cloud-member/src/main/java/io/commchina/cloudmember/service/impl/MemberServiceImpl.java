package io.commchina.cloudmember.service.impl;

import cn.hutool.core.convert.Convert;
import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.commchina.cloudmember.bean.MemberEntity;
import io.commchina.cloudmember.bean.MemberLevelEntity;
import io.commchina.cloudmember.dao.MemberDao;
import io.commchina.cloudmember.exception.PhoneExistException;
import io.commchina.cloudmember.exception.UsernameExistException;
import io.commchina.cloudmember.service.MemberLevelService;
import io.commchina.cloudmember.service.MemberService;
import io.commchina.constant.GitHubConstant;
import io.commchina.http.enums.UserSourceType;
import io.commchina.http.req.SocialUserReq;
import io.commchina.http.req.UserLoginReq;
import io.commchina.http.req.UserRegisterReq;
import io.commchina.tools.HttpUtils;
import io.commchina.tools.PageUtils;
import io.commchina.tools.Query;
import io.commchina.tools.RRException;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service("memberService")
public class MemberServiceImpl extends ServiceImpl<MemberDao, MemberEntity> implements MemberService {

    @Autowired
    private MemberLevelService memberLevelService;

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberEntity> page = this.page(
                new Query<MemberEntity>().getPage(params),
                new QueryWrapper<MemberEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public void regist(UserRegisterReq vo) {
        MemberEntity memberEntity = new MemberEntity();
        // 设置注册时的默认等级
        MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());

        // 检查用户名和手机号是否唯一 使用异常机制让远程感知异常
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUserName());
        memberEntity.setNickname(vo.getUserName());
        // 密码需要加密存储 使用spring的密码加密器
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        String encode = passwordEncoder.encode(vo.getPassword());
        memberEntity.setPassword(encode);
        this.baseMapper.insert(memberEntity);
    }

    @Override
    public void checkPhoneUnique(String phone) throws PhoneExistException {
        Integer count = this.baseMapper.selectCount(Wrappers.<MemberEntity>lambdaQuery().eq(MemberEntity::getMobile, phone));
        if (count > 0) {
            throw new PhoneExistException();
        }
    }

    @Override
    public void checkUserNameUnique(String username) throws UsernameExistException {
        Integer count = this.baseMapper.selectCount(Wrappers.<MemberEntity>lambdaQuery().eq(MemberEntity::getUsername, username));
        if (count > 0) {
            throw new UsernameExistException();
        }
    }

    @Override
    public MemberEntity login(UserLoginReq vo) {
        MemberEntity memberEntity = this.baseMapper.selectOne(Wrappers.<MemberEntity>lambdaQuery()
                .eq(MemberEntity::getUsername, vo.getLoginAccount())
                .or()
                .eq(MemberEntity::getMobile, vo.getLoginAccount())
        );
        if (null == memberEntity) {
            // 登录失败
            return null;
        } else {
            // 获取到数据库的password
            String passwordDb = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // 第一个传递过来的明文密码，第二个是数据库存储的密文密码进行匹配
            boolean matches = passwordEncoder.matches(vo.getPassword(), passwordDb);
            if (matches) {
                return memberEntity;
            } else {
                return null;
            }
        }
    }

    /**
     * 社交登录 -- 微博
     *
     * @param user
     * @Author JnYou
     */
    @Override
    public MemberEntity oauth2WeiboLogin(SocialUserReq socialUser) {
        // 登录和注册合并逻辑
        String uid = socialUser.getUid();
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("uid", uid));
        if (memberEntity == null) {
            //1 如果之前未登陆过，则查询其社交信息进行注册
            Map<String, String> query = new HashMap<>();
            query.put("access_token", socialUser.getAccess_token());
            query.put("uid", uid);
            //封装用户信息并保存
            memberEntity = new MemberEntity();
            try {
                //调用微博api接口获取用户信息
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                //获得昵称，性别，头像
                String name = jsonObject.getString("name");
                String gender = jsonObject.getString("gender");
                String profile_image_url = jsonObject.getString("profile_image_url");
                memberEntity.setNickname(name);
                memberEntity.setGender("m".equals(gender) ? 1 : 2);
                memberEntity.setHeader(profile_image_url);
            } catch (Exception e) {
                e.printStackTrace();
                log.error("invoke weibo api fail.");
            }
            // 查找用户的默认级别
            MemberLevelEntity defaultLevel = memberLevelService.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
            memberEntity.setLevelId(defaultLevel.getId());
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setUid(socialUser.getUid());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            memberEntity.setSourceType(UserSourceType.WEIBO_LOGIN.getCode());
            this.save(memberEntity);
        } else {
            //2 否则更新令牌等信息并返回
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setUid(socialUser.getUid());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            this.updateById(memberEntity);
        }
        return memberEntity;
    }

    /**
     * 社交登录 -- 微信
     *
     * @param user
     * @Author JnYou
     */
    @Override
    public MemberEntity oauth2WechatLogin(SocialUserReq socialUser) {
        String accessToken = socialUser.getAccess_token();
        String openid = socialUser.getOpenid();
        //查询数据库当前用用户是否曾经使用过微信登录
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("uid", socialUser.getUnionid()));
        if (null == memberEntity) {
            log.info("新用户注册，保存信息");

            // 通过accessToken和openid访问微信的资源服务器，获取用户信息
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
            // 发送请求
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpUtil.get(userInfoUrl);
                log.info("resultUserInfo{}", resultUserInfo);
            } catch (Exception e) {
                throw new RRException("获取用户信息失败", -1);
            }
            //解析json
            JSONObject jsonObject = JSON.parseObject(resultUserInfo);
            //获得昵称，性别，头像
            String nickname = jsonObject.getString("nickname");
            String headimgurl = jsonObject.getString("headimgurl");
            String sex = jsonObject.getString("sex");
            String province = jsonObject.getString("province");
            String city = jsonObject.getString("city");
            String country = jsonObject.getString("country");

            memberEntity.setNickname(nickname);
            memberEntity.setGender(Convert.toInt(sex));
            memberEntity.setHeader(headimgurl);
            memberEntity.setSourceType(UserSourceType.WECHAT_LOGIN.getCode());
            memberEntity.setCity(city);
            //向数据库中插入一条记录
            memberEntity.setUid(socialUser.getUnionid());
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setUid(socialUser.getUnionid());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            this.save(memberEntity);
        } else {
            //2 否则更新令牌等信息并返回
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setUid(socialUser.getUnionid());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            this.updateById(memberEntity);
        }
        return memberEntity;
    }

    /**
     * 社交登录 -- GitHub TODO--待测试
     *
     * @param user
     * @Author JnYou
     */
    @Override
    public MemberEntity oauth2GithubLogin(SocialUserReq socialUser) {
        String token = socialUser.getAccess_token();

        MemberEntity memberEntity = this.getOne(Wrappers.<MemberEntity>lambdaQuery().eq(MemberEntity::getUid, socialUser.getId()));
        if (null == memberEntity) {
            log.info("新用户注册，保存信息");

            //根据token发送请求获取登录人的信息  ，通过令牌去获得用户信息
            String userinfo_url = GitHubConstant.USER_INFO_URL.replace("TOKEN", token);
            // 发送请求
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpUtil.get(userinfo_url);
                log.info("resultUserInfo{}", resultUserInfo);
            } catch (Exception e) {
                throw new RRException("获取用户信息失败", -1);
            }
            //解析json
            JSONObject jsonObject = JSON.parseObject(resultUserInfo);
            //获得昵称，性别，头像
            String nickname = jsonObject.getString("name");
            String headimgurl = jsonObject.getString("avatar_url");
            String email = jsonObject.getString("email");

            memberEntity.setNickname(nickname);
            memberEntity.setHeader(headimgurl);
            memberEntity.setEmail(email);
            memberEntity.setSourceType(UserSourceType.WECHAT_LOGIN.getCode());
            //向数据库中插入一条记录
            memberEntity.setUid(Convert.toStr(socialUser.getId()));
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            this.save(memberEntity);
        } else {
            //2 否则更新令牌等信息并返回
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setUid(Convert.toStr(socialUser.getId()));
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            this.updateById(memberEntity);
        }

        return memberEntity;
    }

}