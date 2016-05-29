package com.lcw.myapplication;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.lcw.myapplication.view.MyListView;

/**
 *
 * @author 刘春旺
 *
 */
public class SupportBankActivity extends BaseActivity{

    private static String bankLimit[] = new String[]{
            "单笔五万，单日5万",
            "单笔20万，单日20万",
            "单笔20万，单日20万",
            "单笔100万，单日100万",
            "单笔2万，单日2万",
            "单笔100万，单日100万",
            "单笔1万，单日100万",
            "单笔100万，单日100万",
            "单笔100万，单日100万",
            "单笔100万，单日100万",
            "单笔5000万，单日5万",
            "单笔5万，单日5万",
            "单笔5万，单日5万",
            "单笔5000万，单日5万",
            "单笔5000万，单日5万",
            "单笔100万，单日100万",
            "单笔100万，单日100万"
    };
    private static int mBankLogo[] = new int[]{
            R.drawable.bank_logo_0,
            R.drawable.bank_logo_1,
            R.drawable.bank_logo_2,
            R.drawable.bank_logo_3,
            R.drawable.bank_logo_4,
            R.drawable.bank_logo_5,
            R.drawable.bank_logo_6,
            R.drawable.bank_logo_7,
            R.drawable.bank_logo_8,
            R.drawable.bank_logo_9,
            R.drawable.bank_logo_10,
            R.drawable.bank_logo_11,
            R.drawable.bank_logo_12,
            R.drawable.bank_logo_13,
            R.drawable.bank_logo_14,
            R.drawable.bank_logo_15,
            R.drawable.bank_logo_16,
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_supportbank);

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT) {
            this.findViewById(R.id.ll_top_view).setVisibility(View.GONE);
        }

        ImageButton mBack = (ImageButton) findViewById(R.id.ib_supportbank_back);
        mBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getFinish();
            }
        });

        MyListView listview = (MyListView) findViewById(R.id.lv_supportbank_listview);
        listview.setAdapter(new MyBaseAdapter());
    }

    private class MyBaseAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mBankLogo.length;
        }

        @Override
        public Object getItem(int position) {
            return mBankLogo[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                convertView = inflater.inflate(R.layout.supportbank_item, null);
                holder = new ViewHolder();

                holder.logo = (ImageView) convertView.findViewById(R.id.iv_supportbank_item_logo);
                holder.name = (TextView) convertView.findViewById(R.id.tv_supportbank_item_name);
                holder.number = (TextView) convertView.findViewById(R.id.tv_supportbank_item_number);

                convertView.setTag(holder);
            } else {
                holder = (ViewHolder) convertView.getTag();
            }

            holder.logo.setImageResource(mBankLogo[position]);
            holder.name.setText(BankManageActivity.mBankName[position]);
            holder.number.setText(bankLimit[position]);

            return convertView;
        }

        class ViewHolder {
            ImageView logo;
            TextView name;
            TextView number;
        }
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
