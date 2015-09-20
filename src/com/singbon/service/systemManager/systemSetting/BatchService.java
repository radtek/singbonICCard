package com.singbon.service.systemManager.systemSetting;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.singbon.dao.systemManager.systemSetting.BatchDAO;
import com.singbon.entity.Batch;

/**
 * 批次业务层
 * 
 * @author 郝威
 * 
 */
@Service
public class BatchService {

	@Autowired
	public BatchDAO batchDAO;

	/**
	 * 添加批次
	 * 
	 * @param batch
	 */
	public void save(Batch batch) {
		this.batchDAO.insert(batch);
	}

	/**
	 * 修改批次
	 * 
	 * @param batch
	 */
	public void update(Batch batch) {
		this.batchDAO.update(batch);
	}

	/**
	 * 批次列表
	 * 
	 * @return
	 */
	public List<Batch> selectList(Integer companyId) {

		return this.batchDAO.selectList(companyId);
	}

	/**
	 * 根据主键获取批次
	 * 
	 * @return
	 */
	public Batch selectById(Integer id) {
		return (Batch) this.batchDAO.selectById(id);
	}

	/**
	 * 根据人员部门主键获取批次
	 * 
	 * @return
	 */
	public Batch selectByDeptId(Integer deptId) {
		return this.batchDAO.selectByDeptId(deptId);
	}
}