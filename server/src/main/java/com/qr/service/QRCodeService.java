package com.qr.service;

import com.qr.model.codeStatus;
import com.qr.model.qrcode;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class QRCodeService {
    //二维码记录
    private static Map<String, qrcode> qrm = new HashMap<>();
    private static Map<String,String> tokenCastUid = new HashMap<>();
    private int randStrLength = 20;

    public String getAuthorizationCode(){
        String randStr = RandomStringUtils.randomAlphabetic(randStrLength);
        //记录随机字符串
        addCode(randStr);
        return randStr;
    }

    public boolean isCodeExist(String code){
        codeStatus cs = getStatus(code);
        if (cs == codeStatus.FAKE)
            return false;

        return true;
    }

    public boolean scanCode(String code,String uid){
        qrcode qc = qrm.get(code);
        if(qc != null){
            qc.setUID(uid);
            qc.setScanTimeStamp();
            return true;
        }
        return false;
    }


    private void addCode(String code){
        long timeStamp = new Date().getTime();
        System.out.println(timeStamp);
        qrcode qc = new qrcode(timeStamp);
        qrm.put(code,qc);
    }

    public codeStatus getStatus(String code){
        qrcode qc = qrm.get(code);
        if(qc == null)
            return codeStatus.FAKE;
        if(qc.isScan())
            return codeStatus.SUCCESS;
        else if (qc.isTimeOut())
            return codeStatus.TIMEOUT;
        else
            return codeStatus.FRESH;
    }

    public String getUid(String code){
        qrcode qc = qrm.get(code);
        return qc.getUid();
    }

    public ArrayList<String> findTimeOutCode(){
            ArrayList<String> timeOutCodeList = new ArrayList<>(); // 记录超时的键
            for (String key : qrm.keySet()) {
                qrcode qc = qrm.get(key);
                if (qc.isTimeOut())
                    timeOutCodeList.add(key);
            }

            return timeOutCodeList;
    }

    public void removeCode(String code){
        qrm.remove(code);
    }
}
