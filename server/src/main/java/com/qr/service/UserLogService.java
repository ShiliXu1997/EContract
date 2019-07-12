package com.qr.service;

import com.qr.entity.Log;

import java.util.List;

public interface UserLogService {
//    public int insert(long userId, String detials,String time);
    public List<Log> getLogByUserId(long id);
}
