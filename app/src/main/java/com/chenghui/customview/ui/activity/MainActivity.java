package com.chenghui.customview.ui.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.chenghui.customview.R;
import com.chenghui.customview.widget.CircleProgressBar;

public class MainActivity extends AppCompatActivity {

    private CircleProgressBar mPro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mPro = (CircleProgressBar)findViewById(R.id.circleView);

        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int n = 80;
                mPro.setPercent(n);
            }
        });
    }
}
