package io.commchina.cloudmember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.commchina.cloudmember.bean.IntegrationChangeHistoryEntity;
import io.commchina.tools.PageUtils;

import java.util.Map;

/**
 * 积分变化历史记录
 *
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
public interface IntegrationChangeHistoryService extends IService<IntegrationChangeHistoryEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

