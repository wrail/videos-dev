package com.wrial.service;
/*
 * @Author  Wrial
 * @Date Created in 21:33 2019/8/10
 * @Description VideoService
 */


import com.wrial.pojo.Videos;
import com.wrial.utils.PagedResult;

import java.util.List;

public interface VideoService {

    /**
     * @Description: 保存视频
     */
    public String saveVideo(Videos video);

    /**
     * @Description: 修改视频的封面
     */
    public void updateVideo(String videoId, String coverPath);

    /**
     * @Description: 分页查询视频列表
     */
    public PagedResult getAllVideos(String videoDesc, Integer isSaveRecords, Integer page, Integer pageSize);

    /**
     * @Description: 拿到热搜词列表
     */
    List<String> getHotWords();
}
