package io.commchina.cloudmember.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import io.commchina.cloudmember.bean.MemberLevelEntity;
import io.commchina.cloudmember.dao.MemberLevelDao;
import io.commchina.cloudmember.service.MemberLevelService;
import io.commchina.tools.PageUtils;
import io.commchina.tools.Query;
import org.springframework.stereotype.Service;

import java.util.Map;


@Service("memberLevelService")
public class MemberLevelServiceImpl extends ServiceImpl<MemberLevelDao, MemberLevelEntity> implements MemberLevelService {

    @Override
    public PageUtils queryPage(Map<String, Object> params) {
        IPage<MemberLevelEntity> page = this.page(
                new Query<MemberLevelEntity>().getPage(params),
                new QueryWrapper<MemberLevelEntity>()
        );

        return new PageUtils(page);
    }

    @Override
    public MemberLevelEntity getDefaultLevel() {
        return this.baseMapper.selectOne(Wrappers.<MemberLevelEntity>lambdaQuery().eq(MemberLevelEntity::getDefaultStatus,1));
    }

}