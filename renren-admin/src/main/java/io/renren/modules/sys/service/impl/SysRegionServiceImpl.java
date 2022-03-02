/**
 * Copyright (c) 2018 人人开源 All rights reserved.
 *
 * https://www.renren.io
 *
 * 版权所有，侵权必究！
 */

package io.renren.modules.sys.service.impl;

import io.renren.common.exception.ErrorCode;
import io.renren.common.exception.RenException;
import io.renren.common.service.impl.BaseServiceImpl;
import io.renren.common.utils.ConvertUtils;
import io.renren.modules.sys.dao.SysRegionDao;
import io.renren.modules.sys.dto.SysRegionDTO;
import io.renren.modules.sys.dto.region.Region;
import io.renren.modules.sys.dto.region.RegionCity;
import io.renren.modules.sys.dto.region.RegionProvince;
import io.renren.modules.sys.entity.SysRegionEntity;
import io.renren.modules.sys.enums.RegionLeafEnum;
import io.renren.modules.sys.enums.RegionLevelEnum;
import io.renren.modules.sys.service.SysRegionService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


@Service
public class SysRegionServiceImpl extends BaseServiceImpl<SysRegionDao, SysRegionEntity> implements SysRegionService {

	@Override
	public List<SysRegionDTO> list(Map<String, Object> params) {
		String pid = (String) params.get("pid");

		if(StringUtils.isBlank(pid)){
			//查询一级
			params.put("treeLevel", RegionLevelEnum.ONE.value());
		}
		//查询列表
		List<SysRegionEntity> entityList = baseDao.getList(params);

		List<SysRegionDTO> dtoList = new ArrayList<>(entityList.size());
		for(SysRegionEntity entity : entityList){
			SysRegionDTO dto = new SysRegionDTO();
			BeanUtils.copyProperties(entity, dto);
			dto.setHasChildren(entity.getLeaf() != 1);

			dtoList.add(dto);
		}

		return dtoList;
	}

	@Override
	public List<Map<String, Object>> getTreeList() {
		return baseDao.getTreeList();
	}

	@Override
	public SysRegionDTO get(Long id) {
		SysRegionEntity entity = baseDao.getById(id);

		return ConvertUtils.sourceToTarget(entity, SysRegionDTO.class);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(SysRegionDTO dto) {
		SysRegionEntity entity = ConvertUtils.sourceToTarget(dto, SysRegionEntity.class);

		//查询上级
		SysRegionEntity parentEntity = baseDao.getById(dto.getPid());
		if(parentEntity == null){
			entity.setTreeLevel(RegionLevelEnum.ONE.value());
		}else {
			entity.setTreeLevel(parentEntity.getTreeLevel() + 1);
			//上级存在，且为叶子节点，需要修改为非叶子节点
			if(parentEntity.getLeaf() == RegionLeafEnum.YES.value()){
				parentEntity.setLeaf(RegionLeafEnum.NO.value());
				baseDao.updateById(parentEntity);
			}
		}

		//新增都是叶子节点
		entity.setLeaf(RegionLeafEnum.YES.value());
		insert(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(SysRegionDTO dto) {
		SysRegionEntity entity = ConvertUtils.sourceToTarget(dto, SysRegionEntity.class);

		//上级不能为自身
		if(entity.getId().equals(entity.getPid())){
			throw new RenException(ErrorCode.SUPERIOR_REGION_ERROR);
		}

		//查询上级
		SysRegionEntity parentEntity = baseDao.getById(dto.getPid());
		if(parentEntity == null){
			entity.setTreeLevel(RegionLevelEnum.ONE.value());
		}else {
			entity.setTreeLevel(parentEntity.getTreeLevel() + 1);
			//上级存在，且为叶子节点，需要修改为非叶子节点
			if(parentEntity.getLeaf() == RegionLeafEnum.YES.value()){
				parentEntity.setLeaf(RegionLeafEnum.NO.value());
				baseDao.updateById(parentEntity);
			}
		}

		//查询下级
		int subCount = baseDao.getCountByPid(dto.getId());
		if(subCount == 0){
			entity.setLeaf(RegionLeafEnum.YES.value());
		}else {
			entity.setLeaf(RegionLeafEnum.NO.value());
		}

		updateById(entity);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void delete(Long id) {
		//删除
		baseDao.deleteById(id);
	}

	@Override
	public int getCountByPid(Long pid) {
		return baseDao.getCountByPid(pid);
	}

	@Override
	public List<RegionProvince> getRegion(boolean threeLevel) {
		List<SysRegionEntity> provinceList = baseDao.getListByLevel(RegionLevelEnum.ONE.value());
		List<SysRegionEntity> cityList = baseDao.getListByLevel(RegionLevelEnum.TWO.value());

		List<RegionProvince> provinces = ConvertUtils.sourceToTarget(provinceList, RegionProvince.class);
		List<RegionCity> cities = ConvertUtils.sourceToTarget(cityList, RegionCity.class);

		for(RegionCity city : cities){
			for(RegionProvince province : provinces){
				if(city.getPid().equals(province.getId())){
					province.getCities().add(city);
				}
			}
		}

		//无需显示3级区县
		if(!threeLevel){
			return provinces;
		}

		List<SysRegionEntity> countyList = baseDao.getListByLevel(RegionLevelEnum.THREE.value());
		List<Region> counties = ConvertUtils.sourceToTarget(countyList, Region.class);
		for(Region county : counties){
			for(RegionCity city : cities){
				if(county.getPid().equals(city.getId())){
					city.getCounties().add(county);
				}
			}
		}

		return provinces;
	}
}
