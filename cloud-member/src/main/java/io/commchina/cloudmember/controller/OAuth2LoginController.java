package io.commchina.cloudmember.controller;

import io.commchina.cloudmember.biz.LoginChoose;
import io.commchina.http.req.SocialUserReq;
import io.commchina.http.resp.MemberInfoResp;
import io.commchina.tools.R;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 代码千万行，注释第一行
 * 注释不规范，同事泪两行
 * <p>
 * OAuth2Controller
 *
 * @version 1.0.0
 * @author: JnYou
 **/
@RestController
@RequestMapping("/api/v1/oauth2")
public class OAuth2LoginController {

    private LoginChoose loginChoose;

    public OAuth2LoginController(LoginChoose loginChoose) {
        this.loginChoose = loginChoose;
    }

    @PostMapping("/login")
    public R oAuth2Login(@RequestBody SocialUserReq user) {
        // 策略模式
        MemberInfoResp memberInfoResp = loginChoose.getLoginType(user.getSocialType()).strategyLogin(user);
        return R.ok().put("info", memberInfoResp);
    }


}