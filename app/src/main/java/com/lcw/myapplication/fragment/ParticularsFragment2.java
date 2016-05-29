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
import com.lcw.myapplication.view.RourdCornerNetworkImageView;

import org.json.JSONObject;

/**
 *
 * @author 刘春旺
 *
 */
public class ParticularsFragment2 extends Fragment{

    private ProgressBar mPb;
    private LinearLayout mLl;
    private Context mContext;
    private ProjectParticularsActivity mPPActivity;

    private TextView mCount, mGrade, mType;

    private RourdCornerNetworkImageView mRCNI;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mContext = getActivity();
        if (getActivity() instanceof ProjectParticularsActivity){
            mPPActivity = (ProjectParticularsActivity) getActivity();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_particulars_2, null);

        mPb = (ProgressBar) view.findViewById(R.id.pb_fragment_particulars_2);
        mLl = (LinearLayout) view.findViewById(R.id.ll_fragment_particulars_2);

        mGrade = (TextView) view.findViewById(R.id.tv_fragment_particulars_2_grade);
        mType = (TextView) view.findViewById(R.id.tv_fragment_particulars_2_type);
        mCount = (TextView) view.findViewById(R.id.tv_fragment_particulars_2_count);

        mRCNI = (RourdCornerNetworkImageView) view.findViewById(R.id.rcni_fragment_particulars_2_icon);

        requestAgencyInfoData();

        return view;
    }

    private void loadViewData(String grade, String type, String count, String pic){
        mGrade.setText(grade);
        mType.setText(type);
        mCount.setText(count);

        mRCNI.setDefaultImageResId(R.drawable.inv_proj_8);
        String image_url = ServerApi.IMG_URL + pic;
        mRCNI.setImageUrl(image_url, ServerApi.getImageLoader());
    }

    private void showHintView(){
        mPb.setVisibility(View.VISIBLE);
        mLl.setVisibility(View.GONE);
    }

    private void closeHintView(){
        mPb.setVisibility(View.GONE);
        mLl.setVisibility(View.VISIBLE);
    }

    private void requestAgencyInfoData() {
        if (!Utils.isNetworkConnected(mContext)) {
            mPPActivity.showHintDialog("网络连接失败", false);
            return;
        }
        showHintView();

//        {
//            "module": "borrow",
//                "request": "&q=getagencyinfo",
//                "result": "success",
//                "income": "405",
//                "info": {
//            "id": "12",
//                    "agency_name": "合肥索恒旧机动车经纪有限公司",
//                    "agency_short_name": "索恒旧机动车",
//                    "logo": "data/upfiles/images/2014-06/04/0_agency_diya_1401814761711.jpg",
//                    "intro": "合肥索恒旧机动车经纪有限公司",
//                    "rating": "2",
//                    "create_time": "1401814761"
//        },
//            "score": {
//            "id": "1239",
//                    "common_count": "0",
//                    "money_count": "0",
//                    "stable_count": "0",
//                    "job_count": "0",
//                    "credit_count": "0",
//                    "pawn_count": "0",
//                    "visits_count": "0",
//                    "trial_count": "0",
//                    "total_count": "0",                           //总分
//                    "borrow_nid": "20150600049",
//                    "addtime": "1442475251",
//                    "borrow_pawn_type": "房产",                    //投资类型
//                    "agency_loan_file": "data/upfiles/images/2015-09/17/0_borrow_diya_1442475251619.png",
//                    "borrow_pawn_contract_url": "data/upfiles/images/2015-06/12/0_borrow_diya_1434096409990.jpg",
//                    "pic": "/plugins/pChart/chart.php?common=0&credit=0&money=0&pawn=0&stable=0&visits=0&job=0&trial=0"
//        }
//        }
        ServerApi.getAgencyInfoData(mPPActivity.mBorrowNid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result");
                    if (result.equals("success")) {
                        JSONObject jso2 = new JSONObject(jso.getString("score"));

                        String grade = jso2.getString("nfc_level");
                        String type = jso2.getString("borrow_pawn_type");
                        String count = jso2.getString("total_count");

                        //https://www.haochedai.com//plugins/pChart/chart.php?common=0&credit=0&money=0&pawn=0&stable=0&visits=0&job=0&trial=0
                        String pic = jso2.getString("pic");

                        loadViewData(grade, type, count, pic);

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
