package com.wrial.service.impl;
/*
 * @Author  Wrial
 * @Date Created in 21:33 2019/8/10
 * @Description VideoServiceImpl
 */

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wrial.mapper.UsersMapper;
import com.wrial.mapper.VideosMapper;
import com.wrial.pojo.Users;
import com.wrial.pojo.Videos;
import com.wrial.pojo.vo.VideosVO;
import com.wrial.service.VideoService;
import com.wrial.utils.PagedResult;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.List;

@Service
public class VideoServiceImpl implements VideoService {


    @Autowired
    private Sid sid;
    @Autowired
    private VideosMapper videosMapper;

    @Autowired
    private UsersMapper usersMapper;

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

    /*
    分页展示所有的Video,就是主页上显示的，带有用户头像和昵称，因此会使用VideosVo
     */
    @Override
    public PagedResult getAllVideos(Integer pageNum, Integer pageSize) {

        List<VideosVO> videosVOS = new ArrayList<>();

        PageHelper.startPage(pageNum, pageSize);
        List<Videos> videos = videosMapper.selectAllByDesc();

        //给所有video加上用户属性
        for (Videos video1 : videos) {
            Users user = usersMapper.selectByPrimaryKey(video1.getUserId());
            VideosVO videosVO = new VideosVO();
            BeanUtils.copyProperties(video1, videosVO);
            videosVO.setNickname(user.getNickname());
            videosVO.setFaceImage(user.getFaceImage());
            videosVOS.add(videosVO);
        }

        PageInfo<VideosVO> videosPageInfo = new PageInfo<>(videosVOS);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setPage(pageNum);
        pagedResult.setRows(videosPageInfo.getList());

        //总记录数
        Long recordsCount = videosMapper.totalRecordsCount();
        pagedResult.setRecords(recordsCount);
        //总页数
        int totalPage = 0;
        totalPage = recordsCount % pageSize == 0 ? (int) (recordsCount / pageSize) : (int) (recordsCount / pageSize) + 1;
        pagedResult.setTotal(totalPage);

        return pagedResult;
    }

}
