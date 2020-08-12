package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.ContentCategory;
import com.xiaoshu.entity.ContentCategoryExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ContentCategoryMapper extends BaseMapper<ContentCategory> {

	List<ContentCategory> findCategory();
   
}