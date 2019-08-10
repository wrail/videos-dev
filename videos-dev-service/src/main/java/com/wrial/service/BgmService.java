package com.wrial.service;
/*
 * @Author  Wrial
 * @Date Created in 12:52 2019/8/10
 * @Description BgmService
 */

import com.wrial.pojo.Bgm;

import java.util.List;

public interface BgmService {

    /**
     * @Description: 查询背景音乐列表
     */
    public List<Bgm> queryBgmList();

    /**
     * @Description: 根据id查询bgm信息
     */
    public Bgm queryBgmById(String bgmId);
}
