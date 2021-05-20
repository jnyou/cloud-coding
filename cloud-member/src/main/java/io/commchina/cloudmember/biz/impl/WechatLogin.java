package io.commchina.cloudmember.biz.impl;

import io.commchina.cloudmember.biz.LoginEnum;
import io.commchina.cloudmember.biz.ThirdLoginStrategy;
import io.commchina.http.req.UserLoginReq;
import io.commchina.http.resp.MemberInfoResp;

/**
 * 代码千万行，注释第一行
 * 注释不规范，同事泪两行
 * <p>
 * WechatLogin
 *
 * @version 1.0.0
 * @author: JnYou
 **/
public class WechatLogin implements ThirdLoginStrategy {

    @Override
    public MemberInfoResp strategyLogin(UserLoginReq userLoginVo) {
        // todo
        return null;
    }

    @Override
    public LoginEnum LOGIN_ENUM() {
        return LoginEnum.WECHAT_LOGIN;
    }
}