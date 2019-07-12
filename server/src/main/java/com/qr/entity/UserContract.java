package com.qr.entity;

public class UserContract {
    private long userId1;
    private long userId2;
    private String contractId;

    public long getUserId1() {
        return userId1;
    }

    public void setUserId1(long userId) {
        this.userId1 = userId;
    }

    public long getUserId2() {
        return userId2;
    }

    public void setUserId2(long userId2) {
        this.userId2 = userId2;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }
}
