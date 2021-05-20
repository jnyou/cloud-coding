package io.commchina.cloudmember.biz.impl;

import io.commchina.cloudmember.biz.LoginEnum;
import io.commchina.cloudmember.biz.ThirdLoginStrategy;
import io.commchina.http.req.UserLoginReq;
import io.commchina.http.resp.MemberInfoResp;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * 代码千万行，注释第一行
 * 注释不规范，同事泪两行
 * <p>
 * WeiboLogin
 *
 * @version 1.0.0
 * @author: JnYou
 **/
@Service
@Slf4j
public class WeiboLogin implements ThirdLoginStrategy {

    @Override
    public MemberInfoResp strategyLogin(UserLoginReq userLoginReq) {
        // TODO 微博登录逻辑

        log.info("登录名{}，登录密码{}",userLoginReq.getLoginAccount(),userLoginReq.getPassword());
        return null;
    }

    @Override
    public LoginEnum LOGIN_ENUM() {
        return LoginEnum.WEIBO_LOGIN;
    }
}