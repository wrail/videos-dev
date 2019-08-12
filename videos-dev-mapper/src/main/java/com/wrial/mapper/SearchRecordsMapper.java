package com.wrial.mapper;

import com.wrial.util.MyMapper;
import com.wrial.pojo.SearchRecords;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface SearchRecordsMapper extends MyMapper<SearchRecords> {


	/*
	根据内容进行分组查询，并且根据各个数量进行排序，然后返回排好序的标签集合
	 */
	@Select("select content from search_records group by content order by count(content) desc")
	public List<String> getHotwords();
}