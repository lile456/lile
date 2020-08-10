package com.xiaoshu.dao;

import java.util.List;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Course;

public interface CourseMapper extends BaseMapper<Course> {

	List<Course> findBycode(String code);

	Course findByName(String name);
   
	
}