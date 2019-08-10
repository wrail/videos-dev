package com.wrial.service.impl;
/*
 * @Author  Wrial
 * @Date Created in 12:54 2019/8/10
 * @Description BgmServiceImpl
 */

import com.wrial.mapper.BgmMapper;
import com.wrial.pojo.Bgm;
import com.wrial.service.BgmService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BgmServiceImpl implements BgmService {

    @Autowired
    private BgmMapper bgmMapper;

    @Override
    public List<Bgm> queryBgmList() {
        return bgmMapper.selectAll();
    }

    @Override
    public Bgm queryBgmById(String bgmId) {
        return null;
    }
}
