package com.qr.controller;

import javax.annotation.Resource;
import javax.websocket.server.PathParam;

import com.qr.entity.*;
import com.qr.entity.Contract;
import com.qr.service.*;
import org.apache.commons.codec.binary.Base64;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.security.MessageDigest;
import java.util.*;

@RestController
@RequestMapping("/api")
public class ContractController {
    @Resource
    private SignCodeStatusService signCodeSatausService;
    @Resource
    private  UserContractService userContractService;
    @Resource
    private UserService userService;
    @Resource
    private UserLogService userLogService;
    @Resource
    private LogService logService;
//    @Resource
//    private LogService logService;

    String filePath = "/home/yin/contracts/";

    @RequestMapping("/contracts")
    public Map getContracts(@PathParam("token") String token){
        Map<String,Object> map = new HashMap<String, Object>();
        String userIdS = TokenService.parseToken(token);
        if (userIdS==null){
            map.put("status", 400);
            map.put("message", "请求错误或Token过期");
        }else {
            System.out.println(userIdS);
            long userId = (long) Long.parseLong(userIdS);
            logService.log(userId,"查看所有用户合同");
            System.out.println(userId);
            List<Contract> contracts = new ArrayList<>();

            contracts.addAll(userContractService.getContractByUserId1(userId));
            contracts.addAll(userContractService.getContractByUserId2(userId));

            map.put("status", 200);
            map.put("message", "success");
            List<Map> list=new ArrayList<>();
            System.out.println("contracts.size:"+contracts.size());
            for (int i=0;i<contracts.size();i++){
                Map<String,Object> map1=new HashMap<>();
                Map<String,Object> map2=new HashMap<>();
                Contract contract=contracts.get(i);
                int currentUser=0;//当前用户 0表示甲方 1表示乙方
                System.out.println(contracts.get(i).getId());
                UserContract userContract=userContractService.getUserContract(contracts.get(i).getId());
                User userA=userService.getUserById(userContract.getUserId1());
                User userB=userService.getUserById(userContract.getUserId2());
                if (userContract.getUserId2()==userId)
                    currentUser=1;
                map1.put("id",contract.getId());
                map1.put("type",contract.retnType(currentUser));
                map1.put("title",contract.getTitle());
                String strArray[]=contract.getPath().split("\\.");
                String strArray1[]=strArray[0].split("\\/");
                map2.put("filename",strArray1[strArray1.length-2]);
                map2.put("link",getFileLink(contract.getPath()) );
                map2.put("size",contract.getFileSize());
                map1.put("data",map2);
                map1.put("lastModified",contract.getLastModified());
                map1.put("partAName",userA.getName());
                map1.put("partAIDCard",userA.getIdentity());
                map1.put("partBName",userB.getName());
                map1.put("partBIDCard",userB.getIdentity());
                list.add(map1);
            }
            map.put("data",list);
        }
        return map;
    }

    @RequestMapping(value = "/contracts/{contractId}",method = RequestMethod.GET)
    public Map getContractById(@PathVariable("contractId")String contractId,@PathParam("token")String token){
        Map<String,Object> map = new HashMap<String, Object>();
        String userIdS = TokenService.parseToken(token);
        if (userIdS==null){
            map.put("status", 400);
            map.put("message", "请求错误或Token过期");
        }else {
            long userId = (long) Long.parseLong(userIdS);
            logService.log(userId,"查看Id为"+contractId+"的合同");
            System.out.println(userId);
            UserContract userContract = userContractService.getUserContract(contractId);
            if (userContract.getUserId1() == userId || userContract.getUserId2() == userId) {
                Contract contract = userContractService.getContractByContractId(contractId);
                User userA=userService.getUserById(userContract.getUserId1());
                User userB=userService.getUserById(userContract.getUserId2());
                int currentUser=0;
                map.put("status", 200);
                map.put("message", "success");
                Map<String,Object> map1=new HashMap<>();
                Map<String,Object> map2=new HashMap<>();
                if (userContract.getUserId2()==userId)
                    currentUser=1;
                map1.put("id",contract.getId());
                map1.put("type",contract.retnType(currentUser));
                map1.put("title",contract.getTitle());
                String strArray[]=contract.getPath().split("\\.");
                String strArray1[]=strArray[0].split("\\/");
                map2.put("filename",strArray1[strArray1.length-2]);
                map2.put("link",getFileLink(contract.getPath()));
                map2.put("size",contract.getFileSize());
                map1.put("data",map2);
                map1.put("lastModified",contract.getLastModified());
                map.put("data", map1);
                map1.put("partAName",userA.getName());
                map1.put("partAIDCard",userA.getIdentity());
                map1.put("partBName",userB.getName());
                map1.put("partBIDCard",userB.getIdentity());
            } else {
                map.put("status", 400);
                map.put("message", "您无权访问该合同");
            }
        }
        return map;
    }

