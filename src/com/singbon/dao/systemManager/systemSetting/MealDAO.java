package com.singbon.dao.systemManager.systemSetting;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.singbon.dao.BaseDAO;
import com.singbon.entity.Meal;

/**
 * 餐别设置dao层
 * 
 * @author 郝威
 * 
 */
public interface MealDAO extends BaseDAO{

	/**
	 * 添加
	 * 
	 * @param companyId
	 * @return
	 */
	public void insert(@Param("companyId") Integer companyId);

	/**
	 * 列表
	 * 
	 * @return
	 */
	public List<Meal> selectList(@Param("companyId") Integer companyId);
}