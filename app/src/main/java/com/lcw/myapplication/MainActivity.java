package com.lcw.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.lcw.myapplication.model.LoanModel;
import com.lcw.myapplication.model.TopAdModel;
import com.lcw.myapplication.util.HintDialog2;
import com.lcw.myapplication.util.ServerApi;
import com.lcw.myapplication.util.UserInfo;
import com.lcw.myapplication.util.Utils;
import com.lcw.myapplication.view.DonutProgress;
import com.lcw.myapplication.view.GuideGallery;
import com.lcw.myapplication.view.RourdCornerNetworkImageView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 *
 * @author 刘春旺
 *
 */
public class MainActivity extends BaseActivity {

    private static final int MONTH12 = 12;
    private static final int MONTH6 = 6;
    private static final int MONTH3 = 3;
    private static final int MONTH1 = 1;
    private DonutProgress mDonutProgress;
    private TextView mEarnings;
    private Timer mtimer, mtimer2;
    private GuideGallery mGuideGallery;
    private LinearLayout mContainer;
    private CountDownTimer mCountDownTimer = null;
    private int mRealityPosition = 0;
    private int mPreSelImgIndex = 0;
    private Button m12Month, m6Month, m3Month, m1Month,
            m12MonthUp, m6MonthUp, m3MonthUp, m1MonthUp,
            mInvestment;
    private static List<TopAdModel> mTopAdList = new ArrayList<TopAdModel>();
    private static List<LoanModel> mLoanList = new ArrayList<LoanModel>();
    private static int PER = 90;
    private java.text.DecimalFormat df = new java.text.DecimalFormat("#0.00");
    private static int selectorMonth = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        initView();
        loadADData();

