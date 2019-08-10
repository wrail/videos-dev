package com.wrial.service;

import com.wrial.pojo.Users;
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

    Users queryUserInfo(String userId);

    boolean queryIfFollow(String userId, String fanId);
}
