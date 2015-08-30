package com.singbon.dao.systemManager.systemSetting;

import org.apache.ibatis.annotations.Param;

import com.singbon.dao.BaseDAO;
import com.singbon.entity.WaterRate;

/**
 * 水控费率2、3、4dao层
 * 
 * @author 郝威
 * 
 */
public interface WaterRateDAO extends BaseDAO {

	/**
	 * 添加
	 * 
	 * @param companyId
	 * @return
	 */
	public void insert(@Param("companyId") Integer companyId);

	/**
	 * 根据公司id查询
	 * 
	 * @param companyId
	 * @return
	 */
	public WaterRate selectByCompanyId(@Param("companyId") Integer companyId);
}
