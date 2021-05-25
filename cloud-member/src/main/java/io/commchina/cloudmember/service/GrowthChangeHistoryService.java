package io.commchina.cloudmember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.commchina.cloudmember.bean.GrowthChangeHistoryEntity;
import io.commchina.tools.PageUtils;

import java.util.Map;

/**
 * 成长值变化历史记录
 *
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
public interface GrowthChangeHistoryService extends IService<GrowthChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

