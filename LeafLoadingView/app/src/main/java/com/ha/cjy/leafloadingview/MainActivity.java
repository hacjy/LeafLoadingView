package com.ha.cjy.leafloadingview;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.ha.cjy.leafloadingview.views.LeafLoadingView;

public class MainActivity extends AppCompatActivity {
    private final int REFRESH_PROGRESS = 1000;
    private float mProgress = 0;

    private LeafLoadingView mLoadingView;
    //利用handler实现动画
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case REFRESH_PROGRESS:
                    if (mProgress <= 100) {
                        mProgress += 1;
                        // 随机100ms以内刷新一次
                        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS,
                                100);
                        mLoadingView.setProgress(mProgress);
                    }
                    break;

                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findControl();
    }

    private void findControl(){
        mLoadingView  = (LeafLoadingView) findViewById(R.id.loading_leaf_view);
//        mLoadingView.setProgressColor(Color.RED);
//        mLoadingView.setAmplitude(16);
//        mLoadingView.setAmplitudeDisparity(10);
        mHandler.sendEmptyMessageDelayed(REFRESH_PROGRESS, 100);
    }
}
