package com.qr.controller;

import com.qr.service.QRCodeService;
import com.qr.service.QRCodeStatusService;
import com.qr.service.ServerKeyService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/api/auth")
public class QrCodeController {

    @Autowired
    private QRCodeService qrCodeService;
    @Autowired
    private ServerKeyService serverKeyService;

    private QRCodeStatusService qrCodeStatusService = new QRCodeStatusService();

    @RequestMapping("code")
    @ResponseBody
    public Map getRandStr(){
        //获得随机字符串并形成签名
        String randStr = qrCodeService.getAuthorizationCode();
        //String sign = serverKeyService.signatureMess(randStr);
        System.out.println("--------------随机字符串;"+randStr);
        //System.out.println("--------------随机字符串签名值："+sign);

        Map<String,Object> map = new HashMap<>(); //返回
        //Map<String,String> data = new HashMap<>();//数据
        map.put("status",200);
        map.put("message","OK");
        //data.put("str",randStr);
        //data.put("signature",sign);
        map.put("data",randStr);
        return map;
    }

    @RequestMapping("scanCode")
    @ResponseBody
    public String scan(@RequestParam("code")String code,@RequestParam("uid")String uid){
        qrCodeService.scanCode(code, uid);
        qrCodeStatusService.sendMessage(code);
        return "scanCode";
    }

}
