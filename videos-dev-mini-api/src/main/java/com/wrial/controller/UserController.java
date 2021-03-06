package com.wrial.controller;

import com.wrial.pojo.Users;
import com.wrial.pojo.UsersReport;
import com.wrial.pojo.vo.PublisherVideo;
import com.wrial.pojo.vo.UsersVO;
import com.wrial.service.UserService;
import com.wrial.utils.MyJSONResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

@RestController
@Api(value = "用户相关业务接口", tags = {"用户相关业务controller"})
@RequestMapping("/user")
public class UserController extends BasicController {

    @Autowired
    private UserService userService;


    @ApiOperation(value = "用户上传头像", notes = "用户上传头像的接口")
    @ApiImplicitParam(name = "userId", value = "用户id", required = true, dataType = "String")
    @PostMapping("/uploadFace")
    public MyJSONResult uploadFace(String userId, @RequestParam("file") MultipartFile[] files) throws IOException {


        if (StringUtils.isBlank(userId)) {
            return MyJSONResult.errorMsg("用户id不能为空...");
        }

        // 文件保存的命名空间
        String fileRoot = "d:/dev/videos";
        // 保存到数据库中的相对路径
        String uploadPathDB = "/" + userId + "/face";

        FileOutputStream fileOutputStream = null;
        InputStream inputStream = null;
        try {
            if (files != null && files.length > 0) {

                String fileName = files[0].getOriginalFilename();
                if (StringUtils.isNotBlank(fileName)) {
                    // 文件上传的最终保存路径
                    String finalFacePath = fileRoot + uploadPathDB + "/" + fileName;
                    // 设置数据库保存的路径
                    uploadPathDB += ("/" + fileName);

                    File outFile = new File(finalFacePath);
                    if (outFile.getParentFile() != null || !outFile.getParentFile().isDirectory()) {
                        // 创建父文件夹
                        outFile.getParentFile().mkdirs();
                    }

                    fileOutputStream = new FileOutputStream(outFile);
                    inputStream = files[0].getInputStream();
                    IOUtils.copy(inputStream, fileOutputStream);
                }

            } else {
                return MyJSONResult.errorMsg("上传出错...");
            }
        } catch (Exception e) {
            e.printStackTrace();
            return MyJSONResult.errorMsg("上传出错...");
        } finally {
            if (fileOutputStream != null) {
                fileOutputStream.flush();
                fileOutputStream.close();
            }
        }

        Users user = new Users();
        user.setId(userId);
        user.setFaceImage(uploadPathDB);
        userService.updateUserInfo(user);

        return MyJSONResult.ok(uploadPathDB);

    }

    @ApiOperation(value="查询用户信息", notes="查询用户信息的接口")
    @ApiImplicitParam(name="userId", value="用户id", required=true,
            dataType="String", paramType="query")
    @GetMapping("/query")
    public MyJSONResult query(@RequestParam("userId") String userId,
                              @RequestParam("fanId") String fanId) throws Exception {

        if (StringUtils.isBlank(userId)) {
            return MyJSONResult.errorMsg("用户id不能为空...");
        }

        Users userInfo = userService.queryUserInfo(userId);
        UsersVO userVO = new UsersVO();
        BeanUtils.copyProperties(userInfo, userVO);

        userVO.setFollow(userService.queryIfFollow(userId, fanId));

        return MyJSONResult.ok(userVO);
    }
    @ApiOperation(value = "查询当前用户和发布者关系和发布者信息",notes = "得到当前用户和发布者关系和发布者信息")
    @GetMapping("/queryPublisher")
    public MyJSONResult queryPublisher(String loginUserId, String videoId,
                                       String publishUserId) throws Exception {

        if (StringUtils.isBlank(publishUserId)) {
            return MyJSONResult.errorMsg("");
        }

        // 1. 查询视频发布者的信息
        Users userInfo = userService.queryUserInfo(publishUserId);
        UsersVO publisher = new UsersVO();
        BeanUtils.copyProperties(userInfo, publisher);

        // 2. 查询当前登录者和视频的点赞关系
        boolean userLikeVideo = userService.isUserLikeVideo(loginUserId, videoId);

        PublisherVideo bean = new PublisherVideo();
        bean.setPublisher(publisher);
        bean.setUserLikeVideo(userLikeVideo);

        return MyJSONResult.ok(bean);
    }

    @ApiOperation(value = "关注",notes = "关注接口")
    @PostMapping("/beyourfans")
    public MyJSONResult beyourfans(String userId, String fanId) throws Exception {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return MyJSONResult.errorMsg("");
        }

        userService.saveUserFanRelation(userId, fanId);

        return MyJSONResult.ok("关注成功...");
    }

    @ApiOperation(value = "取消关注",notes = "取消关注接口")
    @PostMapping("/dontbeyourfans")
    public MyJSONResult dontbeyourfans(String userId, String fanId) throws Exception {

        if (StringUtils.isBlank(userId) || StringUtils.isBlank(fanId)) {
            return MyJSONResult.errorMsg("");
        }

        userService.deleteUserFanRelation(userId, fanId);

        return MyJSONResult.ok("取消关注成功...");
    }

    @ApiOperation(value = "举报",notes = "举报接口")
    @PostMapping("/reportUser")
    public MyJSONResult reportUser(@RequestBody UsersReport usersReport) {

        // 保存举报信息
        userService.reportUser(usersReport);

        return MyJSONResult.ok("举报成功！");
    }

}
