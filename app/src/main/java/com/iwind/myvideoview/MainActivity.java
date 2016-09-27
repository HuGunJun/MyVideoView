package com.iwind.myvideoview;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;

import com.iwind.libvideoview.player.SuperVideoPlayer;
import com.iwind.libvideoview.player.VideoBean;
import com.iwind.libvideoview.player.VideoController;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    SuperVideoPlayer mSuperVideoPlayer;
    List<VideoBean> mVideoBeanList = new ArrayList<VideoBean>();

    private SuperVideoPlayer.changeShrinkOrExpand mChangeShrinkOrExpand = new SuperVideoPlayer.changeShrinkOrExpand() {
        @Override
        public void changeToShrink(List<VideoBean> videoBeanList) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        @Override
        public void changeToExpand(List<VideoBean> videoBeanList) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        mSuperVideoPlayer = (SuperVideoPlayer) findViewById(R.id.myplayer);
        mSuperVideoPlayer.setChangeShrinkOrExpand(mChangeShrinkOrExpand);
        VideoBean mVideoBean = new VideoBean();
        VideoBean mVideoBean1 = new VideoBean();
        mVideoBean.setName("视频1");
        mVideoBean.setURL("http://192.168.1.213:8080/com.nkbh.pro/a.mp4");
        mVideoBean1.setName("视频2");
        mVideoBean1.setURL("http://192.168.1.213:8080/com.nkbh.pro/b.mp4");
        mVideoBeanList.add(mVideoBean1);
        mVideoBeanList.add(mVideoBean);
        mSuperVideoPlayer.setPlayList(mVideoBeanList, 0);
    }
}
