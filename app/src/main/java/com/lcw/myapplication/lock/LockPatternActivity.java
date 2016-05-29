package com.lcw.myapplication.lock;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.lcw.myapplication.LoginActivity;
import com.lcw.myapplication.MainActivity;
import com.lcw.myapplication.MyApplication;
import com.lcw.myapplication.R;

import com.lcw.myapplication.lock.LockPatternView.Cell;
import com.lcw.myapplication.lock.LockPatternView.DisplayMode;
import com.lcw.myapplication.lock.LockPatternView.OnPatternListener;
import com.lcw.myapplication.util.ConfigUtil;
import com.lcw.myapplication.util.HintDialog;
import com.lcw.myapplication.util.UserInfo;

import java.util.List;

/**
 *
 * @author 刘春旺
 *
 */
public class LockPatternActivity extends Activity {

	public static final String BUNDLE_SET = "isSetPwd";

	/**
	 * 设置手势密码
	 */
	public static final int TYPE_SET = 0;
	public static final int TYPE_CHECK = 1;
	/**
	 * 开启手势锁
	 */
	public static final int TYPE_HOMECHECK = 2;


	private static final int InfoStatusFirstTimeSetting = 0;
	private static final int InfoStatusConfirmSetting = 1;
	private static final int InfoStatusFailedConfirm = 2;
	private static final int InfoStatusNormal = 3;
	private static final int InfoStatusFailedMatch = 4;
	private static final int InfoStatusSuccessMatch = 5;

	private int status = 0;
	// private OnPatternListener onPatternListener;

	private LockPatternView lockPatternView;
	private LockPatternUtils lockPatternUtils;
	private int isSetPwd = 2;

