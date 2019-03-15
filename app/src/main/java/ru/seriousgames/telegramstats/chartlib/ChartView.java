package ru.seriousgames.telegramstats.chartlib;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v7.app.AppCompatDelegate;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;

import java.util.ArrayList;
import java.util.List;

public class ChartView extends View {

    private final int mDefaultHeight = 400;
    private final Paint debugPaint;

    List<Chart> charts;
    int currentChart;
    private final float PADDING = 20;

    public ChartView(Context ctx){
        super(ctx);
        this.debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.debugPaint.setColor(Color.parseColor("#FF0000"));

        initView();
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.debugPaint.setColor(Color.parseColor("#FF0000"));

        initView();
    }

    private void initView(){

    }

    public void setCharts(List<Chart> charts){
        this.charts = charts;
    }

    public void notifyDataSetChanged(int[] arr){ // [xLeft, xRight] (indexes)
        // change params here
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        int desiredWidth = 250;
        int desiredHeight = mDefaultHeight;

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width;
        int height;

        if (widthMode == MeasureSpec.EXACTLY) {
            width = widthSize;
        } else if (widthMode == MeasureSpec.AT_MOST) {
            width = Math.min(desiredWidth, widthSize);
        } else {
            width = desiredWidth;
        }

        if (heightMode == MeasureSpec.EXACTLY) {
            height = heightSize;
        } else if (heightMode == MeasureSpec.AT_MOST) {
            height = Math.min(desiredHeight, heightSize);
        } else {
            height = desiredHeight;
        }

        setMeasuredDimension(width, height);
    }

    public void setCurrentChart(int c){
        this.currentChart = c;
    }

    @Override
    protected void onDraw(Canvas canvas){
        if (canvas != null) {


            /*canvas.drawLine(PADDING, PADDING, getWidth()-PADDING, PADDING, debugPaint);
            canvas.drawLine(PADDING, PADDING, PADDING, getHeight()-PADDING, debugPaint);
            canvas.drawLine(PADDING, getHeight()-PADDING, getWidth()-PADDING, getHeight()-PADDING, debugPaint);
            canvas.drawLine(getWidth()-PADDING, getHeight()-PADDING, getWidth()-PADDING, PADDING, debugPaint);*/
        }
    }

}
