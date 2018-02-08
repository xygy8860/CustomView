package com.example.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.chenghui.customview.utils.PxUtils;
import com.chenghui.customview.widget.CircleProgressBar;
import com.chenghui.customview.widget.DownloadView;

public class MainActivity extends AppCompatActivity {

    private CircleProgressBar mPro;
    private DownloadView mProgressBar;
    private TimeRunnable mRunnble;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPro = (CircleProgressBar) findViewById(R.id.circleView);
        mProgressBar = (DownloadView) findViewById(R.id.progressbar);

        mRunnble = new TimeRunnable();

        mPro.setCircleColor(getResources().getColor(R.color.lightgreen));
        mPro.setAngleColor(getResources().getColor(R.color.orange));
        mPro.setStripeWidth(PxUtils.dpToPx(10, this));
        mPro.setCenterTextColor(getResources().getColor(R.color.red));
        mPro.setmCenterTextSize(PxUtils.spToPx(30, this));

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = 80;
                //mPro.setPercent(n);

                mProgressBar.start(new DownloadView.OnCompleteListener() {
                    @Override
                    public void startComplete() {
                        mProgressBar.postDelayed(mRunnble, 100);
                    }
                });
            }
        });

    }

    class TimeRunnable implements Runnable {
        int progress = 0;

        @Override
        public void run() {
            if (progress <= 100) {
                try {
                    mPro.setPercent(progress);
                    mProgressBar.setProgress(progress);
                    progress++;
                    mProgressBar.postDelayed(mRunnble, 100);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
