package com.iwind.libvideoview.player;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.iwind.libvideoview.R;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * 作者：HuGuoJun
 * 2016/9/20 10:26
 * 邮箱：www.guojunkuaile@qq.com
 */
public class VideoController extends FrameLayout implements View.OnClickListener, SeekBar.OnSeekBarChangeListener,
        AdapterView.OnItemClickListener {

    private Context mContext;
    private MediaControllerImpl mMediaController;
    //播放或者停止按钮
    private ImageView pause;
    //显示时间
    private TextView tv_time;
    //进度条
    private SeekBar mSeekBar;
    //最大化播放按钮
    private ImageView mExpandImg;
    //缩放播放按钮
    private ImageView mShrinkImg;
    //顶部菜单和底部菜单
    private RelativeLayout rl_top, rl_bottom;
    //播放状态
    private PlayState mPlayState;
    //右边播放列表
    private LinearLayout rl_right_menu;
    //切换显示列表按钮
    private ImageView btn_showmenu;
    //播放列表
    private ListView lv_play_menu;
    //播放选择接口对象
    private PlayListSelecter mPlayListSelecter;
    //视频列表适配器
    private AdapterVideoController adapterVideoController;
    //显示播放状态
    private ImageView iv_play;
    //视频名称
    private TextView tv_title;
    //视频播放对象列表
    private List<VideoBean> mVideoBeanList;

    public VideoController(Context context) {
        super(context);
        InitView(context);

    }

    public VideoController(Context context, AttributeSet attrs) {
        super(context, attrs);
        InitView(context);
    }

    public VideoController(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        InitView(context);
    }

    /**
     * 初始化控件和布局
     *
     * @param context
     */
    private void InitView(Context context) {
        mContext = context;
        View.inflate(context, R.layout.video_controller, this);
        tv_time = (TextView) findViewById(R.id.tv_time);
        rl_bottom = (RelativeLayout) findViewById(R.id.rl_bottom);
        rl_top = (RelativeLayout) findViewById(R.id.rl_top);
        rl_right_menu = (LinearLayout) findViewById(R.id.rl_right_menu);
        mSeekBar = (SeekBar) findViewById(R.id.media_controller_progress);
        mShrinkImg = (ImageView) findViewById(R.id.shrink);
        mExpandImg = (ImageView) findViewById(R.id.expand);
        pause = (ImageView) findViewById(R.id.pause);
        btn_showmenu = (ImageView) findViewById(R.id.btn_showmenu);
        iv_play = (ImageView) findViewById(R.id.iv_play);
        lv_play_menu = (ListView) findViewById(R.id.lv_play_menu);
        tv_title = (TextView) findViewById(R.id.tv_title);
        iv_play.setOnClickListener(this);
        lv_play_menu.setOnItemClickListener(this);
        btn_showmenu.setOnClickListener(this);
        mShrinkImg.setOnClickListener(this);
        mExpandImg.setOnClickListener(this);
        pause.setOnClickListener(this);
        mSeekBar.setOnSeekBarChangeListener(this);
    }

    /**
     * 设置播放列表
     *
     * @param mVideoBeanList
     */
    public void setPlayList(List<VideoBean> mVideoBeanList) {
        this.mVideoBeanList = mVideoBeanList;
        adapterVideoController = new AdapterVideoController(mContext, mVideoBeanList);
        lv_play_menu.setAdapter(adapterVideoController);
        adapterVideoController.notifyDataSetChanged();
    }


    /**
     * 列表点击选择
     *
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        mPlayListSelecter.selecter(mVideoBeanList, mVideoBeanList.get(position));
        this.mVideoBeanList.get(position).setIsPlayNow(true);
        for (int i = 0; i < mVideoBeanList.size(); i++) {
            if (position == i) {
                mVideoBeanList.get(i).setIsPlayNow(true);
            } else {
                mVideoBeanList.get(i).setIsPlayNow(false);
            }
        }
        adapterVideoController.notifyDataSetChanged();
        closeRightMenu();
    }

    /**
     * 设置控制器接口
     *
     * @param mediaController
     */
    public void setMediaControllerImpl(MediaControllerImpl mediaController) {
        this.mMediaController = mediaController;
    }

    /**
     * 设置播放选择接口
     *
     * @param mPlayListSelecter
     */
    public void setPlayListSelecter(PlayListSelecter mPlayListSelecter) {
        this.mPlayListSelecter = mPlayListSelecter;
    }

    /**
     * 显示时间进度
     *
     * @param playSecond
     * @param allSecond
     */
    public void setProgressText(int playSecond, int allSecond) {
        String playSecondStr = "00:00";
        String allSecondStr = "00:00";
        if (playSecond > 0) {
            playSecondStr =
                    new SimpleDateFormat("mm:ss").format(new Date(playSecond));
        }
        if (allSecond > 0) {
            allSecondStr = new SimpleDateFormat("mm:ss").format(new Date(allSecond));
        }
        tv_time.setText(playSecondStr + "/" + allSecondStr);
    }

    /**
     * 设置视频名称
     *
     * @param str_videoName
     */
    public void setVideoTitle(String str_videoName) {
        tv_title.setText(str_videoName);
    }

    /**
     * 设置更新进度条
     *
     * @param progress
     * @param secondProgress
     */
    public void setProgressBar(int progress, int secondProgress) {
        if (progress < 0) progress = 0;
        if (progress > 100) progress = 100;
        if (secondProgress < 0) secondProgress = 0;
        if (secondProgress > 100) secondProgress = 100;
        mSeekBar.setProgress(progress);
        mSeekBar.setSecondaryProgress(secondProgress);
    }

    /**
     * 设置播放状态
     *
     * @param state
     */
    public void setPlayState(PlayState state) {
        this.mPlayState = state;
        pause.setImageResource(state.equals(PlayState.PLAY) ? R.drawable.biz_video_pause : R.drawable.biz_video_play);
        iv_play.setVisibility(state.equals(PlayState.PLAY) ? GONE : VISIBLE);
    }

    /**
     * 获取播放状态
     *
     * @return
     */
    public PlayState getPlayState() {
        return mPlayState;
    }

    /**
     * 切换页面横竖屏幕
     *
     * @param pageType
     */
    public void setPageType(PageType pageType) {
        mExpandImg.setVisibility(pageType.equals(PageType.EXPAND) ? GONE : VISIBLE);
        mShrinkImg.setVisibility(pageType.equals(PageType.SHRINK) ? GONE : VISIBLE);
    }

    /**
     * 总是关闭控制器
     */
    public void allwaysCloseController() {
        if (getVisibility() == VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(mContext,
                    R.anim.anim_exit_from_bottom);
            rl_bottom.startAnimation(animation);
            animation.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setVisibility(View.GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
            Animation animation1 = AnimationUtils.loadAnimation(mContext, R.anim.anim_exit_from_top);
            rl_top.startAnimation(animation1);
            animation1.setAnimationListener(new Animation.AnimationListener() {
                @Override
                public void onAnimationStart(Animation animation) {

                }

                @Override
                public void onAnimationEnd(Animation animation) {
                    setVisibility(GONE);
                }

                @Override
                public void onAnimationRepeat(Animation animation) {

                }
            });
        }
    }

    /**
     * 保持显示控制器
     */
    public void allwaysShowController() {
        if (getVisibility() == GONE) {
            setVisibility(View.VISIBLE);
            rl_bottom.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(mContext,
                    R.anim.anim_enter_from_bottom);
            rl_bottom.startAnimation(animation);
            setVisibility(VISIBLE);
            rl_top.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(mContext, R.anim.anim_enter_from_top);
            rl_top.startAnimation(animation1);
        }
    }


    /***
     * 显示或者隐藏控制器
     */
    public void showOrHideController() {
        //菜单隐藏情况
        if (getVisibility() == View.VISIBLE) {
            Animation animation = AnimationUtils.loadAnimation(mContext,
                    R.anim.anim_exit_from_bottom);
            if (rl_right_menu.getVisibility() == VISIBLE) {
                closeRightMenu();
            }
            rl_bottom.startAnimation(animation);
            animation.setAnimationListener(mAnimationListener);
            Animation animation1 = AnimationUtils.loadAnimation(mContext, R.anim.anim_exit_from_top);
            rl_top.startAnimation(animation1);
            animation1.setAnimationListener(mAnimationListener);
        } else {
            setVisibility(View.VISIBLE);
            rl_bottom.clearAnimation();
            Animation animation = AnimationUtils.loadAnimation(mContext,
                    R.anim.anim_enter_from_bottom);
            rl_bottom.startAnimation(animation);
            setVisibility(VISIBLE);
            rl_top.clearAnimation();
            Animation animation1 = AnimationUtils.loadAnimation(mContext, R.anim.anim_enter_from_top);
            rl_top.startAnimation(animation1);
        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.pause) {
            mMediaController.onPlayOrPause();
        }
        if (v.getId() == R.id.expand) {
            mMediaController.onPageTurn(PageType.EXPAND, mVideoBeanList);
        }
        if (v.getId() == R.id.shrink) {
            mMediaController.onPageTurn(PageType.SHRINK, mVideoBeanList);
        }
        if (v.getId() == R.id.btn_showmenu) {
            if (rl_right_menu.getVisibility() == VISIBLE) {
                closeRightMenu();
            } else {
                showRightMenu();
            }
        }
        if (v.getId() == R.id.iv_play) {
            mMediaController.onPlayOrPause();
            iv_play.setVisibility(GONE);
        }
    }

    /**
     * 显示右侧列表栏
     */
    private void showRightMenu() {
        rl_right_menu.setVisibility(VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(mContext,
                R.anim.anim_enter_from_right);
        rl_right_menu.startAnimation(animation);
    }

    /**
     * 关闭右侧列表栏
     */
    private void closeRightMenu() {
        Animation animation = AnimationUtils.loadAnimation(mContext,
                R.anim.anim_exit_from_right);
        rl_right_menu.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                rl_right_menu.setVisibility(GONE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if (fromUser)
            mMediaController.onUpdateProgress(ProgressState.DOING, getPlayState(), progress);
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mMediaController.onUpdateProgress(ProgressState.START, getPlayState(), 0);
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mMediaController.onUpdateProgress(ProgressState.STOP, getPlayState(), 0);
    }


    private Animation.AnimationListener mAnimationListener = new Animation.AnimationListener() {
        @Override
        public void onAnimationStart(Animation animation) {

        }

        @Override
        public void onAnimationEnd(Animation animation) {
            setVisibility(GONE);
        }

        @Override
        public void onAnimationRepeat(Animation animation) {

        }
    };


    /**
     * 播放样式 展开、缩放
     */
    public enum PageType {
        EXPAND, SHRINK
    }

    /**
     * 播放状态 播放 暂停
     */
    public enum PlayState {
        PLAY, PAUSE
    }

    /**
     * 进度条状态
     */
    public enum ProgressState {
        START, DOING, STOP
    }

    /**
     * 媒体控制器接口
     */
    public interface MediaControllerImpl {

        void onUpdateProgress(ProgressState progressState, PlayState Playstate, int progress);

        void onPlayOrPause();

        void onPageTurn(PageType mpageType, List<VideoBean> videoBeanList);
    }

    /**
     * 播放选择接口
     */
    public interface PlayListSelecter {

        void selecter(List<VideoBean> videoBeanList, VideoBean mvideoBean);

    }

}
