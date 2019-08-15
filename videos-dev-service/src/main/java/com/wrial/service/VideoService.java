package com.wrial.service;
/*
 * @Author  Wrial
 * @Date Created in 21:33 2019/8/10
 * @Description VideoService
 */


import com.wrial.pojo.Comments;
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
    public PagedResult getAllVideos(String userId, String videoDesc, Integer isSaveRecords, Integer page, Integer pageSize);

    /**
     * @Description: 拿到热搜词列表
     */
    public List<String> getHotWords();

    /*
    点赞
     */
    public void userLikeVideo(String userId, String videoId, String videoCreatorId);

    /*
    取消点赞
     */
    public void userUnLikeVideo(String userId, String videoId, String videoCreatorId);

    /*
    分页查询我点赞过的视频
     */
    PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize);

    /*
    分页查询我关注的人的视频
     */
    PagedResult queryMyFollowVideos(String userId, Integer page, Integer pageSize);

    /*
    保存评论
     */
    void saveComment(Comments comment);

    /*
    分页查找评论
     */
    PagedResult getAllComments(String videoId, Integer page, Integer pageSize);
}
