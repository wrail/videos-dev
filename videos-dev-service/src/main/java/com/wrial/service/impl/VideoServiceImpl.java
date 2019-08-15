package com.wrial.service.impl;
/*
 * @Author  Wrial
 * @Date Created in 21:33 2019/8/10
 * @Description VideoServiceImpl
 */

import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.wrial.mapper.*;
import com.wrial.pojo.*;
import com.wrial.pojo.vo.CommentsVO;
import com.wrial.pojo.vo.VideosVO;
import com.wrial.service.VideoService;
import com.wrial.utils.PagedResult;
import com.wrial.utils.TimeAgoUtils;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import java.util.ArrayList;
import java.util.Date;
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
    @Autowired
    private UsersFansMapper usersFansMapper;
    @Autowired
    private CommentsMapper commentsMapper;

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
    public PagedResult getAllVideos(String userId, String videoDesc, Integer isSaveRecords, Integer pageNum, Integer pageSize) {

        List<Videos> videos = new ArrayList<>();

        if (videoDesc.equals("undefined") || StringUtils.isBlank(videoDesc)) {
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
        //如果存在desc的话就进行模糊查询 如果存在userId就也加上userId这个条件
        //如果userId为空就正常查询
        if (userId.equals("undefined") || StringUtils.isBlank(userId)) {

            videos = videosMapper.selectAllByDesc(videoDesc);
        } else {
            videos = videosMapper.selectAllByDescAndId(userId, videoDesc);
        }

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

    /*
    查询点赞过的视频
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyLikeVideos(String userId, Integer page, Integer pageSize) {

        List<VideosVO> list = new ArrayList<>();


        PageHelper.startPage(page, pageSize);
        //1-查询当前用户喜欢的所有视频
        Example example = new Example(UsersLikeVideos.class);
        example.createCriteria()
                .andEqualTo("userId", userId);
        List<UsersLikeVideos> usersLikeVideos = usersLikeVideosMapper.selectByExample(example);

        //包装为VideoVo （通过users_like_videos的两个属性分别查找信息并包装）
        for (UsersLikeVideos usersLikeVideo : usersLikeVideos) {
            VideosVO videosVO = new VideosVO();
            Users user = usersMapper.selectByPrimaryKey(usersLikeVideo.getUserId());
            Videos videos = videosMapper.selectByPrimaryKey(usersLikeVideo.getVideoId());
            BeanUtils.copyProperties(videos, videosVO);
            videosVO.setFaceImage(user.getFaceImage());
            videosVO.setNickname(user.getNickname());
            list.add(videosVO);
        }

        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    /*
    分页查询我关注的人的视频
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult queryMyFollowVideos(String fanId, Integer page, Integer pageSize) {

        List<VideosVO> list = new ArrayList<>();
        PageHelper.startPage(page, pageSize);
        //得到当前用户的所有粉丝
        Example example = new Example(UsersFans.class);
        example.createCriteria().andEqualTo("fanId", fanId);
        List<UsersFans> myFellers = usersFansMapper.selectByExample(example);

        //和上个方法一样进行包装
        for (UsersFans feller : myFellers) {

            Users users = usersMapper.selectByPrimaryKey(feller.getUserId());
            Example example1 = new Example(Videos.class);
            example1.createCriteria().andEqualTo("userId", users.getId());
            List<Videos> videos = videosMapper.selectByExample(example1);
            for (Videos video : videos) {
                VideosVO videosVO = new VideosVO();
                BeanUtils.copyProperties(video, videosVO);
                videosVO.setNickname(users.getNickname());
                videosVO.setFaceImage(users.getFaceImage());
                list.add(videosVO);
            }

        }
        PageInfo<VideosVO> pageList = new PageInfo<>(list);

        PagedResult pagedResult = new PagedResult();
        pagedResult.setTotal(pageList.getPages());
        pagedResult.setRows(list);
        pagedResult.setPage(page);
        pagedResult.setRecords(pageList.getTotal());

        return pagedResult;
    }

    /*
    增加评论
     */
    @Override
    public void saveComment(Comments comment) {
        String id = sid.nextShort();
        comment.setId(id);
        comment.setCreateTime(new Date());
        commentsMapper.insert(comment);
    }

    /*
    分页去取评论
     */
    @Transactional(propagation = Propagation.SUPPORTS)
    @Override
    public PagedResult getAllComments(String videoId, Integer page, Integer pageSize) {

        List<CommentsVO> list = new ArrayList<>();
        PageHelper.startPage(page, pageSize);
        Example example = new Example(Comments.class);
        example.createCriteria().andEqualTo("videoId", videoId);
        List<Comments> comments = commentsMapper.selectByExample(example);

        for (Comments comment : comments) {

            Example example1 = new Example(Videos.class);
            example1.createCriteria().andEqualTo("id", videoId);
            Videos videos = videosMapper.selectOneByExample(example1);
            Example example2 = new Example(Users.class);
            example2.createCriteria().andEqualTo("id", videos.getUserId());
            Users user = usersMapper.selectOneByExample(example2);
            CommentsVO commentsVO = new CommentsVO();
            BeanUtils.copyProperties(comment,commentsVO);
            commentsVO.setNickname(user.getNickname());
            commentsVO.setFaceImage(user.getFaceImage());
            list.add(commentsVO);
        }

        /*
        将时间转为几天前那种格式
         */
        for (CommentsVO c : list) {
            String timeAgo = TimeAgoUtils.format(c.getCreateTime());
            c.setTimeAgoStr(timeAgo);
        }

        PageInfo<CommentsVO> pageList = new PageInfo<>(list);

        PagedResult grid = new PagedResult();
        grid.setTotal(pageList.getPages());
        grid.setRows(list);
        grid.setPage(page);
        grid.setRecords(pageList.getTotal());
        return grid;
    }


}