        m12Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLoanList != null && mLoanList.size() > 0) {
                    updateBackgroundColor(MONTH12);
                    startTimerDownloadView(Double.parseDouble(mLoanList.get(0).getBorrow_apr()));
                    if (mLoanList.get(0).getBorrow_nid().trim().equals("0")) {
                        timer();
                    }else{
                        stopCountDownTimer();
                    }
                }
            }
        });
        m6Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLoanList != null && mLoanList.size() > 0){
                    updateBackgroundColor(MONTH6);
                    startTimerDownloadView(Double.parseDouble(mLoanList.get(1).getBorrow_apr()));
                    if (mLoanList.get(1).getBorrow_nid().trim().equals("0")) {
                        timer();
                    }else{
                        stopCountDownTimer();
                    }
                }
            }
        });
        m3Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLoanList != null && mLoanList.size() > 0){
                    updateBackgroundColor(MONTH3);
                    startTimerDownloadView(Double.parseDouble(mLoanList.get(2).getBorrow_apr()));
                    if (mLoanList.get(2).getBorrow_nid().trim().equals("0")) {
                        timer();
                    }else{
                        stopCountDownTimer();
                    }
                }
            }
        });
        m1Month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mLoanList != null && mLoanList.size() > 0){
                    updateBackgroundColor(MONTH1);
                    startTimerDownloadView(Double.parseDouble(mLoanList.get(3).getBorrow_apr()));
                    if (mLoanList.get(3).getBorrow_nid().trim().equals("0")) {
                        timer();
                    }else{
                        stopCountDownTimer();
                    }
                }
            }
        });
        mInvestment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (selectorMonth) {
                    case MONTH12:
                        startInvestment(mLoanList.get(0).getBorrow_apr(), mLoanList.get(0).getBorrow_nid());
                        break;
                    case MONTH6:
                        startInvestment(mLoanList.get(1).getBorrow_apr(), mLoanList.get(1).getBorrow_nid());
                        break;
                    case MONTH3:
                        startInvestment(mLoanList.get(2).getBorrow_apr(), mLoanList.get(2).getBorrow_nid());
                        break;
                    case MONTH1:
                        startInvestment(mLoanList.get(3).getBorrow_apr(), mLoanList.get(3).getBorrow_nid());
                        break;
                    default:
                        break;
                }
            }
        });

        //退出登陆
        Button btn1 = (Button) findViewById(R.id.test_exitlogin);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserInfo.exitLogin();
                Toast.makeText(mContext, "已退出登录", Toast.LENGTH_SHORT).show();
            }
        });
        //银行卡 绑定
        Button btn2 = (Button) findViewById(R.id.test_yinhangka);
        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserInfo.isLogin()) {
                    startActivity(new Intent(mContext, BankManageActivity.class));
                } else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
            }
        });
        //修改密码
        Button btn3 = (Button) findViewById(R.id.test_gaimima);
        btn3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (UserInfo.isLogin()) {
                    startActivity(new Intent(mContext, AlterPasswordActivity.class));
                } else {
                    startActivity(new Intent(mContext, LoginActivity.class));
                }
            }
        });
    }

    private void initView(){
        mGuideGallery = (GuideGallery) findViewById(R.id.gg_home_guidegallery);
        mContainer = (LinearLayout) findViewById(R.id.ll_home_container);
        m12Month = (Button) findViewById(R.id.btn_home_12month);
        m6Month = (Button) findViewById(R.id.btn_home_6month);
        m3Month = (Button) findViewById(R.id.btn_home_3month);
        m1Month = (Button) findViewById(R.id.btn_home_1month);
        m12MonthUp = (Button) findViewById(R.id.btn_home_12month_up);
        m6MonthUp = (Button) findViewById(R.id.btn_home_6month_up);
        m3MonthUp = (Button) findViewById(R.id.btn_home_3month_up);
        m1MonthUp = (Button) findViewById(R.id.btn_home_1month_up);
        mDonutProgress = (DonutProgress) findViewById(R.id.dp_home_donutprogress);
        mEarnings = (TextView) findViewById(R.id.tv_home_earnings);
        mInvestment = (Button) findViewById(R.id.btn_home_investment);
    }

    private void timer(){
        try{
            SimpleDateFormat formatter1 = new SimpleDateFormat("yyyy-MM-dd");
            long current = System.currentTimeMillis();
            Date curDate = new Date(current);
            String ymd = formatter1.format(curDate);

            SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String t9 = ymd + " 09:00:00";
            String t15 = ymd + " 15:00:00";
            String t24 = ymd + " 23:59:59";
            long time9 = formatter2.parse(t9).getTime();
            long time15 = formatter2.parse(t15).getTime();
            long time24 = formatter2.parse(t24).getTime();
            if (current < time9){
                startCountDownTimer((time9 - current) / 1000);
                return;
            }
            if (current > time15){
                startCountDownTimer((time24 - current + 9*60*60*1000)/1000);
                return;
            }
            if (current < time15){
                startCountDownTimer((time15 - current) / 1000);
                return;
            }
        }catch (Exception e){
            //
        }
    }

    private String format(long ms) {
        int mi = 60;
        int hh = mi * 60;

        long hour = ms / hh;
        long minute = (ms - hour * hh) / mi;
        long second = ms - hour * hh - minute * mi;

        String strHour = hour < 10 ? "0" + hour : "" + hour;
        String strMinute = minute < 10 ? "0" + minute : "" + minute;
        String strSecond = second < 10 ? "0" + second : "" + second;
        return"倒计时:" + strHour + "时" + strMinute + "分" + strSecond + "秒";
    }

    private long mTime = 0;
    private void startCountDownTimer(long time){
        if (mtimer2 != null) {
            mtimer2.cancel();
            mtimer2 = null;
        }
        mTime = time;

        mtimer2 = new Timer();
        mtimer2.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mTime--;
                        if (mTime <= 0) {
                            mInvestment.setText("立即投资");
                            mInvestment.setBackgroundResource(R.drawable.home_btn_selector);
                            mInvestment.setClickable(true);
                        } else {
                            mInvestment.setText(format(mTime));
                            mInvestment.setBackgroundResource(R.drawable.home_btn_selector_noclick);
                            mInvestment.setClickable(false);
                        }
                    }
                });
            }
        }, 0, 1000);
    }

    private void stopCountDownTimer(){
        if (mtimer2 != null) {
            mtimer2.cancel();
            mtimer2 = null;
        }
        mInvestment.setText("立即投资");
        mInvestment.setBackgroundResource(R.drawable.home_btn_selector);
        mInvestment.setClickable(true);
    }

    private void startInvestment(String borrow_apr, String borrow_nid){
        if (UserInfo.isLogin()){
            Intent intent = new Intent(mContext, InvestmentActivity.class);
            intent.putExtra("APR", borrow_apr);
            intent.putExtra("NID", borrow_nid);
            startActivity(intent);
        }else{
            startActivity(new Intent(mContext, LoginActivity.class));
        }
    }

    private void updateBackgroundColor(int id){
        selectorMonth = id;
        m12MonthUp.setVisibility(View.GONE);
        m6MonthUp.setVisibility(View.GONE);
        m3MonthUp.setVisibility(View.GONE);
        m1MonthUp.setVisibility(View.GONE);
        switch (id){
            case MONTH12:
                m12MonthUp.setVisibility(View.VISIBLE);
                break;
            case MONTH6:
                m6MonthUp.setVisibility(View.VISIBLE);
                break;
            case MONTH3:
                m3MonthUp.setVisibility(View.VISIBLE);
                break;
            case MONTH1:
                m1MonthUp.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private void startTimerDownloadView(final double progressCount){
        if (mtimer != null) {
            mtimer.cancel();
            mtimer = null;
        }
        mDonutProgress.setProgress(0);
        mEarnings.setText("00.00");

        final double pro = progressCount/PER;

        mtimer = new Timer();
        mtimer.schedule(new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        int progress = mDonutProgress.getProgress() + 1;
                        double progressFont = progress * pro;

                        if (PER > progress) {
                            mDonutProgress.setProgress(progress);
                            mEarnings.setText(df.format(progressFont));
                        } else {
                            mEarnings.setText(progressCount + "");
                        }
                    }
                });
            }
        }, 100, 10);
    }

    private void initTopADData() {
        initFocusIndicatorContainer();
        mGuideGallery.setAdapter(new TopAdAdapter());

        mGuideGallery.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> arg0, View arg1, int selIndex, long arg3) {
                mRealityPosition = selIndex;
                selIndex = selIndex % mTopAdList.size();
                //pre
                ImageView preSelImg = (ImageView) mContainer.findViewById(mPreSelImgIndex);
                preSelImg.setImageDrawable(getResources().getDrawable(R.drawable.home_ic_focus));
                //cur
                ImageView curSelImg = (ImageView) mContainer.findViewById(selIndex);
                curSelImg.setImageDrawable(getResources().getDrawable(R.drawable.home_ic_focus_select));

                mPreSelImgIndex = selIndex;
            }

            public void onNothingSelected(AdapterView<?> arg0) {
                //
            }
        });

        mGuideGallery.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                position = position % mTopAdList.size();
                String url = mTopAdList.get(position).getUrl();
                String name = mTopAdList.get(position).getName();
                Toast.makeText(mContext, url + name, Toast.LENGTH_SHORT).show();
            }
        });

        mRealityPosition = mTopAdList.size() * 100 - 1;
        adTimerState(true);
    }

    private void adTimerState(boolean isOpen) {
        if (isOpen) {
            if (mCountDownTimer != null || mTopAdList == null || mTopAdList.size() <= 0)
                return;
            mCountDownTimer = new CountDownTimer(10000000, 5000) {

                @Override
                public void onTick(long millisUntilFinished) {
                    mGuideGallery.setSelection(++mRealityPosition);
                }

                @Override
                public void onFinish() {
                    //
                }
            };
            mCountDownTimer.start();
        } else {
            if (mCountDownTimer != null) {
                mCountDownTimer.cancel();
                mCountDownTimer = null;
            }
        }
    }

    private void initFocusIndicatorContainer() {
        for (int i = 0; i < mTopAdList.size(); i++) {
            ImageView localImageView = new ImageView(this);
            localImageView.setId(i);
            LinearLayout.LayoutParams localLayoutParams = new LinearLayout.LayoutParams(30, 30);
            localImageView.setLayoutParams(localLayoutParams);
            localImageView.setPadding(8, 8, 8, 8);
            localImageView.setImageResource(R.drawable.home_ic_focus);
            mContainer.addView(localImageView);
        }
    }

    private void loadADData() {
        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败");
        }

        showLoadDialog();
        ServerApi.getHomeTopADData(10, 1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    if (mTopAdList.size() > 0) {
                        mTopAdList.clear();
                    }
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result");
                    if (result.equals("success")) {
                        JSONArray jsa = new JSONArray(jso.getString(TopAdModel.CONTENT));
                        TopAdModel topAdModel = null;
                        for (int i = 0; i < jsa.length(); i++) {
                            topAdModel = new TopAdModel();
                            JSONObject jso2 = jsa.getJSONObject(i);
                            topAdModel.setUrl(jso2.getString(TopAdModel.URL));
                            topAdModel.setPic(jso2.getString(TopAdModel.PIC));
                            topAdModel.setName(jso2.getString(TopAdModel.NAME));
                            mTopAdList.add(topAdModel);
                            topAdModel = null;
                        }
                        initTopADData();

                        requestInvestmentData();
                        return;
                    }
                    if (result.equals("error")) {

                        String errorMsg = jso.getString("error_remark");
                        showHintDialog(errorMsg);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();

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

    private void requestInvestmentData(){
        ServerApi.getHomeLoanData(1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    closeDialog();

                    if (mLoanList.size() > 0) {
                        mLoanList.clear();
                    }
                    JSONObject jso = new JSONObject(response);

                    String result = jso.getString("result");
                    if (result.equals("success")){
                        JSONObject jsoList = jso.getJSONObject(LoanModel.LIST);
                        LoanModel loanModel = null;
                        JSONObject jso12 = jsoList.getJSONObject(LoanModel.YEAR);
                        loanModel = new LoanModel();
                        loanModel.setBorrow_apr(jso12.getString(LoanModel.BORROW_APR));
                        loanModel.setBorrow_nid(jso12.getString(LoanModel.BORROW_NID));
                        mLoanList.add(loanModel);
                        loanModel = null;
                        JSONObject jso6 = jsoList.getJSONObject(LoanModel.HALF);
                        loanModel = new LoanModel();
                        loanModel.setBorrow_apr(jso6.getString(LoanModel.BORROW_APR));
                        loanModel.setBorrow_nid(jso6.getString(LoanModel.BORROW_NID));
                        mLoanList.add(loanModel);
                        loanModel = null;
                        JSONObject jso3 = jsoList.getJSONObject(LoanModel.QUARTER);
                        loanModel = new LoanModel();
                        loanModel.setBorrow_apr(jso3.getString(LoanModel.BORROW_APR));
                        loanModel.setBorrow_nid(jso3.getString(LoanModel.BORROW_NID));
                        mLoanList.add(loanModel);
                        loanModel = null;
                        JSONObject jso1 = jsoList.getJSONObject(LoanModel.SPIRIT);
                        loanModel = new LoanModel();
                        loanModel.setBorrow_apr(jso1.getString(LoanModel.BORROW_APR));
                        loanModel.setBorrow_nid(jso1.getString(LoanModel.BORROW_NID));
                        mLoanList.add(loanModel);
                        loanModel = null;

                        if (mLoanList != null && mLoanList.size() > 0) {
                            updateBackgroundColor(12);
                            startTimerDownloadView(Double.parseDouble(mLoanList.get(0).getBorrow_apr()));
                            if (mLoanList.get(0).getBorrow_nid().trim().equals("0")) {
                                timer();
                            }
                        }
                        return;
                    }
                    if (result.equals("error")) {

                        String errorMsg = jso.getString("error_remark");
                        showHintDialog(errorMsg);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();

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

    private class TopAdAdapter extends BaseAdapter{

        public int getCount() {
            return Integer.MAX_VALUE;
        }

        public Object getItem(int position) {
            return mTopAdList.get(position);
        }

        public long getItemId(int position) {
            return position;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            RourdCornerNetworkImageView imageView = new RourdCornerNetworkImageView(mContext);
            imageView.setAdjustViewBounds(true);
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            imageView.setLayoutParams(new Gallery.LayoutParams(Gallery.LayoutParams.MATCH_PARENT, Gallery.LayoutParams.MATCH_PARENT));

            position = position % mTopAdList.size();
            imageView.setDefaultImageResId(R.drawable.imageloader);
            String image_url = ServerApi.IMG_URL + mTopAdList.get(position).getPic();
            imageView.setImageUrl(image_url, ServerApi.getImageLoader());

            return imageView;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        adTimerState(true);
    }

    @Override
    protected void onPause() {
        super.onPause();

        adTimerState(false);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (mtimer != null) {
            mtimer.cancel();
            mtimer = null;
        }
    }

}

