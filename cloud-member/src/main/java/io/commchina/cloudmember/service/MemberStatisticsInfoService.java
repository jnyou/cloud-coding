package io.commchina.cloudmember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.commchina.cloudmember.bean.MemberStatisticsInfoEntity;
import io.commchina.tools.PageUtils;

import java.util.Map;

/**
 * 会员统计信息
 *
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
public interface MemberStatisticsInfoService extends IService<MemberStatisticsInfoEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

