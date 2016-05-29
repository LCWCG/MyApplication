package com.lcw.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.lcw.myapplication.util.HintDialog2;
import com.lcw.myapplication.util.ServerApi;
import com.lcw.myapplication.util.Utils;

import org.json.JSONObject;

/**
 *
 * @author 刘春旺
 *
 */
public class InvestmentActivity extends BaseActivity implements View.OnClickListener{

    /**
     * 默认投资金额
     */
    private static final int DEFAULTMONEY = 10000;
    /**
     * 最小投资金额
     */
    private static final int MINMONEY = 50;
    private java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
    private ImageButton mBack;
    private EditText mMoneyEt;
    private TextView mEarnings;
    private TextView mMaxMoneyTv, mResidueMoneyTv;
    private Button mInfoBtn, mtopupBtn, mAffirmBtn;

    private LinearLayout mTopupLl;
    /**
     * 借款利率
     */
    private double mBorrowApr;
    /**
     * 款项ID
     */
    private String mBorrowNid;

    private double mBalance = 0;//可用金额(账户余额)
    private double mBorrowAccountWait = 0;//剩余投标金额(最高可投金额)
    private String mName;//标名

    private ProgressDialog mProgressDialog;

    private void showLoadDialog(){
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.show();
        mProgressDialog.setMessage("载入中，请稍候……");
    }

    private void closeDialog(){
        if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_investment);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
        }

        Intent data = getIntent();
        mBorrowApr = Double.parseDouble(data.getStringExtra("APR"));
        mBorrowNid = data.getStringExtra("NID");

        initView();
        mBack.setOnClickListener(this);
        mInfoBtn.setOnClickListener(this);
        mtopupBtn.setOnClickListener(this);
        mAffirmBtn.setOnClickListener(this);

//        mMoney.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        mMoneyEt.addTextChangedListener(mTextWatcher);

        requestData();

        //*********************************************************************提现
        Button btn = (Button) findViewById(R.id.test_tixian);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ExtractMoneyActivity.class);
                intent.putExtra("BALANCE", mBalance + "");
                startActivity(intent);
            }
        });
        //*********************************************************************
    }

    private void requestData(){

        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败", false);
            return;
        }

        showLoadDialog();
