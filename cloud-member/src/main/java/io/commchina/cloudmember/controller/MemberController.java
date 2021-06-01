package io.commchina.cloudmember.controller;

import io.commchina.cloudmember.bean.MemberEntity;
import io.commchina.cloudmember.exception.PhoneExistException;
import io.commchina.cloudmember.exception.UsernameExistException;
import io.commchina.cloudmember.service.MemberService;
import io.commchina.exception.BizCodeEnume;
import io.commchina.http.req.SocialUserReq;
import io.commchina.http.req.UserLoginReq;
import io.commchina.http.req.UserRegisterReq;
import io.commchina.tools.PageUtils;
import io.commchina.tools.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.Map;


/**
 * 会员
 *
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
@RestController
@RequestMapping("member/member")
public class MemberController {
    @Autowired
    private MemberService memberService;

//    @Autowired
//    private CouponFeignService couponFeignService;

    // 旧第三方登录方式
//    @PostMapping("/oauth2/login")
//    public R oauthLogin(@RequestBody SocialUserReq user) {
//        MemberEntity memberEntity = memberService.oauthLogin(user);
//        if(null != memberEntity){
//            return R.ok().put("memberEntity",memberEntity);
//        } else {
//            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(),BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
//        }
//    }

    /**
     * 测试openFeign组件
     *
     * @return
     * @Author jnyou
     */
//    @RequestMapping("/coupons")
//    public R test() {
//        MemberEntity memberEntity = new MemberEntity();
//        memberEntity.setNickname("会员昵称张三");
//        //假设张三去数据库查了后返回了张三的优惠券信息
//        R membercoupons = couponFeignService.membercoupons();
//
//        //打印会员和优惠券信息
//        return R.ok().put("member", memberEntity).put("coupons", membercoupons.get("coupons"));
//    }

    @PostMapping("/login")
    public R login(@RequestBody UserLoginReq vo) {
        MemberEntity memberEntity = memberService.login(vo);
        if(null != memberEntity){
            return R.ok().setData(memberEntity);
        } else {
            return R.error(BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getCode(),BizCodeEnume.LOGINACCT_PASSWORD_INVAILD_EXCEPTION.getMsg());
        }
    }

    /**
     * 注册
     */
    @PostMapping("/regist")
    public R regist(@RequestBody UserRegisterReq vo) {
        try {
            memberService.regist(vo);
        } catch (PhoneExistException e) {
            return R.error(BizCodeEnume.PHONE_EXIST_EXCEPTION.getCode(), BizCodeEnume.PHONE_EXIST_EXCEPTION.getMsg());
        } catch (UsernameExistException e) {
            return R.error(BizCodeEnume.USER_EXIST_EXCEPTION.getCode(), BizCodeEnume.USER_EXIST_EXCEPTION.getMsg());
        }
        return R.ok();
    }

    /**
     * 列表
     */
    @RequestMapping("/list")
    //@RequiresPermissions("memberservice:member:list")
    public R list(@RequestParam Map<String, Object> params) {
        PageUtils page = memberService.queryPage(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    //@RequiresPermissions("memberservice:member:info")
    public R info(@PathVariable("id") Long id) {
        MemberEntity member = memberService.getById(id);

        return R.ok().put("member", member);
    }

    /**
     * 保存
     */
    @RequestMapping("/save")
    //@RequiresPermissions("memberservice:member:save")
    public R save(@RequestBody MemberEntity member) {
        memberService.save(member);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    //@RequiresPermissions("memberservice:member:update")
    public R update(@RequestBody MemberEntity member) {
        memberService.updateById(member);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    //@RequiresPermissions("memberservice:member:delete")
    public R delete(@RequestBody Long[] ids) {
        memberService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
