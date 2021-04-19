package de.killbuqs.mall.product.service.impl;

import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import de.killbuqs.common.utils.PageUtils;
import de.killbuqs.common.utils.Query;
import de.killbuqs.mall.product.dao.CategoryDao;
import de.killbuqs.mall.product.entity.CategoryEntity;
import de.killbuqs.mall.product.service.CategoryBrandRelationService;
import de.killbuqs.mall.product.service.CategoryService;
import de.killbuqs.mall.product.vo.ui.Catalog2Vo;
import de.killbuqs.mall.product.vo.ui.Catalog2Vo.Catalog3Vo;

@Service("categoryService")
public class CategoryServiceImpl extends ServiceImpl<CategoryDao, CategoryEntity> implements CategoryService {

	@Autowired
	private CategoryBrandRelationService categoryBrandRelationService;

	@Autowired
	private StringRedisTemplate redisTemplate;

	@Autowired
	private RedissonClient redisson;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		IPage<CategoryEntity> page = this.page(new Query<CategoryEntity>().getPage(params),
				new QueryWrapper<CategoryEntity>());

		return new PageUtils(page);
	}

	@Override
	public List<CategoryEntity> listWithTree() {

		// 查出所有分类，组装成父子的父型结构
		List<CategoryEntity> entities = baseMapper.selectList(null);

		List<CategoryEntity> level3Menus = entities.stream().filter(categoryEntity -> categoryEntity.getCatLevel() == 3)
				.collect(Collectors.toList());

		List<CategoryEntity> level2Menus = addChildrenForLevel(entities, 2, level3Menus);

		List<CategoryEntity> level1Menus = addChildrenForLevel(entities, 1, level2Menus);

		return level1Menus;
	}

	private List<CategoryEntity> addChildrenForLevel(List<CategoryEntity> entities, int level,
			List<CategoryEntity> level3Menus) {
		return entities.stream() //
				.filter(categoryEntity -> categoryEntity.getCatLevel() == level) //
				.map(menu -> {
					menu.setChildren(getChildren(menu, level3Menus));
					return menu;
				}) //
				.sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort())
						- (menu2.getSort() == null ? 0 : menu2.getSort())) //
				.collect(Collectors.toList());
	}

	@Override
	public Long[] findCatelogPath(Long catelogId) {
		List<Long> paths = new ArrayList<>();

		CategoryEntity byId = this.getById(catelogId);
		paths.add(catelogId);

		for (int i = 0; i < 3; i++) {
			Long parentCid = byId.getParentCid();
			if (parentCid != 0) {
				byId = this.getById(parentCid);
				paths.add(parentCid);
			} else {
				break;
			}
		}
		Collections.reverse(paths);
		return (Long[]) paths.toArray(new Long[paths.size()]);
	}

	private List<CategoryEntity> getChildren(CategoryEntity currentEntity, List<CategoryEntity> all) {
		return all.stream() //
				.filter(categoryEntity -> currentEntity.getCatId() == categoryEntity.getParentCid()) //
				.sorted((menu1, menu2) -> (menu1.getSort() == null ? 0 : menu1.getSort())
						- (menu2.getSort() == null ? 0 : menu2.getSort())) //
				.collect(Collectors.toList());

	}

	@Override
	public void removeMenuByIds(List<Long> asList) {
		// TODO 检查当前删除的菜单是否被别的地方引用
		baseMapper.deleteBatchIds(asList);

	}

	/**
	 *
	 * @CacheEvict 缓存失效
	 *
	 */
//	@Caching( evict = {
//			@CacheEvict(value = { "category" }, key = "'getLevel1Categories'"),
//			@CacheEvict(value = { "category" }, key = "'getCatalogJson'")
//	})
	@CacheEvict(value = { "category" }, allEntries = true)  // 失效模式
//	@CachePut 双写模式
	@Transactional
	@Override
	public void updateCascade(CategoryEntity category) {
		this.updateById(category);
		categoryBrandRelationService.updateCategory(category.getCatId(), category.getName());
	}

	/**
	 * 当前方法的结果将会被缓存，如果缓存中有，直接用不调用方法，如果缓存中没有，会调用方法，并将结果放入缓存
	 * 
	 * 缓存的key默认自动生成，格式为： 缓存的名字::SimpleKey [] 缓存的alue值为：默认使用jdk序列化机制，将序列化后的数据存到redis
	 * 缓存的默认时间是-1, 数据永不过期
	 *
	 * 自定义 key: key属性指定，可接受一个SpEL
	 * https://docs.spring.io/spring-framework/docs/current/reference/html/integration.html#cache-spel-context
	 * ttl: spring.cache.redis.time-to-live （毫秒） value值: 改为json格式
	 * CacheAutoConfiguration
	 * 
	 *
	 *
	 */
	@Cacheable(value = { "category" }, key = "#root.method.name", sync = true)
