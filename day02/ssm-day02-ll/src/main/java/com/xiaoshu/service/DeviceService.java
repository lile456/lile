package com.xiaoshu.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.github.pagehelper.util.StringUtil;
import com.xiaoshu.dao.DeviceMapper;
import com.xiaoshu.dao.TypeMapper;
import com.xiaoshu.dao.UserMapper;
import com.xiaoshu.entity.Device;
import com.xiaoshu.entity.User;
import com.xiaoshu.entity.UserExample;
import com.xiaoshu.entity.Vo;
import com.xiaoshu.entity.UserExample.Criteria;

import aj.org.objectweb.asm.Type;

@Service
public class DeviceService {

	@Autowired
	private DeviceMapper dm;
	@Autowired
	private TypeMapper tm;
	
	// 新增
	public void addDevice(Device d) throws Exception {
		dm.insert(d);
	};

	// 修改
	public void updateDevice(Device d) throws Exception {
		dm.updateByPrimaryKeySelective(d);
	};

	// 删除
	public void deleteDevice(Integer id) throws Exception {
		dm.deleteByPrimaryKey(id);
	};

	public PageInfo<Vo> findDevicePage(Vo v, int pageNum, int pageSize) {
		PageHelper.startPage(pageNum, pageSize);
		List<Vo> vList = dm.findAll(v);
		PageInfo<Vo> pageInfo = new PageInfo<Vo>(vList);
		return pageInfo;
	}

	public List<com.xiaoshu.entity.Type> findType() {
		
		return tm.selectAll();
	}

	public List<Device> findByName(String devicename) {
		
		return dm.findByName(devicename);
	}

	public List<Vo> findAll() {
		return dm.findVo();
	}
	


}
