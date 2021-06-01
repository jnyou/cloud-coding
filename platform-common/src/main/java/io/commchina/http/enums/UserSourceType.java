package io.commchina.http.enums;

/**
 * 代码千万行，注释第一行
 * 注释不规范，同事泪两行
 * <p>
 * UserSourceType
 *
 * @version 1.0.0
 * @author: JnYou
 **/
public enum UserSourceType {


    WECHAT_LOGIN(1, "WeChat"),
    WEIBO_LOGIN(2, "Weibo");

    private Integer code;
    private String msg;

    UserSourceType(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }
}
