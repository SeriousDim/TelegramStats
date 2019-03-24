package ru.seriousgames.telegramstats.chartlib;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ru.seriousgames.telegramstats.MainActivity;
import ru.seriousgames.telegramstats.R;

public class ChartView extends View {

    private final int mDefaultHeight = 400;
    private final Paint debugPaint;

    private Paint linePaint, textPaint, pathPaint;
    private Paint ovalPaint, fillPaint;

    List<Chart> charts;
    int currentChart;
    private final float PADDING = 35;
    private final float DIVIDES = 6;
    private float pxBetweenLines, pxBetweenX, pxBetweenY, oldPxBetweenY;
    private float leftBound = 0, rightBound = 40;
    private float leftC, leftD, rightC, rightD, strLength;
    private float downCrd;
    private boolean created, drawingFlag;
    private int modulo = 1;
    MainActivity ctx;

    public ChartView(Context ctx){
        super(ctx);
        this.debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.debugPaint.setColor(Color.parseColor("#FF0000"));
        this.ctx = (MainActivity) ctx;

        initView();
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.debugPaint.setColor(Color.parseColor("#FF0000"));
        this.ctx = (MainActivity)context;

        initView();
    }

    private void initView(){
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setColor(Color.parseColor("#afafaf"));
        linePaint.setStrokeWidth(1);
        textPaint = new Paint();
        textPaint.setTextSize(25);
        textPaint.setColor(Color.parseColor("#afafaf"));
        pathPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pathPaint.setStrokeWidth(5);
        pathPaint.setStyle(Paint.Style.STROKE);

        ovalPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        ovalPaint.setStyle(Paint.Style.STROKE);
        ovalPaint.setStrokeWidth(3);

        fillPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        strLength = textPaint.measureText("Mar 99");

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                int action = motionEvent.getAction();
                if (action == MotionEvent.ACTION_DOWN) {
                    drawingFlag = true;
                    downCrd = motionEvent.getX();
                }
                else if (action == MotionEvent.ACTION_MOVE){
                    downCrd = motionEvent.getX();
                }
                /*else if (action == MotionEvent.ACTION_UP)
                    drawingFlag = false;*/
                invalidate();

                return true;
            }
        });
    }

    public void setLineVisibility(int line, boolean b){
        getCurrentChart().setLineVisibility(line, b);
        pxBetweenY = (getHeight() - PADDING)/getMaxAmongVisible(leftC, rightC);
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

        if (leftBound < 0)
            leftBound = 0;

        Log.d("stag", "height CV: "+getHeight()+" "+(getHeight() - PADDING));
        Log.d("stag", "maxY: "+getCurrentChart().getMaxYAmongVisible());

        this.oldPxBetweenY = pxBetweenY;

        Log.d("stag", "pxBetweenY: "+pxBetweenY);

        leftC = (float)Math.floor(leftBound);
        leftD = leftBound - leftC;
        rightC = (float)Math.floor(rightBound);
        rightD = rightBound - rightC;

        this.pxBetweenY = (getHeight() - PADDING)/getMaxAmongVisible(leftC, rightC);

        pxBetweenX = getWidth()/(rightBound-leftBound);
        drawingFlag = false;
        ctx.hideInfo();
        setModulo();
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

    public void setModulo(){
        int ind = 1;
        float px = ind * pxBetweenX;
        while (px < 2 * strLength){
            ind++;
            px = ind * pxBetweenX;
        }
        modulo = ind;
    }

    public float getMaxAmongVisible(float left, float right){
        float max = 0;
        Chart chart = getCurrentChart();
        for (int i = 0; i < chart.y.length; i++){
            if (chart.yVisible[i]){
                for (float j = left; j <= right; j++){
                    max = Math.max(max, chart.y[i][(int)j]);
                }
            }
        }
        return max;
    }

    public String getShortNum(float num){
        String s = "";
        int i = 1;
        float n = num;
        while (n >= 1000){
            n /= 1000;
            i++;
        }
        s = ((int)Math.floor(n))+"";
        switch(i){
            case 2:
                s += "."+((int)Math.floor((n - Math.floor(n)) * 10));
                s += "K";
                break;
            case 3:
                s += "."+((int)Math.floor((n - Math.floor(n)) * 10));
                s += "M";
                break;
        }

        return s;
    }

    public void notifyInfoView(List<Float> arr, String c, float x){
        String[] s = new String[arr.size()];
        for (int i=0; i<s.length; i++)
            s[i] = getShortNum(arr.get(i));
        ctx.setDataToInfoActivity(s, c, x);
    }

    public String getFullDate(Calendar c){
        String part = drawDate(c);
        int day = c.get(Calendar.DAY_OF_WEEK);
        String dayS = "";
        switch(day){
            case 7:
                dayS = "Sun, ";
                break;
            case 1:
                dayS = "Mon, ";
                break;
            case 2:
                dayS = "Tue, ";
                break;
            case 3:
                dayS = "Wed, ";
                break;
            case 4:
                dayS = "Thu, ";
                break;
            case 5:
                dayS = "Fri, ";
                break;
            case 6:
                dayS = "Sat, ";
                break;
        }
        return dayS+part;
    }

    @Override
    protected void onDraw(Canvas canvas){
        if (!created){
            pxBetweenLines = getHeight()/DIVIDES;
            //notifyDataSetChanged(new float[]{leftBound, rightBound});
            TypedValue a = new TypedValue();
            ctx.getTheme().resolveAttribute(R.attr.colorBackgroundFloating, a, true);
            if (a.type >= TypedValue.TYPE_FIRST_COLOR_INT && a.type <= TypedValue.TYPE_LAST_COLOR_INT) {
                fillPaint.setColor(a.data);
            }
            created = true;
        }

        if (canvas != null) {
            for (int i = 0; i < DIVIDES; i++){
                canvas.drawLine(0, (getHeight() - PADDING) - pxBetweenLines * i, getWidth(),
                        (getHeight() - PADDING) - pxBetweenLines * i, linePaint);
                canvas.drawText(getShortNum(getMaxAmongVisible(leftC, rightC)/DIVIDES * i), 10, (getHeight() - PADDING) - pxBetweenLines * i - 5, textPaint);
            }

            Chart chart = getCurrentChart();
            for (int i=0; i < chart.y.length; i++){
                if (chart.yVisible[i]){
                    Path path = new Path();
                    float pos = -pxBetweenX * leftD;
                    /*float drawedPos = pos;*/
                    Date date= new Date(chart.x[(int)leftC]);
                    Calendar c = Calendar.getInstance();
                    c.setTime(date);
                    String ready = drawDate(c);
                    if (leftC%modulo==0)
                        canvas.drawText(ready, pos, (getHeight() - 5), textPaint);

                    path.moveTo(-pxBetweenX * leftD, (getHeight() - PADDING) - chart.y[i][(int)leftC] * pxBetweenY);
                    for (int j = (int)leftC + 1; j <= rightBound+(1-rightD); j++){
                        pos += pxBetweenX;
                        path.lineTo(pos, (getHeight() - PADDING) - chart.y[i][j] * pxBetweenY);

                        date= new Date(chart.x[j]);
                        c = Calendar.getInstance();
                        c.setTime(date);
                        ready = drawDate(c);

                        if (j%modulo==0)
                            canvas.drawText(ready, pos, (getHeight() - 5), textPaint);
                    }
                    pathPaint.setColor(chart.yColors[i]);
                    canvas.drawPath(path, pathPaint);
                }
            }

            if (drawingFlag){
                ArrayList<Float> arr = new ArrayList<>();
                float ind = Math.round(leftBound + downCrd / pxBetweenX);
                float x = -pxBetweenX * leftD + (ind - leftC) * pxBetweenX;
                canvas.drawLine(x, 0, x, getHeight(), linePaint);
                for (int i = 0; i < chart.y.length; i++){
                    if (chart.yVisible[i]){
                        canvas.drawCircle(x, (getHeight() - PADDING) - chart.y[i][(int)ind] * pxBetweenY, 5, fillPaint);
                        ovalPaint.setColor(chart.yColors[i]);
                        canvas.drawCircle(x, (getHeight() - PADDING) - chart.y[i][(int)ind] * pxBetweenY, 7, ovalPaint);
                        arr.add((float)chart.y[i][(int)ind]);
                    }
                }
                Date date= new Date(chart.x[(int)ind]);
                Calendar c = Calendar.getInstance();
                c.setTime(date);
                notifyInfoView(arr, getFullDate(c), x);
            }

            Log.d("stag", "leftBound (norm, c, d): "+leftBound+" "+leftC+" "+leftD);
            Log.d("stag", "rightBound (norm, c, d): "+rightBound+" "+rightC+" "+rightD);
        }
    }

}
