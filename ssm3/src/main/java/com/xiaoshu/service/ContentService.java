package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.ContentCategoryMapper;
import com.xiaoshu.dao.ContentMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.Content;
import com.xiaoshu.entity.ContentCategory;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.UserExample.Criteria;
import com.xiaoshu.entity.Vo;

@Service
public class ContentService {

	@Autowired
	private ContentMapper cm;
	@Autowired
	private ContentCategoryMapper gm;

	// 新增
	public void addContent(Content c) throws Exception {
		cm.insert(c);
	};

	// 修改
	public void updateContent(Content c) throws Exception {
		cm.updateByPrimaryKeySelective(c);
	};

	// 删除
	public void deleteContent(Integer id) throws Exception {
		cm.deleteByPrimaryKey(id);
	};
	
	// 分页查询
	public PageInfo<Vo> findContentPage(Vo v, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Vo> vList = cm.findAll(v);
		PageInfo<Vo> pageInfo = new PageInfo<Vo>(vList);
		return pageInfo;
	}

	public List<ContentCategory> findCategory() {
		
		return gm.findCategory();
		
	}

	public List<Content> findByName(String contenttitle) {
		
		return cm.findByName(contenttitle);
	}

	public ContentCategory findC(String categoryname) {
		ContentCategory c = gm.findC(categoryname);
		return c;
	}

	public void addCategory(ContentCategory g1) {
		gm.addCategory(g1);
	}

	public void addConten(Content c) {
		cm.insert(c);
	}


}
