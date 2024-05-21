package com.example.pieview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

/**
 * 仪表盘控件
 */
public class PieView extends View {

    //控件宽高
    protected int mWidth;
    protected int mHeight;
    //边距
    protected float mPadding;
    //饼图的圆点
    protected int mRadius;

    // 默认控件大小
    private final static int DEFAULT_SIZE = 180;
    // 默认边距
    private final static int DEFAULT_PADDING = 5;

    // 默认开始角度
    private final static int DEFAULT_START_ANGLE = 135;
    // 模式结束角度
    private final static int DEFAULT_END_ANGLE = 270;
    // 默认内圈刻度距离外圈的间距
    private final static int DEFAULT_SPACE_IN_OUT = 40;
    // 默认中心圆半径
    private final static int DEFAULT_CENTER_RADIUS = 40;
    // 中心圆半径
    private int centerRadius;
    // 标题文字内容
    private String title;

    // 内圈刻度距离外圈的间距
    private int spaceInOut;
    // 开始角度
    private int startAngle;
    // 结束角度
    private int endAngle;


    // 外圈矩形定义
    private RectF rectF;
    // 内圈文字矩形定义
    private RectF textRectF;

    // 定义刻度文字画笔
    private Paint wordPaint;
    private int wordPainColor;
    // 定义外圈圆弧画笔
    private Paint outPaint;
    private int outPaintColor;

    // 定义外圈圆弧level1画笔
    private Paint outLevel1Paint;
    private int outLevel1Color;

    // 定义外圈圆弧level2画笔
    private Paint outLevel2Paint;
    private int outLevel2Color;
    // 定义内圈园画笔
    private Paint inPaint;
    private int inPaintColor;

    // 定义指针画笔
    private Paint pointPaint;
    private int pointColor;
    // 定义标题画笔
    private Paint titlePaint;
    private int titleColor;

    private List<String> workList = new ArrayList<>();

    public PieView(Context context) {
        this(context, null);
    }

    public PieView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PieView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (workList.isEmpty()) {
            workList.add("0");
            workList.add("5");
            workList.add("10");
            workList.add("15");
            workList.add("20");
            workList.add("25");
            workList.add("30");
        }

