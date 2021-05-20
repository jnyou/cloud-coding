package io.commchina.cloudmember.biz.impl;

import io.commchina.cloudmember.biz.ThirdLoginStrategy;
import io.commchina.http.enums.LoginEnum;
import io.commchina.http.req.SocialUserReq;
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
    public MemberInfoResp strategyLogin(SocialUserReq socialUserReq) {
        // TODO 微博登录逻辑

        log.info("社交用户唯一ID：{}", socialUserReq.getUid());
        return null;
    }

    @Override
    public LoginEnum LOGIN_ENUM() {
        return LoginEnum.WEIBO_LOGIN;
    }
}