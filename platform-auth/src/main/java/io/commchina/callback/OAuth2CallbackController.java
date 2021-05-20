package io.commchina.callback;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import io.commchina.constant.AuthServerConstant;
import io.commchina.http.resp.MemberInfoResp;
import io.commchina.remote.CloudMemberRemote;
import io.commchina.http.req.SocialUserReq;
import io.commchina.tools.HttpUtils;
import io.commchina.tools.R;
import lombok.extern.slf4j.Slf4j;
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
     * @param code
     * @param session
     */
    @GetMapping("/oauth2.0/weibo/success")
    public MemberInfoResp weibo(@RequestParam("code") String code, HttpSession session) throws Exception {
        // 根据code换取AccessToken  https://api.weibo.com/oauth2/access_token?client_id=YOUR_CLIENT_ID&client_secret=YOUR_CLIENT_SECRET&grant_type=authorization_code&redirect_uri=YOUR_REGISTERED_REDIRECT_URI&code=CODE
        Map<String,String> map = new HashMap<>();
        map.put("client_id","1229245080");
        map.put("client_secret","1ce8cd74348ec151f1e429186da434bf");
        map.put("grant_type","authorization_code");
        map.put("code",code);
        map.put("redirect_uri","http://auth.gmall.com/oauth2.0/weibo/success");
        HttpResponse response = HttpUtils.doPost("https://api.weibo.com", "/oauth2/access_token", "POST", new HashMap<String, String>(), new HashMap<String, String>(), map);
        Map<String, String> errors = new HashMap<>();
        if(response.getStatusLine().getStatusCode() == 200){
            // 获取到了accessToken
            String json = EntityUtils.toString(response.getEntity());
            SocialUserReq socialUser = JSON.parseObject(json, SocialUserReq.class);
            // 判断该用户是登录还是注册
            R r = cloudMemberRemote.oauthLogin(socialUser);
            //2.1 远程调用成功，返回首页并携带用户信息
            if (r.getCode() == 0) {
                String jsonString = JSON.toJSONString(r.get("memberEntity"));
                log.info("获取到的JSON字符串信息{}",jsonString);
                MemberInfoResp memberInfoResp = JSON.parseObject(jsonString, new TypeReference<MemberInfoResp>() {
                });
                log.info("用户：{}登录成功",memberInfoResp.getNickname());
                // 子域session共享问题，发session id 的时候指定为父级域名
                session.setAttribute(AuthServerConstant.LOGIN_USER, memberInfoResp);
                // 成功之后回到主页面
//                return "redirect:http://gmall.com";
                return memberInfoResp;
            }else {
                log.error("======登录失败======");
                //2.2 否则返回登录页
                errors.put("msg", "登录失败，请重试");
                session.setAttribute("errors", errors);
//                return "redirect:http://auth.gmall.com/login.html";
                return null;
            }
        } else {
            log.error("======登录失败======");
            // 失败跳转到登录页面
            errors.put("msg", "获得第三方授权失败，请重试");
            session.setAttribute("errors", errors);
//            return "redirect:http://auth.gmall.com/login.html";
            return null;
        }
    }

}