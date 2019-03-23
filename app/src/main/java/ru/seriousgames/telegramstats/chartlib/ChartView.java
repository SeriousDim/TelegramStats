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

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

public class ChartView extends View {

    private final int mDefaultHeight = 400;
    private final Paint debugPaint;

    private Paint linePaint, textPaint, pathPaint;

    List<Chart> charts;
    int currentChart;
    private final float PADDING = 35;
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
        textPaint = new Paint();
        textPaint.setTextSize(20);
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

    public void notifyDataSetChanged(boolean left, float... arr){ // [xLeft, xRight] (indexes)
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

    public String drawDate(Calendar d){
        int month = d.get(Calendar.MONTH);
        String strMonth = "";
        int date = d.get(Calendar.DAY_OF_MONTH);
        switch (month){
            case 0:
                strMonth = "Jan ";
                break;
            case 1:
                strMonth = "Feb ";
                break;
            case 2:
                strMonth = "Mar ";
                break;
            case 3:
                strMonth = "Apr ";
                break;
            case 4:
                strMonth = "May ";
                break;
            case 5:
                strMonth = "Jun ";
                break;
            case 6:
                strMonth = "Jul ";
                break;
            case 7:
                strMonth = "Aug ";
                break;
            case 8:
                strMonth = "Sep ";
                break;
            case 9:
                strMonth = "Oct ";
                break;
            case 10:
                strMonth = "Nov ";
                break;
            case 11:
                strMonth = "Dec ";
                break;
        }
        strMonth = strMonth+date;
        return strMonth;
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
                        (getHeight() - PADDING) - pxBetweenLines * i, linePaint);
                canvas.drawText((int)Math.floor(getCurrentChart().getMaxYAmongVisible()/DIVIDES * i)+"", 10, (getHeight() - PADDING) - pxBetweenLines * i - 5, textPaint);
            }

            Chart chart = getCurrentChart();
            for (int i=0; i < chart.y.length; i++){
                if (chart.yVisible[i]){
                    Path path = new Path();
                    float tw;
                    float pos = -pxBetweenX * leftD;
                    float drawedPos = pos;
                    Date date= new Date(chart.x[(int)leftC]);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    String ready = drawDate(c);
                    tw = textPaint.measureText(ready);
                    canvas.drawText(ready, pos, (getHeight() - 3), textPaint );

                    path.moveTo(-pxBetweenX * leftD, (getHeight() - PADDING) - chart.y[i][(int)leftC] * pxBetweenY);
                    for (int j = (int)leftC + 1; j <= rightBound+(1-rightD); j++){
                        pos += pxBetweenX;
                        path.lineTo(pos, (getHeight() - PADDING) - chart.y[i][j] * pxBetweenY);

                        date= new Date(chart.x[j]);
                        c = Calendar.getInstance();
                        c.setTime(date);
                        ready = drawDate(c);

                        if (drawedPos + 2*tw < pos){
                            drawedPos = pos;
                            canvas.drawText(ready, pos, (getHeight() - 5), textPaint);
                        }
                    }
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
