package com.qr.service.impl;

import com.qr.dao.ContractDao;
import com.qr.dao.UserContractDao;
import com.qr.entity.UserContract;
import com.qr.entity.Contract;
import com.qr.service.UserContractService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@Transactional(rollbackFor = Exception.class)
public class UserContractServiceImpl implements UserContractService {
    @Resource
    private UserContractDao userContractDao;
    @Resource
    private ContractDao contractDao;
    @Override
    public int insert(long userId1, long userId2,int status, String title, String path, long fileSize, int sign1, int sign2,int agree,long lastModified) {
        if (userId1==userId2) return 0;
        String contractId =UUID.randomUUID().toString();
        System.out.println("create contract:"+contractId);
        if(userContractDao.insert(userId1,userId2,contractId)==1 &&
                contractDao.insert(contractId,status,title,path,fileSize,sign1,sign2,agree,lastModified)==1)
            return 1;
        return 0;
    }

    @Override
    public List<Contract> getContractByUserId1(long userId1) {
        List<String> contractsId = userContractDao.getConstractIdByUserId1(userId1);
        List<Contract> contracts=new ArrayList<>();
        for(int i=0;i<contractsId.size();i++){
            System.out.println(contractsId.get(i));
            Contract contract= contractDao.getContractByContractId(contractsId.get(i));
            contracts.add(contract);
        }
        return contracts;
    }

    @Override
    public List<Contract> getContractByUserId2(long userId2) {
        List<String> contractsId = userContractDao.getContractIdByUserId2(userId2);
        List<Contract> contracts=new ArrayList<>();
        for(int i=0;i<contractsId.size();i++){
            Contract contract= contractDao.getContractByContractId(contractsId.get(i));
            contracts.add(contract);
        }
        return contracts;
    }

    @Override
    public Contract getContractByContractId(String id) {
        return contractDao.getContractByContractId(id);
    }

    @Override
    public boolean signContract(String contractId, long userId) {
        UserContract userContract = userContractDao.getUserContractById(contractId);
        if (userId==userContract.getUserId1()){
            contractDao.updateSign1ById(contractId);
        }else if (userId==userContract.getUserId2()){
            contractDao.updateSign2ById(contractId);
        }else
            return false;
        return true;
    }

    @Override
    public UserContract getUserContract(String contractId) {
        return userContractDao.getUserContractById(contractId);
    }

    @Override
    public int agreeContract(String contractId, int agreeNum) {
        return contractDao.updateAgree(contractId,agreeNum);
    }

    @Override
    public int deleteContract(String id) {
        if (userContractDao.deleteContractByContractId(id)==1&&contractDao.delectContractById(id)==1)
            return 1;
        return 0;
    }

    @Override
    public int updateTimeStamp(String id, long timeStamp) {
        return contractDao.updateTimeStampById(id,timeStamp);
    }

    @Override
    public int updateType(String contractId,int type) {
        return contractDao.updateStatus(contractId,type);
    }


}
