package com.chenghui.customview.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenghui.customview.R;

/**
 * 下载开始前动画
 *
 * Created by sunqinwei on 2017/2/17.
 */
public class DownloadStartLayout extends RelativeLayout {

    private ImageView mDownloadImg;
    private TextView mDownloadTxt;
    private RelativeLayout mDownloadLayout;

    private ViewGroup.LayoutParams mParms;
    private int mHeight; // 控件高度
    private int mWidth; // 控件宽度
    private double mDurationWidth; // 控件宽度，中间变换值
    private double mSpace; // 每次缩小距离

    private int mDurationTime = 1000; // 持续时间
    private int mSpaceTime = 10; // 间隔时间

    private StartRunnable mRunnable;
    private OnCompleteListener mOnCompleteListener;
    private boolean isZoomIn = true; // 是否缩小动画
    private boolean isZoomOut = true; // 是否放大动画 默认实现

    // 动画完成监听
    public interface OnCompleteListener {
        void complete(RelativeLayout layout);
    }

    public DownloadStartLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        View v = LayoutInflater.from(context).inflate(R.layout.download_start_animation_layout, this, true);
        mDownloadLayout = (RelativeLayout) v.findViewById(R.id.download_start_layout);
        mDownloadTxt = (TextView) v.findViewById(R.id.download_start_txt);
        mDownloadImg = (ImageView) v.findViewById(R.id.download_start_img);
    }

    public void startZoomInAntimation(OnCompleteListener listener){
        isZoomOut = false;
        startAnimation(listener);
    }

    /**
     * 下载完成
     * @param str
     */
    public void setText(String str){
        mDownloadTxt.setText(str);
        mDownloadTxt.setVisibility(VISIBLE);
        mDownloadImg.clearAnimation();
        mDownloadImg.setVisibility(GONE);
    }

    /**
     * 开始动画
     *
     * @param listener 动画完成监听器
     */
    public void startAnimation(OnCompleteListener listener) {
        mOnCompleteListener = listener;
        mDownloadTxt.setVisibility(GONE);

        mParms = mDownloadLayout.getLayoutParams();
        mHeight = mDownloadLayout.getHeight();
        mWidth = mDownloadLayout.getWidth();
        mDurationWidth = mWidth;

        mSpace = (mWidth - mHeight) * 1.0 / (mDurationTime / mSpaceTime) * 2;

        mRunnable = new StartRunnable();
        mDownloadLayout.postDelayed(mRunnable, 10);
    }


    class StartRunnable implements Runnable {

        private boolean isStartImageAnimation = false;

        @Override
        public void run() {
            if (isZoomIn) { // 缩小动画
                if (mDurationWidth > mHeight) {
                    // 持续缩小动画，通过持续缩小 width 实现
                    mDurationWidth = mDurationWidth - mSpace;
                    mParms.width = (int) mDurationWidth;
                    mDownloadLayout.setLayoutParams(mParms);
                    mDownloadLayout.postDelayed(mRunnable, mSpaceTime);
                } else if (!isStartImageAnimation) {
                    // 缩放动画结束，开始旋转动画
                    mDownloadImg.setVisibility(VISIBLE);

                    // setInterpolator表示设置旋转速率。LinearInterpolator为匀速效果，Accelerateinterpolator为加速效果、DecelerateInterpolator为减速效果
                    Animation operatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim._360_rotate);
                    LinearInterpolator lin = new LinearInterpolator();
                    operatingAnim.setInterpolator(lin);
                    mDownloadImg.startAnimation(operatingAnim);
                    isStartImageAnimation = true;
                    mDownloadLayout.postDelayed(mRunnable, 500);
                } else {
                    //完成动画回调
                    if(isZoomOut){
                        isZoomIn = false;
                        mDownloadLayout.postDelayed(mRunnable, 10);
                    }else{
                        if(mOnCompleteListener != null){
                            mOnCompleteListener.complete(mDownloadLayout);
                        }
                    }
                }
            } else { // 放大动画
                if (mWidth > mDurationWidth) {
                    // 持续缩小动画，通过持续缩小 width 实现
                    mDurationWidth = mDurationWidth + mSpace;
                    mParms.width = (int) mDurationWidth;
                    mDownloadLayout.setLayoutParams(mParms);
                    mDownloadLayout.postDelayed(mRunnable, mSpaceTime);
                }else{
                    if(mOnCompleteListener != null){
                        mDownloadImg.clearAnimation();
                        mOnCompleteListener.complete(mDownloadLayout);
                    }
                }
            }
        }
    }

}
