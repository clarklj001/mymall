package de.killbuqs.mall.product.app;

import java.util.Arrays;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.R;
import de.killbuqs.mall.product.entity.SpuInfoEntity;
import de.killbuqs.mall.product.service.SpuInfoService;
import de.killbuqs.mall.product.vo.SpuSaveVo;



/**
 * spu信息
 *
 * @author jlong
 * @email jie.long@killbuqs.de
 * @date 2021-03-14 21:40:00
 */
@RestController
@RequestMapping("product/spuinfo")
public class SpuInfoController {
    @Autowired
    private SpuInfoService spuInfoService;

    /**
     * https://easydoc.xyz/s/78237135/ZUqEdvA4/DhOtFr4A
     * 
     * 上架到Elastic Search
     * 
     */
    @PostMapping("/{spuId}/up")
    public R spuUp(@PathVariable("spuId") Long spuId){
    	
    	spuInfoService.up(spuId);
    	
    	return R.ok();
    }
    
    /**
     * 列表
     */
    @RequestMapping("/list")
    public R list(@RequestParam Map<String, Object> params){
        PageUtils page = spuInfoService.queryPageByCondition(params);

        return R.ok().put("page", page);
    }


    /**
     * 信息
     */
    @RequestMapping("/info/{id}")
    public R info(@PathVariable("id") Long id){
		SpuInfoEntity spuInfo = spuInfoService.getById(id);

        return R.ok().put("spuInfo", spuInfo);
    }

    /**
     * https://easydoc.xyz/s/78237135/ZUqEdvA4/5ULdV3dd
     * 
     * 新增商品
     */
    @RequestMapping("/save")
    public R save(@RequestBody SpuSaveVo vo){
//		spuInfoService.save(spuInfo);
    	
    	spuInfoService.saveSpuInfo(vo);

        return R.ok();
    }

    /**
     * 修改
     */
    @RequestMapping("/update")
    public R update(@RequestBody SpuInfoEntity spuInfo){
		spuInfoService.updateById(spuInfo);

        return R.ok();
    }

    /**
     * 删除
     */
    @RequestMapping("/delete")
    public R delete(@RequestBody Long[] ids){
		spuInfoService.removeByIds(Arrays.asList(ids));

        return R.ok();
    }

}
