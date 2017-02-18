package com.chenghui.customview.widget;

import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.chenghui.customview.R;
import com.chenghui.customview.utils.BezierEvalutor;

/**
 * 下载动画
 * <p/>
 * Created by sunqinwei on 2017/2/17.
 */
public class DownloadView extends RelativeLayout {

    private View mBall2;
    private View mBall1;
    private DownloadStartLayout mDownloadStart; // 自定义view 控制下载开始前动画
    private RelativeLayout mProgressLayout; // 下载根布局

    private ProgressBar mProgressBar; // 自定义样式bar,显示下载进度
    private ImageView mProgressImg; // 右侧旋转动画图标
    private TextView mProgressTxt; // 显示下载进度

    private OnCompleteListener mListener;
    private int mWidth;
    private int mHeight;
    private float mStartX;
    private float mStartY;

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
        } else {
            mProgressBar.setProgress(progress);
            mProgressTxt.setText(progress + "%");
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

        mWidth = mProgressLayout.getWidth();
        mHeight = mProgressLayout.getHeight();

        startBeziAnimator();
    }

    // 开始贝塞尔曲线动画
    private void startBeziAnimator() {

        ValueAnimator bezierValueAnimator = getBeziValueAnimator(mBall1,3000);
        AnimatorSet bezierAnimatorSet = new AnimatorSet();
        //按顺序播放动画
        bezierAnimatorSet.playSequentially(bezierValueAnimator);//然后按顺序播放这些动画集合
        bezierAnimatorSet.setTarget(mBall1);
        bezierAnimatorSet.start();
    }

    /**
     * @author mikyou getBeziValueAnimator 构造一个贝塞尔曲线动画
     */
    private ValueAnimator getBeziValueAnimator(final View view,int duration) {
        float x = view.getX();
        float y = view.getY();

        // 贝塞尔曲线动画,不断修改ImageView的坐标,PointF(x,y)
        PointF pointF0 = new PointF(x, y + view.getHeight()/2);// 创建P0点，起点
        PointF pointF3 = new PointF(-view.getHeight(), mProgressLayout.getHeight()/2);// 创建P3点，终点
        BezierEvalutor mBezierEvalutor = new BezierEvalutor(view.getHeight(), mProgressLayout.getHeight());// 创建一个估值器，然后并把P1，P2点传入
        /**
         * @author zhongqihong
         *         创建一个ValueAnimator，并把起点P0和终点P3传入,然后在BezierEvalutor重写的方法evalute中得到P0
         *         ，P3 然后通过上一步利用BezierEvalutor构造器将P1,P2两个点传入，所以这就是说
         *         在BezierEvalutor重写的方法evalute可以得到P0，P1,P2,P3点对象，然后通过贝塞尔的公式
         *         即可计算出该轨迹上的任意一点的坐标，并实时返回一个PontF点的对象，然后通过addUpdateListener
         *         监听事件实时获得最新点的坐标然后将这些最新点坐标去更新爱心的ImageVIew控件的X，Y坐标
         * */
        ValueAnimator animator = ValueAnimator.ofObject(mBezierEvalutor, pointF0, pointF3);
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                PointF pointF = (PointF) animation.getAnimatedValue(); // 通过addUpdateListener监听事件实时获得从mBezierEvalutor估值器对象evalute方法实时计算出最新点的坐标
                view.setX(pointF.x);// 然后去更新该爱心ImageView的X,Y坐标
                view.setY(pointF.y);
                Log.e("123","point: x ：" + pointF.x + "  y: " + pointF.y);
            }
        });
        animator.setTarget(view);
        animator.setDuration(duration);
        return animator;
    }
}
