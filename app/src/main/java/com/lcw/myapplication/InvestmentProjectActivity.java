package com.lcw.myapplication;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

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
public class InvestmentProjectActivity extends BaseActivity{

    /**
     * 款项ID
     */
    private String mBorrowNid;
    public static InvestmentProjectActivity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;

        setContentView(R.layout.activity_inv_proj);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
        }

        ImageButton back = (ImageButton) findViewById(R.id.ib_inv_proj_back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinish();
            }
        });

        LinearLayout invProjInfoLl = (LinearLayout) findViewById(R.id.ll_inv_proj_info);
        invProjInfoLl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, ProjectParticularsActivity.class);
                intent.putExtra("NID", mBorrowNid);
                startActivity(intent);
            }
        });

        Button affirm = (Button) findViewById(R.id.btn_inv_proj_affirm);
        affirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinish();
            }
        });

        Intent data = getIntent();
        mBorrowNid = data.getStringExtra("NID");

        requestData();
    }

    private void requestData() {
        if (!Utils.isNetworkConnected(mContext)) {
            showHintDialog("网络连接失败", false);
            return;
        }
        showLoadDialog();
        //        {
//            "module": "borrow",
//                "request": "&q=getborrowone",
//                "result": "success",
//                "name": "房产抵押借款51万（第一期25万）",//标名
//                "user_id": "42041",
//                "borrow_nid": "20150600050",
//                "style_title": "按月付息到期还本",     //还款方式
//                "account": "250000.00",              //借款金额
//                "borrow_apr": "19.20",               //借款年利率
//                "borrow_period_name": "12个月",
//                "borrow_account_scale": "0.00",
//                "account_min": "",
//                "tender_account_min": "50",
//                "borrow_other_time": "574520",
//                "status": "1",
//                "tender_times": "0",
//                "repay_month_account": "4000",
//                "borrow_account_wait": "250000.00",    //剩余投标金额
//                "count_down": "1",
//                "tender_time": "2015-09-16 14:59:14",
//                "borrow_status_nid": "loan",           //借款状态id的属性>>>>full=>满标待审loan=>立即投资repay=>还款中
//                "nowtime": "2015-09-16 17:58:59",
//                "borrow_period": "12",                 //融资期限
//                "borrow_pawn_type": "房产",             //风控措施
//                "agency_id": "12",                     //保障机构id
//                "username": "dgthre",                  //用户名
//                "borrow_type": "year",
//                "is_acrivity": "0",
//                "invest_type": "pawn",
//                "avatarurl": "/data/avatar/42041_avatar_middle.jpg",
//                "roam": {
//            "id": "",
//                    "user_id": "",
//                    "borrow_nid": "",
//                    "account_min": "",
//                    "portion_total": "",
//                    "portion_yes": "",
//                    "portion_wait": "",
//                    "recover_yes": "",
//                    "recover_wait": "",
//                    "voucher": "",
//                    "vouch_style": "",
//                    "borrow_account": "",
//                    "borrow_account_use": "",
//                    "risk": "",
//                    "upfiles_id": "",
//                    "contents": "",
//                    "borrowuser": "",
//                    "borrowaccount": "",
//                    "borrowrepaytype": "",
//                    "borrowperiod": "",
//                    "borrowtime": "",
//                    "interestrate": "",
//                    "signarea": ""
//        },
//            "borrow_contents": "本人有套住房位于合肥市瑶海区，由于需要资金周转，办理抵押借款51万。"
//        }

        ServerApi.getLendMoneyData(mBorrowNid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result");
                    if (result.equals("success")) {
                        String name = jso.getString("name").trim();//标名
                        String style = jso.getString("style_title").trim();//还款方式
                        String apr = jso.getString("borrow_apr").trim();//借款年利率
                        String period = jso.getString("borrow_period").trim();//融资期限
                        String account = jso.getString("account").trim();//借款总额
                        String status = jso.getString("borrow_status_nid").trim();//满标状态 full=>满标待审loan=>立即投资repay=>还款中

                        String pawnType = jso.getString("borrow_pawn_type").trim();//风控措施

                        loadViewData(name, style, apr, period, account, status, pawnType);

                        requestAgencyInfoData();
                        return;
                    }
                    if (result.equals("error")) {
                        String errorMsg = jso.getString("error_remark");
                        showHintDialog(errorMsg, false);
                        return;
                    }
                } catch (Exception e) {
                    showHintDialog("请求错误", false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showHintDialog("请求错误", false);
            }
        });
    }

    private void requestAgencyInfoData() {
//        {
//            "module": "borrow",
//                "request": "&q=getagencyinfo",
//                "result": "success",
//                "income": "1920",
//                "info": {
//                    "id": "12",
//                    "agency_name": "合肥索恒旧机动车经纪有限公司",
//                    "agency_short_name": "索恒旧机动车",                                                                 //保障机构
//                    "logo": "data/upfiles/images/2014-06/04/0_agency_diya_1401814761711.jpg",
//                    "intro": "合肥索恒旧机动车经纪有限公司",
//                    "rating": "2",
//                    "create_time": "1401814761"
//        },
//            "score": {
//            "id": "1237",
//                    "common_count": "0",
//                    "money_count": "0",
//                    "stable_count": "0",
//                    "job_count": "0",
//                    "credit_count": "0",
//                    "pawn_count": "0",
//                    "visits_count": "0",
//                    "trial_count": "0",
//                    "total_count": "0",
//                    "borrow_nid": "20150600050",
//                    "addtime": "1442367259",
//                    "borrow_pawn_type": "房产",                                                                            //投资类型
//                    "agency_loan_file": "data/upfiles/images/2015-09/16/0_borrow_diya_1442367259514.png",
//                    "borrow_pawn_contract_url": "data/upfiles/images/2015-06/12/0_borrow_diya_1434096331126.jpg",
//                    "pic": "/plugins/pChart/chart.php?common=0&credit=0&money=0&pawn=0&stable=0&visits=0&job=0&trial=0"
//        }
//        }
        ServerApi.getAgencyInfoData(mBorrowNid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result");
                    if (result.equals("success")) {
                        JSONObject jso2 = new JSONObject(jso.getString("info"));

                        TextView agencyShortNameTv = (TextView) findViewById(R.id.tv_inv_proj_agency_short_name);
                        agencyShortNameTv.setText("保障机构：" + jso2.getString("agency_short_name"));

                        requestLendPersonData();
                        return;
                    }
                    if (result.equals("error")) {
                        String errorMsg = jso.getString("error_remark");
                        showHintDialog(errorMsg, false);
                        return;
                    }
                } catch (Exception e) {
                    showHintDialog("请求错误", false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showHintDialog("请求错误", false);
            }
        });
    }

    private void requestLendPersonData() {
//        {
//            "module": "borrow",
//                "request": "&q=getborrowrecord",
//                "result": "success",
//                "tender": {
//            "tendertotal": "0",                         //投资人总数
//                    "tenderlist": [
//                                                        //account ..投标金额
                                                           //addtime...投标时间
                                                           //username..投标人
// ]
//        },
//            "userinfo": {
//            "username": "dgthre",
//                    "sex": "男",
//                    "born": "1983年06月13日",
//                    "work_city": "未填",
//                    "marry": "已婚",
//                    "income": "10000元以上",
//                    "house": "有商品房（无贷款）",
//                    "is_car": "无",
//                    "shebao": "有",
//                    "borrow_contents": "本人有套住房位于合肥市瑶海区，由于需要资金周转，办理抵押借款51万。",
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
//                "type_name": "毕业学校",
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

                        TextView personTv = (TextView) findViewById(R.id.tv_inv_proj_person);
                        personTv.setText("已有" + jso2.getString("tendertotal") + "人投资");

                        closeDialog();
                        return;
                    }
                    if (result.equals("error")) {
                        String errorMsg = jso.getString("error_remark");
                        showHintDialog(errorMsg, false);
                        return;
                    }
                } catch (Exception e) {
                    showHintDialog("请求错误", false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                showHintDialog("请求错误", false);
            }
        });
    }

    private void loadViewData(String name, String style, String apr, String period,
                              String account, String status, String pawnType){
        TextView nameTv = (TextView) findViewById(R.id.tv_inv_proj_name);
        nameTv.setText(name);

        Button styleBtn = (Button) findViewById(R.id.btn_inv_proj_style);
        int lengthStyle = style.length();
        if (lengthStyle % 2 == 0){
            style = style.substring(0, lengthStyle / 2) + "\n" + style.substring(lengthStyle / 2, lengthStyle);
        }else{
            style = style.substring(0, lengthStyle / 2 + 1) + "\n" + style.substring(lengthStyle / 2 + 1, lengthStyle);
        }
        styleBtn.setText(style);

        TextView aprTv = (TextView) findViewById(R.id.tv_inv_proj_apr);
        aprTv.setText(apr);
        TextView periodTv = (TextView) findViewById(R.id.tv_inv_proj_period);
        periodTv.setText(period);
        TextView accountTv = (TextView) findViewById(R.id.tv_inv_proj_account);
        accountTv.setText(account);

        Button statusBtn = (Button) findViewById(R.id.btn_inv_proj_status);
        statusBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinish();
            }
        });
        if (status.equals("full")){
            statusBtn.setText("满标待审");
            statusBtn.setClickable(false);
        }
        if (status.equals("loan")){
            statusBtn.setText("立即投资");
            statusBtn.setClickable(true);
        }
        if (status.equals("repay")){
            statusBtn.setText("还款中");
            statusBtn.setClickable(false);
        }

        TextView pawnTypeTv = (TextView) findViewById(R.id.tv_inv_proj_pawntype);
        pawnTypeTv.setText("风控措施：" + pawnType);
    }

    private ProgressDialog mProgressDialog;

    private void showLoadDialog(){
        mProgressDialog = new ProgressDialog(mContext);
        mProgressDialog.show();
        mProgressDialog.setMessage("载入中，请稍候……");
    }

    private void closeDialog(){
        if (mProgressDialog != null && mProgressDialog.isShowing()){
            mProgressDialog.cancel();
            mProgressDialog = null;
        }
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
