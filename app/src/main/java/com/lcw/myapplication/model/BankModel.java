package com.lcw.myapplication.model;

/**
 *
 * @author 刘春旺
 *
 */
public class BankModel {

    public static final String ACCOUNT = "account";
    public static final String BANK = "bank";
    public static final String IS_DEFAULT = "is_default";
    public static final String ID = "id";

    private String account;
    private String bank;
    private String is_default;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }

    public String getBank() {
        return bank;
    }

    public void setBank(String bank) {
        this.bank = bank;
    }

    public String getIs_default() {
        return is_default;
    }

    public void setIs_default(String is_default) {
        this.is_default = is_default;
    }
}
