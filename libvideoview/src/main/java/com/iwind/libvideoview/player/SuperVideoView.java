package com.iwind.libvideoview.player;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.VideoView;

/**
 * 作者：HuGuoJun
 * 2016/9/20 09:37
 * 邮箱：www.guojunkuaile@qq.com
 */
public class SuperVideoView extends VideoView {


    private int videoWidth;
    private int videoHeight;


    public SuperVideoView(Context context) {
        super(context);
    }

    public SuperVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public SuperVideoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }


    public void setVideoWidth(int videoWidth) {
        this.videoWidth = videoWidth;
    }

    public void setVideoHeight(int videoHeight) {
        this.videoHeight = videoHeight;
    }

}
