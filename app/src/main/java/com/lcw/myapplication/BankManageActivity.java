package com.lcw.myapplication;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.lcw.myapplication.model.BankModel;
import com.lcw.myapplication.util.HintDialog2;
import com.lcw.myapplication.util.ServerApi;
import com.lcw.myapplication.util.Utils;
import com.lcw.myapplication.view.MyListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * @author 刘春旺
 *
 */
public class BankManageActivity extends BaseActivity{

    private MyListView mListView;
    private MyBaseAdapter adapter;
    public static String mBankName[] = new String[]{
            "工商银行",
            "农业银行",
            "中国银行",
            "建设银行",
            "交通银行",
            "招商银行",
            "邮政储蓄银行",
            "光大银行",
            "民生银行",
            "广发银行",
            "中信银行",
            "浦发银行",
            "兴业银行",
            "上海银行",
            "北京银行",
            "平安银行",
            "浙商银行"
    };
    private static int mBankCircleLogo[] = new int[]{
            R.drawable.bank_circle_logo_0,
            R.drawable.bank_circle_logo_1,
            R.drawable.bank_circle_logo_2,
            R.drawable.bank_circle_logo_3,
            R.drawable.bank_circle_logo_4,
            R.drawable.bank_circle_logo_5,
            R.drawable.bank_circle_logo_6,
            R.drawable.bank_circle_logo_7,
            R.drawable.bank_circle_logo_8,
            R.drawable.bank_circle_logo_9,
            R.drawable.bank_circle_logo_10,
            R.drawable.bank_circle_logo_11,
            R.drawable.bank_circle_logo_12,
            R.drawable.bank_circle_logo_13,
            R.drawable.bank_circle_logo_14,
            R.drawable.bank_circle_logo_15,
            R.drawable.bank_circle_logo_16
    };
    private static int mBanBg[] = new int[]{
            R.drawable.bank_bg_0,
            R.drawable.bank_bg_1,
            R.drawable.bank_bg_2,
            R.drawable.bank_bg_3,
            R.drawable.bank_bg_4,
            R.drawable.bank_bg_5,
            R.drawable.bank_bg_6,
            R.drawable.bank_bg_7,
            R.drawable.bank_bg_8,
            R.drawable.bank_bg_9,
            R.drawable.bank_bg_10,
            R.drawable.bank_bg_11,
            R.drawable.bank_bg_12,
            R.drawable.bank_bg_13,
            R.drawable.bank_bg_14,
            R.drawable.bank_bg_15,
            R.drawable.bank_bg_16
    };
    private ProgressDialog mProgressDialog;
    private static ArrayList<BankModel> mList = new ArrayList<BankModel>();

    private String mLaunchClassName = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_bankmanage);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
        }

        try{
            mLaunchClassName = getIntent().getStringExtra("LAUNCH_CLASS_NAME_FLAG");
        }catch(Exception e){
            //
        }

        ImageButton mBack = (ImageButton) findViewById(R.id.ib_bankmanage_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinish();
            }
        });

        LinearLayout mAddLL = (LinearLayout) findViewById(R.id.ll_bankmanage_add_id);
        mAddLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadUserInfo();
            }
        });

        mListView = (MyListView) findViewById(R.id.lv_bankmanage_listview);
        adapter = new MyBaseAdapter();
        mListView.setAdapter(adapter);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                if (mList.get(position).getIs_default().equals("1")){

                    returnBankCode(position);

                    return;
                }

                getSetDefaultBank(mList.get(position).getId());
            }
        });

        loadData();
    }

    private void returnBankCode(int position){
        if (!TextUtils.isEmpty(mLaunchClassName) && mLaunchClassName.equals("ExtractMoneyActivity")){
            Intent intent = new Intent(mContext, ExtractMoneyActivity.class);
            intent.putExtra("BANK_CODE", mList.get(position).getAccount());
            setResult(ExtractMoneyActivity.RESULT_CODE, intent);
            getFinish();
        }
    }

    private void loadData(){
        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败", false);
            return;
        }
        showLoadDialog();

