package com.lcw.myapplication.model;

/**
 *
 * @author 刘春旺
 *
 */
public class CashLogModel {

    public static final String NID = "nid";
    public static final String TOTAl = "total";
    public static final String CREDITED = "credited";
    public static final String FEE = "fee";
    public static final String STATUS = "status";
    public static final String ADD_TIME = "addtime";

    private String nid;
    private String total;
    private String credited;
    private String fee;
    private String status;
    private String addtime;

    public String getNid() {
        return nid;
    }

    public void setNid(String nid) {
        this.nid = nid;
    }

    public String getTotal() {
        return total;
    }

    public void setTotal(String total) {
        this.total = total;
    }

    public String getCredited() {
        return credited;
    }

    public void setCredited(String credited) {
        this.credited = credited;
    }

    public String getFee() {
        return fee;
    }

    public void setFee(String fee) {
        this.fee = fee;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAddtime() {
        return addtime;
    }

    public void setAddtime(String addtime) {
        this.addtime = addtime;
    }
}
