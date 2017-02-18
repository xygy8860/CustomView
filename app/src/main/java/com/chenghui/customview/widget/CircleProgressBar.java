package com.chenghui.customview.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.chenghui.customview.utils.PxUtils;

/**
 * Created by sunqinwei on 2017/2/10.
 * <p/>
 * 圆形百分比进度条
 */
public class CircleProgressBar extends View {

    //圆的半径
    private float mRadius;
    //色带的宽度
    private float mStripeWidth;
    //总体大小
    private int mHeight;
    private int mWidth;
    //动画位置百分比进度
    private int mCurPercent = 0;
    //实际百分比进度
    private int mPercent;
    //圆心坐标
    private float x;
    private float y;

    //要画的弧度
    private int mEndAngle;
    //小圆的颜色
    private int mSmallColor = 0xffafb4db; // 默认颜色
    //大圆颜色
    private int mBigColor = 0xff6950a1;

    //中心百分比文字大小
    private float mCenterTextSize;

    // 大圆画笔
    private Paint bigCirclePaint;
    private Paint sectorPaint;
    private Paint smallCirclePaint;
    private Paint textPaint;
    private RectF rect;

    // 将前面二个构造函数都指向第三个
    public CircleProgressBar(Context context) {
        this(context, null);
    }

    // XML中使用此构造函数
    public CircleProgressBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgressBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // 获取自定义属性
        // 用户可以根据自定义属性来控制自定义view的展现形式
        mStripeWidth = PxUtils.dpToPx(30, context);
        mCenterTextSize = PxUtils.spToPx(20, context);
        mRadius = PxUtils.dpToPx(100, context);

        //绘制大圆
        bigCirclePaint = new Paint();
        bigCirclePaint.setAntiAlias(true); // 抗锯齿
        bigCirclePaint.setColor(mBigColor); // 设置大圆颜色

        //绘制小圆,颜色透明
        smallCirclePaint = new Paint();
        smallCirclePaint.setAntiAlias(true);
        smallCirclePaint.setColor(mBigColor);

        // 饼状图
        rect = new RectF();
        sectorPaint = new Paint();
        sectorPaint.setColor(mSmallColor);
        sectorPaint.setAntiAlias(true);

        // 文字画笔
        textPaint = new Paint();
        textPaint.setColor(Color.WHITE);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //获取测量模式
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        //获取测量大小
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.EXACTLY && heightMode == MeasureSpec.EXACTLY) {
            mRadius = widthSize / 2;
            x = widthSize / 2;
            y = heightSize / 2;
            mWidth = widthSize;
            mHeight = heightSize;
        }

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            mWidth = (int) (mRadius * 2);
            mHeight = (int) (mRadius * 2);
            x = mRadius;
            y = mRadius;
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 大圆
        canvas.drawCircle(x, y, mRadius, bigCirclePaint); // 通过canvas画圆

        //饼状图
        rect.intersect(0, 0, mWidth, mHeight);
        mEndAngle = (int) (mCurPercent * 3.6); // 百分比*360度即为弧度
        canvas.drawArc(rect, 270, mEndAngle, true, sectorPaint);

        // 小圆
        canvas.drawCircle(x, y, mRadius - mStripeWidth, smallCirclePaint);

        //绘制文本
        String text = mCurPercent + "%";
        textPaint.setTextSize(mCenterTextSize);
        float textLength = textPaint.measureText(text);
        canvas.drawText(text, x - textLength / 2, y, textPaint);
    }

    //外部设置百分比数
    public void setPercent(int percent) {
        if (percent > 100) {
            throw new IllegalArgumentException("percent must less than 100!");
        }
        setCurPercent(percent);
    }

    //内部设置百分比 用于动画效果
    private void setCurPercent(int percent) {

        mPercent = percent;

        new Thread(new Runnable() {
            @Override
            public void run() {
                int sleepTime = 1;
                for (int i = 0; i < mPercent; i++) {
                    if (i % 20 == 0) {
                        sleepTime += 2;
                    }
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    mCurPercent = i;
                    CircleProgressBar.this.postInvalidate();
                }
            }

        }).start();

    }

}
