package io.commchina.remote.resp;

import lombok.Data;

@Data
public class UserLoginReq {

    private String loginAccount;
    private String password;
}
