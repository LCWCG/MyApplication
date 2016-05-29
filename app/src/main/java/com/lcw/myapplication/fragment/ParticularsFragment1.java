package com.lcw.myapplication.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.lcw.myapplication.ProjectParticularsActivity;
import com.lcw.myapplication.R;
import com.lcw.myapplication.util.ServerApi;
import com.lcw.myapplication.util.Utils;

import org.json.JSONObject;

/**
 *
 * @author 刘春旺
 *
 */
public class ParticularsFragment1 extends Fragment{

    private Context mContext;
    private ProjectParticularsActivity mPPActivity;
    private TextView mNameTv, mSexTv, mIncomeTv, mHouseTv, mSheBaoTv,
            mMarryTv, mCityTv, mCarTv;
    private ProgressBar mPb;
    private LinearLayout mLl;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mContext = getActivity();
        if (getActivity() instanceof ProjectParticularsActivity){
            mPPActivity = (ProjectParticularsActivity) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_particulars_1, null);

        mNameTv = (TextView) view.findViewById(R.id.tv_fragment_particulars_1_name);
        mSexTv = (TextView) view.findViewById(R.id.tv_fragment_particulars_1_sex);
        mIncomeTv = (TextView) view.findViewById(R.id.tv_fragment_particulars_1_income);
        mHouseTv = (TextView) view.findViewById(R.id.tv_fragment_particulars_1_house);
        mSheBaoTv = (TextView) view.findViewById(R.id.tv_fragment_particulars_1_shebao);
        mMarryTv = (TextView) view.findViewById(R.id.tv_fragment_particulars_1_marry);
        mCityTv = (TextView) view.findViewById(R.id.tv_fragment_particulars_1_work_city);
        mCarTv = (TextView) view.findViewById(R.id.tv_fragment_particulars_1_car);

        mPb = (ProgressBar) view.findViewById(R.id.pb_fragment_particulars_1);
        mLl = (LinearLayout) view.findViewById(R.id.ll_fragment_particulars_1);

        requestLendPersonData();

        return view;
    }

    private void loadViewData(String username, String sex, String income, String house, String shebao,
                              String marry, String work_city, String is_car){
        mNameTv.setText(username);
        mSexTv.setText(sex);
        mIncomeTv.setText(income);
        mHouseTv.setText(house);
        mSheBaoTv.setText(shebao);
        mMarryTv.setText(marry);
        mCityTv.setText(work_city);
        mCarTv.setText(is_car);
    }

    private void showHintView(){
        mPb.setVisibility(View.VISIBLE);
        mLl.setVisibility(View.GONE);
    }

    private void closeHintView(){
        mPb.setVisibility(View.GONE);
        mLl.setVisibility(View.VISIBLE);
    }

    private void requestLendPersonData() {
        if (!Utils.isNetworkConnected(mContext)) {
            mPPActivity.showHintDialog("网络连接失败", false);
            return;
        }
        showHintView();

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

        ServerApi.getLendPersonData(mPPActivity.mBorrowNid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result");
                    if (result.equals("success")) {
                        JSONObject jso2 = new JSONObject(jso.getString("userinfo"));
                        String username = jso2.getString("username");
                        String sex = jso2.getString("sex");
                        String income = jso2.getString("income");
                        String house = jso2.getString("house");
                        String shebao = jso2.getString("shebao");
                        String marry = jso2.getString("marry");
                        String work_city = jso2.getString("work_city");
                        String is_car = jso2.getString("is_car");

                        loadViewData(username, sex, income, house, shebao, marry, work_city, is_car);

                        closeHintView();
                        return;
                    }
                    if (result.equals("error")) {
                        String errorMsg = jso.getString("error_remark");

                        mPPActivity.showHintDialog(errorMsg, false);
                        return;
                    }
                } catch (Exception e) {

                    mPPActivity.showHintDialog("请求错误", false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mPPActivity.showHintDialog("请求错误", false);
            }
        });
    }

}
