package com.wrial.controller;

import com.wrial.utils.RedisOperator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class BasicController {
	
	@Autowired
	public RedisOperator redis;
	
	public static final String USER_REDIS_SESSION = "user-redis-session";
	
	// 文件保存的命名空间
	public static final String FILE_SPACE = "D:\\dev\\videos";
	
	// ffmpeg所在目录
	public static final String FFMPEG_EXE = "D:\\我的软件\\ffmpeg\\ffmpeg\\bin\\ffmpeg.exe";
	
	// 每页分页的记录数
	public static final Integer PAGE_SIZE = 5;
	
}
