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
    搜索（模糊查询）
     */
    @Select("select * from videos where video_desc like concat('%',#{desc},'%') order by create_time desc")
    List<Videos> selectAllByDesc(String desc);

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