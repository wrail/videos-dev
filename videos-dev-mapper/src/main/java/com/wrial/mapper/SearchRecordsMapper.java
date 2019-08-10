package com.wrial.mapper;

import com.wrial.util.MyMapper;
import com.wrial.pojo.SearchRecords;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {
	
	public List<String> getHotwords();
}