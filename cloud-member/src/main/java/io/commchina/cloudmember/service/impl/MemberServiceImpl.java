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
        // ??????????????????????????????
        MemberLevelEntity memberLevelEntity = memberLevelService.getDefaultLevel();
        memberEntity.setLevelId(memberLevelEntity.getId());

        // ??????????????????????????????????????? ???????????????????????????????????????
        checkPhoneUnique(vo.getPhone());
        checkUserNameUnique(vo.getUserName());
        memberEntity.setMobile(vo.getPhone());
        memberEntity.setUsername(vo.getUserName());
        memberEntity.setNickname(vo.getUserName());
        // ???????????????????????? ??????spring??????????????????
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
            // ????????????
            return null;
        } else {
            // ?????????????????????password
            String passwordDb = memberEntity.getPassword();
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            // ?????????????????????????????????????????????????????????????????????????????????????????????
            boolean matches = passwordEncoder.matches(vo.getPassword(), passwordDb);
            if (matches) {
                return memberEntity;
            } else {
                return null;
            }
        }
    }

    /**
     * ???????????? -- ??????
     *
     * @param user
     * @Author JnYou
     */
    @Override
    public MemberEntity oauth2WeiboLogin(SocialUserReq socialUser) {
        // ???????????????????????????
        String uid = socialUser.getUid();
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("uid", uid));
        if (memberEntity == null) {
            //1 ???????????????????????????????????????????????????????????????
            Map<String, String> query = new HashMap<>();
            query.put("access_token", socialUser.getAccess_token());
            query.put("uid", uid);
            //???????????????????????????
            memberEntity = new MemberEntity();
            try {
                //????????????api????????????????????????
                HttpResponse response = HttpUtils.doGet("https://api.weibo.com", "/2/users/show.json", "get", new HashMap<>(), query);
                String json = EntityUtils.toString(response.getEntity());
                JSONObject jsonObject = JSON.parseObject(json);
                //??????????????????????????????
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
            // ???????????????????????????
            MemberLevelEntity defaultLevel = memberLevelService.getOne(new QueryWrapper<MemberLevelEntity>().eq("default_status", 1));
            memberEntity.setLevelId(defaultLevel.getId());
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setUid(socialUser.getUid());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            memberEntity.setSourceType(UserSourceType.WEIBO_LOGIN.getCode());
            this.save(memberEntity);
        } else {
            //2 ????????????????????????????????????
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setUid(socialUser.getUid());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            this.updateById(memberEntity);
        }
        return memberEntity;
    }

    /**
     * ???????????? -- ??????
     *
     * @param user
     * @Author JnYou
     */
    @Override
    public MemberEntity oauth2WechatLogin(SocialUserReq socialUser) {
        String accessToken = socialUser.getAccess_token();
        String openid = socialUser.getOpenid();
        //???????????????????????????????????????????????????????????????
        MemberEntity memberEntity = this.getOne(new QueryWrapper<MemberEntity>().eq("uid", socialUser.getUnionid()));
        if (null == memberEntity) {
            log.info("??????????????????????????????");

            // ??????accessToken???openid???????????????????????????????????????????????????
            String baseUserInfoUrl = "https://api.weixin.qq.com/sns/userinfo" +
                    "?access_token=%s" +
                    "&openid=%s";
            String userInfoUrl = String.format(baseUserInfoUrl, accessToken, openid);
            // ????????????
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpUtil.get(userInfoUrl);
                log.info("resultUserInfo{}", resultUserInfo);
            } catch (Exception e) {
                throw new RRException("????????????????????????", -1);
            }
            //??????json
            JSONObject jsonObject = JSON.parseObject(resultUserInfo);
            //??????????????????????????????
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
            //?????????????????????????????????
            memberEntity.setUid(socialUser.getUnionid());
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setUid(socialUser.getUnionid());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            this.save(memberEntity);
        } else {
            //2 ????????????????????????????????????
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setUid(socialUser.getUnionid());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            this.updateById(memberEntity);
        }
        return memberEntity;
    }

    /**
     * ???????????? -- GitHub TODO--?????????
     *
     * @param user
     * @Author JnYou
     */
    @Override
    public MemberEntity oauth2GithubLogin(SocialUserReq socialUser) {
        String token = socialUser.getAccess_token();

        MemberEntity memberEntity = this.getOne(Wrappers.<MemberEntity>lambdaQuery().eq(MemberEntity::getUid, socialUser.getId()));
        if (null == memberEntity) {
            log.info("??????????????????????????????");

            //??????token????????????????????????????????????  ????????????????????????????????????
            String userinfo_url = GitHubConstant.USER_INFO_URL.replace("TOKEN", token);
            // ????????????
            String resultUserInfo = null;
            try {
                resultUserInfo = HttpUtil.get(userinfo_url);
                log.info("resultUserInfo{}", resultUserInfo);
            } catch (Exception e) {
                throw new RRException("????????????????????????", -1);
            }
            //??????json
            JSONObject jsonObject = JSON.parseObject(resultUserInfo);
            //??????????????????????????????
            String nickname = jsonObject.getString("name");
            String headimgurl = jsonObject.getString("avatar_url");
            String email = jsonObject.getString("email");

            memberEntity.setNickname(nickname);
            memberEntity.setHeader(headimgurl);
            memberEntity.setEmail(email);
            memberEntity.setSourceType(UserSourceType.WECHAT_LOGIN.getCode());
            //?????????????????????????????????
            memberEntity.setUid(Convert.toStr(socialUser.getId()));
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            this.save(memberEntity);
        } else {
            //2 ????????????????????????????????????
            memberEntity.setAccessToken(socialUser.getAccess_token());
            memberEntity.setUid(Convert.toStr(socialUser.getId()));
            memberEntity.setExpiresIn(socialUser.getExpires_in());
            this.updateById(memberEntity);
        }

        return memberEntity;
    }

}