package com.lcw.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
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
public class AlterLoginPasswordActivity extends BaseActivity{

    private Button mAffirm;
    private EditText mOldPassEt;
    private EditText mNewPassEt;
    private EditText mNewPassAffirmEt;
    private ImageButton mClearOldP, mClearNewP, mClearnewPAff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alter_login_password);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
        }

        ImageButton back = (ImageButton) findViewById(R.id.ib_alter_login_password_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinish();
            }
        });

        findViewById(R.id.btn_alp_forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "忘记密码——陈朱海", Toast.LENGTH_SHORT).show();
            }
        });

        mAffirm = (Button) findViewById(R.id.btn_alp_affirm);
        mAffirm.setClickable(false);
        mAffirm.setBackgroundResource(R.drawable.home_btn_selector_noclick);
        mAffirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestData();
            }
        });

        mOldPassEt = (EditText) findViewById(R.id.et_alp_old_p);
        mNewPassEt = (EditText) findViewById(R.id.et_alp_new_p);
        mNewPassAffirmEt = (EditText) findViewById(R.id.et_alp_new_p_affirm);

        mClearOldP = (ImageButton) findViewById(R.id.ib_alp_old_p_clear);
        mClearNewP = (ImageButton) findViewById(R.id.ib_alp_new_p_clear);
        mClearnewPAff = (ImageButton) findViewById(R.id.ib_alp_new_p_aff_clear);

        mClearOldP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mOldPassEt.setText("");
            }
        });

        mClearNewP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewPassEt.setText("");
            }
        });

        mClearnewPAff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNewPassAffirmEt.setText("");
            }
        });

        mOldPassEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String oldP = mOldPassEt.getText().toString().trim();
                String newP = mNewPassEt.getText().toString().trim();
                String newPAff = mNewPassAffirmEt.getText().toString().trim();
                if (oldP.length() > 0){
                    mClearOldP.setVisibility(View.VISIBLE);
                }else{
                    mClearOldP.setVisibility(View.GONE);
                }
                updataHintInfo(oldP, newP, newPAff);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mNewPassEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String oldP = mOldPassEt.getText().toString().trim();
                String newP = mNewPassEt.getText().toString().trim();
                String newPAff = mNewPassAffirmEt.getText().toString().trim();
                if (newP.length() > 0){
                    mClearNewP.setVisibility(View.VISIBLE);
                }else{
                    mClearNewP.setVisibility(View.GONE);
                }
                updataHintInfo(oldP, newP, newPAff);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        mNewPassAffirmEt.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String oldP = mOldPassEt.getText().toString().trim();
                String newP = mNewPassEt.getText().toString().trim();
                String newPAff = mNewPassAffirmEt.getText().toString().trim();
                if (newPAff.length() > 0){
                    mClearnewPAff.setVisibility(View.VISIBLE);
                }else{
                    mClearnewPAff.setVisibility(View.GONE);
                }
                updataHintInfo(oldP, newP, newPAff);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void updataHintInfo(String oldP, String newP, String newPAff){
        mAffirm.setClickable(false);
        mAffirm.setBackgroundResource(R.drawable.home_btn_selector_noclick);

        if (!TextUtils.isEmpty(oldP) && !TextUtils.isEmpty(newP) && !TextUtils.isEmpty(newPAff)){
            mAffirm.setClickable(true);
            mAffirm.setBackgroundResource(R.drawable.home_btn_selector);
        }
    }

    private void requestData(){
        hintKeyboard();
        String oldP = mOldPassEt.getText().toString().trim();
        final String newP = mNewPassEt.getText().toString().trim();
        final String newPAff = mNewPassAffirmEt.getText().toString().trim();

        if (!newP.equals(newPAff)){
            showHintDialog("两次密码输入不相同，请重新输入");
            return;
        }

        if (!Utils.isPassword(newP)){
            showHintDialog("密码不合法，6-16字符");
            return;
        }

        if (!Utils.isPassword(newPAff)){
            showHintDialog("密码不合法，6-16字符");
            return;
        }

        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败");
            return;
        }

        showLoadDialog();

        ServerApi.getAlterLoginPassData(oldP, newP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    closeDialog();
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result");
                    if (result.equals("success")) {
                        UserInfo.setUserInfo(null, null, null, newPAff);
                        Toast.makeText(mContext, "密码修改成功", Toast.LENGTH_SHORT).show();

                        getFinish();
                        return;
                    }
                    if (result.equals("error")) {
                        String errorMsg = jso.getString("error_remark");

                        showHintDialog(errorMsg);
                    }
                } catch (Exception e) {

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

    private ProgressDialog mProgressDialog;

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
