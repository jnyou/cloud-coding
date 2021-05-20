package io.commchina.http.req;

import lombok.Data;

@Data
public class UserLoginReq {

    private String loginAccount;
    private String password;
}
