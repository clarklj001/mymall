package de.killbuqs.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.member.entity.MemberCollectSubjectEntity;

import java.util.Map;

/**
 * 会员收藏的专题活动
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:32:33
 */
public interface MemberCollectSubjectService extends IService<MemberCollectSubjectEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

