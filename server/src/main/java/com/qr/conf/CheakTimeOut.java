package com.qr.conf;

import com.qr.service.QRCodeService;
import com.qr.service.QRCodeStatusService;
import com.qr.service.SignCodeStatusService;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;

@Component
public class CheakTimeOut {

    @Resource
    private QRCodeService qrCodeService;
    @Resource
    private SignCodeStatusService statusService;


    @Scheduled(fixedRate=3*60*1000)
    public void removeTimeOutQRCode() {
        System.out.println("--------------------删除超时登陆二维码------------------");
        ArrayList<String> timeOutCodeList = qrCodeService.findTimeOutCode();
        int length = timeOutCodeList.size();
        if(length == 0)
            System.out.println("没有超时的二维码");
        else
            System.out.println("有超时的二维码，开始清理...");


        for(int i = 0;i<length;i++){
            String code = timeOutCodeList.get(i);
            System.out.println("超时的二维码："+ code);
            //qrCodeStatusService.sendMessage(code);
            qrCodeService.removeCode(code);
        }

        //检查超时的合同状态连接
        System.out.println("-----------——删除超时合同二维码---------------------------");
        //statusService.removeTimeOutSignCode();
        SignCodeStatusService.removeTimeOutSignCode();
    }
}
