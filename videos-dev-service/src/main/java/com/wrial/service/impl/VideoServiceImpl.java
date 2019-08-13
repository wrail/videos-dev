package com.wrial.service.impl;
/*
 * @Author  Wrial
 * @Date Created in 21:33 2019/8/10
 * @Description VideoServiceImpl
 */

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wrial.mapper.SearchRecordsMapper;
import com.wrial.mapper.UsersLikeVideosMapper;
import com.wrial.mapper.UsersMapper;
import com.wrial.mapper.VideosMapper;
import com.wrial.pojo.SearchRecords;
import com.wrial.pojo.Users;
import com.wrial.pojo.UsersLikeVideos;
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

    @Autowired
    private SearchRecordsMapper searchRecordsMapper;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

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
    isSaveRecord：1 - 需要保存    0 - 不需要保存 ，或者为空的时候
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public PagedResult getAllVideos(String videoDesc, Integer isSaveRecords, Integer pageNum, Integer pageSize) {

        if (videoDesc.equals("undefined")) {
            videoDesc = "";
        }

        if (isSaveRecords == 1) {
            SearchRecords records = new SearchRecords();
            records.setContent(videoDesc);
            records.setId(sid.nextShort());
            //保存热搜词
            searchRecordsMapper.insert(records);
        }

        List<VideosVO> videosVOS = new ArrayList<>();

        PageHelper.startPage(pageNum, pageSize);
        //如果存在desc的话就进行模糊查询
        List<Videos> videos = videosMapper.selectAllByDesc(videoDesc);

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

    /*
    从数据库中分组并且按照次数排序得到热搜词
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public List<String> getHotWords() {
        return searchRecordsMapper.getHotwords();
    }

    /*
    点赞实现
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userLikeVideo(String userId, String videoId, String videoCreatorId) {
        // 1-保存用户和视频的喜欢点赞关联关系表
        String likeId = sid.nextShort();
        UsersLikeVideos ulv = new UsersLikeVideos();
        ulv.setId(likeId);
        ulv.setUserId(userId);
        ulv.setVideoId(videoId);
        usersLikeVideosMapper.insert(ulv);
        // 2-视频喜欢数量累加
        videosMapper.addVideoLikeCount(videoId);
        // 3-用户受喜欢数量的累加
        usersMapper.addReceiveLikeCount(videoCreatorId);
    }

    /*
    取消点赞实现
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void userUnLikeVideo(String userId, String videoId, String videoCreatorId) {
        // 1-删除用户和视频的喜欢点赞关联关系表
        Example example = new Example(UsersLikeVideos.class);
        Example.Criteria criteria = example.createCriteria();
        criteria.andEqualTo("userId", userId).andEqualTo("videoId", videoId);
        usersLikeVideosMapper.deleteByExample(example);
        // 2-视频喜欢数量累减
        videosMapper.reduceVideoLikeCount(videoId);
        // 3-用户受喜欢数量的累减
        usersMapper.reduceReceiveLikeCount(videoCreatorId);
    }


}
