package com.lcw.myapplication.view;

/**
 * Copyright (C) 2013 The Android Open Source Project
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.android.volley.ui.NetworkImageView;


/**
 * Handles fetching an image from a URL as well as the life-cycle of the
 * associated request.
 */

/**
 *
 * @author 刘春旺
 *
 */
public class RourdCornerNetworkImageView extends NetworkImageView {

    private int mCornerRadius = 0;

    public RourdCornerNetworkImageView(Context context) {
        this(context, null);
    }

    public RourdCornerNetworkImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public RourdCornerNetworkImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    /*
     * set the corner radius with dp
     * @param radius   corner radius
     */

    public int getCornerRadius() {
        return mCornerRadius;
    }

    public void setCornerRadius(int radius) {
        mCornerRadius = radius;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        // Round some corners betch!
        Drawable maiDrawable = getDrawable();

        float Radius = mCornerRadius * getContext().getResources().getDisplayMetrics().density;

        if (maiDrawable instanceof BitmapDrawable && mCornerRadius > 0) {
            Paint paint = ((BitmapDrawable) maiDrawable).getPaint();
            final int color = 0xff000000;

            final RectF rectF = new RectF(0, 0, getWidth(), getHeight());

            // Create an off-screen bitmap to the PorterDuff alpha blending to work right
            int saveCount = canvas.saveLayer(rectF, null,
                    Canvas.MATRIX_SAVE_FLAG |
                            Canvas.CLIP_SAVE_FLAG |
                            Canvas.HAS_ALPHA_LAYER_SAVE_FLAG |
                            Canvas.FULL_COLOR_LAYER_SAVE_FLAG |
                            Canvas.CLIP_TO_LAYER_SAVE_FLAG);


            paint.setAntiAlias(true);
            canvas.drawARGB(0, 0, 0, 0);
            paint.setColor(color);
            canvas.drawRoundRect(rectF, Radius, Radius, paint);

            Xfermode oldMode = paint.getXfermode();

            // This is the paint already associated with the BitmapDrawable that super draws

            paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            super.onDraw(canvas);
            paint.setXfermode(oldMode);
            canvas.restoreToCount(saveCount);

        } else {
            super.onDraw(canvas);
        }
    }
}
