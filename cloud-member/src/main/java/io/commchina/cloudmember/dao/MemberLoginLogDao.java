package io.commchina.cloudmember.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.commchina.cloudmember.bean.MemberLoginLogEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员登录记录
 * 
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
@Mapper
public interface MemberLoginLogDao extends BaseMapper<MemberLoginLogEntity> {
	
}
