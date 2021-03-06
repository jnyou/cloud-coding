package io.commchina.http.req;

import io.commchina.http.enums.LoginEnum;
import lombok.Data;
import lombok.experimental.Accessors;


/**
 * 用以封装社交登录认证后换回的令牌等信息
 */
@Data
@Accessors(chain = true)
public class SocialUserReq {

    /**
     * 令牌
     */
    private String access_token;

    private String remind_in;

    private String isRealName;

    /**
     * 令牌过期时间
     */
    private long expires_in;

    private String scope;

    /**
     * 微博社交用户的唯一标识
     */
    private String uid;


    /**
     * 微信登录的openid
     */
    private String openid;
    /**
     * 微信社交用户的唯一标识
     */
    private String unionid;


    /**
     * GitHub信息相关
     */
    private String token_type;
    /**
     * Github返回的唯一标识
     */
    private int id;


    /**
     * 登录类型枚举
     */
    private LoginEnum socialType;

}
