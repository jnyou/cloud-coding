package io.commchina.cloudmember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.commchina.cloudmember.bean.MemberLevelEntity;
import io.commchina.tools.PageUtils;

import java.util.Map;

/**
 * 会员等级
 *
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
public interface MemberLevelService extends IService<MemberLevelEntity> {

    PageUtils queryPage(Map<String, Object> params);

    MemberLevelEntity getDefaultLevel();

}

