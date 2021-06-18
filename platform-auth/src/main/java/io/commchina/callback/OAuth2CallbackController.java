package io.commchina.callback;

import cn.hutool.http.HttpUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.commchina.http.enums.LoginEnum;
import io.commchina.http.req.SocialUserReq;
import io.commchina.http.resp.MemberInfoResp;
import io.commchina.remote.CloudMemberRemote;
import io.commchina.tools.HttpUtils;
import io.commchina.tools.R;
import io.commchina.tools.RRException;
import io.commchina.utils.ConstantPropertiesUtil;
import io.commchina.constant.GitHubConstant;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpResponse;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpSession;
import java.util.HashMap;
import java.util.Map;

/**
 * 代码千万行，注释第一行
 * 注释不规范，同事泪两行
 * <p>
 * OAuth2Controller
 *
 * @version 1.0.0
 * @author: JnYou
 **/
@Slf4j
//@Controller
@RestController
public class OAuth2CallbackController {

    @Autowired
    CloudMemberRemote cloudMemberRemote;

    /**
     * web请求：https://api.weibo.com/oauth2/authorize?client_id=1229245080&response_type=code&redirect_uri=http://auth.cloudmall.com/oauth2.0/weibo/success
     * weibo回调API
     *
     * @param code
     * @param session
     */
    @GetMapping("/oauth2.0/weibo/success")
    public MemberInfoResp weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        // 根据code换取AccessToken  https://api.weibo.com/oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=authorization_code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE
        Map<String, String> map = new HashMap<>();
        map.put("client_id", "1229245080");
        map.put("client_secret", "1ce8cd74348ec151f1e429186da434bf");
        map.put("grant_type", "authorization_code");
        map.put("code", code);
        map.put("redirect_uri", "http://auth.gmall.com/oauth2.0/weibo/success");
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "POST", new HashMap<String, String>(), new HashMap<String, String>(), map);
        Map<String, String> errors = new HashMap<>();
        if (response.getStatusLine().getStatusCode() == 200) {
            // 获取到了accessToken
            String json = EntityUtils.toString(response.getEntity());
            SocialUserReq socialUser = JSON.parseObject(json, SocialUserReq.class).setSocialType(LoginEnum.WEIBO_LOGIN);
            return remoteInvokeDataHandle(socialUser);
        } else {
            log.error("======登录失败======");
            // 失败跳转到登录页面
            errors.put("msg", "获得第三方授权失败，请重试");
            session.setAttribute("errors", errors);
//            return "redirect:http://auth.gmall.com/login.html";
            return null;
        }
    }


    /**
     * WeChat回调API
     *
     * @param code
     * @param state
     * @param session
     */
//    @GetMapping("/api/ucenter/wx/callback")
    @GetMapping("/oauth2.0/wechat/success")
    public MemberInfoResp wechat(String code, String state, HttpSession session) {

        //得到授权临时票据code
        log.info("code = " + code);
        log.info("state = " + state);

        // 从redis中将state获取出来，和当前传入的state作比较
        // 如果一致则放行，如果不一致则抛出异常：非法访问

        // 向认证服务器发送请求换取access_token
        String baseAccessTokenUrl = "https://api.weixin.qq.com/sns/oauth2/access_token" +
                "?appid=%s" +
                "&secret=%s" +
                "&code=%s" +
                "&grant_type=authorization_code";
        // 请求路径accessTokenUrl
        String accessTokenUrl = String.format(baseAccessTokenUrl, ConstantPropertiesUtil.WX_OPEN_APP_ID,
                ConstantPropertiesUtil.WX_OPEN_APP_SECRET,
                code);
        log.info("拼接的accessTokenUrl:{}", accessTokenUrl);
        // 使用httpclient发送请求，得到返回结果
        String result = null;
        try {
            result = HttpUtil.get(accessTokenUrl);
            log.info("获取到的accessToken令牌{}", result);
        } catch (Exception e) {
            throw new RRException("获取access_token失败", -1);
        }
        // json字符串 accessToken{"access_token":"34_NYg-DpsIkG1fw3cfaq7vFiyVVbe1e-DuN1qnQ7CYXRIT_UMPkiXNfzHXOMQUtzSx7LFYK4HQmdM2hxgM55PLrR0iOYjLKJmXYTveFnJWWuI","expires_in":7200,"refresh_token":"34_vACdEUuu-6fcPMcYxs5iDQ2Subi485AjzewGUHV7lC-nwpSFiXPcs7OCXs2VlU8-JdWxV45LJ-JuNJ3DULV8yvV4dvfjlmELgNEGU9abg3U","openid":"o3_SC5-822XmQdwCDFYQkMAXHY78","scope":"snsapi_login","unionid":"oWgGz1FDH1k93xz9uvK1B1HPKa4c"}
        log.info("返回的认证信息{}", result);

        // 授权后得到的信息封装到SocialUserReq实例中
        SocialUserReq socialUser = JSON.parseObject(result, SocialUserReq.class).setSocialType(LoginEnum.WECHAT_LOGIN);
        String accessToken = socialUser.getAccess_token();
        String openid = socialUser.getOpenid();

        // 判断该用户是登录还是注册
        return remoteInvokeDataHandle(socialUser);

        // 使用JWT根据用户对象生成token字符串  //因为端口号不同存在跨域问题，cookie不能跨域，所以这里使用url重写
//        String jwtToken = JwtUtils.getJwtToken(member.getId(), member.getNickname());
//        // 返回首页面
//        return "redirect:http://localhost:3000?token=" + jwtToken;
    }

    public MemberInfoResp github(String code, String state, HttpSession session) {
        if (!StringUtils.isEmpty(code) && !StringUtils.isEmpty(state)) {
            // 拿code换取token
            String token_url = GitHubConstant.TOKEN_URL.replace("CODE", code);
            String result = null;
            try {
                result = HttpUtil.get(token_url);
                // {"access_token":"gho_16C7e42F292c6912E7710c838347Ae178B4a", "scope":"repo,gist", "token_type":"bearer"}
                log.info("获取到的令牌信息：{}", result);
                // 获取accessToken
                SocialUserReq socialUser = JSON.parseObject(result, SocialUserReq.class).setSocialType(LoginEnum.GITHUB_LONGN);
                // 判断该用户是登录还是注册
                return remoteInvokeDataHandle(socialUser);
            } catch (Exception e) {
                throw new RRException("获取access_token失败", -1);
            }


        }
        return null;
    }

    public MemberInfoResp remoteInvokeDataHandle(SocialUserReq socialUser) {
        // 判断该用户是登录还是注册
        R r = cloudMemberRemote.oauthLogin(socialUser);
        // 2.1 远程调用成功，返回首页并携带用户信息
        if (r.getCode() == 0) {
            String jsonString = JSON.toJSONString(r.get("memberEntity"));
            log.info("获取到的JSON字符串信息{}", jsonString);
            MemberInfoResp memberInfoResp = JSON.parseObject(jsonString, new TypeReference<MemberInfoResp>() {
            });
            log.info("用户：{}登录成功", memberInfoResp.getNickname());
            return memberInfoResp;
        } else {
            log.error("======登录失败======");
            return null;
        }
    }

}