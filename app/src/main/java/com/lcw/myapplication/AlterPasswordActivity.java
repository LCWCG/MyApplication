package com.lcw.myapplication;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;

import com.lcw.myapplication.lock.LockPatternActivity;
import com.lcw.myapplication.lock.LockPatternUtils;
import com.lcw.myapplication.util.ConfigUtil;
import com.lcw.myapplication.view.ToggleButton;

/**
 *
 * @author 刘春旺
 *
 */
public class AlterPasswordActivity extends BaseActivity{

    private ToggleButton mToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_alter_password);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
        }

        ImageButton back = (ImageButton) findViewById(R.id.ib_alter_password_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinish();
            }
        });

        mToggle = (ToggleButton) findViewById(R.id.tb_control_switch);

        findViewById(R.id.rl_alter_pass_login).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, AlterLoginPasswordActivity.class));
            }
        });

        findViewById(R.id.rl_alter_pass_pay).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(mContext, AlterPayPasswordActivity.class));
            }
        });
    }

    private void initData(){
        boolean isState = MyApplication.cfg.getBooleanShareData(ConfigUtil.IS_NEED_SCREEN_PASS,false);
        if (isState){
            mToggle.setToggle(true, false);
        }else{
            mToggle.setToggle(false, false);
        }
        mToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean toggleOn = mToggle.getToggleOn();
                if (toggleOn) {
                    MyApplication.cfg.storeBooleanShareData(ConfigUtil.IS_NEED_SCREEN_PASS, false);
                    MyApplication.cfg.commit();

                    mToggle.toggleOff();
                } else {
                    mToggle.toggleOn();
                    showLockPattern(LockPatternActivity.TYPE_SET);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        initData();
    }

    /**
     * 设置手势密码
     *
     * @param type
     */
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
