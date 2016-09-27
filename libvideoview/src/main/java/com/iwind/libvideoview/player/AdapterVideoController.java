package com.iwind.libvideoview.player;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.iwind.libvideoview.R;

import java.util.List;

/**
 * 作者：HuGuoJun
 * 2016/9/23 15:21
 * 邮箱：www.guojunkuaile@qq.com
 */
public class AdapterVideoController extends BaseAdapter {

    private Context mContext;

    private List<VideoBean> mVideoBeanList;

    public AdapterVideoController(Context context, List<VideoBean> mVideoBeanList) {
        this.mVideoBeanList = mVideoBeanList;
        this.mContext = context;
    }

    @Override
    public int getCount() {
        return mVideoBeanList.size();
    }

    @Override
    public Object getItem(int position) {
        return mVideoBeanList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = LayoutInflater.from(mContext).inflate(R.layout.item_video, null);
            holder.tv_video_name = (TextView) convertView.findViewById(R.id.tv_video_name);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.tv_video_name.setText(mVideoBeanList.get(position).getName());
        holder.tv_video_name.setTextColor(mVideoBeanList.get(position).isPlayNow() ? Color.RED : Color.WHITE);


        return convertView;
    }

    class ViewHolder {
        TextView tv_video_name;
    }

}