    @RequestMapping(value = "/contracts/create",method = RequestMethod.POST)
    public Map createContract(@PathParam("token")String token, @RequestParam("file") MultipartFile file
            ,@RequestParam("title")String title,@RequestParam("partBName")String partBName,@RequestParam("partBIDCard")String partBIDCard){
        String path;
        String fileName;
        Map<String,Object> map = new HashMap<String, Object>();
        String userIdS = TokenService.parseToken(token);
        if (userIdS==null){
            map.put("status", 400);
            map.put("message", "请求错误或Token过期");
        }else {
            long userAID = (long) Long.parseLong(userIdS);
            logService.log(userAID,"创建合同");
            try {
                if (file.isEmpty()) {
                    map.put("status", 400);
                    map.put("message", "文件为空");
                    return map;
                }
                // 获取文件名
                fileName = file.getOriginalFilename();
                //合同序列号
                UUID fileId = UUID.randomUUID();
                System.out.println(fileId);
                // 获取文件的后缀名
                String suffixName = fileName.substring(fileName.lastIndexOf("."));
                //后缀名
                System.out.println(suffixName);
                // 设置文件存储路径
                path = filePath + fileId + suffixName;
                //获取文件大小
                long fileSize = file.getSize();
                System.out.println(path);
                File dest = new File(path);
                // 检测是否存在目录
                if (!dest.getParentFile().exists()) {
                    dest.getParentFile().mkdirs();// 新建文件夹
                }
                file.transferTo(dest);// 文件写入
                long userBID = userService.getUserIdBydentity(partBIDCard);
//                long userAID = 111L;//(long) claims.get("userId");
                long timeStamp = System.currentTimeMillis();
                if (userContractService.insert(userAID, userBID, 1, title, path, fileSize, 0, 0, 0, timeStamp) == 1) {
                    map.put("status", 200);
                    map.put("message", "success");
                } else {
                    map.put("status", 400);
                    map.put("message", "上传失败");
                }
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return map;
    }

    @RequestMapping(value = "/contracts/{contractId}/update",method = RequestMethod.POST)
    public Map modifyContract(@PathVariable("contractId")String contractId,@PathParam("token")String token,@RequestParam("file")MultipartFile file){
        String path;
        Map<String,Object> map=new HashMap<>();
        String userIdS = TokenService.parseToken(token);
        if (userIdS==null){
            map.put("status", 400);
            map.put("message", "请求错误或Token过期");
        }else {
            long userId = (long) Long.parseLong(userIdS);
            logService.log(userId,"更新id为"+contractId+"的合同");
            Contract contract = userContractService.getContractByContractId(contractId);
            path = contract.getPath();
            File dest = new File(path);
            try {
                file.transferTo(dest);// 文件写入
                map.put("status", 200);
                map.put("message", "success");
            } catch (IllegalStateException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            UserContract userContract = userContractService.getUserContract(contractId);
            if (userContract.getUserId1() == userId) {
                userContractService.agreeContract(contractId, 0);
                userContractService.updateType(contractId,3);
            }
            if (userContract.getUserId2() == userId) {
                userContractService.agreeContract(contractId, 1);
                userContractService.updateType(contractId,2);
            }
            long timeStamp = System.currentTimeMillis();
            userContractService.updateTimeStamp(contractId, timeStamp);
        }
        return map;
    }

    @RequestMapping(value = "/contracts/{contractId}/accept",method = RequestMethod.POST)
    public Map agreeContract(@PathVariable("contractId")String contractId,@PathParam("token")String token){
        Map<String,Object> map=new HashMap<>();
        String userIdS = TokenService.parseToken(token);
        if (userIdS==null){
            map.put("status", 400);
            map.put("message", "请求错误或Token过期");
        }else {
            long userId = (long) Long.parseLong(userIdS);
            logService.log(userId,"同意id为"+contractId+"的合同");
//            int currentUser;//当前用户 0表示甲 1表示乙
            UserContract userContract = userContractService.getUserContract(contractId);
            Contract contract = userContractService.getContractByContractId(contractId);
            if (userContract.getUserId1() == userId) {
                System.out.println("accept"+userId);
                if (contract.getAgree() == 1||contract.getAgree() == 0)
                    if(userContractService.agreeContract(contractId, 2)==1){
                        map.put("status", 200);
                        map.put("message", "success");
                    }
            }
            else if (userContract.getUserId2() == userId) {
                if (contract.getAgree() == 0)
                    if(userContractService.agreeContract(contractId, 2)==1){
                        map.put("status", 200);
                        map.put("message", "success");
                    }
            }else {
                map.put("status", 400);
                map.put("message", "fail");
            }
        }
        return map;
    }

    @RequestMapping(value = "/contracts/{contractId}/decline",method = RequestMethod.POST)
    public Map declineContract(@PathVariable("contractId")String contractId,@PathParam("token") String token){
        Map<String,Object> map=new HashMap<>();
        String userIdS = TokenService.parseToken(token);
        if (userIdS==null){
            map.put("status", 400);
            map.put("message", "请求错误或Token过期");
        }else {
            long userId = (long) Long.parseLong(userIdS);
            logService.log(userId,"拒绝id为"+contractId+"的合同");
            UserContract userContract = userContractService.getUserContract(contractId);
            Contract contract = userContractService.getContractByContractId(contractId);
            if (userContract.getUserId1() == userId) {
                if (contract.getAgree() == 1)
                    userContractService.deleteContract(contractId);
                if(contract.getAgree()==2)
                    userContractService.agreeContract(contractId,1);
            }
            if (userContract.getUserId2() == userId) {
                if (contract.getAgree() == 0)
                    userContractService.deleteContract(contractId);
                if(contract.getAgree()==2)
                    userContractService.agreeContract(contractId,0);
            }
            map.put("status", 200);
            map.put("message", "success");
        }
        return map;
    }

    @RequestMapping(value = "/logs",method = RequestMethod.GET)
    public Map getAllLogs(@PathParam("token") String token){
        Map<String,Object> map=new HashMap<>();
        System.out.println("logs"+token);
        String userIdS = TokenService.parseToken(token);
        if (userIdS==null){
            map.put("status", 400);
            map.put("message", "请求错误或Token过期");
        }else {
            long userId = (long) Long.parseLong(userIdS);
            List<Log> logs = userLogService.getLogByUserId(userId);
            map.put("status", 200);
            map.put("message", "success");
            map.put("data", logs);
        }
        return map;
    }

    public String getFileLink(String filePath){
        String a[] = filePath.split("/");
        String link = "47.95.214.69:1002/allContract/"+a[a.length-1];
        return link;
    }


    @RequestMapping(value = "/contracts/{contractId}/signCode")
    @ResponseBody
    public Map getContractsHash(@PathVariable("contractId") String contractId,@PathParam("token")String token){
        //根据conractId获取合同文件
        String path = userContractService.getContractByContractId(contractId).getPath();
        UserContract userContract=userContractService.getUserContract(contractId);
        //甲方
        String uid1 = String.valueOf(userContract.getUserId1());
        //乙方
        String uid2 = String.valueOf(userContract.getUserId2());
        File file = new File(path);
        String fileStr = "";
        String fileHash = "";
        Map<String,Object> response = new HashMap<>();
        Reader reader = null;
        BufferedReader bufferedReader = null;
        try {
            reader = new FileReader(file);
            bufferedReader = new BufferedReader(reader);
            String line = bufferedReader.readLine();
            while(line != null){
                fileStr += line;
                line = bufferedReader.readLine();
            }

            //做hash  padding填充
            MessageDigest md = MessageDigest.getInstance("SHA1");
            md.update(fileStr.getBytes());
            StringBuffer buf = new StringBuffer();
            byte[] bits = md.digest();

//            BASE64Encoder base64 = new BASE64Encoder();
            fileHash = Base64.encodeBase64String(bits);

            bufferedReader.close();
            reader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        if(fileStr.compareTo("") == 0 || fileHash.compareTo("") == 0){
            response.put("status", 400);
            response.put("message", "failed");
            response.put("data", "notiong");
        }
        else{
            signCodeSatausService.addSignCode(contractId,fileHash,uid1,uid2);
            response.put("status", 200);
            response.put("message", "success");
            response.put("data", "CONTRACTSCODE"+fileHash);
        }

        return response;
    }

}