package com.lcw.myapplication.model;

/**
 *
 * @author 刘春旺
 *
 */
public class LoanModel {

    public static final String LIST = "list";
    public static final String YEAR = "year";
    public static final String HALF = "half";
    public static final String QUARTER = "quarter";
    public static final String SPIRIT = "spirit";

    public static final String BORROW_APR = "borrow_apr";
//    public static final String BORROW_FULL_STATUS = "borrow_full_status";
    public static final String BORROW_NID = "borrow_nid";

    private String borrow_apr;
//    private String borrow_full_status;
    private String borrow_nid;

    public String getBorrow_apr() {
        return borrow_apr;
    }

    public void setBorrow_apr(String borrow_apr) {
        this.borrow_apr = borrow_apr;
    }

//    public String getBorrow_full_status() {
//        return borrow_full_status;
//    }

//    public void setBorrow_full_status(String borrow_full_status) {
//        this.borrow_full_status = borrow_full_status;
//    }

    public String getBorrow_nid() {
        return borrow_nid;
    }

    public void setBorrow_nid(String borrow_nid) {
        this.borrow_nid = borrow_nid;
    }

}