//        {
//            "module": "user",
//                "request": "&q=banklist",
//                "result": "success",
//                "method": "post",
//                "user_id": "42337",
//                "list": ""
//        }
        ServerApi.getBankListData(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result").trim();
                    if (result.equals("success")) {
                        if (mList.size() > 0) {
                            mList.clear();
                        }
                        JSONArray jsa = new JSONArray(jso.getString("list"));
                        BankModel bankModel;
                        for (int i = 0; i < jsa.length(); i++) {
                            JSONObject jso2 = jsa.getJSONObject(i);
                            bankModel = new BankModel();
                            bankModel.setAccount(jso2.getString(BankModel.ACCOUNT));
                            bankModel.setBank(jso2.getString(BankModel.BANK));
                            bankModel.setIs_default(jso2.getString(BankModel.IS_DEFAULT));
                            bankModel.setId(jso2.getString(BankModel.ID));
                            mList.add(bankModel);
                            bankModel = null;
                        }
                        if (mList.size() > 0) {
                            adapter.notifyDataSetChanged();
                        }

                        closeDialog();
                        return;
                    }
                    if (result.equals("error")) {
                        String error_remark = jso.getString("error_remark");

                        showHintDialog(error_remark, false);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    closeDialog();
//                    showHintDialog("请求错误", false);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showHintDialog("请求错误", false);
            }
        });
    }

    public static final int REQUEST_CODE = 11;
    /**
     * 绑卡 result
     */
    public static final int RESULT_CODE_1 = 12;
    /**
     * 身份认证result
     */
    public static final int RESULT_CODE_2 = 13;

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            if (resultCode == RESULT_CODE_1){
                loadData();
            }
            if (resultCode == RESULT_CODE_2){
                Intent intent = new Intent(mContext, BankAddActivity.class);
                intent.putExtra("NAME", data.getStringExtra("NAME"));
                startActivityForResult(intent, REQUEST_CODE);
            }
        }
    }

    private void loadUserInfo(){
        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败", true);
            return;
        }
        showLoadDialog();

        ServerApi.getUserInfoData(new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result").trim();
                    if (result.equals("success")) {
                        String state = jso.getString("realname_status").trim();
                        if (state.equals("1")) {
                            String realname = jso.getString("realname");
                            Intent intent = new Intent(mContext, BankAddActivity.class);
                            intent.putExtra("NAME", realname);
                            startActivityForResult(intent, REQUEST_CODE);

                            closeDialog();
                            return;
                        }

                        //去身份认证
                        startActivityForResult(new Intent(mContext, IDApproveActivity.class), REQUEST_CODE);
                        closeDialog();
                        return;
                    }
                    if (result.equals("error")) {
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

    private void setDefault(String id){
        int position = 0;

        for (int i=0; i<mList.size();i++){
            if (mList.get(i).getId().equals(id)){
                position = i;
                mList.get(i).setIs_default("1");
            }else{
                mList.get(i).setIs_default("0");
            }
        }
        adapter.notifyDataSetChanged();

        returnBankCode(position);
    }

    private void getSetDefaultBank(final String id){
        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败", true);
            return;
        }
        showLoadDialog();

        ServerApi.getBankDefaultData(id, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    closeDialog();
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result").trim();
                    if (result.equals("success")) {
                        setDefault(id);

                        closeDialog();
                        return;
                    }
                    if (result.equals("error")) {
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

    private class MyBaseAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.bankmanage_item, null);
                holder = new ViewHolder();

                holder.rlBg = (RelativeLayout) convertView.findViewById(R.id.rl_bankmanage_item_bg);
                holder.ivLogo = (ImageView) convertView.findViewById(R.id.iv_bankmanage_item_logo);
                holder.name = (TextView) convertView.findViewById(R.id.tv_bankmanage_item_name);
                holder.number = (TextView) convertView.findViewById(R.id.tv_bankmanage_item_number);
                holder.state = (TextView) convertView.findViewById(R.id.tv_bankmanage_item_state);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }
            String name = mList.get(position).getBank();
            int pos = position(name);
            holder.rlBg.setBackgroundResource(mBanBg[pos]);
            holder.ivLogo.setImageResource(mBankCircleLogo[pos]);
            holder.name.setText(name + "\n储蓄卡");
            String account = mList.get(position).getAccount();
            int accountLength = account.length();
            holder.number.setText("**** **** **** " + account.substring(accountLength - 4, accountLength));
            if (mList.get(position).getIs_default().trim().equals("1")){
                holder.state.setText("默认卡");
            }else{
                holder.state.setText("");
            }

            return convertView;
        }

        class ViewHolder {
            RelativeLayout rlBg;
            ImageView ivLogo;
            TextView name;
            TextView number;
            TextView state;
        }
    }

    private int position(String name){
        int pos = 0;
        for (int i = 0;i < mBankName.length; i++){
            if (mBankName[i].equals(name)){
                pos = i;
                break;
            }
        }
        return pos;
    }

}
