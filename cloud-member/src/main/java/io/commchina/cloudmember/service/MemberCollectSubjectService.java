package io.commchina.cloudmember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.commchina.cloudmember.bean.MemberCollectSubjectEntity;
import io.commchina.tools.PageUtils;

import java.util.Map;

/**
 * 会员收藏的专题活动
 *
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
public interface MemberCollectSubjectService extends IService<MemberCollectSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

