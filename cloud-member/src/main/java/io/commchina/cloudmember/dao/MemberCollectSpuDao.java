package io.commchina.cloudmember.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.commchina.cloudmember.bean.MemberCollectSpuEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员收藏的商品
 * 
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
@Mapper
public interface MemberCollectSpuDao extends BaseMapper<MemberCollectSpuEntity> {
	
}
