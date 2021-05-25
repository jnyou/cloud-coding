package io.commchina.cloudmember.biz.impl;

import io.commchina.cloudmember.bean.MemberEntity;
import io.commchina.cloudmember.biz.ThirdLoginStrategy;
import io.commchina.cloudmember.service.MemberService;
import io.commchina.http.enums.LoginEnum;
import io.commchina.http.req.SocialUserReq;
import io.commchina.http.resp.MemberInfoResp;
import io.commchina.tools.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Objects;

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
public class WeiboLoginBiz implements ThirdLoginStrategy {

    @Autowired
    private MemberService memberService;

    @Override
    public MemberInfoResp strategyLogin(SocialUserReq socialUserReq) {
        MemberInfoResp memberInfoResp = new MemberInfoResp();
        MemberEntity memberEntity = memberService.oauthLogin(socialUserReq);
        if(Objects.nonNull(memberEntity)) BeanUtils.copyProperties(memberEntity,memberInfoResp);
        return memberInfoResp;
    }

    @Override
    public LoginEnum LOGIN_ENUM() {
        return LoginEnum.WEIBO_LOGIN;
    }
}