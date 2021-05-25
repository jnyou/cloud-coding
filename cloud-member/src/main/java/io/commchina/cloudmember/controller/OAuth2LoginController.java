package io.commchina.cloudmember.controller;

import io.commchina.cloudmember.biz.LoginChoose;
import io.commchina.exception.BizCodeEnume;
import io.commchina.http.req.SocialUserReq;
import io.commchina.http.resp.MemberInfoResp;
import io.commchina.tools.R;
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
@RestController
@RequestMapping("/api/v1/oauth2")
public class OAuth2LoginController {

    private LoginChoose loginChoose;

    public OAuth2LoginController(LoginChoose loginChoose) {
        this.loginChoose = loginChoose;
    }

    @PostMapping("/login")
    public R oAuth2Login(@RequestBody SocialUserReq user) {
        log.info("请求参数：{}",user);
        // 策略模式
        MemberInfoResp memberInfoResp = loginChoose.getLoginType(user.getSocialType()).strategyLogin(user);
        if(null != memberInfoResp){
            log.info("社交用户唯一ID：{}", memberInfoResp.getSocialUid());
            return R.ok().put("memberEntity",memberInfoResp);
        } else {
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(), BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
        }
    }


}