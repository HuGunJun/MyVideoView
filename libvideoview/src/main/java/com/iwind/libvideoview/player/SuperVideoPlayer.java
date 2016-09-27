package com.iwind.libvideoview.player;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.iwind.libvideoview.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者：HuGuoJun
 * 2016/9/20 09:41
 * 邮箱：www.guojunkuaile@qq.com
 */
public class SuperVideoPlayer extends RelativeLayout {

    //更新定时器
    private final int TIME_SHOW_CONTROLLER = 3000;
    private final int TIME_UPDATE_PLAY_TIME = 1000;
    //切换控制器显示状态
    private final int MSG_UPDATE_CONTROLLER_STATE = 2020;
    //更新进度消息
    private final int MSG_UPDATE_TIME_AND_PROGRESS = 1010;
    //上下文
    private Context mContext;
    //视频播放器
    private SuperVideoView mVideoView;
    //播放控制器
    private VideoController mVideoController;
    //定时器
    private Timer mUpdateTimer;
    //横竖屏幕接口
    private changeShrinkOrExpand mChangeShrinkOrExpand;
    /**
     * 发送消息
     */
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_TIME_AND_PROGRESS:
                    upDatePlayTimeAndProgress();
                    break;
                case MSG_UPDATE_CONTROLLER_STATE:
                    mVideoController.allwaysCloseController();
                    break;
            }
        }
    };

    /**
     * 播放器准备播放监听
     */
    private MediaPlayer.OnPreparedListener mPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mediaPlayer.setOnInfoListener(new MediaPlayer.OnInfoListener() {
                @Override
                public boolean onInfo(MediaPlayer mp, int what, int extra) {
                    if (what == MediaPlayer.MEDIA_INFO_VIDEO_RENDERING_START) {
                        return true;
                    }
                    return false;
                }
            });
            mVideoView.setVideoWidth(SuperVideoPlayer.this.getWidth());
            mVideoView.setVideoHeight(SuperVideoPlayer.this.getHeight());
        }
    };

    /**
     * 媒体播放完成监听
     */
    private MediaPlayer.OnCompletionListener mOnCompletionListener = new MediaPlayer.OnCompletionListener() {
        @Override
        public void onCompletion(MediaPlayer mp) {
            mVideoController.allwaysShowController();
            mVideoController.setProgressBar(0, mVideoView.getDuration());
            mVideoController.setProgressText(0, mVideoView.getDuration());
            mVideoView.seekTo(0);
            stopUpdatePlayTimeAndProgress();
        }
    };


    /**
     * 触摸监听
     */
    private OnTouchListener mOnTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mVideoController.showOrHideController();
            }
            return true;
        }
    };


    /**
     * 切换播放项目监听
     */
    private VideoController.PlayListSelecter mPlayListSelecter = new VideoController.PlayListSelecter() {
        @Override
        public void selecter(List<VideoBean> videoBeanList, VideoBean mvideoBean) {
            stopUpdatePlayTimeAndProgress();
            mVideoView.seekTo(0);
            mVideoView.setVideoPath(mvideoBean.getURL());
            startUpDatePlayTimeAndProgress();
        }
    };

    /**
     * 媒体控制器实现接口
     */
    private VideoController.MediaControllerImpl mMediaControllerImpl = new VideoController.MediaControllerImpl() {


        @Override
        public void onUpdateProgress(VideoController.ProgressState state, VideoController.PlayState
                Playstate, int progress) {
            //手动滑动开始
            if (state == VideoController.ProgressState.START) {
                stopUpdatePlayTimeAndProgress();
            }
            //滑动中
            if (state == VideoController.ProgressState.DOING) {
                int time = progress * mVideoView.getDuration() / 100;
                mVideoView.seekTo(time);
                upDatePlayTimeAndProgress();
            }
            //滑动停止
            if (state == VideoController.ProgressState.STOP) {
                if (Playstate == VideoController.PlayState.PLAY) {
                    startUpDatePlayTimeAndProgress();
                } else if (Playstate == VideoController.PlayState.PAUSE) {
                    startUpDatePlayTimeAndProgress();
                }
            }
        }

        @Override
        public void onPlayOrPause() {
            if (mVideoView.isPlaying()) {
                stopUpdatePlayTimeAndProgress();
            } else {
                startUpDatePlayTimeAndProgress();
            }
        }

        @Override
        public void onPageTurn(VideoController.PageType mpageType, List<VideoBean> videoBeanList) {
            if (mpageType == VideoController.PageType.SHRINK) {
                mVideoController.setPageType(VideoController.PageType.SHRINK);
                mChangeShrinkOrExpand.changeToShrink(videoBeanList);
            } else if (mpageType == VideoController.PageType.EXPAND) {
                mVideoController.setPageType(VideoController.PageType.EXPAND);
                mChangeShrinkOrExpand.changeToExpand(videoBeanList);
            }
        }

    };

    /**
     * 设置修改横竖屏幕接口
     *
     * @param mchageShrinkOrExpand
     */
    public void setChangeShrinkOrExpand(changeShrinkOrExpand mchageShrinkOrExpand) {
        this.mChangeShrinkOrExpand = mchageShrinkOrExpand;
    }

    /**
     * 更新进度和时间
     */
    private void startUpDatePlayTimeAndProgress() {
        mVideoView.start();
        mVideoController.setPlayState(VideoController.PlayState.PLAY);
        mUpdateTimer = new Timer();
        mUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_UPDATE_TIME_AND_PROGRESS);
            }
        }, 0, TIME_UPDATE_PLAY_TIME);
        mUpdateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                mHandler.sendEmptyMessage(MSG_UPDATE_CONTROLLER_STATE);
            }
        }, TIME_SHOW_CONTROLLER);
    }


    /**
     * 停止更新时间和进度
     */
    private void stopUpdatePlayTimeAndProgress() {
        mVideoView.pause();
        mVideoController.setPlayState(VideoController.PlayState.PAUSE);
        if (mUpdateTimer != null) {
            mHandler
                    .removeMessages(MSG_UPDATE_TIME_AND_PROGRESS);
            mHandler.removeMessages(MSG_UPDATE_CONTROLLER_STATE);
            mUpdateTimer.cancel();
            mUpdateTimer = null;
        }
    }


    /**
     * 更新播放进度
     */
    private void upDatePlayTimeAndProgress() {
        mVideoController.setProgressText(mVideoView.getCurrentPosition(), mVideoView.getDuration());
        int allTime = mVideoView.getDuration();
        int playTime = mVideoView.getCurrentPosition();
        int loadProgress = mVideoView.getBufferPercentage();
        int progress = playTime * 100 / allTime;
        mVideoController.setProgressBar(progress, loadProgress);
    }


    /**
     * 设置播放列表
     *
     * @param mVideoBeanList
     */
    public void setPlayList(List<VideoBean> mVideoBeanList, int seekTo) {
        int count = 0;
        for (int i = 0; i < mVideoBeanList.size(); i++) {
            count++;
            if (mVideoBeanList.get(i).isPlayNow()) {
                mVideoController.setVideoTitle(mVideoBeanList.get(i).getName());
                mVideoView.setVideoPath(mVideoBeanList.get(i).getURL());
                mVideoController.setPlayState(VideoController.PlayState.PLAY);
                mVideoView.seekTo(seekTo);
                mVideoView.start();
                startUpDatePlayTimeAndProgress();
                break;
            }
            if (count == mVideoBeanList.size()) {
                mVideoBeanList.get(0).setIsPlayNow(true);
                mVideoController.setVideoTitle(mVideoBeanList.get(0).getName());
                mVideoView.setVideoPath(mVideoBeanList.get(0).getURL());
                mVideoController.setPlayState(VideoController.PlayState.PLAY);
                mVideoView.start();
                startUpDatePlayTimeAndProgress();
            }
        }
        mVideoController.setPlayList(mVideoBeanList);
    }




    public SuperVideoPlayer(Context context) {
        super(context);
        InitView(context);
    }

    public SuperVideoPlayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitView(context);
    }

    public SuperVideoPlayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }


    /**
     * 初始化控件和布局
     *
     * @param context
     */
    private void InitView(Context context) {
        this.mContext = context;
        View.inflate(context, R.layout.video_player, this);
        mVideoView = (SuperVideoView) findViewById(R.id.video_view);
        mVideoController = (VideoController) findViewById(R.id.video_controller);
        //设置控制器
        mVideoController.setMediaControllerImpl(mMediaControllerImpl);
        //切换播放项目监听
        mVideoController.setPlayListSelecter(mPlayListSelecter);
        //设置播放器准备状态监听
        mVideoView.setOnPreparedListener(mPreparedListener);
        //设置播放完成监听
        mVideoView.setOnCompletionListener(mOnCompletionListener);
        //触摸监听
        mVideoView.setOnTouchListener(mOnTouchListener);
    }


    /**
     * 接口修改横竖屏幕
     */
    public interface changeShrinkOrExpand {

        void changeToShrink(List<VideoBean> videoBeanList);

        void changeToExpand(List<VideoBean> videoBeanList);
    }

}
