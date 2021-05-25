package io.commchina.cloudmember.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import io.commchina.cloudmember.bean.MemberReceiveAddressEntity;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员收货地址
 * 
 * @author jnyou
 * @email xiaojian19970910@gmail.com
 */
@Mapper
public interface MemberReceiveAddressDao extends BaseMapper<MemberReceiveAddressEntity> {
	
}
