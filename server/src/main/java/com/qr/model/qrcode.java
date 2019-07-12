package com.qr.model;
import java.util.Date;


public class qrcode {
    private int timeOut = 4*60*1000;  //四分钟超时
    private long generateTimeStamp;
    private long scanTimeStamp;
    private String uid;

    public qrcode(long timeStamp){
        this.generateTimeStamp= timeStamp;
        this.uid = "";
        scanTimeStamp = 0;
    }
    public void setUID(String uid){
        this.uid = uid;
    }

    public void setScanTimeStamp() {
        this.scanTimeStamp = new Date().getTime();
    }

    public String getUid() {
        return uid;
    }
    public boolean isTimeOut(){
        if(scanTimeStamp == 0)
        {
            long current = new Date().getTime();
            long inter = current - this.generateTimeStamp;
            if(inter >= timeOut)
                return true;
            return false;
        }
        else{
            long inter = this.scanTimeStamp - this.generateTimeStamp;
            if(inter >= timeOut)
                return true;
            return false;
        }
    }
    public boolean isScan(){
        if(uid.compareTo("") != 0 && !isTimeOut())
            return true;
        else
            return false;
    }
}
