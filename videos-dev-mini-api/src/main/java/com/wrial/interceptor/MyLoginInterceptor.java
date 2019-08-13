package com.wrial.interceptor;
/*
 * @Author  Wrial
 * @Date Created in 11:52 2019/8/13
 * @Description 登录拦截器
 */

import com.wrial.utils.JsonUtils;
import com.wrial.utils.MyJSONResult;
import com.wrial.utils.RedisOperator;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;

import static com.wrial.controller.BasicController.USER_REDIS_SESSION;


public class MyLoginInterceptor extends HandlerInterceptorAdapter {


    @Autowired
    private RedisOperator redisOperator;

    /*
    执行登录拦截 通过在前端设置的请求头中的userId 和 userToken来进行判断
    1. 如果userId 和 userToken都是null 那就是未登录
    2. 如果userId 不为null，userToken 为在redis中查不到（说明被覆盖了），也就是说在另外一个地方登录了
    3. 如果都正常，说明登录成功
     */
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String userId = request.getHeader("headerUserId");
        String userToken = request.getHeader("headerUserToken");
        if (StringUtils.isNotBlank(userId) && StringUtils.isNotBlank(userToken)) {
            String uniqueToken = redisOperator.get(USER_REDIS_SESSION + ":" + userId);
            if (StringUtils.isEmpty(uniqueToken) && StringUtils.isBlank(uniqueToken)) {
                System.out.println("请登录...");
                returnErrorResponse(response, new MyJSONResult().errorTokenMsg("请登录..."));
                return false;
            } else {
                if (!uniqueToken.equals(userToken)) {
                    System.out.println("账号在别处登录...");
                    returnErrorResponse(response, new MyJSONResult().errorTokenMsg("账号在别处登录..."));
                    return false;
                }
            }
        } else {
            System.out.println("请登录...");
            returnErrorResponse(response, new MyJSONResult().errorTokenMsg("请登录..."));
            return false;
        }


        /**
         * 返回 false：请求被拦截，返回
         * 返回 true ：请求OK，可以继续执行，放行
         */
        return true;
    }

    public void returnErrorResponse(HttpServletResponse response, MyJSONResult result)
            throws IOException, UnsupportedEncodingException {
        OutputStream out = null;
        try {
            response.setCharacterEncoding("utf-8");
            response.setContentType("text/json");
            out = response.getOutputStream();
            out.write(JsonUtils.objectToJson(result).getBytes("utf-8"));
            out.flush();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

}
