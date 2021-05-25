package io.commchina.cloudmember.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.commchina.cloudmember.bean.MemberStatisticsInfoEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员统计信息
 * 
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
@Mapper
public interface MemberStatisticsInfoDao extends BaseMapper<MemberStatisticsInfoEntity> {
	
}