//        {
//            "module": "user",
//                "request": "&q=getaccount",
//                "result": "success",
//                "method": "post",
//                "user_id": "42332",
//                "total": "10000.00",//资金总额
//                "balance": "10000.00",//可用金额
//                "frost": "0.00",//冻结金额
//                "await": "0.00",
//                "repay": "0.00",
//                "frost_cash": "0.00"
//        }
        ServerApi.getUserMoneyInfoData(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result");
                    if (result.equals("success")){
                        mBalance = Double.parseDouble(jso.getString("balance").trim());
                        requestLendMoney();
                        return;
                    }
                    if (result.equals("error")){
                        String errorMsg = jso.getString("error_remark");
                        showHintDialog(errorMsg, false);
                        return;
                    }
                }catch (Exception e){
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

    private void requestLendMoney(){
        //        {
//            "module": "borrow",
//                "request": "&q=getborrowone",
//                "result": "success",
//                "name": "房产抵押借款51万（第一期25万）",//标名
//                "user_id": "42041",
//                "borrow_nid": "20150600050",
//                "style_title": "按月付息到期还本",     //还款方式
//                "account": "250000.00",              //借款金额
//                "borrow_apr": "19.20",               //借款年利率
//                "borrow_period_name": "12个月",
//                "borrow_account_scale": "0.00",
//                "account_min": "",
//                "tender_account_min": "50",
//                "borrow_other_time": "574520",
//                "status": "1",
//                "tender_times": "0",
//                "repay_month_account": "4000",
//                "borrow_account_wait": "250000.00",    //剩余投标金额
//                "count_down": "1",
//                "tender_time": "2015-09-16 14:59:14",
//                "borrow_status_nid": "loan",           //借款状态id的属性>>>>full=>满标待审loan=>满标待审repay=>还款中
//                "nowtime": "2015-09-16 17:58:59",
//                "borrow_period": "12",                 //融资期限
//                "borrow_pawn_type": "房产",             //风控措施
//                "agency_id": "12",                     //保障机构id
//                "username": "dgthre",                  //用户名
//                "borrow_type": "year",
//                "is_acrivity": "0",
//                "invest_type": "pawn",
//                "avatarurl": "/data/avatar/42041_avatar_middle.jpg",
//                "roam": {
//            "id": "",
//                    "user_id": "",
//                    "borrow_nid": "",
//                    "account_min": "",
//                    "portion_total": "",
//                    "portion_yes": "",
//                    "portion_wait": "",
//                    "recover_yes": "",
//                    "recover_wait": "",
//                    "voucher": "",
//                    "vouch_style": "",
//                    "borrow_account": "",
//                    "borrow_account_use": "",
//                    "risk": "",
//                    "upfiles_id": "",
//                    "contents": "",
//                    "borrowuser": "",
//                    "borrowaccount": "",
//                    "borrowrepaytype": "",
//                    "borrowperiod": "",
//                    "borrowtime": "",
//                    "interestrate": "",
//                    "signarea": ""
//        },
//            "borrow_contents": "本人有套住房位于合肥市瑶海区，由于需要资金周转，办理抵押借款51万。"
//        }

        ServerApi.getLendMoneyData(mBorrowNid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result");
                    if (result.equals("success")){
                        mBorrowAccountWait = Double.parseDouble(jso.getString("borrow_account_wait").trim());
                        mName = jso.getString("name").trim();
                        loadData();
                        return;
                    }
                    if (result.equals("error")){
                        String errorMsg = jso.getString("error_remark");
                        showHintDialog(errorMsg, false);
                        return;
                    }
                }catch (Exception e){
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

    private void loadData(){
        closeDialog();

        if (mBalance < DEFAULTMONEY){
            mMoneyEt.setText(mBalance + "");
            mMoneyEt.setSelection(mMoneyEt.length());
            mEarnings.setText(df.format(mBalance + mBalance * mBorrowApr / 100));
        }else{
            mMoneyEt.setText(DEFAULTMONEY + "");
            mMoneyEt.setSelection(mMoneyEt.length());
            mEarnings.setText(df.format(DEFAULTMONEY + DEFAULTMONEY * mBorrowApr / 100));
        }

        mMaxMoneyTv.setText(mBorrowAccountWait + "");
        mResidueMoneyTv.setText(mBalance + "");
        mInfoBtn.setText(mName);
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
                if (money <= mBorrowAccountWait){
                    mEarnings.setText(df.format(money + money * mBorrowApr / 100));
                }else{
                    mEarnings.setText(df.format(mBorrowAccountWait + mBorrowAccountWait * mBorrowApr / 100));
                    mMoneyEt.setText(mBorrowAccountWait + "");
                    mMoneyEt.setSelection(mMoneyEt.length());
                    hintKeyboard();
                }

                double money2 = Double.parseDouble(mMoneyEt.getText().toString().trim());
                if (money2 <= mBalance){
                    mAffirmBtn.setClickable(true);
                    mAffirmBtn.setBackgroundResource(R.drawable.home_btn_selector);
                    mTopupLl.setVisibility(View.INVISIBLE);
                }else{
                    mAffirmBtn.setClickable(false);
                    mAffirmBtn.setBackgroundResource(R.drawable.home_btn_selector_noclick);
                    mTopupLl.setVisibility(View.VISIBLE);
                }
            }catch (Exception e){
                mTopupLl.setVisibility(View.INVISIBLE);
                mAffirmBtn.setClickable(false);
                mAffirmBtn.setBackgroundResource(R.drawable.home_btn_selector_noclick);

                if (TextUtils.isEmpty(mMoneyEt.getText().toString())){
                    mEarnings.setText("0.00");
                    return;
                }
                mEarnings.setText("输入格式错误！");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
        }

    };

    private void initView(){
        mBack = (ImageButton) findViewById(R.id.ib_investment_back);
        mMoneyEt = (EditText) findViewById(R.id.et_investment_money);
        mEarnings = (TextView) findViewById(R.id.tv_investment_earnings);
        mTopupLl = (LinearLayout) findViewById(R.id.ll_investment_top_up);

        mMaxMoneyTv = (TextView) findViewById(R.id.tv_investment_max_money);
        mResidueMoneyTv = (TextView) findViewById(R.id.tv_investment_residue_money);

        mInfoBtn = (Button) findViewById(R.id.btn_investment_info);
        mtopupBtn = (Button) findViewById(R.id.btn_investment_top_up);
        mAffirmBtn = (Button) findViewById(R.id.btn_investment_affirm);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_investment_back:
                getFinish();
                break;
            case R.id.btn_investment_info:
                Intent intent = new Intent(mContext, InvestmentProjectActivity.class);
                intent.putExtra("NID", mBorrowNid);
                startActivity(intent);
                break;
            case R.id.btn_investment_top_up:
                Toast.makeText(mContext, "充值页面——刘波", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_investment_affirm:
                mMoney = Double.parseDouble(mMoneyEt.getText().toString().trim());
                if (mMoney < MINMONEY){
                    showHintDialog("投资金额不足，50元起投", true);
                    return;
                }

                loadUserInfo();
                break;
            default:
                break;
        }
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

                        Intent intent = new Intent(mContext, InputPasswordActivity.class);
                        if (password.equals(paypassword)) {
                            intent.putExtra("ISB", true);
                        }else{
                            intent.putExtra("ISB", false);
                        }
                        startActivityForResult(intent, REQUEST_CODE);

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

    private double mMoney;
    private static final int REQUEST_CODE = 11;

    public static final int RESULT_CODE1 = 1;
    public static final int RESULT_CODE2 = 2;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (REQUEST_CODE != requestCode) return;
        if (resultCode == RESULT_CODE1){
            showHintDialog("请输入支付密码", true);
        }
        if (resultCode == RESULT_CODE2){
            requestPay(mBorrowNid, data.getStringExtra("PASSWORD"), mMoney + "", null, null);
        }
    }

//    user_id	            数字		                          必填	        用户id，系统生成的用户唯一标识
//    borrow_nid	        11位纯数字		                  必填	        借款标nid，系统生成的借款标标识
//    paypassword	        英文、数字 不小于6-15个字符之间		  必填	        英文、数字 不小于6-15个字符之间 用户交易密码
//    account	            数字，最多可带有两位小数		      必填	        用户投资金额（用于除流转标以外的标种）
//    portion	            纯数字			                                购买份数（用于流转标）
//    contents				投资备注
//    borrow_password	    英文、数字 不小于6-15个字符之间			            借款密码，当借款用户有设借款密码时使用（用于信用标）

    private void requestPay(String borrow_nid, String paypassword, String account, String portion, String borrow_password){

        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败", true);
            return;
        }

        showLoadDialog();

        ServerApi.getPayData(borrow_nid, paypassword, account, portion, borrow_password,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        try {
                            JSONObject jso = new JSONObject(response);
                            String result = jso.getString("result");
                            if (result.equals("success")) {
                                Toast.makeText(mContext, "恭喜您，投资成功", Toast.LENGTH_SHORT).show();
                                closeDialog();

                                requestData();
                                return;
                            }
                            if (result.equals("error")) {
                                String errorMsg = jso.getString("error_remark");
                                showHintDialog(errorMsg, true);
                                return;
                            }
                        } catch (Exception e) {
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

}
