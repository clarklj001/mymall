package de.killbuqs.mall.product;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import de.killbuqs.mall.product.entity.BrandEntity;
import de.killbuqs.mall.product.service.BrandService;
import de.killbuqs.mall.product.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import lombok.extern.slf4j.XSlf4j;

@Slf4j
@RunWith(SpringRunner.class)
@SpringBootTest
@Transactional  
public class MyMallProductApplicationTest {
	
	@Autowired
	private BrandService brandService;
	
	@Autowired
	private CategoryService categoryService;
	
	@Test
	public void contextLoads() {
		BrandEntity entity = new BrandEntity();
		entity.setDescript("desc");
		entity.setName("Apple");
		brandService.save(entity );
		
		List<BrandEntity> list = brandService.list(new QueryWrapper<BrandEntity>().eq("brand_id", 1L));
		
		list.forEach(System.out::println);
		
	}
	
	@Test
	public void testFindPath() {
		Long[] catelogPath = categoryService.findCatelogPath(225l);
		
		log.info("Path: {}", Arrays.asList(catelogPath));
		
		
	}
}
