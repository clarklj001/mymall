package de.killbuqs.mall.member.dao;

import de.killbuqs.mall.member.entity.MemberStatisticsInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 会员统计信息
 * 
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:32:33
 */
@Mapper
public interface MemberStatisticsInfoDao extends BaseMapper<MemberStatisticsInfoEntity> {
	
}
