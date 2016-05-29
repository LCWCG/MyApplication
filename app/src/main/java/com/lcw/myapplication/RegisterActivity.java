package com.lcw.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
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
import com.lcw.myapplication.lock.LockPatternActivity;
import com.lcw.myapplication.lock.LockPatternUtils;
import com.lcw.myapplication.util.HintDialog;
import com.lcw.myapplication.util.HintDialog2;
import com.lcw.myapplication.util.HintDialog3;
import com.lcw.myapplication.util.ServerApi;
import com.lcw.myapplication.util.UserInfo;
import com.lcw.myapplication.util.Utils;
import com.lcw.myapplication.view.CircleProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * @author 刘春旺
 *
 */
public class RegisterActivity extends BaseActivity implements View.OnClickListener{

    private static final String TEL = "4008301068";

    private ImageButton mBack;
    private LinearLayout mLlPhone, mLlCode, mLlSetPassword;
    private Button mPhoneNext, mCodeNext, mAffirm;
    private TextView mTitle;
    private EditText mPhoneEt, mCodeEt;
    private ImageButton mPhoneClear, mPasswordClear1, mPasswordClear2;
    private ProgressDialog mProgressDialog;
    private EditText mPasswordEt1, mPasswordEt2;
    private Button mTerms;
    private CircleProgressBar  mCodePb;
    private Button mSendSms;
    private CountDownTimer mTimer;
    private int progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_register);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
        }

        initView();
        mBack.setOnClickListener(this);
        mPhoneNext.setOnClickListener(this);
        mCodeNext.setOnClickListener(this);
        mAffirm.setOnClickListener(this);

//        mPhoneEt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        mPhoneEt.addTextChangedListener(mPhoneWatcher);
        mPhoneClear.setOnClickListener(this);

        mPhoneNext.setClickable(false);
        mPhoneNext.setBackgroundResource(R.drawable.home_btn_selector_noclick);
        //
