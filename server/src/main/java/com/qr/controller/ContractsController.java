package com.qr.controller;

import com.qr.service.TokenService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/contracts")
public class ContractsController {

    @RequestMapping("/signCode")
    public Map getSignCode(@RequestParam("contractId")String contractId){
        String token = TokenService.createToken("CONTRACT_SIGN_CODE", contractId);
        Map<String,Object> response = new HashMap<>();
        response.put("status",200);
        response.put("message","success");
        response.put("data",token);
        return response;
    }
}
