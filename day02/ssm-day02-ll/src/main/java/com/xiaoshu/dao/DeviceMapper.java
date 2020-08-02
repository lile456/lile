package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Device;
import com.xiaoshu.entity.DeviceExample;
import com.xiaoshu.entity.Vo;

import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface DeviceMapper extends BaseMapper<Device> {
    
	List<Vo> findAll(Vo v);

	List<Device> findByName(String devicename);

	List<Vo> findVo();
}