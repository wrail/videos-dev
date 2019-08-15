package com.wrial.service;

import com.wrial.pojo.Users;
import com.wrial.pojo.UsersReport;
import org.springframework.stereotype.Service;

@Service
public interface UserService {
    /**
     * @Description: 判断用户名是否存在
     */
    public boolean queryUsernameIsExist(String username);
    /**
     * @Description: 保存用户(用户注册)
     */
    public void saveUser(Users user);

    /*
     检查数据库有没有此用户（用户登录）
     */
    Users checkUser(String username, String password);

    /*
    修改信息
     */
    void updateUserInfo(Users user);

    /*
    查询用户信息
     */
    Users queryUserInfo(String userId);

    /*
    查询是否点过赞
     */
    boolean isUserLikeVideo(String loginUserId, String videoId);

    /*
    查询是否关注
     */
    boolean queryIfFollow(String userId, String fanId);

    /**
     * @Description: 增加用户和粉丝的关系
     */
    public void saveUserFanRelation(String userId, String fanId);

    /**
     * @Description: 删除用户和粉丝的关系
     */
    public void deleteUserFanRelation(String userId, String fanId);

    /**
     * @Description: 举报用户
     */
    void reportUser(UsersReport usersReport);
}
