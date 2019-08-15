package com.wrial.service.impl;

import com.wrial.mapper.UsersFansMapper;
import com.wrial.mapper.UsersLikeVideosMapper;
import com.wrial.mapper.UsersMapper;
import com.wrial.mapper.UsersReportMapper;
import com.wrial.pojo.Users;
import com.wrial.pojo.UsersFans;
import com.wrial.pojo.UsersLikeVideos;
import com.wrial.pojo.UsersReport;
import org.apache.commons.lang3.StringUtils;
import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import com.wrial.service.UserService;
import tk.mybatis.mapper.entity.Example;

import java.util.Date;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {


    @Autowired
    private UsersMapper usersMapper;

    @Autowired
    private UsersFansMapper usersFansMapper;
    //全局ID
    @Autowired
    private Sid sid;

    @Autowired
    private UsersLikeVideosMapper usersLikeVideosMapper;

    @Autowired
    private UsersReportMapper usersReportMapper;

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public boolean queryUsernameIsExist(String username) {
        Example example = new Example(Users.class);
        example.createCriteria().andEqualTo("username", username);
        Users users = usersMapper.selectOneByExample(example);
        if (users == null) {
            return false;
        } else {
            return true;
        }

    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED)
    public void saveUser(Users user) {

        String id = sid.nextShort();
        user.setId(id);
        usersMapper.insert(user);
    }

    @Override
    @Transactional(propagation = Propagation.SUPPORTS)
    public Users checkUser(String username, String password) {
        Example example = new Example(Users.class);
        example.createCriteria().andEqualTo("username", username).andEqualTo("password", password);
        Users users = usersMapper.selectOneByExample(example);
        return users;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void updateUserInfo(Users user) {

        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", user.getId());
        usersMapper.updateByExampleSelective(user, userExample);
    }

    @Override
    public Users queryUserInfo(String userId) {
        Example userExample = new Example(Users.class);
        Example.Criteria criteria = userExample.createCriteria();
        criteria.andEqualTo("id", userId);
        Users user = usersMapper.selectOneByExample(userExample);
        return user;

    }

    @Override
    public boolean isUserLikeVideo(String loginUserId, String videoId) {

        //如果是未登录用户就返回false，不用在数据库中查找
        if (StringUtils.isBlank(loginUserId) || StringUtils.isBlank(videoId)) {
            return false;
        }
        Example example = new Example(UsersLikeVideos.class);
        example.createCriteria().andEqualTo("userId", loginUserId)
                .andEqualTo("videoId", videoId);
        List<UsersLikeVideos> videos = usersLikeVideosMapper.selectByExample(example);
        if (videos.size() > 0) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public boolean queryIfFollow(String userId, String fanId) {
        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        List<UsersFans> list = usersFansMapper.selectByExample(example);

        if (list != null && !list.isEmpty() && list.size() > 0) {
            return true;
        }

        return false;
    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void saveUserFanRelation(String userId, String fanId) {

        String relId = sid.nextShort();

        UsersFans userFan = new UsersFans();
        userFan.setId(relId);
        userFan.setUserId(userId);
        userFan.setFanId(fanId);

        usersFansMapper.insert(userFan);

        usersMapper.addFansCount(userId);
        usersMapper.addFellersCount(fanId);

    }

    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void deleteUserFanRelation(String userId, String fanId) {

        Example example = new Example(UsersFans.class);
        Example.Criteria criteria = example.createCriteria();

        criteria.andEqualTo("userId", userId);
        criteria.andEqualTo("fanId", fanId);

        usersFansMapper.deleteByExample(example);

        usersMapper.reduceFansCount(userId);
        usersMapper.reduceFellersCount(fanId);

    }

    /*
    举报，插入一条数据
     */
    @Transactional(propagation = Propagation.REQUIRED)
    @Override
    public void reportUser(UsersReport userReport) {

        String urId = sid.nextShort();
        userReport.setId(urId);
        userReport.setCreateDate(new Date());
        usersReportMapper.insert(userReport);
    }

}
