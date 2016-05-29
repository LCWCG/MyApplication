package com.lcw.myapplication.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 *
 * @author 刘春旺
 *
 */
public class CircleProgressBar extends View {

    RectF oval;
    Paint paint;
    private int maxProgress = 60;
    private int progress = 0;

    public CircleProgressBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        oval = new RectF();
        paint = new Paint();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        if (width != height) {
            int min = Math.min(width, height);
            width = min;
            height = min;
        }

        int bg = Color.parseColor("#00000000");
        int circle_bg = Color.parseColor("#00000000");
        int circle_color = Color.parseColor("#fb5d5d");

        paint.setAntiAlias(true); // 设置画笔为抗锯齿-无锯齿

        paint.setColor(bg); // 设置画笔颜色
        canvas.drawColor(Color.TRANSPARENT); // 白色背景

        int progressStrokeWidth = 4;
        paint.setStrokeWidth(progressStrokeWidth); //线宽
        paint.setStyle(Paint.Style.STROKE);

        oval.left = progressStrokeWidth / 2; // 左上角x
        oval.top = progressStrokeWidth / 2; // 左上角y
        oval.right = width - progressStrokeWidth / 2; // 左下角x
        oval.bottom = height - progressStrokeWidth / 2; // 右下角y

        paint.setColor(circle_bg);
        canvas.drawArc(oval, -90, 360, false, paint); // 绘制白色圆圈，即进度条背景

        paint.setColor(circle_color);
        canvas.drawArc(oval, -90, ((float) progress / maxProgress) * 360, false, paint); // 绘制进度圆弧，这里是蓝色

        paint.setStrokeWidth(3);
        String text = progress + "s";
        int textHeight = height / 3;
        paint.setTextSize(textHeight);
        int textWidth = (int) paint.measureText(text, 0, text.length());
        paint.setStyle(Paint.Style.FILL);
        canvas.drawText(text, width / 2 - textWidth / 2, height / 2 + textHeight / 2, paint);

    }


    public int getMaxProgress() {
        return maxProgress;
    }

    public void setMaxProgress(int maxProgress) {
        this.maxProgress = maxProgress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        this.invalidate();
    }

    /**
     * 非UI线程调用
     */
    public void setProgressNotInUiThread(int progress) {
        this.progress = progress;
        this.postInvalidate();
    }
}
