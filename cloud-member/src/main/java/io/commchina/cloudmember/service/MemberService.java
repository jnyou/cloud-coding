package io.commchina.cloudmember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.commchina.cloudmember.bean.MemberEntity;
import io.commchina.cloudmember.exception.PhoneExistException;
import io.commchina.cloudmember.exception.UsernameExistException;
import io.commchina.http.req.SocialUserReq;
import io.commchina.http.req.UserLoginReq;
import io.commchina.http.req.UserRegisterReq;
import io.commchina.tools.PageUtils;

import java.util.Map;

/**
 * 会员
 *
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);

    void regist(UserRegisterReq vo);

    void checkPhoneUnique(String email) throws PhoneExistException;

    void checkUserNameUnique(String username) throws UsernameExistException;

    MemberEntity login(UserLoginReq vo);

    MemberEntity oauthLogin(SocialUserReq user);
}

