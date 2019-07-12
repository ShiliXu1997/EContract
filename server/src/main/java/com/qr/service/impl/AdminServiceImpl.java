package com.qr.service.impl;

import com.qr.dao.AdminDao;
import com.qr.entity.Administrator;
import com.qr.service.AdminService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdminServiceImpl implements AdminService {
    @Resource
    private AdminDao adminDao;
    @Override
    public Administrator getAdmin() {
        return adminDao.getAdmin();
    }
}
