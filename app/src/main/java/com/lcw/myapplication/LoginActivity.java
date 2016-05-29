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
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.lcw.myapplication.util.HintDialog2;
import com.lcw.myapplication.util.ServerApi;
import com.lcw.myapplication.util.UserInfo;
import com.lcw.myapplication.util.Utils;

import org.json.JSONObject;

/**
 *
 * @author 刘春旺
 *
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener{

    private EditText mLoginUser;
    private EditText mPassword;
    private ImageButton mPhoneClear, mpasswordClear;
    private Button mAffirm, mRegister, mForget;
    private ImageButton mBack;
    private ProgressDialog mProgressDialog;
    public static LoginActivity mLoginActivity;

    private boolean isAlter = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mLoginActivity = this;

        setContentView(R.layout.activity_login);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
        }

        initView();
        mBack.setOnClickListener(this);
        mAffirm.setOnClickListener(this);
        mPhoneClear.setOnClickListener(this);
        mpasswordClear.setOnClickListener(this);
        mRegister.setOnClickListener(this);
        mForget.setOnClickListener(this);

//        mLoginUser.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        mLoginUser.addTextChangedListener(mUserWatcher);
        mPassword.addTextChangedListener(mPasswordWatcher);

        mAffirm.setClickable(false);
        mAffirm.setBackgroundResource(R.drawable.home_btn_selector_noclick);

        try{
            if (getIntent().getStringExtra("IS").equals("alter")){
                isAlter = true;
            }
        }catch (Exception e){
            //
        }

        String phone = UserInfo.getPhone();
        if (!TextUtils.isEmpty(phone)){
            mLoginUser.setText(phone);
            mLoginUser.setSelection(mLoginUser.length());
        }
    }

    private void initView(){
        mLoginUser = (EditText) findViewById(R.id.et_login_user);
        mPassword = (EditText) findViewById(R.id.et_login_password);

        mAffirm = (Button) findViewById(R.id.btn_login_affirm);
        mBack = (ImageButton) findViewById(R.id.iv_login_back);

        mPhoneClear = (ImageButton) findViewById(R.id.ib_login_user_clear);
        mpasswordClear = (ImageButton) findViewById(R.id.ib_login_password_clear);

        mRegister = (Button) findViewById(R.id.btn_login_register);
        mForget = (Button) findViewById(R.id.btn_login_forget);
    }

    private void showLoadDialog(){
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.show();
        mProgressDialog.setMessage("登录中……");
    }

    private void closeDialog(){
        if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
    }

    private void getLogin(){
        hintKeyboard();
        final String phone = mLoginUser.getText().toString().trim();
        final String password = mPassword.getText().toString().trim();
        if (!Utils.isMobileNO(phone)){
            showHintDialog("手机号不合法");
            return;
        }
//        if (!Utils.isPassword(password)){
//            showHintDialog("密码不合法，6-16字符");
//            return;
//        }

        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败");
            return;
        }

        showLoadDialog();

        ServerApi.getLoginData(phone, password, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try{
                    closeDialog();
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result");
                    if (result.equals("success")){
                        String uid = jso.getString("user_id");
                        UserInfo.setUserInfo(phone, phone, uid, password);
                        Toast.makeText(mContext, "登录成功", Toast.LENGTH_SHORT).show();

                        if (isAlter == true){
                            startActivity(new Intent(mContext, MainActivity.class));
                        }
                        getFinish();
                        return;
                    }
                    if (result.equals("error")){
                        String errorMsg = jso.getString("error_remark");

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

    private TextWatcher mUserWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
           //
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String phone = mLoginUser.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            if (phone.length() > 0){
                mPhoneClear.setVisibility(View.VISIBLE);
            }else{
                mPhoneClear.setVisibility(View.GONE);
            }
            updataHintInfo(phone, password);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
        }
    };

    private TextWatcher mPasswordWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String phone = mLoginUser.getText().toString().trim();
            String password = mPassword.getText().toString().trim();
            if (password.length() > 0){
                mpasswordClear.setVisibility(View.VISIBLE);
            }else{
                mpasswordClear.setVisibility(View.GONE);
            }
            updataHintInfo(phone, password);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
        }
    };

    private void updataHintInfo(String phone, String password){
        mAffirm.setClickable(false);
        mAffirm.setBackgroundResource(R.drawable.home_btn_selector_noclick);

        if (!TextUtils.isEmpty(phone) && !TextUtils.isEmpty(password)){
            mAffirm.setClickable(true);
            mAffirm.setBackgroundResource(R.drawable.home_btn_selector);
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

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.iv_login_back:
                getFinish();
                break;
            case R.id.btn_login_affirm:
                getLogin();
                break;
            case R.id.ib_login_user_clear:
                mLoginUser.setText("");
                break;
            case R.id.ib_login_password_clear:
                mPassword.setText("");
                break;
            case R.id.btn_login_register:
                startActivity(new Intent(mContext, RegisterActivity.class));
                break;
            case R.id.btn_login_forget:
                Toast.makeText(mContext, "忘记密码——陈朱海", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
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
