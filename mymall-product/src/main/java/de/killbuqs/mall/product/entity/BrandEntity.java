package de.killbuqs.mall.product.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import de.killbuqs.common.valid.AddGroup;
import de.killbuqs.common.valid.ListValue;
import de.killbuqs.common.valid.UpdateGroup;
import de.killbuqs.common.valid.UpdateStatusGroup;

import java.io.Serializable;
import java.util.Date;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Null;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.URL;

import lombok.Data;

/**
 * 品牌
 * 
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
@Data
@TableName("pms_brand")
public class BrandEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 品牌id
	 */
	@NotNull(message = "修改必须指定品牌id", groups = { UpdateGroup.class })
	@Null(message = "新增不能指定id", groups = { AddGroup.class })
	@TableId
	private Long brandId;
	/**
	 * 品牌名
	 */
	@NotBlank(message = "品牌名必须提交", groups = { AddGroup.class, UpdateGroup.class })
	private String name;
	/**
	 * 品牌logo地址
	 */
//	@NotBlank(groups = { AddGroup.class})
//	@URL(message = "logo必须是一个合法的url地址", groups = { AddGroup.class, UpdateGroup.class })
	private String logo;
	/**
	 * 介绍
	 */
	private String descript;
	/**
	 * 显示状态[0-不显示；1-显示]
	 */
	@NotNull(groups = { AddGroup.class, UpdateStatusGroup.class })
	@ListValue(values = { 0, 1 }, groups = { AddGroup.class, UpdateStatusGroup.class })
	private Integer showStatus;
	/**
	 * 检索首字母
	 */
	@NotEmpty(groups = { AddGroup.class })
	@Pattern(regexp = "^[a-zA-Z]$", message = "检索首字母是且仅能是1个字符", groups = { AddGroup.class, UpdateGroup.class })
//	@Pattern(regexp = "/^[A-Za-z]+$/", message = "检索首字母是且仅能是1个字符"/* , groups = { AddGroup.class, UpdateGroup.class } */)
	private String firstLetter;
	/**
	 * 排序
	 */
	@NotNull(groups = { AddGroup.class })
	@Min(value = 0, message = "排序必须大于等于0", groups = { AddGroup.class, UpdateGroup.class })
	private Integer sort;

}
