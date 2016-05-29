package com.lcw.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.lcw.myapplication.model.BankModel;
import com.lcw.myapplication.util.HintDialog2;
import com.lcw.myapplication.util.ServerApi;
import com.lcw.myapplication.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 *
 * @author 刘春旺
 *
 */
public class ExtractMoneyActivity extends BaseActivity{

    private Button mAffirm;

    private java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");

    //银行卡号
    private TextView mBankCodeTv;
    //账户余额
    private TextView mMaxMoneyTv;
    //输入的提现金额
    private EditText mMoneyEt;
    //手续费
    private TextView mCostTv;
    //实际提现：XX:XX元
    private TextView mRealTv;

    private double mBalance = 0;//可用金额(账户余额)
    private double mReal = 0;//实际提现金额

    private String mAccount = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_extract_money);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
        }

        initView();

        findViewById(R.id.ib_extract_money_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinish();
            }
        });

        findViewById(R.id.btn_extract_money_log).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, CashLogActivity.class));
            }
        });

        findViewById(R.id.rl_extract_money_add_bank).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, BankManageActivity.class);
                intent.putExtra("LAUNCH_CLASS_NAME_FLAG", "ExtractMoneyActivity");
                startActivityForResult(intent, REQUEST_CODE);
            }
        });

        mAffirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUserInfo();
            }
        });

        mBalance = Double.parseDouble(getIntent().getStringExtra("BALANCE"));
        mMaxMoneyTv.setText(mBalance + "");

        mMoneyEt.addTextChangedListener(mTextWatcher);

        mAffirm.setBackgroundResource(R.drawable.home_btn_selector_noclick);
        mAffirm.setClickable(false);

        loadData();
    }

    private void initView(){
        mBankCodeTv = (TextView) findViewById(R.id.tv_extract_money_bank_code);
        mMaxMoneyTv = (TextView) findViewById(R.id.tv_extract_max_money);
        mMoneyEt = (EditText) findViewById(R.id.et_extract_money);
        mCostTv = (TextView) findViewById(R.id.tv_extract_money_cost);
        mRealTv = (TextView) findViewById(R.id.tv_extract_money_real);
        mAffirm = (Button) findViewById(R.id.btn_extract_money_affirm);
    }

    /**
     * 选择银行卡
     */
    public static final int REQUEST_CODE = 22;
    public static final int RESULT_CODE = 23;

    /**
     * 支付
     */
    public static final int REQUEST_CODE_2 = 24;
    public static final int RESULT_CODE_2_1 = 25;
    public static final int RESULT_CODE_2_2 = 26;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_CODE){
            mAccount = data.getStringExtra("BANK_CODE");
            int accountLength = mAccount.length();
            mBankCodeTv.setText("**** **** **** " + mAccount.substring(accountLength - 4, accountLength));
        }
        if (requestCode == REQUEST_CODE_2){
            if (resultCode == RESULT_CODE_2_1){
                showHintDialog("请输入支付密码", true);
            }
            if (resultCode == RESULT_CODE_2_2){
                String password = data.getStringExtra("PASSWORD");
                String money = mMoneyEt.getText().toString().trim();

                requestCashData(password, money, mAccount);
            }
        }
    }

    private void loadData(){
        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败", false);
            return;
        }
        showLoadDialog();

        ServerApi.getBankListData(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result").trim();
                    if (result.equals("success")) {

                        requestIsVIPData();

                        JSONArray jsa = new JSONArray(jso.getString("list"));
                        for (int i = 0; i < jsa.length(); i++) {
                            JSONObject jso2 = jsa.getJSONObject(i);
                            if (jso2.getString(BankModel.IS_DEFAULT).trim().equals("1")) {

                                mAccount = jso2.getString(BankModel.ACCOUNT);
                                int accountLength = mAccount.length();
                                mBankCodeTv.setText("**** **** **** " + mAccount.substring(accountLength - 4, accountLength));

                                break;
                            }
                        }
                        return;
                    }
                    if (result.equals("error")) {
                        String error_remark = jso.getString("error_remark");

                        showHintDialog(error_remark, false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    closeDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showHintDialog("请求错误", false);
            }
        });
    }

    private void requestIsVIPData(){

        ServerApi.getIsVIPData(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result").trim();
                    if (result.equals("success")) {
                        if (jso.getString("vip_status").trim().equals("1")) {
                            isVIP = true;
                        } else {
                            isVIP = false;
                        }

                        requestCostRuleData();
                        return;
                    }
                    if (result.equals("error")) {
                        String error_remark = jso.getString("error_remark");

                        showHintDialog(error_remark, false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    showHintDialog("请求错误", false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showHintDialog("请求错误", false);
            }
        });
    }

    private void requestCostRuleData(){
        ServerApi.getCostRuleData(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result").trim();
                    if (result.equals("success")) {
                        JSONArray jsa;
                        if (isVIP) {
                            jsa = new JSONArray(jso.getString("vip"));
                        } else {
                            jsa = new JSONArray(jso.getString("vip_no"));
                        }
                        MaxExtractMoney = Double.parseDouble(jsa.getString(0));
                        BasicMoney = Double.parseDouble(jsa.getString(1));
                        BasicCost = Double.parseDouble(jsa.getString(2));
                        AddMoney = Double.parseDouble(jsa.getString(3));
                        AddCost = Double.parseDouble(jsa.getString(4));
                        MAXCost = Double.parseDouble(jsa.getString(5));

                        closeDialog();
                        return;
                    }
                    if (result.equals("error")) {
                        String error_remark = jso.getString("error_remark");

                        showHintDialog(error_remark, false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    showHintDialog("请求错误", false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showHintDialog("请求错误", false);
            }
        });
    }

    private void loadUserInfo(){
        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败", true);
            return;
        }
        showLoadDialog();

        ServerApi.getUserInfoData(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result").trim();
                    if (result.equals("success")) {
                        String password = jso.getString("password").trim();
                        String paypassword = jso.getString("paypassword").trim();

                        Intent intent = new Intent(mContext, InputExtractPasswordActivity.class);
                        if (password.equals(paypassword)) {
                            intent.putExtra("ISB", true);
                        } else {
                            intent.putExtra("ISB", false);
                        }
                        intent.putExtra("MONEY", df.format(mReal));
                        startActivityForResult(intent, REQUEST_CODE_2);

                        closeDialog();
                        return;
                    }
                    if (result.equals("error")) {
                        String error_remark = jso.getString("error_remark");

                        showHintDialog(error_remark, true);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    showHintDialog("请求错误", true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showHintDialog("请求错误", true);
            }
        });
    }

    private void requestCashData(String paypassword, String money, String account){
        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败", true);
            return;
        }
        showLoadDialog();

        ServerApi.getCashData(paypassword, money, account, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result").trim();
                    if (result.equals("success")) {

                        Toast.makeText(mContext, "提现成功", Toast.LENGTH_SHORT).show();
                        double mo = Double.parseDouble(mMoneyEt.getText().toString().trim());
                        mBalance = mBalance - mo;
                        mMaxMoneyTv.setText(mBalance + "");

                        closeDialog();
                        return;
                    }
                    if (result.equals("error")) {
                        String error_remark = jso.getString("error_remark");

                        showHintDialog(error_remark, true);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    showHintDialog("请求错误", true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showHintDialog("请求错误", true);
            }
        });
    }

    private ProgressDialog mProgressDialog;

    private void showLoadDialog(){
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.show();
        mProgressDialog.setMessage("请稍候……");
    }

    private void closeDialog(){
        if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    private void showHintDialog(String errorMsg,final boolean isCancelable){
        closeDialog();
        if (mContext == null) return;
        final HintDialog2 dialog2 = new HintDialog2(mContext, "提示", errorMsg, "我知道了", R.layout.hintdialoglayout2,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (!isCancelable) getFinish();
                    }
                });
        dialog2.show();
        dialog2.setCancelable(isCancelable);
    }

    private void getFinish(){
        finish();
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK){
            getFinish();
        }
        return super.onKeyDown(keyCode, event);
    }

    private void hintKeyboard(){
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private TextWatcher mTextWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            try{
                double money = Double.parseDouble(mMoneyEt.getText().toString().trim());
                //最高提现金额 >= 用户帐户余额....以余额为最高提现界限
                //最高提现金额 < 用户帐户余额....以最高提现金额为最高提现界限
                if (MaxExtractMoney >= mBalance){
                    if (money <= mBalance){

                        arithmetic(money);
                    }else{
                        mMoneyEt.setText(mBalance + "");
                        mMoneyEt.setSelection(mMoneyEt.length());
                        hintKeyboard();

                        arithmetic(mBalance);
                    }
                }else{
                    if (money <= MaxExtractMoney){

                        arithmetic(money);
                    }else{
                        mMoneyEt.setText(MaxExtractMoney + "");
                        mMoneyEt.setSelection(mMoneyEt.length());
                        hintKeyboard();

                        arithmetic(MaxExtractMoney);
                    }
                }

                if (mReal > 0){
                    mAffirm.setClickable(true);
                    mAffirm.setBackgroundResource(R.drawable.home_btn_selector);
                }
            }catch (Exception e){
                mAffirm.setClickable(false);
                mAffirm.setBackgroundResource(R.drawable.home_btn_selector_noclick);

                mCostTv.setText("0.00");
                mRealTv.setText("");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
        }

    };

    /**
     * 是否为VIP——默认非VIP
     */
    private boolean isVIP = false;

    /**
     * 最高提现金额
     */
    private double MaxExtractMoney = 50000.0;
    /**
     * 10000元以内
     */
    private double BasicMoney = 10000.0;
    /**
     * 收取5.0元费用。
     */
    private double BasicCost = 10.0;
    /**
     * 每增加10000.0元
     */
    private double AddMoney = 10000.0;
    /**
     * 扣费2.0元
     */
    private double AddCost = 6.0;
    /**
     * 当日最高扣费金额300.0元
     */
    private double MAXCost = 300.0;

    private void arithmetic(double money){
        if (money <= BasicMoney){

            mCostTv.setText(BasicCost + "");
            mReal = money - BasicCost;
            mRealTv.setText("实际提现：" + df.format(mReal) + "元");
        }else{
            int i = (money - BasicMoney) % AddMoney == 0 ? 0 : 1;
            double cost = BasicCost + ((int)((money - BasicMoney) / AddMoney) + i) * AddCost;

            if (cost > MAXCost){
                cost = MAXCost;
            }
            mCostTv.setText(cost + "");
            mReal = money - cost;
            mRealTv.setText("实际提现：" + df.format(mReal) + "元");
        }
    }

}
