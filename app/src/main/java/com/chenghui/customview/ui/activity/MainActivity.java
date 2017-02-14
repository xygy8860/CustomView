package com.chenghui.customview.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.chenghui.customview.R;
import com.chenghui.customview.widget.CirclePercentView1;
import com.chenghui.customview.widget.CircleProgressBar;

public class MainActivity extends AppCompatActivity {

    private CirclePercentView1 mPro;
    private CircleProgressBar mPro1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPro = (CirclePercentView1)findViewById(R.id.circleView);
        mPro1 = (CircleProgressBar)findViewById(R.id.circleView1);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = (int)(Math.random()*100);
                mPro.setPercent(n);
                mPro1.setPercent(n);
            }
        });
    }
}
