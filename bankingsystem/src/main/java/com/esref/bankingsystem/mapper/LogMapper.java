package com.esref.bankingsystem.mapper;

import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.esref.bankingsystem.models.Log;

@Mapper
public interface LogMapper {
	
	public void save(Log log);
	public List<Log> getLogsByAccountId(long accountId);

}
