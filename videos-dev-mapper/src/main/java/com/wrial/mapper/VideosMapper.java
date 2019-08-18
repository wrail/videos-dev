package com.wrial.mapper;

import com.wrial.util.MyMapper;
import com.wrial.pojo.Videos;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

public interface VideosMapper extends MyMapper<Videos> {

    /*
    得到video总数
     */
    @Select("select count(*) from videos")
    Long totalRecordsCount();

    /*
    主页搜索，模糊搜索（模糊查询），个人页面搜索复用
     */
    @Select("select * from videos where status = 1 and video_desc like concat('%',#{desc},'%') order by create_time desc")
    List<Videos> selectAllByDesc(String desc);
    /*

     */
    @Select("select * from videos where status = 1 and user_id = #{userId} and video_desc like concat('%',#{desc},'%') order by create_time desc")
    List<Videos> selectAllByDescAndId(String userId, String desc);

    /*
    点赞
     */
    @Update("update videos set like_counts = like_counts+1 where id = #{videoId}")
    void addVideoLikeCount(String videoId);

    /*
    取消点赞
     */
    @Update("update videos set like_counts = like_counts-1 where id = #{videoId}")
    void reduceVideoLikeCount(String videoId);

}