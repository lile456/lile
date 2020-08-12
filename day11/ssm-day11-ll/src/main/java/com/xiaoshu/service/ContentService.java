package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.xiaoshu.dao.ContentCategoryMapper;
import com.xiaoshu.dao.ContentMapper;
import com.xiaoshu.entity.Content;
import com.xiaoshu.entity.ContentCategory;
import com.xiaoshu.entity.Vo;

@Service
public class ContentService {

	@Autowired
	private ContentMapper cm;
	@Autowired 
	private ContentCategoryMapper ccm;

	// 新增
	public void addContent(Content t) throws Exception {
		cm.insert(t);
	};

	// 修改
	public void updateContent(Content t) throws Exception {
		cm.updateByPrimaryKeySelective(t);
	};

	// 删除
	public void deleteContent(Integer id) throws Exception {
		cm.deleteByPrimaryKey(id);
	};

	public PageInfo<Vo> findUserPage(Vo v, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		
		List<Vo> userList = cm.findAll(v);
		PageInfo<Vo> pageInfo = new PageInfo<Vo>(userList);
		return pageInfo;
	}

	

	public List<Vo> findVo() {
		
		return cm.findVo();
	}

	public List<ContentCategory> findCategory() {
		List<ContentCategory> list = ccm.findCategory();
		return list;
	};


}
