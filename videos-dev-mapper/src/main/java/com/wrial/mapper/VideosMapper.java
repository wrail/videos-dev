package com.wrial.mapper;

import com.wrial.util.MyMapper;
import com.wrial.pojo.Videos;
import org.apache.ibatis.annotations.Select;

import java.util.List;

public interface VideosMapper extends MyMapper<Videos> {

    @Select("select count(*) from videos")
    Long totalRecordsCount();


    @Select("select * from videos where video_desc like concat('%',#{desc},'%') order by create_time desc")
    List<Videos> selectAllByDesc(String desc);
}