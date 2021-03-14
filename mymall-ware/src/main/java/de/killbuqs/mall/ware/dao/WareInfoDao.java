package de.killbuqs.mall.ware.dao;

import de.killbuqs.mall.ware.entity.WareInfoEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

/**
 * 仓库信息
 * 
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:48:08
 */
@Mapper
public interface WareInfoDao extends BaseMapper<WareInfoEntity> {
	
}
