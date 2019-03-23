package ru.seriousgames.telegramstats.chartlib;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import java.util.List;

public class ChartView extends View {

    private final int mDefaultHeight = 400;
    private final Paint debugPaint;

    private Paint linePaint, textPaint, pathPaint;

    List<Chart> charts;
    int currentChart;
    private final float PADDING = 30;
    private final float DIVIDES = 6;
    private float pxBetweenLines, pxBetweenX, pxBetweenY;
    private float leftBound = 0, rightBound = 40;
    private float leftC, leftD, rightC, rightD;
    private boolean created;

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
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#afafaf"));
        linePaint.setStrokeWidth(1);
        textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(30);
        textPaint.setColor(Color.parseColor("#afafaf"));
        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStrokeWidth(5);
        pathPaint.setStyle(Paint.Style.STROKE);
    }

    public void setLineVisibility(int line, boolean b){
        getCurrentChart().setLineVisibility(line, b);
        pxBetweenY = (getHeight() - PADDING)/getCurrentChart().getMaxYAmongVisible();
        //startAnimation();
        invalidate();
    }

    public void setCharts(List<Chart> charts){
        this.charts = charts;
    }

    public void notifyDataSetChanged(float... arr){ // [xLeft, xRight] (indexes)
        // change params here
        leftBound = arr[0];
        rightBound = arr[1];

        Log.d("stag", "height CV: "+getHeight()+" "+(getHeight() - PADDING));
        Log.d("stag", "maxY: "+getCurrentChart().getMaxYAmongVisible());

        this.pxBetweenY = (getHeight() - PADDING)/getCurrentChart().getMaxYAmongVisible();

        Log.d("stag", "pxBetweenY: "+pxBetweenY);

        leftC = (float)Math.floor(leftBound);
        leftD = leftBound - leftC;
        rightC = (float)Math.floor(rightBound);
        rightD = rightBound - rightC;

        pxBetweenX = getWidth()/(rightBound-leftBound);
        Log.d("stag", "pxBetweenX: "+pxBetweenX);

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

    public Chart getCurrentChart(){
        return charts.get(currentChart);
    }

    public void setCurrentChart(int c){
        this.currentChart = c;
    }

    @Override
    protected void onDraw(Canvas canvas){
        if (!created){
            pxBetweenLines = getHeight()/DIVIDES;
            //notifyDataSetChanged(new float[]{leftBound, rightBound});
            created = true;
        }

        if (canvas != null) {
            for (int i = 0; i < DIVIDES; i++){
                canvas.drawLine(0, (getHeight() - PADDING) - pxBetweenLines * i, getWidth(),
                        (getHeight() - 30) - pxBetweenLines * i, linePaint);
                canvas.drawText(Math.floor(getCurrentChart().getMaxYAmongVisible()/DIVIDES * i)+"", PADDING, (getHeight() - PADDING) - pxBetweenLines * i - 5, textPaint);
            }

            Chart chart = getCurrentChart();
            for (int i = 0; i < chart.y.length; i++){
                if (chart.yVisible[i]){
                    Path path = new Path();
                    path.moveTo(0, (getHeight() - PADDING) - chart.y[i][(int) leftC] * pxBetweenY);
                    for (float j = leftC + 1; j <= rightC + 2; j++){
                        path.lineTo(j * pxBetweenX, (getHeight() - PADDING) - chart.y[i][(int)j] * pxBetweenY);
                    }
                    Matrix mtx = new Matrix();
                    //mtx.postTranslate(-leftD * pxBetweenX, 0);
                    //path.transform(mtx);
                    pathPaint.setColor(chart.yColors[i]);
                    canvas.drawPath(path, pathPaint);
                }
            }

            Log.d("stag", "leftBound (norm, c, d): "+leftBound+" "+leftC+" "+leftD);
            Log.d("stag", "rightBound (norm, c, d): "+rightBound+" "+rightC+" "+rightD);

            /*canvas.drawLine(PADDING, PADDING, getWidth()-PADDING, PADDING, debugPaint);
            canvas.drawLine(PADDING, PADDING, PADDING, getHeight()-PADDING, debugPaint);
            canvas.drawLine(PADDING, getHeight()-PADDING, getWidth()-PADDING, getHeight()-PADDING, debugPaint);
            canvas.drawLine(getWidth()-PADDING, getHeight()-PADDING, getWidth()-PADDING, PADDING, debugPaint);*/
        }
    }

}
