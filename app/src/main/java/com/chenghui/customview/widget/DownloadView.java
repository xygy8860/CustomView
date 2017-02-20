package com.chenghui.customview.widget;

import android.animation.Animator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenghui.customview.R;
import com.chenghui.customview.utils.BallEvalutor;

/**
 * 下载动画
 * <p/>
 * Created by sunqinwei on 2017/2/17.
 */
public class DownloadView extends RelativeLayout {

    private DownloadStartLayout mDownloadStart; // 自定义view 控制下载开始前动画
    private RelativeLayout mProgressLayout; // 下载根布局

    private ProgressBar mProgressBar; // 自定义样式bar,显示下载进度
    private ImageView mProgressImg; // 右侧旋转动画图标
    private TextView mProgressTxt; // 显示下载进度

    private View mBall1; // 四个动画小球
    private View mBall2;
    private View mBall3;
    private View mBall4;

    private int mHeight; // 布局高度
    private OnCompleteListener mListener; // 下载开始动画完成监听，完成后可传入下载进度

    private TimeRunnable runnable; // 定时器
    private Handler handler = new Handler();

    public interface OnCompleteListener { // 开始动画完成监听
        void startComplete();
    }

    public DownloadView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View v = LayoutInflater.from(context).inflate(R.layout.download_animation_layout, this, true);

        mDownloadStart = (DownloadStartLayout) v.findViewById(R.id.download_progress_start);
        mProgressLayout = (RelativeLayout) v.findViewById(R.id.download_progress_layout);
        mProgressImg = (ImageView) v.findViewById(R.id.download_progress_img);
        mProgressBar = (ProgressBar) v.findViewById(R.id.download_progressbar);
        mProgressTxt = (TextView) v.findViewById(R.id.download_progress_txt);
        mBall1 = v.findViewById(R.id.download_progress_ball1);
        mBall2 = v.findViewById(R.id.download_progress_ball2);
        mBall3 = v.findViewById(R.id.download_progress_ball3);
        mBall4 = v.findViewById(R.id.download_progress_ball4);
    }

    public void start(OnCompleteListener listener) {
        mListener = listener;
        mDownloadStart.startAnimation(new DownloadStartLayout.OnCompleteListener() {
            @Override
            public void complete(RelativeLayout layout) {
                startComplete();
            }
        });
    }

    /**
     * 设置下载进度
     *
     * @param progress 下载进度 最大为100
     */
    public void setProgress(int progress) throws Exception {
        if (progress > 100) {
            throw new Exception(" progeress must less than 100");
        } else if (progress < 0) {
            throw new Exception(" progeress must more than 0");
        } else if(progress < 100){
            mProgressBar.setProgress(progress);
            mProgressTxt.setText(progress + "%");
        } else { // 下载完成
            mProgressLayout.setVisibility(GONE);
            mDownloadStart.setVisibility(VISIBLE);
            mDownloadStart.setText("下载完成");

            handler.removeCallbacks(runnable);

            // 结束动画
            mBall1.clearAnimation();
            mBall2.clearAnimation();
            mBall3.clearAnimation();
            mBall4.clearAnimation();

            mProgressImg.clearAnimation();
        }
    }

    private void startComplete() {
        mProgressLayout.setVisibility(VISIBLE);
        mDownloadStart.setVisibility(GONE);

        Animation operatingAnim = AnimationUtils.loadAnimation(getContext(), R.anim._360_rotate);
        LinearInterpolator lin = new LinearInterpolator();
        operatingAnim.setInterpolator(lin);
        mProgressImg.startAnimation(operatingAnim);

        mListener.startComplete();

        // 布局完成监听，不然 mProgressLayout.getWidth() 获得值为0
        final ViewTreeObserver vto2 = mProgressLayout.getViewTreeObserver();
        vto2.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mHeight = mProgressLayout.getHeight();
                runnable = new TimeRunnable();
                handler.post(runnable);
                // 可能会多次调用，需要移除监听器
                vto2.removeGlobalOnLayoutListener(this);
            }
        });
    }

    // 开始贝塞尔曲线动画
    private void startBeziAnimator(final View view, int duration) {

        final float x = view.getX();
        final float y = view.getY();

        // 正弦波动画,不断修改View的坐标,PointF(x,y)
        PointF pointF0 = new PointF(x, y + view.getHeight() / 2);// 创建P0点，起点
        PointF pointF3 = new PointF(-view.getHeight(), mHeight / 2);// 创建P3点，终点
        BallEvalutor mBezierEvalutor = new BallEvalutor(view.getHeight(), mHeight);// 创建一个估值器
        ValueAnimator animator = ValueAnimator.ofObject(mBezierEvalutor, pointF0, pointF3);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue();
                // 从估值器对象evalute方法实时计算出最新点的坐标
                // 然后去更新View的X,Y坐标
                view.setX(pointF.x);
                view.setY(pointF.y);
            }
        });
        animator.setTarget(view);
        animator.setDuration(duration);
        animator.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {

            }

            @Override
            public void onAnimationEnd(Animator animation) {
                // 动画结束，恢复初始位置，等待下一个循环
                view.setX(x);
                view.setY(y);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        animator.start();
    }

    class TimeRunnable implements Runnable {

        int action = 0; // 初始

        @Override
        public void run() {
            switch (action) {
                case 0: // ball1
                    action++;
                    startBeziAnimator(mBall1, 1500);
                    handler.postDelayed(runnable, 30);
                    break;
                case 1: // ball2
                    action++;
                    startBeziAnimator(mBall2, 1530);
                    handler.postDelayed(runnable, 30);
                    break;
                case 2: // ball3
                    action++;
                    startBeziAnimator(mBall3, 1560);
                    handler.postDelayed(runnable, 30);
                    break;
                case 3: // ball4
                    action++;
                    startBeziAnimator(mBall4, 1590);
                    handler.postDelayed(runnable, 30);
                    break;
                case 4: // 一个循环结束
                    // 考虑到runnable的队列延迟，间隔设置为1.8s, 整体算下来差不多2s 一个循环
                    action = 0;
                    handler.postDelayed(runnable, 1800);
                    break;
            }
        }
    }
}
