package com.sunfusheng.gank.viewprovider;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.sunfusheng.gank.App;
import com.sunfusheng.gank.R;
import com.sunfusheng.gank.model.GankItemGirl;
import com.sunfusheng.gank.util.DateUtil;
import com.sunfusheng.gank.widget.GildeImageView.GlideImageView;
import com.sunfusheng.gank.widget.MultiType.ItemViewProvider;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by sunfusheng on 2017/1/17.
 */
public class GankItemGirlViewProvider extends ItemViewProvider<GankItemGirl, GankItemGirlViewProvider.ViewHolder> {

    @NonNull
    @Override
    protected ViewHolder onCreateViewHolder(@NonNull LayoutInflater inflater, @NonNull ViewGroup parent) {
        View view = inflater.inflate(R.layout.item_gank_girl, parent, false);
        return new ViewHolder(view);
    }

    @Override
    protected void onBindViewHolder(@NonNull ViewHolder holder, @NonNull GankItemGirl item) {
        holder.rlGirl.setTag(true);
        holder.tvTime.setTypeface(App.songTi);
        holder.tvTime.setText(DateUtil.convertString2String(item.publishedAt));
        holder.givGirl.loadNetImage(item.url, R.mipmap.liuyifei);
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.rl_girl)
        RelativeLayout rlGirl;
        @BindView(R.id.giv_girl)
        GlideImageView givGirl;
        @BindView(R.id.tv_time)
        TextView tvTime;

        ViewHolder(View view) {
            super(view);
            ButterKnife.bind(this, view);
        }
    }
}