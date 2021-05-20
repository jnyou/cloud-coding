package io.commchina.remote;

import io.commchina.remote.resp.SocialUserReq;
import io.commchina.remote.resp.UserLoginReq;
import io.commchina.remote.resp.UserRegisterReq;
import io.commchina.tools.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * 代码千万行，注释第一行
 * 注释不规范，同事泪两行
 * <p>
 * CloudMemberRemote
 *
 * @version 1.0.0
 * @author: JnYou
 **/
@FeignClient("member-service")
public interface CloudMemberRemote {

    @PostMapping("/member/member/regist")
    R regist(@RequestBody UserRegisterReq vo);

    @PostMapping("/member/member/login")
    R login(@RequestBody UserLoginReq vo);

    @PostMapping("/member/member/oauth2/login")
    R oauthLogin(@RequestBody SocialUserReq user);


}