	private TextView mTip;
	private ImageButton mBack;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		requestWindowFeature(Window.FEATURE_NO_TITLE);

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
			getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
		}

		setContentView(R.layout.activity_lockpattern);

		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
			this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
		}

		lockPatternView = (LockPatternView) findViewById(R.id.lpv_lock);
		mTip = (TextView) findViewById(R.id.lockpatter_tip);

		Button forgetPwd = (Button) findViewById(R.id.btn_lockpattern_forgetpwd);
		forgetPwd.setOnClickListener(mForgetListener);

		mBack = (ImageButton) findViewById(R.id.ib_lock_pattern_back);
		mBack.setOnClickListener(mBackListener);

		Bundle bundle = this.getIntent().getExtras();
		if(bundle != null){
			isSetPwd = bundle.getInt(BUNDLE_SET);
		}

		if (isSetPwd == TYPE_SET){
			forgetPwd.setVisibility(View.GONE);
		}

		if (isSetPwd == TYPE_HOMECHECK){
			this.findViewById(R.id.ll_top_view).setVisibility(View.INVISIBLE);
			this.findViewById(R.id.ll_top_view_bar).setVisibility(View.INVISIBLE);
			forgetPwd.setVisibility(View.VISIBLE);
		}

		TextView phoneTv = (TextView) this.findViewById(R.id.lockpatter_tip_phone);
		String phone = UserInfo.getPhone();
		phoneTv.setText(phone.substring(0, 3) + "****" + phone.substring(phone.length() - 4, phone.length()));

		switch(isSetPwd){
			case TYPE_SET:
				status = InfoStatusFirstTimeSetting;
				break;
			case TYPE_CHECK:
				status = InfoStatusNormal;
				break;
			case TYPE_HOMECHECK:
				status = InfoStatusNormal;
				break;
		}

		updateOutlook();

		lockPatternUtils = new LockPatternUtils(this);
		lockPatternView.setOnPatternListener(new OnPatternListener() {

			public void onPatternStart() {

			}

			public void onPatternDetected(List<Cell> pattern) {

				switch(status){
					case InfoStatusFirstTimeSetting:
						lockPatternUtils.saveLockPattern(pattern);
						status = InfoStatusConfirmSetting;
						mHandler.sendEmptyMessageDelayed(1, 1000);
						updateOutlook();
						break;

					case InfoStatusFailedConfirm:
						lockPatternUtils.saveLockPattern(pattern);
						status = InfoStatusConfirmSetting;
						mHandler.sendEmptyMessageDelayed(1, 1000);
						updateOutlook();
						break;


					case InfoStatusConfirmSetting:

						int result = lockPatternUtils.checkPattern(pattern);
						if (result!= 1) {
							lockPatternView.setDisplayMode(DisplayMode.Wrong);
							status = InfoStatusFailedConfirm;
							mHandler.sendEmptyMessageDelayed(1, 1000);
							updateOutlook();
						} else {
							//确认成功
							mHandler.sendEmptyMessageDelayed(2, 500);
						}
						break;

					case InfoStatusNormal:
						result = lockPatternUtils.checkPattern(pattern);
						if (result!= 1) {
							lockPatternView.setDisplayMode(DisplayMode.Wrong);
							status = InfoStatusFailedMatch;
							mHandler.sendEmptyMessageDelayed(1, 1000);
							updateOutlook();
						} else {
							//确认成功
							mHandler.sendEmptyMessageDelayed(2, 500);
						}
						break;
					case InfoStatusFailedMatch:
						result = lockPatternUtils.checkPattern(pattern);
						if (result!= 1) {
							lockPatternView.setDisplayMode(DisplayMode.Wrong);
							status = InfoStatusFailedMatch;
							mHandler.sendEmptyMessageDelayed(1, 1000);
							updateOutlook();
						} else {
							//确认成功
							mHandler.sendEmptyMessageDelayed(2, 500);
						}
						break;
					case InfoStatusSuccessMatch:
						//确认成功
						mHandler.sendEmptyMessageDelayed(2, 500);
						break;

				}
			}

			public void onPatternCleared() {

			}

			public void onPatternCellAdded(List<Cell> pattern) {

			}
		});
	}

	private Handler mHandler = new Handler(){
		public void handleMessage(Message msg){
			switch(msg.what){
				case 1:
					lockPatternView.clearPattern();
					break;
				case 2:
					if(isSetPwd == TYPE_SET){
						MyApplication.cfg.storeBooleanShareData(ConfigUtil.IS_NEED_SCREEN_PASS, true);
						MyApplication.cfg.commit();

						Toast.makeText(getApplicationContext(),"设置完成",Toast.LENGTH_SHORT).show();
						finish();
					}
					if (isSetPwd == TYPE_HOMECHECK){
						Intent it = new Intent();
						it.setClassName(LockPatternActivity.this, MainActivity.class.getName());
						startActivity(it);
						finish();
					}
				default:
					super.handleMessage(msg);
			}
		}
	};



	private void updateOutlook(){
		switch (status) {
			case InfoStatusFirstTimeSetting:
				mTip.setText("请设置手势密码");
				break;
			case InfoStatusConfirmSetting:
				mTip.setText("请再次确认手势密码");
				break;
			case InfoStatusFailedConfirm:
				mTip.setText("确认失败，请重试");
				break;
			case InfoStatusNormal:
				mTip.setText("请绘制手势密码");
				break;
			case InfoStatusFailedMatch:
				mTip.setText("输入错误,请重试 !");
				break;
			case InfoStatusSuccessMatch:
				mTip.setText("欢迎进入 !");
				break;
			default:
				break;
		}

	}

	private View.OnClickListener mForgetListener = new View.OnClickListener() {

		@Override
		public void onClick(View v) {
			final HintDialog dialog = new HintDialog(LockPatternActivity.this, "提示", "忘记手势密码，需要重新登录哦~"
					, "重新登录", "退出", R.layout.hintdialoglayout,
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							UserInfo.exitLogin();

							Intent it = new Intent();
							it.setClassName(LockPatternActivity.this, LoginActivity.class.getName());
							it.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
							it.putExtra("IS", "alter");
							startActivity(it);
						}
					},
					new View.OnClickListener() {
						@Override
						public void onClick(View view) {
							LockPatternActivity.this.finish();
						}
					});
			dialog.show();
		}
	};

	private View.OnClickListener mBackListener = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			finish();
		}
	};

	public boolean onKeyUp(int keyCode, KeyEvent event){
		if (keyCode == KeyEvent.KEYCODE_BACK){
			if (isSetPwd == TYPE_SET){
				finish();
			}
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	@Override
	public void onBackPressed() {
		return;
	}
}

