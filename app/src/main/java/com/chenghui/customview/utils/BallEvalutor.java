package com.chenghui.customview.utils;

import android.animation.TypeEvaluator;
import android.graphics.PointF;

/**
 * @author mikyou
 *         自定义估值器
 */
public class BallEvalutor implements TypeEvaluator<PointF> {
    private int mA; // 正弦波A

    public BallEvalutor(int mHeight, int parentHeight) {
        mA = (parentHeight - mHeight) / 2;
    }

    @Override
    public PointF evaluate(float t, PointF p0, PointF p3) {
        //时间因子t: 0~1
        PointF point = new PointF();
        // 正弦波公式  y = Asin(Tx+w)+k
        point.x = p0.x - (p0.x - p3.x) * t;
        point.y = (float) (mA * Math.sin(3 * Math.PI / (p0.x - p3.x) * (point.x - p3.x))) + mA;
        return point;//实时返回最新计算出的点对象
    }

}
