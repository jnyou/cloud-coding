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

    /**
     * 令牌过期时间
     */
    private long expires_in;

    /**
     * 该社交用户的唯一标识
     */
    private String uid;

    private String isRealName;

    /**
     * 登录类型枚举
     */
    private LoginEnum socialType;

}
