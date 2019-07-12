package com.qr.service;


import com.qr.entity.Contract;
import com.qr.model.signCode;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

@Service
public class SignCodeStatusService {
    @Resource
    private UserContractService userContractService;

    private static Map<String, signCode> signCodeMap = new HashMap<>();

    public void addSignCode(String contractId,String hash,String uid1,String uid2){
        long l = 10*60*1000;
        Contract contract =userContractService.getContractByContractId(contractId);
        boolean uid1sign = false;
        boolean uid2sign = false;
        if (contract.getSign1()==1)
            uid1sign = true;
        if (contract.getSign2()==1)
            uid2sign = true;
        signCode code = new signCode(hash, l, uid1, uid2,uid1sign,uid2sign);
        signCodeMap.put(contractId, code);
    }

    public boolean isExists(String contractsId){
        if(signCodeMap.get(contractsId) == null)
            return false;

        return true;
    }

    public static void removeTimeOutSignCode(){
        for(String key:signCodeMap.keySet()){
            signCode code = signCodeMap.get(key);
            if(code.isTimeOut()){
                signCodeMap.remove(key);
                System.out.println("清除超时合同二维码:"+key);
            }
        }
    }

    public boolean userSign(String uid,String contractId){
        signCode code = signCodeMap.get(contractId);
        if(code == null){
            System.out.println("没有这个合同id:"+contractId);
            return false;
        }
        System.out.println("合同id存在");
        return code.sign(uid);
    }
    public String getContractId(String hash){
        for(String id:signCodeMap.keySet()){
            if(signCodeMap.get(id).getHash().compareTo(hash) == 0)
                return id;
        }

        return null;
    }

    public boolean signBoth(String contractId){
        signCode code = signCodeMap.get(contractId);
        if(code == null)
            return false;

        signCode.SCSTATUS status = code.signCodeStatus();
        if(status == signCode.SCSTATUS.ASIGN)
            return true;

        return false;
    }
}
