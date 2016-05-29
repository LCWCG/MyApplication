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
public class HintDialog extends ProgressDialog {
    private int mResId = -1;

    private String mTitle = null;
    private String mMessage = null;
    private String mOkMessage;
    private String mCancelMesaage;

    private View.OnClickListener mOkListener = null, mCancelListener = null;

    public HintDialog(Context context, String title, String message, String okMessage, String cancelMesaage,
                      int resId, View.OnClickListener OkListener, View.OnClickListener cancelListener) {
        super(context, R.style.HintDialogDimEnabled);
        mTitle = title;
        mMessage = message;
        mResId = resId;
        mOkListener = OkListener;
        mCancelListener = cancelListener;
        mOkMessage = okMessage;
        mCancelMesaage = cancelMesaage;
    }

    @Override
    public void show() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        super.show();

        if (mResId != -1)
            this.setContentView(mResId);

        this.setCanceledOnTouchOutside(false);

        TextView textViewTitle = (TextView) this.findViewById(R.id.dialog_title);
        TextView textViewMessage = (TextView) this.findViewById(R.id.dialog_message);

        if (mTitle != null)
            textViewTitle.setText(mTitle);

        if (mMessage != null)
            textViewMessage.setText(mMessage);

        Button cancelButton = (Button) this.findViewById(R.id.button_cancel);
        Button okButton = (Button) this.findViewById(R.id.button_ok);
        if(mOkMessage!=null){
            okButton.setText(mOkMessage);
        }
        if(mCancelMesaage!=null){
            cancelButton.setText(mCancelMesaage);
        }
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
                if (mCancelListener != null) {
                    mCancelListener.onClick(v);
                }
            }
        });
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
