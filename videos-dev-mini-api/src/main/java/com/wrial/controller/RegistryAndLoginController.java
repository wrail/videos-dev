package com.wrial.controller;

import com.wrial.pojo.Users;
import com.wrial.pojo.vo.UsersVO;
import com.wrial.utils.MyJSONResult;
import com.wrial.utils.MD5Utils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import com.wrial.service.UserService;

import java.util.UUID;


@RestController
@Api(value = "用户登录注册接口",tags = {"注册登录的controller"})
public class RegistryAndLoginController extends BasicController {


    @Autowired
    private UserService userService;


    @ApiOperation(value = "用户注册",notes = "用户注册接口")
    @PostMapping("/regist")
    public MyJSONResult registry(@RequestBody Users user) throws Exception {
        // 1. 判断用户名和密码必须不为空
        if (StringUtils.isBlank(user.getUsername()) || StringUtils.isBlank(user.getPassword())) {
            return MyJSONResult.errorMsg("用户名和密码不能为空");
        }

        // 2. 判断用户名是否存在
        boolean usernameIsExist = userService.queryUsernameIsExist(user.getUsername());

        // 3. 保存用户，注册信息
        if (!usernameIsExist) {
            user.setNickname(user.getUsername());
            user.setPassword(MD5Utils.getMD5Str(user.getPassword()));
            user.setFansCounts(0);
            user.setReceiveLikeCounts(0);
            user.setFollowCounts(0);
            userService.saveUser(user);
        } else {
            return MyJSONResult.errorMsg("用户名已经存在，请换一个再试");
        }

        //为了安全，设置为密码为null
        user.setPassword("");
        UsersVO userVO = setUserRedisSessionToken(user);
        return MyJSONResult.ok(userVO);
    }

    /*
    提取出的方法
     */

    public UsersVO setUserRedisSessionToken(Users user) {
        String uniqueToken = UUID.randomUUID().toString();
        String key = USER_REDIS_SESSION + ":" + user.getId();
        redis.set(key, uniqueToken, 1000 * 60 * 30);

        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(user, userVO);
        userVO.setUserToken(uniqueToken);
        return userVO;
    }


    @ApiOperation(value = "用户登录",notes = "用户登录接口")
    @PostMapping("/login")
    public MyJSONResult login(@RequestBody Users user) throws Exception {

        String username = user.getUsername();
        String password = user.getPassword();

        if (StringUtils.isBlank(username) || StringUtils.isBlank(password)){
            return MyJSONResult.errorMsg("用户名或者密码不能为空！");
        }

        boolean usernameIsExist = userService.queryUsernameIsExist(username);
        if (usernameIsExist){

            Users checkUser = userService.checkUser(username, MD5Utils.getMD5Str(password));

            if (checkUser!=null){
                checkUser.setPassword("");
                UsersVO userVO = setUserRedisSessionToken(checkUser);
                return MyJSONResult.ok(userVO);
            }else {
                return MyJSONResult.errorMsg("密码错误，请重试！");
            }


        }else {
            return MyJSONResult.errorMsg("此用户未注册！请先注册");
        }

    }
    @ApiOperation(value="用户注销", notes="用户注销接口")
    @ApiImplicitParam(name="userId", value="用户id", required=true,
            dataType="String", paramType="query")
    @DeleteMapping("/logout")
    public MyJSONResult logout(@RequestParam("userId") String userId) throws Exception {
        redis.del(USER_REDIS_SESSION + ":" + userId);
        return MyJSONResult.ok();
    }

}
