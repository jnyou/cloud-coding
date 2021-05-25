package io.commchina.cloudmember.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.commchina.cloudmember.bean.MemberEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员
 * 
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
@Mapper
public interface MemberDao extends BaseMapper<MemberEntity> {
	
}
