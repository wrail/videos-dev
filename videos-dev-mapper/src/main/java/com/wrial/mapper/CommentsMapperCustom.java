package com.wrial.mapper;


import com.wrial.pojo.vo.CommentsVO;
import com.wrial.util.MyMapper;
import com.wrial.pojo.Comments;

import java.util.List;

public interface CommentsMapperCustom extends MyMapper<Comments> {
	
	public List<CommentsVO> queryComments(String videoId);
}