//        mCodeEt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        mCodeEt.addTextChangedListener(mCodeWatcher);

        mCodeNext.setClickable(false);
        mCodeNext.setBackgroundResource(R.drawable.home_btn_selector_noclick);

        mSendSms.setOnClickListener(this);
        //
        mPasswordClear1.setOnClickListener(this);
        mPasswordClear2.setOnClickListener(this);
        mTerms.setOnClickListener(this);
        mPasswordEt1.addTextChangedListener(mPasswordWatcher1);
        mPasswordEt2.addTextChangedListener(mPasswordWatcher2);

        mAffirm.setClickable(false);
        mAffirm.setBackgroundResource(R.drawable.home_btn_selector_noclick);
    }

    private TextWatcher mCodeWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String code = mCodeEt.getText().toString().trim();

            mCodeNext.setClickable(false);
            mCodeNext.setBackgroundResource(R.drawable.home_btn_selector_noclick);
            if (!TextUtils.isEmpty(code)){
                mCodeNext.setClickable(true);
                mCodeNext.setBackgroundResource(R.drawable.home_btn_selector);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
        }
    };

    private TextWatcher mPhoneWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String phone = mPhoneEt.getText().toString().trim();
            if (phone.length() > 0){
                mPhoneClear.setVisibility(View.VISIBLE);
            }else{
                mPhoneClear.setVisibility(View.GONE);
            }

            mPhoneNext.setClickable(false);
            mPhoneNext.setBackgroundResource(R.drawable.home_btn_selector_noclick);
            if (!TextUtils.isEmpty(phone)){
                mPhoneNext.setClickable(true);
                mPhoneNext.setBackgroundResource(R.drawable.home_btn_selector);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
        }
    };

    private TextWatcher mPasswordWatcher1 = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String password1 = mPasswordEt1.getText().toString().trim();
            String password2 = mPasswordEt2.getText().toString().trim();
            if (password1.length() > 0){
                mPasswordClear1.setVisibility(View.VISIBLE);
            }else{
                mPasswordClear1.setVisibility(View.GONE);
            }
            updataHintInfo(password1, password2);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
        }
    };

    private TextWatcher mPasswordWatcher2 = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String password1 = mPasswordEt1.getText().toString().trim();
            String password2 = mPasswordEt2.getText().toString().trim();
            if (password2.length() > 0){
                mPasswordClear2.setVisibility(View.VISIBLE);
            }else{
                mPasswordClear2.setVisibility(View.GONE);
            }
            updataHintInfo(password1, password2);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
        }
    };

    private void updataHintInfo(String password1, String password2){
        mAffirm.setClickable(false);
        mAffirm.setBackgroundResource(R.drawable.home_btn_selector_noclick);
        if (!TextUtils.isEmpty(password1) && !TextUtils.isEmpty(password2)){
            mAffirm.setClickable(true);
            mAffirm.setBackgroundResource(R.drawable.home_btn_selector);
        }
    }

    private void initView(){
        mBack = (ImageButton) findViewById(R.id.ib_register_back);

        mLlPhone = (LinearLayout) findViewById(R.id.ll_register_phone);
        mLlCode = (LinearLayout) findViewById(R.id.ll_register_code);
        mLlSetPassword = (LinearLayout) findViewById(R.id.ll_register_set_password);

        mPhoneNext = (Button) findViewById(R.id.btn_register_phone_next);
        mCodeNext = (Button) findViewById(R.id.btn_register_code_next);
        mAffirm = (Button) findViewById(R.id.btn_register_password_affirm);

        mTitle = (TextView) findViewById(R.id.tv_register_title);

        mPhoneEt = (EditText) findViewById(R.id.et_register_phone);
        mPhoneClear = (ImageButton) findViewById(R.id.ib_register_phone_clear);

        mCodeEt = (EditText) findViewById(R.id.et_register_code);
        mCodePb = (CircleProgressBar) findViewById(R.id.pb_register_code);
        mSendSms = (Button) findViewById(R.id.btn_register_sms);

        mPasswordEt1 = (EditText) findViewById(R.id.et_register_password1);
        mPasswordEt2 = (EditText) findViewById(R.id.et_register_password2);
        mPasswordClear1 = (ImageButton) findViewById(R.id.ib_register_password1_clear);
        mPasswordClear2 = (ImageButton) findViewById(R.id.ib_register_password2_clear);
        mTerms = (Button) findViewById(R.id.btn_register_password_terms);

    }

    private void startTimer(){
        mCodePb.setVisibility(View.VISIBLE);
        mSendSms.setVisibility(View.GONE);
        progress = 60;
        mTimer = new CountDownTimer(100000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                if (progress > 0){
                    mCodePb.setProgress(progress);
                    progress --;
                }else{
                    stopTimer();
                }
            }

            @Override
            public void onFinish() {
                //
            }
        };
        mTimer.start();
    }

    private void stopTimer(){
        mCodePb.setVisibility(View.GONE);
        mSendSms.setVisibility(View.VISIBLE);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
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
        if (mLlPhone.getVisibility() == View.VISIBLE){
            stopTimer();
            finish();
            return;
        }
        if (mLlCode.getVisibility() == View.VISIBLE){
            mLlPhone.setVisibility(View.VISIBLE);
            mLlCode.setVisibility(View.GONE);
            mLlSetPassword.setVisibility(View.GONE);
            mTitle.setText("注册");
            mCodeEt.setText("");
            return;
        }
        if (mLlSetPassword.getVisibility() == View.VISIBLE){
            mLlPhone.setVisibility(View.GONE);
            mLlCode.setVisibility(View.VISIBLE);
            mLlSetPassword.setVisibility(View.GONE);
            mTitle.setText("填写验证码");
            mPasswordEt1.setText("");
            mPasswordEt2.setText("");
            return;
        }
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.ib_register_back:
                getFinish();
                break;
            case R.id.btn_register_phone_next:
                hintKeyboard();
                getRegister(mPhoneEt.getText().toString().trim(), null, null, 1);
                break;
            case R.id.btn_register_code_next:
                hintKeyboard();
                getRegister(mPhoneEt.getText().toString().trim(), mCodeEt.getText().toString().trim(), null, 2);
                break;
            case R.id.btn_register_password_affirm:
                hintKeyboard();
                String phone = mPhoneEt.getText().toString().trim();
                String password1 = mPasswordEt1.getText().toString().trim();
                String password2 = mPasswordEt2.getText().toString().trim();
                if (!Utils.isPassword(password1)){
                    showHintDialog("密码不合法，6-16字符");
                    return;
                }
                if (!Utils.isPassword(password2)){
                    showHintDialog("密码不合法，6-16字符");
                    return;
                }
                if (!password1.equals(password2)){
                    showHintDialog("两次密码输入不相同，请重新输入");
                    return;
                }
                getRegister(phone, null, password1, 3);
                break;
            case R.id.ib_register_phone_clear:
                mPhoneEt.setText("");
                break;
            case R.id.ib_register_password1_clear:
                mPasswordEt1.setText("");
                break;
            case R.id.ib_register_password2_clear:
                mPasswordEt2.setText("");
                break;
            case R.id.btn_register_password_terms:
                Toast.makeText(mContext, "快车财富用户协议", Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_register_sms:
                final HintDialog3 dialog3 = new HintDialog3(mContext, "重新发送验证码短信", "联系客服人员", R.layout.hintdialoglayout3,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                getRegister(mPhoneEt.getText().toString().trim(), null, null, 1);
                            }
                        },
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                final HintDialog dialog = new HintDialog(mContext, "提示", "400-830-1068", "取消", "呼叫", R.layout.hintdialoglayout,
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                               //
                                            }
                                        },
                                        new View.OnClickListener() {
                                            @Override
                                            public void onClick(View view) {
                                                Uri uri = Uri.parse("tel:" + TEL);
                                                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                                startActivity(intent);
                                            }
                                        });
                                dialog.show();
                            }
                        });
                dialog3.show();
                break;
            default:
                break;
        }
    }

