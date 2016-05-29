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
import com.lcw.myapplication.util.Utils;

import org.json.JSONObject;

import java.net.URLEncoder;

/**
 *
 * @author 刘春旺
 *
 */
public class IDApproveActivity extends BaseActivity{

    private EditText mNameEt, mIDEt;
    private ImageButton mNameClear, mIDClear;
    private Button mAffirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_approve);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
        }

        ImageButton mBack = (ImageButton) findViewById(R.id.ib_approve_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinish();
            }
        });

        mNameEt = (EditText) findViewById(R.id.et_approve_name);
        mNameEt.addTextChangedListener(mNameWatcher);

        mIDEt = (EditText) findViewById(R.id.et_approve_id);
        mIDEt.addTextChangedListener(mIDWatcher);

        mNameClear = (ImageButton) findViewById(R.id.ib_approve_name_clear);
        mNameClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mNameEt.setText("");
            }
        });

        mIDClear = (ImageButton) findViewById(R.id.ib_approve_id_clear);
        mIDClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mIDEt.setText("");
            }
        });

        mAffirm = (Button) findViewById(R.id.btn_approve_affirm);
        mAffirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hintKeyboard();

                String name = mNameEt.getText().toString().trim();
                String id = mIDEt.getText().toString().trim();
                requestData(id, name);
            }
        });
        mAffirm.setClickable(false);
        mAffirm.setBackgroundResource(R.drawable.home_btn_selector_noclick);

        loadUserInfo();
    }

    private void loadUserInfo(){
        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败", false);
            return;
        }
        showLoadDialog();

        ServerApi.getUserInfoData(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    closeDialog();

                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result").trim();
                    if (result.equals("success")) {
                        String state = jso.getString("realname_status").trim();
                        if (state.equals("1")) {
                            mNameEt.setText(jso.getString("realname").trim());
                            mIDEt.setText(jso.getString("card_id").trim());
                            return;
                        }
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

    private void requestData(String card_id, final String realname){
        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败", true);
            return;
        }
        showLoadDialog();

        String realname2 = null;
        try{
            realname2 = URLEncoder.encode(realname, "UTF-8");
        }catch (Exception e){
            //
        }

        ServerApi.getApproveData(card_id, realname2, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result").trim();
                    if (result.equals("success")) {

                        Toast.makeText(mContext, "恭喜您，身份认证成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, BankManageActivity.class);
                        intent.putExtra("NAME", realname);
                        setResult(BankManageActivity.RESULT_CODE_2, intent);

                        closeDialog();
                        getFinish();
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

    private TextWatcher mNameWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String name = mNameEt.getText().toString().trim();
            String id = mIDEt.getText().toString().trim();
            if (name.length() > 0){
                mNameClear.setVisibility(View.VISIBLE);
            }else{
                mNameClear.setVisibility(View.GONE);
            }
            updataHintInfo(name, id);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
        }
    };

    private TextWatcher mIDWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String name = mNameEt.getText().toString().trim();
            String id = mIDEt.getText().toString().trim();
            if (id.length() > 0){
                mIDClear.setVisibility(View.VISIBLE);
            }else{
                mIDClear.setVisibility(View.GONE);
            }
            updataHintInfo(name, id);
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
        }
    };

    private void updataHintInfo(String name, String id){
        mAffirm.setClickable(false);
        mAffirm.setBackgroundResource(R.drawable.home_btn_selector_noclick);

        if (!TextUtils.isEmpty(name) && !TextUtils.isEmpty(id)){
            mAffirm.setClickable(true);
            mAffirm.setBackgroundResource(R.drawable.home_btn_selector);
        }
    }

    private void hintKeyboard(){
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
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

}
