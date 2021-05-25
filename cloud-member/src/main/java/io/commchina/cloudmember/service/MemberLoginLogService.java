package io.commchina.cloudmember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.commchina.cloudmember.bean.MemberLoginLogEntity;
import io.commchina.tools.PageUtils;

import java.util.Map;

/**
 * 会员登录记录
 *
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
public interface MemberLoginLogService extends IService<MemberLoginLogEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

