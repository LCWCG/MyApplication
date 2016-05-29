package com.lcw.myapplication.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.lcw.myapplication.R;

/**
 *
 * @author 刘春旺
 *
 */
public class HintDialog3 extends ProgressDialog {

    private int mResId = -1;

    private String mMessage1 = null;
    private String mMessage2 = null;

    private View.OnClickListener mListener1 = null;
    private View.OnClickListener mListener2 = null;

    public HintDialog3(Context context, String content1, String content2,
                       int resId, View.OnClickListener Listener1, View.OnClickListener Listener2) {
        super(context, R.style.HintDialogDimEnabled);
        mMessage1 = content1;
        mMessage2 = content2;
        mResId = resId;
        mListener1 = Listener1;
        mListener2 = Listener2;
    }

    @Override
    public void show() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.show();

        if (mResId != -1)
            this.setContentView(mResId);

        this.setCanceledOnTouchOutside(false);

        Button okButton1 = (Button) this.findViewById(R.id.dialog_content1);
        Button okButton2 = (Button) this.findViewById(R.id.dialog_content2);

        if(okButton1 != null){
            okButton1.setText(mMessage1);
        }
        if(okButton2 != null){
            okButton2.setText(mMessage2);
        }
        okButton1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener1 != null) {
                    mListener1.onClick(v);
                }
            }
        });
        okButton2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mListener2 != null) {
                    mListener2.onClick(v);
                }
            }
        });
    }

    @Override
    public void dismiss() {
        if (isShowing())
            super.dismiss();
    }
}
