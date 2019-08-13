package com.wrial.mapper;

import com.wrial.util.MyMapper;
import com.wrial.pojo.Users;
import org.apache.ibatis.annotations.Update;

public interface UsersMapper extends MyMapper<Users> {
	
	/**
	 * @Description: 用户受喜欢数累加
	 */
	@Update("update users set receive_like_counts = receive_like_counts+1 where id = #{userId}")
	public void addReceiveLikeCount(String userId);
	
	/**
	 * @Description: 用户受喜欢数累减
	 */
	@Update("update users set receive_like_counts = receive_like_counts+1 where id = #{userId}")
	public void reduceReceiveLikeCount(String userId);
	
	/**
	 * @Description: 增加粉丝数
	 */
	public void addFansCount(String userId);
	
	/**
	 * @Description: 增加关注数
	 */
	public void addFollersCount(String userId);
	
	/**
	 * @Description: 减少粉丝数
	 */
	public void reduceFansCount(String userId);
	
	/**
	 * @Description: 减少关注数
	 */
	public void reduceFollersCount(String userId);
}