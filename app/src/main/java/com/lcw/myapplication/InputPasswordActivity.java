package com.lcw.myapplication;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

/**
 *
 * @author 刘春旺
 *
 */
public class InputPasswordActivity extends BaseActivity{

    private EditText passwordEt;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_input_password);

        ImageButton close = (ImageButton) findViewById(R.id.ib_input_password_close);
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinish();
            }
        });

        Button forget = (Button) findViewById(R.id.btn_input_password_forget);
        forget.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(mContext, "忘记密码——陈朱海", Toast.LENGTH_SHORT).show();
                getFinish();
            }
        });

        Intent data = getIntent();
        boolean isB = data.getBooleanExtra("ISB", false);
        TextView hint = (TextView) findViewById(R.id.tv_input_password_hint);
        if (isB){
            hint.setText("支付密码与登录密码相同，建议稍后修改");
            hint.setVisibility(View.VISIBLE);
        }else{
            hint.setVisibility(View.GONE);
        }

        passwordEt = (EditText) findViewById(R.id.et_input_password);

        Button mAffirm = (Button) findViewById(R.id.btn_input_password_topup_affirm);
        mAffirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String password = passwordEt.getText().toString().trim();
                Intent data = new Intent(mContext, InvestmentActivity.class);
                if (TextUtils.isEmpty(password)){
                    setResult(InvestmentActivity.RESULT_CODE1, data);
                    getFinish();
                    return;
                }
                data.putExtra("PASSWORD", password);
                setResult(InvestmentActivity.RESULT_CODE2, data);
                getFinish();
            }
        });
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
