package com.qr.entity;

import java.util.Map;

public class Contract {
    private String id;
    private int type;
    private String title;
    private String path;
    private long fileSize;
    private int sign1;
    private int sign2;
    private int agree;
    private long lastModified;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public int getSign1() {
        return sign1;
    }

    public void setSign1(int sign1) {
        this.sign1 = sign1;
    }

    public int getSign2() {
        return sign2;
    }

    public void setSign2(int sign2) {
        this.sign2 = sign2;
    }

    public int getAgree() {
        return agree;
    }

    public void setAgree(int agree) {
        this.agree = agree;
    }

    public long getLastModified() {
        return lastModified;
    }

    public void setLastModified(long lastModified) {
        this.lastModified = lastModified;
    }

    //0表示甲；1表示乙
    public int retnType(int userId){
        if (sign1==0&&sign2==1)
            return 4;
        if (sign1==1&&sign2==1)
            return 5;
        if (userId==0){
            switch (agree){
                case 0:
                    return 2;
                case 1:
                    return 1;
                case 2:
                    return 3;
            }
            if (sign1==1&&sign2==0)
                return 4;
            if (sign1==1&&sign2==1)
                return 5;

        }
        if (userId==1){
            switch (agree){
                case 0:
                    return 1;
                case 1:
                    return 2;
                case 2:
                    return 3;
            }
        }


        return 0;
    }
}

