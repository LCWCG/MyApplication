package com.lcw.myapplication.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.lcw.myapplication.ProjectParticularsActivity;
import com.lcw.myapplication.R;
import com.lcw.myapplication.util.ServerApi;
import com.lcw.myapplication.util.Utils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 *
 * @author 刘春旺
 *
 */
public class ParticularsFragment3 extends Fragment{

    private ProgressBar mPb;
    private LinearLayout mLl;
    private Context mContext;
    private ProjectParticularsActivity mPPActivity;
    private ListView mListView;

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mContext = getActivity();
        if (getActivity() instanceof ProjectParticularsActivity){
            mPPActivity = (ProjectParticularsActivity) getActivity();
        }

        mList = new ArrayList<Person>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_particulars_3, null);

        mPb = (ProgressBar) view.findViewById(R.id.pb_fragment_particulars_3);
        mLl = (LinearLayout) view.findViewById(R.id.ll_fragment_particulars_3);
        mListView = (ListView) view.findViewById(R.id.lv_fragment_particulars_3_listview);

        requestLendPersonData();

        return view;
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
                        if (mList != null && mList.size() > 0) mList.clear();

                        JSONObject jso2 = new JSONObject(jso.getString("tender"));
                        JSONArray jsa = new JSONArray(jso2.getString("tenderlist"));

                        Person mPerson;
                        for (int i = 0; i < jsa.length(); i++){
                            JSONObject jsoCh = jsa.getJSONObject(i);
                            mPerson = new Person();
                            mPerson.setUserName(jsoCh.getString("username"));
                            mPerson.setMoney(jsoCh.getString("account"));
                            mPerson.setTime(jsoCh.getString("addtime"));
                            mList.add(mPerson);
                            mPerson = null;
                        }

                        mListView.setAdapter(new MyBaseAdapter());
                        closeHintView();
                        return;
                    }
                    if (result.equals("error")) {
                        String errorMsg = jso.getString("error_remark");

                        mPPActivity.showHintDialog(errorMsg, false);
                        return;
                    }
                } catch (Exception e) {

//                    mPPActivity.showHintDialog("请求错误", false);
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

                mPPActivity.showHintDialog("请求错误", false);
            }
        });
    }

    private class MyBaseAdapter extends BaseAdapter {

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
                convertView = inflater.inflate(R.layout.fragment_particulars_3_item, null);
                holder = new ViewHolder();

                holder.mName = (TextView) convertView.findViewById(R.id.tv_fragment_3_item_name);
                holder.mMoney = (TextView) convertView.findViewById(R.id.tv_fragment_3_item_money);
                holder.mTime = (TextView) convertView.findViewById(R.id.tv_fragment_3_item_time);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            if (position % 2 == 0){
                convertView.setBackgroundColor(Color.parseColor("#FFE5E3E4"));
            }else{
                convertView.setBackgroundResource(R.color.all_view_bg);
            }

            holder.mName.setText(mList.get(position).getUserName());
            holder.mMoney.setText(mList.get(position).getMoney());
            holder.mTime.setText(mList.get(position).getTime());

            return convertView;
        }

        class ViewHolder {
            TextView mName;
            TextView mMoney;
            TextView mTime;
        }
    }

    private ArrayList<Person> mList;

    private class Person{

        private String userName;
        private String money;
        private String time;

        public String getUserName() {
            return userName;
        }

        public void setUserName(String userName) {
            this.userName = userName;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getTime() {
            return time;
        }

        public void setTime(String time) {
            this.time = time;
        }
    }

}
