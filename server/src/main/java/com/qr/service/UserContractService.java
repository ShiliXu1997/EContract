package com.qr.service;

import com.qr.entity.UserContract;
import com.qr.entity.Contract;

import java.util.List;

public interface UserContractService {
    public int insert(long userId1, long userId2,int status, String title, String path,long fileSize, int sign1, int sign2,int agree,long lastModified);
    public List<Contract> getContractByUserId1(long userId1);
    public List<Contract> getContractByUserId2(long userId2);
    public Contract getContractByContractId(String id);
    public boolean signContract(String contractId,long userId);
    public UserContract getUserContract(String contractId);
    public int agreeContract(String contractId,int agreeNum);
    public int deleteContract(String id);
    public int updateTimeStamp(String id,long timeStamp);
    public int updateType(String contractId,int type);
}