//	@Cacheable(value = {"category"}, key = "'level1Categories'" )
	@Override
	public List<CategoryEntity> getLevel1Categories() {
		return this.baseMapper.selectList(new QueryWrapper<CategoryEntity>().eq("parent_cid", 0));
	}

	@Cacheable(value = { "category" }, key = "#root.methodName")
	@Override
	public Map<String, List<Catalog2Vo>> getCatalogJson() {

		// 缓存中没有数据，去数据库读取
		List<CategoryEntity> selectList = this.baseMapper.selectList(null);

		// 查出所有1级分类
		List<CategoryEntity> level1Categories = getParentCid(selectList, 0L);

		Map<String, List<Catalog2Vo>> parentCid = level1Categories.stream()
				.collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
					// 查询每一个一级分类的二级分类
					List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
					List<Catalog2Vo> catalog2Vos = null;
					if (categoryEntities != null) {
						catalog2Vos = categoryEntities.stream().map(level2Catalog -> {
							Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null,
									level2Catalog.getCatId().toString(), level2Catalog.getName());
							// 找当前二级分类的三级分类
							List<CategoryEntity> level3Catalogs = getParentCid(selectList, level2Catalog.getCatId());
							if (level3Catalogs != null) {
								List<Catalog3Vo> level3CatalogVos = level3Catalogs.stream().map(level3Catalog -> {
									Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(
											level2Catalog.getCatId().toString(), level3Catalog.getCatId().toString(),
											level3Catalog.getName());
									return catalog3Vo;
								}).collect(Collectors.toList());
								catalog2Vo.setCatalog3List(level3CatalogVos);
							}

							return catalog2Vo;
						}).collect(Collectors.toList());
					}

					return catalog2Vos;
				}));
		return parentCid;
	}

	@Deprecated
	public Map<String, List<Catalog2Vo>> getCatalogJsonWithRedis() {
		// 缓冲中存json数据,数据重用
		// 处理缓存穿透: 空结果缓存
		// 处理缓存雪崩： 设置加随机值的过期时间
		// 处理缓存击穿: 加锁

		// 加入缓存逻辑，缓存中存的数据是json字符串，跨平台兼容
		String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
		if (StringUtils.isEmpty(catalogJSON)) {
			Map<String, List<Catalog2Vo>> catalogJsonFromDb = getCatalogJsonFromDb();
			return catalogJsonFromDb;
		}
		Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON,
				new TypeReference<Map<String, List<Catalog2Vo>>>() {
				});
		return result;

	}

	/**
	 * 
	 * 缓存里面的数据需要和数据库保持一致性 1) 双写模式： 改完数据库，同时写入缓存. 同时写的情况下，可能产生脏数据，解决方案可以加锁
	 * 2）失效模式：改完数据库，删调缓存信息，同时写的情况下，可能产生脏数据，解决方案可以加锁
	 * 
	 * 一致性解决方案： 缓存的数据都有过期时间，数据过期下一次查询触发主动更新 读写数据的时候，加上分布式的读写锁，经常写会影响性能，很少写，只读不影响。
	 * 
	 * 
	 * @return
	 */
	@Deprecated
	public Map<String, List<Catalog2Vo>> getCatalogJsonFromDbWithRedissonLock() {

		// 注意锁的名字要体现锁的粒度
		// 锁的粒度： 具体缓存的是某个数据， 11号商品的锁： product-11-lock
		RLock lock = redisson.getLock("catalogJson-lock");
		lock.lock();

		Map<String, List<Catalog2Vo>> dataFromDb;
		try {
			dataFromDb = getCatalogJsonFromDb();
		} finally {
			lock.unlock();
		}

		return dataFromDb;
	}

	public Map<String, List<Catalog2Vo>> getCatalogJsonFromDb() {

		// 本地锁，锁当前容器实例的进程 (synchronized, JUC(lock))，在分布式环境下，每台容器都有一把单独的锁。
		// 可以使用分布式锁，保证整个分布式环境只有一把锁，但是效率不高
		synchronized (this) {
			// 得到锁之后，再去缓存中确定一次，如果没有才需要去数据库中查询
			String catalogJSON = redisTemplate.opsForValue().get("catalogJSON");
			if (!StringUtils.isEmpty(catalogJSON)) {
				Map<String, List<Catalog2Vo>> result = JSON.parseObject(catalogJSON,
						new TypeReference<Map<String, List<Catalog2Vo>>>() {
						});
				return result;
			}

			// 缓存中没有数据，去数据库读取
			List<CategoryEntity> selectList = this.baseMapper.selectList(null);

			// 查出所有1级分类
			List<CategoryEntity> level1Categories = getParentCid(selectList, 0L);

			Map<String, List<Catalog2Vo>> parentCid = level1Categories.stream()
					.collect(Collectors.toMap(k -> k.getCatId().toString(), v -> {
						// 查询每一个一级分类的二级分类
						List<CategoryEntity> categoryEntities = getParentCid(selectList, v.getCatId());
						List<Catalog2Vo> catalog2Vos = null;
						if (categoryEntities != null) {
							catalog2Vos = categoryEntities.stream().map(level2Catalog -> {
								Catalog2Vo catalog2Vo = new Catalog2Vo(v.getCatId().toString(), null,
										level2Catalog.getCatId().toString(), level2Catalog.getName());
								// 找当前二级分类的三级分类
								List<CategoryEntity> level3Catalogs = getParentCid(selectList,
										level2Catalog.getCatId());
								if (level3Catalogs != null) {
									List<Catalog3Vo> level3CatalogVos = level3Catalogs.stream().map(level3Catalog -> {
										Catalog3Vo catalog3Vo = new Catalog2Vo.Catalog3Vo(
												level2Catalog.getCatId().toString(),
												level3Catalog.getCatId().toString(), level3Catalog.getName());
										return catalog3Vo;
									}).collect(Collectors.toList());
									catalog2Vo.setCatalog3List(level3CatalogVos);
								}

								return catalog2Vo;
							}).collect(Collectors.toList());
						}

						return catalog2Vos;
					}));

			String jsonString = JSON.toJSONString(parentCid);
			redisTemplate.opsForValue().set("catalogJSON", jsonString, 1l, TimeUnit.DAYS);

			return parentCid;
		}

	}

	private List<CategoryEntity> getParentCid(List<CategoryEntity> selectList, Long parentCid) {
		return selectList.stream().filter(item -> item.getParentCid().equals(parentCid)).collect(Collectors.toList());
	}

}