package com.wrial.service.impl;
/*
 * @Author  Wrial
 * @Date Created in 21:33 2019/8/10
 * @Description VideoServiceImpl
 */

import com.wrial.mapper.VideosMapper;
import com.wrial.pojo.Videos;
import com.wrial.service.VideoService;
import io.swagger.annotations.Api;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class VideoServiceImpl implements VideoService {


    @Autowired
    private Sid sid;
    @Autowired
    private VideosMapper videosMapper;

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public String saveVideo(Videos video) {

        //获取全局id
        String id = sid.nextShort();
        video.setId(id);
        videosMapper.insertSelective(video);
        return id;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateVideo(String videoId, String coverPath) {

        Videos video = new Videos();
        video.setId(videoId);
        video.setCoverPath(coverPath);
        videosMapper.updateByPrimaryKeySelective(video);
    }

}
