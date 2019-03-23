package ru.seriousgames.telegramstats.chartlib;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.List;

public class ChartSlider extends View {

    private final int mDefaultHeight = 400;
    private final float THUMB_WIDTH_IN_DP = 8;
    private final float PADDING_TOP = 1;
    private final Paint debugPaint;
    private Paint linePaint;

    ChartView chartView;
    Thumb leftThumb, rightThumb;
    List<Chart> charts;
    int currentChart;
    Paint rectGray;
    float pxBetweenX, ratioWidthAndMaxY, oldRatioWidthAndMaxY;

    float downCrd;
    boolean downed;
    boolean created, animation;
    boolean saved;
    Canvas cnvSaved;

    int fps;
    long time;
    long elapsed;
    Matrix mtx;

    public ChartSlider(Context ctx){
        super(ctx);
        this.debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.debugPaint.setColor(Color.parseColor("#FF0000"));

        initView(ctx);
    }

    public ChartSlider(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        this.debugPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.debugPaint.setColor(Color.parseColor("#FF0000"));

        initView(ctx);
    }

    private void initView(Context ctx){
        leftThumb = new Thumb(true, 0, pxFromDp(THUMB_WIDTH_IN_DP, ctx));
        linePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        linePaint.setStrokeWidth(2);
        this.rectGray = new Paint(Paint.ANTI_ALIAS_FLAG);
        this.rectGray.setColor(Color.parseColor("#3a7f8081"));
        this.linePaint.setStyle(Paint.Style.STROKE);

        this.time = System.currentTimeMillis();

        setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (motionEvent.getAction()==MotionEvent.ACTION_DOWN &&
                        leftThumb.isPointInThumb(motionEvent.getX(), motionEvent.getY())){
                    downCrd = motionEvent.getX();
                    downed = true;
                }
                if (motionEvent.getAction()==MotionEvent.ACTION_MOVE && downed){
                    float diff = motionEvent.getX()-downCrd;
                    leftThumb.plusX(diff);
                    downCrd = motionEvent.getX();
                    invalidate();
                }
                if (motionEvent.getAction()==MotionEvent.ACTION_UP){
                    downed = false;
                }
                notifyChartView(leftThumb.x/pxBetweenX, (leftThumb.x+leftThumb.width)/pxBetweenX+30);
                return true;
            }
        });
    }

    public void setChartView(ChartView view){
        if (view !=  null)
            this.chartView = view;
    }

    public void setCharts(List<Chart> charts){
        this.charts = charts;
    }

    public void notifyChartView(float... arr){
        this.chartView.notifyDataSetChanged(arr);
    }

    private float pxFromDp(float dp, Context ctx) {
        return dp * ctx.getResources().getDisplayMetrics().density;
    }

    public void setCurrentChart(int c){
        this.currentChart = c;
        //pxBetweenX = getWidth()/getCurrentChart().x.length;
        //setRatios();
    }

    public void setLineVisibility(int line, boolean b){
        getCurrentChart().setLineVisibility(line, b);
        setRatios();
        //startAnimation();
        invalidate();
    }

    public void startAnimation (){
        animation = true;

        float ratio = ratioWidthAndMaxY/oldRatioWidthAndMaxY;
    }

    public void setRatios(){
        this.oldRatioWidthAndMaxY = ratioWidthAndMaxY;
        this.ratioWidthAndMaxY = (getHeight()-PADDING_TOP)/getCurrentChart().getMaxYAmongVisible();
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

    @Override
    protected void onDraw(Canvas canvas){
        if (!created) {
            pxBetweenX = (float)Math.floor(getWidth()/getCurrentChart().x.length)+1;
            setRatios();
            notifyChartView(leftThumb.x, 40);
        }

        if (canvas != null) {
            if (animation){

            }
            drawChart(canvas);
            leftThumb.draw(canvas);
            canvas.drawRect(0, 0, leftThumb.x, getHeight(), rectGray);
        }
    }

    private Chart getCurrentChart(){
        return charts.get(currentChart);
    }

    private void drawChart(Canvas cnv){
        Chart chart = getCurrentChart();
        for (int i = 0; i < chart.y.length; i++){
            drawChartLine(cnv, i);
        }
    }

    private void drawChartLine(Canvas cnv, int lineNum){
        Chart chart = getCurrentChart();
        if (chart.yVisible[lineNum]) {
            Path line = new Path();
            linePaint.setColor(chart.yColors[lineNum]);
            line.moveTo(0, getHeight() - chart.y[lineNum][0] * ratioWidthAndMaxY);
            for (int i = 1; i < chart.x.length; i++) {
                if (chart.yTypes[lineNum]==1)
                    line.lineTo(i * pxBetweenX, getHeight() - chart.y[lineNum][i] * ratioWidthAndMaxY);
            }
            cnv.drawPath(line, linePaint);
        }
    }

    public class Thumb{

        public boolean left;
        private float x;
        public float width;
        public float[] hitbox;
        public Paint paint;

        public Thumb(boolean left, float x, float width) {
            this.left = left;
            this.x = x;
            this.width = width;
            this.hitbox = new float[2];
            hitbox[0] = x-width;
            hitbox[1] = x+width+width;
            this.paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            this.paint.setColor(Color.parseColor("#60669dc7"));
        }

        public void setX(float x){
            if (this.x > getWidth()-this.width)
                this.x = getWidth()-this.width;
            if (this.x < 0)
                this.x = 0;
            else
                this.x = x;

            hitbox[0] = this.x - this.width;
            hitbox[1] = this.x + this.width*2;
        }

        public void plusX(float value){
            setX(this.x + value);
        }

        public void draw(Canvas cnv){
            cnv.drawRect(x, 0, x+width, getHeight(), paint);

            //cnv.drawRect(hitbox[0], 0, hitbox[1], getHeight(), debugPaint);
        }

        public boolean isPointInThumb(float x, float y){
            return (x >= this.hitbox[0] && x <= this.hitbox[1]);
        }

    }

}
