package com.singbon.dao.systemManager.systemSetting;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.singbon.dao.BaseDAO;
import com.singbon.entity.Batch;

/**
 * 批次管理dao层
 * 
 * @author 郝威
 * 
 */
public interface BatchDAO extends BaseDAO {

	/**
	 * 根据人员部门主键获取批次
	 * 
	 * @return
	 */
	public Batch selectByDeptId(@Param("deptId") Integer deptId);

	/**
	 * 挂失
	 * 
	 * @return
	 */
	public void black(@Param("id") Integer id);
	
	/**
	 * 查询挂失批次列表
	 * 
	 * @return
	 */
	public List<Batch> selectBlackList(@Param("companyId") Integer companyId);
}
