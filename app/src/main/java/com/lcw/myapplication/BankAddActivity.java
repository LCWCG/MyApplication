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
public class BankAddActivity extends BaseActivity {

    private EditText mBlockEt;
    private Button mNext;
    private ImageButton mBlockClear;
    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bank_add);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
        }

        ImageButton mBack = (ImageButton) findViewById(R.id.ib_ba_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinish();
            }
        });

        Button mSupport = (Button) findViewById(R.id.ib_ba_support);
        mSupport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, SupportBankActivity.class));
            }
        });

        mBlockEt = (EditText) findViewById(R.id.et_ba_1_block);
//        mBlockEt.setInputType(EditorInfo.TYPE_CLASS_PHONE);
        mBlockEt.addTextChangedListener(mBlockWatcher);

        mBlockClear = (ImageButton) findViewById(R.id.ib_ba_1_block_clear);
        mBlockClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mBlockEt.setText("");
            }
        });

        TextView name = (TextView) findViewById(R.id.tv_ba_1_name);
        name.setText(getIntent().getStringExtra("NAME"));

        mNext = (Button) findViewById(R.id.et_ba_1_next);
        mNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hintKeyboard();

                String block = mBlockEt.getText().toString().trim();
                if (!Utils.isNumeric(block) || block.length() < 14 || block.length() > 21) {
                    showHintDialog("银行卡格式不合法", true);
                    return;
                }
                addBank(block);
            }
        });
        mNext.setClickable(false);
        mNext.setBackgroundResource(R.drawable.home_btn_selector_noclick);
    }

    private TextWatcher mBlockWatcher = new TextWatcher() {

        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            //
        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            String block = mBlockEt.getText().toString().trim();
            if (block.length() > 0){
                mBlockClear.setVisibility(View.VISIBLE);
            }else{
                mBlockClear.setVisibility(View.GONE);
            }
            //4 9 14 19 24
            //1111 1111 1111 1111 1111
            mNext.setClickable(false);
            mNext.setBackgroundResource(R.drawable.home_btn_selector_noclick);

            if (!TextUtils.isEmpty(block) && block.length() >= 14){
                mNext.setClickable(true);
                mNext.setBackgroundResource(R.drawable.home_btn_selector);
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {
            //
        }
    };

    private void hintKeyboard(){
        View view = getWindow().peekDecorView();
        if (view != null) {
            InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    private void addBank(String account){
        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败", true);
            return;
        }
        showLoadDialog();

        ServerApi.getBankAddData(account, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result").trim();
                    if (result.equals("success")){

                        Toast.makeText(mContext, "恭喜您，绑定成功", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(mContext, BankManageActivity.class);
                        setResult(BankManageActivity.RESULT_CODE_1, intent);

                        closeDialog();
                        getFinish();
                        return;
                    }
                    if (result.equals("error")){
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
