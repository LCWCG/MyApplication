package com.lcw.myapplication;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.lcw.myapplication.model.CashLogModel;
import com.lcw.myapplication.util.HintDialog2;
import com.lcw.myapplication.util.ServerApi;
import com.lcw.myapplication.util.Utils;
import com.lcw.myapplication.view.HorizontalListView;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * @author 刘春旺
 *
 */
public class CashLogActivity extends BaseActivity{

    private HorizontalListView mListView;
    private TextView mLogNumber;
    private ArrayList<CashLogModel> mList;

    private View hintView, contentView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_cashlog);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
        }

        findViewById(R.id.ib_cash_log_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinish();
            }
        });

        hintView = this.findViewById(R.id.view_cash_log_hint);
        contentView = this.findViewById(R.id.view_cash_log_content);

        mListView = (HorizontalListView) findViewById(R.id.hlv_cash_log_listview);
        mLogNumber = (TextView) findViewById(R.id.tv_cash_log_number);

        requestCashLogData();
    }

    private void requestCashLogData(){
        if (!Utils.isNetworkConnected(mContext)){
            showHintDialog("网络连接失败");
            return;
        }
        showLoadDialog();

        ServerApi.getCashListData(100, 1, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jso = new JSONObject(response);
                    String result = jso.getString("result").trim();

                    mLogNumber.setText(jso.getString("total").trim());
                    if (result.equals("success")) {

                        mList = new ArrayList<CashLogModel>();
                        CashLogModel cashLogModel;
                        JSONArray jsa = jso.getJSONArray("list");
                        for (int i = 0; i < jsa.length(); i++){
                            JSONObject jsoC = jsa.getJSONObject(i);
                            cashLogModel = new CashLogModel();

                            cashLogModel.setNid(jsoC.getString(CashLogModel.NID));
                            cashLogModel.setTotal(jsoC.getString(CashLogModel.TOTAl));
                            cashLogModel.setCredited(jsoC.getString(CashLogModel.CREDITED));
                            cashLogModel.setFee(jsoC.getString(CashLogModel.FEE));
                            cashLogModel.setStatus(jsoC.getString(CashLogModel.STATUS));
                            cashLogModel.setAddtime(jsoC.getString(CashLogModel.ADD_TIME));

                            mList.add(cashLogModel);
                            cashLogModel = null;
                        }

                        if (mList != null && mList.size() > 0){
                            mListView.setAdapter(new CashBaseAdapter());
                            hintView.setVisibility(View.GONE);
                            contentView.setVisibility(View.VISIBLE);
                        }else{
                            hintView.setVisibility(View.VISIBLE);
                            contentView.setVisibility(View.GONE);
                        }

                        closeDialog();
                        return;
                    }
                    if (result.equals("error")) {
                        String error_remark = jso.getString("error_remark");

                        showHintDialog(error_remark);
                        return;
                    }
                } catch (Exception e) {
                    e.printStackTrace();

                    hintView.setVisibility(View.VISIBLE);
                    contentView.setVisibility(View.GONE);

                    closeDialog();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                showHintDialog("请求错误");
            }
        });
    }

    private class CashBaseAdapter extends BaseAdapter{

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
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            ViewHolder holder = null;
            if (convertView == null) {
                holder=new ViewHolder();

                convertView = View.inflate(mContext, R.layout.cashlog_item, null);

                holder.code = (TextView)convertView.findViewById(R.id.tv_cashlog_item_code);

                holder.nid = (TextView)convertView.findViewById(R.id.tv_cashlog_item_1);
                holder.total = (TextView)convertView.findViewById(R.id.tv_cashlog_item_2);
                holder.credited = (TextView)convertView.findViewById(R.id.tv_cashlog_item_3);
                holder.fee = (TextView)convertView.findViewById(R.id.tv_cashlog_item_4);
                holder.status = (TextView)convertView.findViewById(R.id.tv_cashlog_item_5);
                holder.addtime = (TextView)convertView.findViewById(R.id.tv_cashlog_item_6);

                convertView.setTag(holder);
            }else {
                holder = (ViewHolder)convertView.getTag();
            }

            holder.code.setText((position + 1) + "");

            holder.nid.setText(mList.get(position).getNid());
            holder.total.setText(mList.get(position).getTotal() + "元");
            holder.credited.setText(mList.get(position).getCredited() + "元");
            holder.fee.setText(mList.get(position).getFee() + "元");
            String status = mList.get(position).getStatus();
            if (status.equals("0")){
                holder.status.setText("审核中");
            }
            if (status.equals("1")){
                holder.status.setText("提现成功");
            }
            if (status.equals("2")){
                holder.status.setText("提现失败");
            }
            if (status.equals("3")){
                holder.status.setText("用户取消");
            }
            holder.addtime.setText(mList.get(position).getAddtime());

            return convertView;
        }

        private class ViewHolder{

            private TextView code;

            private TextView nid;
            private TextView total;
            private TextView credited;
            private TextView fee;
            private TextView status;
            private TextView addtime;

        }
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
