package com.qr.model;

import java.util.Date;

public class signCode {
    public enum  SCSTATUS{PASIGN,PBSIGN,NSIGN,ASIGN};
    private String hash;
    private long valTimeStamp;
    private String uid1;
    private String uid2;
    private boolean uid1sign;
    private boolean uid2sign;

    public signCode(String hash, long timeOut, String uid1, String uid2,boolean uid1sign,boolean uid2sign) {
        this.hash = hash;
        this.valTimeStamp = new Date().getTime() + timeOut;
        this.uid1 = uid1;
        this.uid2 = uid2;
        this.uid1sign = uid1sign;
        this.uid2sign = uid2sign;
    }

    public SCSTATUS signCodeStatus(){
        if(uid1sign && uid1sign)
            return SCSTATUS.ASIGN;
        else if(uid1sign || uid2sign)
            return SCSTATUS.PASIGN;
        else
            return SCSTATUS.NSIGN;
    }

    public boolean sign(String uid){
        if(uid.compareTo(uid1) == 0){
            System.out.println("甲方"+uid+"签名");
            uid1sign = true;
            return true;
        }
        if (uid.compareTo(uid2) == 0) {
            System.out.println("乙方"+uid+"签名");
            uid2sign = true;
            return true;
        }
        System.out.println(uid+"不是合同签署方");
        return false;
    }

    public boolean isTimeOut(){
        long l = new Date().getTime();
        if(l > this.valTimeStamp)
            return true;
        else
            return false;
    }

    public String getHash(){
        return this.hash;
    }

}
