package io.commchina.cloudmember.biz;


import io.commchina.http.enums.LoginEnum;
import io.commchina.http.req.SocialUserReq;
import io.commchina.http.resp.MemberInfoResp;

/**
 * 代码千万行，注释第一行
 * 注释不规范，同事泪两行
 * <p>
 * ThirdLoginStrategy
 *
 * @version 1.0.0
 * @author: JnYou
 **/
public interface ThirdLoginStrategy {

    MemberInfoResp strategyLogin(SocialUserReq socialUserReq);

    LoginEnum LOGIN_ENUM();
}