//    #########################################################################################################
//             error_code                        error_remark
//                1                              手机号码不能为空！
//                2                              手机号已被使用
//                3                              动态码错误
//                4                              密码长度为6-15位
//                5                              注册失败
//                6                              验证码发送失败
//                7                              注册来源不能为空！
//    #########################################################################################################

    private void getRegister(final String phone, String code, final String password, final int step){
        if (step == 1){
            if (!Utils.isMobileNO(phone)){
                showHintDialog("手机号不合法");
                return;
            }
        }
        if (step == 2){
            if (!Utils.isNumeric(code) || code.length() > 6){
                showHintDialog("验证码不合法，0-6位数字");
                return;
            }
        }
        if (step == 3){
            //
        }

        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败");
            return;
        }

        showLoadDialog();

        ServerApi.getRegisterData(phone, code, password, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    closeDialog();

                    final JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result");
                    if (result.equals("success")){
                        if (step == 1){
                            final HintDialog dialog = new HintDialog(mContext, "确认手机号码", "我们将发送验证码短信到你的手机" + "\n" + phone
                                    , "取消", "确认", R.layout.hintdialoglayout,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //
                                        }
                                    },
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            startTimer();
                                            mLlPhone.setVisibility(View.GONE);
                                            mLlCode.setVisibility(View.VISIBLE);
                                            mLlSetPassword.setVisibility(View.GONE);
                                            mTitle.setText("填写验证码");
                                        }
                                    });
                            dialog.show();
                        }
                        if (step == 2){
                            mLlPhone.setVisibility(View.GONE);
                            mLlCode.setVisibility(View.GONE);
                            mLlSetPassword.setVisibility(View.VISIBLE);
                            mTitle.setText("填写密码");
                        }
                        if (step == 3){
                            try {
                                String uid = jso.getString("user_id");
                                UserInfo.setUserInfo(phone, phone, uid, password);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            final HintDialog dialog = new HintDialog(mContext, "注册成功", "设置手势密码", "去设置", "取消", R.layout.hintdialoglayout,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            showLockPattern(LockPatternActivity.TYPE_SET);

                                            if (LoginActivity.mLoginActivity != null){
                                                LoginActivity.mLoginActivity.finish();
                                            }
                                            finish();
                                        }
                                    },
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            if (LoginActivity.mLoginActivity != null){
                                                LoginActivity.mLoginActivity.finish();
                                            }
                                            finish();
                                        }
                                    });
                            dialog.show();
                            dialog.setCancelable(false);
                        }
                        return;
                    }
                    if (result.equals("error")) {
                        String errorCode = jso.getString("error_code");
                        String errorMsg = jso.getString("error_remark");
                        if (errorCode.equals("2")){

                            final HintDialog dialog = new HintDialog(mContext, "提示", errorMsg, "找回密码", "取消", R.layout.hintdialoglayout,
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Toast.makeText(mContext, "找回密码——陈朱海", Toast.LENGTH_SHORT).show();
                                            //finish()掉当前activity
                                        }
                                    },
                                    new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            //
                                        }
                                    });
                            dialog.show();
                            return;
                        }

                        showHintDialog(errorMsg);
                    }
                }catch (Exception e){

                    showHintDialog("请求错误");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showHintDialog("请求错误");
            }
        });
    }

    private  void showLockPattern( int type) {
        LockPatternUtils lockPatternUtils = new LockPatternUtils(mContext);
        String locknum = lockPatternUtils.getLockPaternString();

        if (TextUtils.isEmpty(locknum)) {
            type = LockPatternActivity.TYPE_SET;
        }

        Intent it = new Intent();
        it.setClassName(mContext, LockPatternActivity.class.getName());
        it.putExtra(LockPatternActivity.BUNDLE_SET, type);
        mContext.startActivity(it);
    }

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

    private void showHintDialog(String errorMsg){
        closeDialog();
        if (mContext == null) return;
        final HintDialog2 dialog2 = new HintDialog2(mContext, "提示", errorMsg, "我知道了", R.layout.hintdialoglayout2,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //
                    }
                });
        dialog2.show();
    }

    private void hintKeyboard(){
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }
}
