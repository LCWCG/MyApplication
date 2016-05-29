package com.lcw.myapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.lcw.myapplication.lock.LockPatternActivity;
import com.lcw.myapplication.lock.LockPatternUtils;
import com.lcw.myapplication.util.ConfigUtil;
import com.lcw.myapplication.util.UserInfo;

/**
 *
 * @author 刘春旺
 *
 */
public class LaunchActivity extends Activity{

    /*private Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1){
                if (UserInfo.isLogin()){
                    showLockPattern(LaunchActivity.this, LockPatternActivity.TYPE_HOMECHECK);
                }else{
                    Intent it = new Intent();
                    it.setClassName(LaunchActivity.this, MainActivity.class.getName());
                    startActivity(it);
                    LaunchActivity.this.finish();
                }
            }
        }
    };
*/
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_launch);


        /*new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Message msg = new Message();
                msg.what = 1;
                mHandler.sendEmptyMessage(msg.what);
            }

        }).start();*/

        init();
    }

    private void init() {

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
//        RelativeLayout layout = (RelativeLayout) findViewById(R.id.rl_launch);
//        layout.startAnimation(animation);
        ImageView iv = (ImageView) findViewById(R.id.iv_launch);
        iv.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation animation) {
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                if (UserInfo.isLogin()){
                    showLockPattern(LaunchActivity.this, LockPatternActivity.TYPE_HOMECHECK);
                }else{
                    Intent it = new Intent();
                    it.setClassName(LaunchActivity.this, MainActivity.class.getName());
                    startActivity(it);
                    LaunchActivity.this.finish();
                }
            }
        });
    }

    private  void showLockPattern(Activity ctx, int type) {

        boolean flag = MyApplication.cfg.getBooleanShareData(ConfigUtil.IS_NEED_SCREEN_PASS,false);

        LockPatternUtils lockPatternUtils = new LockPatternUtils(ctx);
        String locknum = lockPatternUtils.getLockPaternString();
        if (flag && !TextUtils.isEmpty(locknum)) {
            Intent it = new Intent();
            it.setClassName(ctx, LockPatternActivity.class.getName());
            it.putExtra(LockPatternActivity.BUNDLE_SET, type);
            ctx.startActivity(it);
        } else {
            Intent it = new Intent();
            it.setClassName(ctx, MainActivity.class.getName());
            startActivity(it);
        }

        LaunchActivity.this.finish();
    }

}
