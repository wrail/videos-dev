package com.wrial.controller;
/*
 * @Author  Wrial
 * @Date Created in 10:51 2019/8/15
 * @Description 评论相关
 */

import com.wrial.pojo.Comments;
import com.wrial.service.VideoService;
import com.wrial.utils.MyJSONResult;
import com.wrial.utils.PagedResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/comment")
@Api(value = "评论相关接口", tags = {"评论相关controller"})
public class CommentController {


    @Autowired
    private VideoService videoService;

    @ApiOperation(value = "保存评论", notes = "写评论的接口")
    @PostMapping("/saveComment")
    public MyJSONResult saveComment(@RequestBody Comments comment,
                                    String fatherCommentId, String toUserId) throws Exception {

        comment.setFatherCommentId(fatherCommentId);
        comment.setToUserId(toUserId);

        videoService.saveComment(comment);
        return MyJSONResult.ok();
    }


    @ApiOperation(value = "展示评论", notes = "分页获取评论接口")
    @GetMapping("/getVideoComments")
    public MyJSONResult getVideoComments(@RequestParam("videoId") String videoId,
                                         @RequestParam(value = "page",defaultValue = "1") Integer page,
                                         @RequestParam(value = "pageSize",defaultValue = "10") Integer pageSize) throws Exception {

        if (StringUtils.isBlank(videoId)) {
            return MyJSONResult.ok();
        }

        PagedResult list = videoService.getAllComments(videoId, page, pageSize);

        return MyJSONResult.ok(list);
    }

}
