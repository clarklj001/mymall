package de.killbuqs.mall.member.service;

import com.baomidou.mybatisplus.extension.service.IService;
import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.mall.member.entity.MemberEntity;

import java.util.Map;

/**
 * 会员
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 23:32:33
 */
public interface MemberService extends IService<MemberEntity> {

    PageUtils queryPage(Map<String, Object> params);
}

