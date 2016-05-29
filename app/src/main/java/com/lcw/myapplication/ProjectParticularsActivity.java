package com.lcw.myapplication;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.lcw.myapplication.fragment.ParticularsFragment1;
import com.lcw.myapplication.fragment.ParticularsFragment2;
import com.lcw.myapplication.fragment.ParticularsFragment3;
import com.lcw.myapplication.util.HintDialog2;
import com.lcw.myapplication.util.ServerApi;

import org.json.JSONObject;

/**
 *
 * @author 刘春旺
 *
 */
public class ProjectParticularsActivity extends FragmentActivity implements View.OnClickListener, ViewPager.OnPageChangeListener{

    private Context mContext;
    private ViewPager mViewPager;
    private SparseArray<Fragment> mContentFragments;
    private Fragment mContent;
    private View view1, view2, view3;
    private Button mHintNumber;

    private static final int COUNT = 3;
    /**
     * 借款人信息
     */
    private static final int POS_0 = 0;
    /**
     *风控信息
     */
    private static final int POS_1 = 1;
    /**
     *投资记录
     */
    private static final int POS_2 = 2;
    /**
     * 款项ID
     */
    public String mBorrowNid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;

        requestWindowFeature(Window.FEATURE_NO_TITLE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }

        setContentView(R.layout.activity_proj_parti);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
        }

        ImageButton back = (ImageButton) findViewById(R.id.ib_proj_parti_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinish();
            }
        });
        Button mAffirm = (Button) findViewById(R.id.btn_proj_parti_affirm);
        mAffirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (InvestmentProjectActivity.mActivity != null){
                    InvestmentProjectActivity.mActivity.finish();
                }
                getFinish();
            }
        });

        RelativeLayout rl1 = (RelativeLayout) findViewById(R.id.rl_proj_parti_1);
        RelativeLayout rl2 = (RelativeLayout) findViewById(R.id.rl_proj_parti_2);
        RelativeLayout rl3 = (RelativeLayout) findViewById(R.id.rl_proj_parti_3);
        rl1.setOnClickListener(this);
        rl2.setOnClickListener(this);
        rl3.setOnClickListener(this);

        view1 = findViewById(R.id.view_proj_parti_1);
        view2 = findViewById(R.id.view_proj_parti_2);
        view3 = findViewById(R.id.view_proj_parti_3);

        mContentFragments = new SparseArray<Fragment>();
        mViewPager = (ViewPager) findViewById(R.id.proj_parti_viewpager);

        MyFragmentPagerAdapter fAdapter = new MyFragmentPagerAdapter(getSupportFragmentManager());
        mViewPager.setAdapter(fAdapter);
        mViewPager.setOnPageChangeListener(this);

        mBorrowNid = getIntent().getStringExtra("NID");

        mHintNumber = (Button) findViewById(R.id.btn_proj_parti_hint_number);
        requestData();
    }

    public void showHintDialog(String errorMsg,final boolean isCancelable){
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
            case R.id.rl_proj_parti_1:
                changBg(POS_0);
                mViewPager.setCurrentItem(POS_0);
                break;
            case R.id.rl_proj_parti_2:
                changBg(POS_1);
                mViewPager.setCurrentItem(POS_1);
                break;
            case R.id.rl_proj_parti_3:
                changBg(POS_2);
                mViewPager.setCurrentItem(POS_2);
                break;
            default:
                break;
        }
    }

    private void changBg(int id){
        view1.setVisibility(View.INVISIBLE);
        view2.setVisibility(View.INVISIBLE);
        view3.setVisibility(View.INVISIBLE);
        switch (id) {
            case POS_0:
                view1.setVisibility(View.VISIBLE);
                break;
            case POS_1:
                view2.setVisibility(View.VISIBLE);
                break;
            case POS_2:
                view3.setVisibility(View.VISIBLE);
                break;
            default:
                break;
        }
    }

    private class MyFragmentPagerAdapter extends FragmentPagerAdapter {

        public MyFragmentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            mContent = mContentFragments.get(position);

            switch (position) {
                case POS_0:
                    if (mContent == null) {
                        mContent = new ParticularsFragment1();
                        mContentFragments.put(POS_0, mContent);
                    }
                    ParticularsFragment1 fragment1 = (ParticularsFragment1)mContentFragments.get(POS_0);
                    return fragment1;
                case POS_1:
                    if (mContent == null) {
                        mContent = new ParticularsFragment2();
                        mContentFragments.put(POS_1, mContent);
                    }
                    ParticularsFragment2 fragment2 = (ParticularsFragment2) mContentFragments.get(POS_1);
                    return fragment2;
                case POS_2:
                    if (mContent == null) {
                        mContent = new ParticularsFragment3();
                        mContentFragments.put(POS_2, mContent);
                    }
                    ParticularsFragment3 fragment3 = (ParticularsFragment3) mContentFragments.get(POS_2);
                    return fragment3;
            }
            return null;
        }

        @Override
        public int getCount() {
            return COUNT;
        }

        @SuppressWarnings("deprecation")
        @Override
        public void destroyItem(View container, int position, Object object) {
            // TODO Auto-generated method stub
            super.destroyItem(container, position, object);
        }

    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        //
    }

    @Override
    public void onPageSelected(int position) {
        switch (position) {
            case POS_0:
                changBg(POS_0);
                break;
            case POS_1:
                changBg(POS_1);
                break;
            case POS_2:
                changBg(POS_2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onPageScrollStateChanged(int state) {
        //
    }

    private void requestData() {
//        {
//            "module": "borrow",
//                "request": "&q=getborrowrecord",
//                "result": "success",
//                "tender": {
//            "tendertotal": "4",
//                    "tenderlist": [
//            {
//                "account": "9440.00",
//                    "addtime": "1442536075",
//                    "contents": "快车财富Android",
//                    "username": "18255160737"
//            },
//            {
//                "account": "90.00",
//                    "addtime": "1442536067",
//                    "contents": "快车财富Android",
//                    "username": "18255160737"
//            },
//            {
//                "account": "90.00",
//                    "addtime": "1442536045",
//                    "contents": "快车财富Android",
//                    "username": "18255160737"
//            },
//            {
//                "account": "100.00",
//                    "addtime": "1442490858",
//                    "contents": "",
//                    "username": "18255160737"
//            }
//            ]
//        },
//            "userinfo": {
//            "username": "背对烟花",
//                    "sex": "男",
//                    "born": "1987年02月18日",
//                    "work_city": "未填",
//                    "marry": "已婚",
//                    "income": "10000元以上",
//                    "house": "有商品房（无贷款）",
//                    "is_car": "有",
//                    "shebao": "有",
//                    "borrow_contents": "本人有套住房位于合肥市瑶海区，想要办理抵押借款68万，用于生意资金周转。",
//                    "borrow_use": "本次借款用于销售"
//        },
//            "approve": [
//            {
//                "type_name": "手机验证",
//                    "status": "已通过",
//                    "addtime": "2015-06-12"
//            },
//            {
//                "type_name": "实名认证",
//                    "status": "已通过",
//                    "addtime": "2015-06-12"
//            },
//            {
//                "type_name": "房产证",
//                    "status": "已通过",
//                    "addtime": "2015-06-12"
//            },
//            {
//                "type_name": "身份证",
//                    "status": "已通过",
//                    "addtime": "2015-06-12"
//            }
//            ]
//        }

        ServerApi.getLendPersonData(mBorrowNid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result");
                    if (result.equals("success")) {
                        JSONObject jso2 = new JSONObject(jso.getString("tender"));

                        int tendertotal = Integer.parseInt(jso2.getString("tendertotal").toString().trim());
                        if (tendertotal > 0){
                            mHintNumber.setVisibility(View.VISIBLE);
                            mHintNumber.setText(tendertotal + "");
                        }
                        return;
                    }
                    if (result.equals("error")) {
                        //
                        return;
                    }
                } catch (Exception e) {
                    //
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                //
            }
        });
    }

}
