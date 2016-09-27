package com.iwind.libvideoview.player;

import java.io.Serializable;

/**
 * 作者：HuGuoJun
 * 2016/9/23 15:00
 * 邮箱：www.guojunkuaile@qq.com
 */
public class VideoBean implements Serializable {
    //播放地址
    private String URL;
    //视频名称
    private String name;
    //是否正在播放
    private boolean isPlayNow;

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isPlayNow() {
        return isPlayNow;
    }

    public void setIsPlayNow(boolean isPlayNow) {
        this.isPlayNow = isPlayNow;
    }

    @Override
    public String toString() {
        return "VideoBean{" +
                "isPlayNow=" + isPlayNow +
                ", name='" + name + '\'' +
                ", URL='" + URL + '\'' +
                '}';
    }
}
