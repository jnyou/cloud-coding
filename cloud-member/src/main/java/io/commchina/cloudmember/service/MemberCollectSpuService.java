package io.commchina.cloudmember.service;

import com.baomidou.mybatisplus.extension.service.IService;
import io.commchina.cloudmember.bean.MemberCollectSpuEntity;
import io.commchina.tools.PageUtils;

import java.util.Map;

/**
 * 会员收藏的商品
 *
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
public interface MemberCollectSpuService extends IService<MemberCollectSpuEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

