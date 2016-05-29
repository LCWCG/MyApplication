package com.lcw.myapplication.util;


import android.app.ProgressDialog;
import android.content.Context;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.lcw.myapplication.R;

/**
 *
 * @author 刘春旺
 *
 */
public class HintDialog2 extends ProgressDialog {
    private int mResId = -1;

    private String mTitle = null;
    private String mMessage = null;
    private String mOkMessage;

    private View.OnClickListener mOkListener = null;

    public HintDialog2(Context context, String title, String message, String okMessage,
                      int resId, View.OnClickListener OkListener) {
        super(context, R.style.HintDialogDimEnabled);
        mTitle = title;
        mMessage = message;
        mResId = resId;
        mOkListener = OkListener;
        mOkMessage = okMessage;
    }

    @Override
    public void show() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.show();

        if (mResId != -1)
            this.setContentView(mResId);

        this.setCanceledOnTouchOutside(false);

        TextView textViewTitle = (TextView) this.findViewById(R.id.dialog_title2);
        TextView textViewMessage = (TextView) this.findViewById(R.id.dialog_message2);

        if (mTitle != null)
            textViewTitle.setText(mTitle);

        if (mMessage != null)
            textViewMessage.setText(mMessage);

        Button okButton = (Button) this.findViewById(R.id.button_ok2);
        if(mOkMessage!=null){
            okButton.setText(mOkMessage);
        }
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mOkListener != null) {
                    mOkListener.onClick(v);
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