        // 获取自定义属性
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.PieView);
        wordPainColor = typedArray.getColor(R.styleable.PieView_color_pie_word_paint, ContextCompat.getColor(getContext(), R.color.color_black));
        outPaintColor = typedArray.getColor(R.styleable.PieView_color_pie_out_paint, ContextCompat.getColor(getContext(), R.color.color_pie_out_paint));
        outLevel1Color = typedArray.getColor(R.styleable.PieView_color_pie_out_level1_paint, ContextCompat.getColor(getContext(), R.color.color_pie_out_level1_paint));
        outLevel2Color = typedArray.getColor(R.styleable.PieView_color_pie_out_level2_paint, ContextCompat.getColor(getContext(), R.color.color_pie_out_level2_paint));
        inPaintColor = typedArray.getColor(R.styleable.PieView_color_pie_in_paint, ContextCompat.getColor(getContext(), R.color.color_pie_in_paint));
        titleColor = typedArray.getColor(R.styleable.PieView_color_pie_title_paint, ContextCompat.getColor(getContext(), R.color.color_pie_title_paint));
        pointColor = typedArray.getColor(R.styleable.PieView_color_pie_point_paint, ContextCompat.getColor(getContext(), R.color.color_pie_point_paint));
        startAngle = typedArray.getInt(R.styleable.PieView_pie_start_angle, DEFAULT_START_ANGLE);
        endAngle = typedArray.getInt(R.styleable.PieView_pie_end_angle, DEFAULT_END_ANGLE);
        spaceInOut = typedArray.getInt(R.styleable.PieView_pie_space_in_out, DEFAULT_SPACE_IN_OUT);
        centerRadius = typedArray.getInt(R.styleable.PieView_pie_center_radius, DEFAULT_CENTER_RADIUS);
        title = typedArray.getString(R.styleable.PieView_pie_title_text);
        if (title == null) {
            title = "km/h";
        }
        typedArray.recycle();
        initPaint();
    }

    public void setWordList(List<String> wordList) {
        this.workList = wordList;
        invalidate();
    }

    public void setCurrentData(int data) {
        this.data = data;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //屏幕宽高
        mWidth = w;
        mHeight = h;
        //圆点
        mRadius = (int) (Math.min(mWidth, mHeight) / 2 - mPadding);
        rectF = new RectF(-mRadius, -mRadius, mRadius, mRadius);
        textRectF = new RectF(-mRadius + spaceInOut, -mRadius + spaceInOut, mRadius - spaceInOut, mRadius - spaceInOut);

    }

    private void initPaint() {
        // 内圈画笔
        inPaint = new Paint();
        inPaint.setAntiAlias(true);
        inPaint.setColor(inPaintColor);
        inPaint.setStyle(Paint.Style.FILL);
        // 刻度文字画笔
        wordPaint = new Paint();
        wordPaint.setAntiAlias(true);
        wordPaint.setTextSize(24);
        wordPaint.setColor(wordPainColor);
        wordPaint.setTextAlign(Paint.Align.LEFT);
        wordPaint.setStyle(Paint.Style.STROKE);

        // 外圈画笔
        outPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outPaint.setAntiAlias(true);
        outPaint.setColor(outPaintColor);
        outPaint.setStyle(Paint.Style.STROKE);
        outPaint.setStrokeWidth(10);
        // 外圈画笔level1
        outLevel1Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outLevel1Paint.setColor(outLevel1Color);
        outLevel1Paint.setAntiAlias(true);
        outLevel1Paint.setStyle(Paint.Style.STROKE);
        outLevel1Paint.setStrokeWidth(10);
        // 外圈画笔level2
        outLevel2Paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        outLevel2Paint.setColor(outLevel2Color);
        outLevel2Paint.setAntiAlias(true);
        outLevel2Paint.setStyle(Paint.Style.STROKE);
        outLevel2Paint.setStrokeWidth(10);
        // 指针画笔
        pointPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        pointPaint.setColor(pointColor);
        pointPaint.setAntiAlias(true);
        pointPaint.setStyle(Paint.Style.FILL);
        pointPaint.setStrokeWidth(10);
        // 标题画笔
        titlePaint = new Paint();
        titlePaint.setAntiAlias(true);
        titlePaint.setTextSize(24);
        titlePaint.setColor(titleColor);
        titlePaint.setTextAlign(Paint.Align.LEFT);
        titlePaint.setStyle(Paint.Style.STROKE);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size = dp2px(DEFAULT_SIZE);
        mPadding = Math.max(Math.max(getPaddingLeft(), getPaddingTop()),
                Math.max(getPaddingRight(), getPaddingBottom()));
        mPadding = Math.max(dp2px(DEFAULT_PADDING), mPadding);
        setMeasuredDimension(measureSize(widthMeasureSpec, size), measureSize(heightMeasureSpec, size));
    }

    private float data = 0;


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.translate(mWidth / 2, mHeight / 2);
        // 绘制外圈
        drawOutCircle(canvas);
        // 画标题
        drawTitle(canvas);
        // 画指针
        drawPoint(canvas);
        // 画内圆
        drawInCircle(canvas);
        // 画刻度文字
        drawWordText(canvas);

    }

    private void drawWordText(Canvas canvas) {
        canvas.save();
//        debug(canvas);
        Rect rect = new Rect();
        Path mTextPath = new Path();
        for (int i = 0; i < workList.size(); i++) {
            String text = workList.get(i);
            wordPaint.getTextBounds(text, 0, text.length(), rect);
            float startAngle = this.startAngle + ((float) endAngle / (workList.size() - 1)) * i;
            float sweepAngle = (float) endAngle / (workList.size() - 1);
            mTextPath.reset();
            mTextPath.addArc(textRectF, startAngle, sweepAngle);
            canvas.drawTextOnPath(text, mTextPath, 0, 0, wordPaint);
        }
        canvas.restore();
    }

    private void debug(Canvas canvas) {
        canvas.drawLine(-mRadius, 0, mRadius, 0, wordPaint);
        canvas.drawLine(0, -mRadius, 0, mRadius, wordPaint);
    }

    private void drawInCircle(Canvas canvas) {
        canvas.drawCircle(0, 0, dp2px(centerRadius), inPaint);
    }

    private void drawPoint(Canvas canvas) {
        Path path = new Path();
        RectF rectF1 = new RectF(-centerRadius, -centerRadius, centerRadius, centerRadius);

        // 获取最大值和最大角度的换算关系 比值
        int maxValue = Integer.parseInt(workList.get(workList.size() - 1));
        int ratio = (endAngle / maxValue);
        // 求出指针的开始角度
        float pointStartAngle = (data * ratio) + startAngle;

        path.addArc(rectF1, pointStartAngle, 30);

        /**
         * 算出指针的弧度
         */
        float angle = (float) Math.toRadians(pointStartAngle);


        float[] pointC = new float[2];
        float[] pointB = new float[2];
        float[] pointD = new float[2];
        /**
         * C点坐标  指针最末点的位置坐标（远离圆心）
         */
        pointC[0] = (float) (Math.cos(angle) * mRadius);
        pointC[1] = (float) (Math.sin(angle) * mRadius);

        /**
         * B点坐标
         */
        pointB[0] = (float) (Math.cos(angle + Math.toRadians(30)) * centerRadius);
        pointB[1] = (float) (Math.sin(angle + Math.toRadians(30)) * centerRadius);

        /**
         * D点坐标
         */
        pointD[0] = (float) (Math.cos(angle - Math.toRadians(30)) * centerRadius);
        pointD[1] = (float) (Math.sin(angle - Math.toRadians(30)) * centerRadius);


        path.lineTo(pointB[0], pointB[1]);
        path.lineTo(pointC[0], pointC[1]);
        path.lineTo(pointD[0], pointD[1]);
        path.close();
        canvas.drawPath(path, pointPaint);
    }

    private void drawTitle(Canvas canvas) {
        float textLength = wordPaint.measureText(title);
        canvas.drawText(title, -(textLength) / 2, -mRadius / 2, wordPaint);
    }

    private void drawOutCircle(Canvas canvas) {
        // 每个数据的间隔角度值
        int interval = endAngle / (workList.size() - 1);
        // 绘制外圈
        canvas.drawArc(rectF, startAngle, startAngle + endAngle - interval * 3, false, outPaint);
        // 绘制外圈leve1
        canvas.drawArc(rectF, startAngle + endAngle - interval * 2, interval, false, outLevel1Paint);
        // 绘制外圈leve2
        canvas.drawArc(rectF, startAngle + endAngle - interval, interval, false, outLevel2Paint);
    }


    /**
     * dp2px
     */
    protected int dp2px(float dpValue) {
        float density = getResources().getDisplayMetrics().density;
        return (int) (dpValue * density + 0.5f);
    }

    /**
     * 判断当前控件宽高类型
     */
    private int measureSize(int measureSpec, int defaultSize) {

        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.AT_MOST:
                break;
            case MeasureSpec.EXACTLY:
                defaultSize = specSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                break;
        }
        return defaultSize;
    }

    public int getCenterRadius() {
        return centerRadius;
    }

    public void setCenterRadius(int centerRadius) {
        this.centerRadius = centerRadius;
    }

    public int getSpaceInOut() {
        return spaceInOut;
    }

    public void setSpaceInOut(int spaceInOut) {
        this.spaceInOut = spaceInOut;
    }

    public int getStartAngle() {
        return startAngle;
    }

    public void setStartAngle(int startAngle) {
        this.startAngle = startAngle;
    }

    public int getEndAngle() {
        return endAngle;
    }

    public void setEndAngle(int endAngle) {
        this.endAngle = endAngle;
    }

    public int getWordPainColor() {
        return wordPainColor;
    }

    public void setWordPainColor(int wordPainColor) {
        this.wordPainColor = wordPainColor;
    }

    public int getOutPaintColor() {
        return outPaintColor;
    }

    public void setOutPaintColor(int outPaintColor) {
        this.outPaintColor = outPaintColor;
    }

    public int getOutLevel1Color() {
        return outLevel1Color;
    }

    public void setOutLevel1Color(int outLevel1Color) {
        this.outLevel1Color = outLevel1Color;
    }

    public int getOutLevel2Color() {
        return outLevel2Color;
    }

    public void setOutLevel2Color(int outLevel2Color) {
        this.outLevel2Color = outLevel2Color;
    }

    public int getInPaintColor() {
        return inPaintColor;
    }

    public void setInPaintColor(int inPaintColor) {
        this.inPaintColor = inPaintColor;
    }

    public int getPointColor() {
        return pointColor;
    }

    public void setPointColor(int pointColor) {
        this.pointColor = pointColor;
    }

    public int getTitleColor() {
        return titleColor;
    }

    public void setTitleColor(int titleColor) {
        this.titleColor = titleColor;
    }
}
