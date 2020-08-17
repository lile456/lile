package com.xiaoshu.dao;

import com.xiaoshu.base.dao.BaseMapper;
import com.xiaoshu.entity.Bank;
import com.xiaoshu.entity.BankExample;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface BankMapper extends BaseMapper<Bank> {

	Bank findById(int i);

	Bank findByname(String getbName);
}