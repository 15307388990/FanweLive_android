package com.fanwe.xianrou.adapter;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanwe.lib.dialog.ISDDialogConfirm;
import com.fanwe.lib.dialog.impl.SDDialogBase;
import com.fanwe.library.adapter.SDSimpleRecyclerAdapter;
import com.fanwe.library.adapter.viewholder.SDRecyclerViewHolder;
import com.fanwe.library.view.CircleImageView;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveRechargeVipActivity;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.dialog.common.AppDialogConfirm;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.utils.GlideUtil;
import com.fanwe.xianrou.manager.XRActivityLauncher;
import com.fanwe.xianrou.model.QKSmallVideoListModel;
import com.makeramen.roundedimageview.RoundedImageView;

import java.util.List;

/**
 * Created by Administrator on 2017/7/20.
 */

public class QKTabSmallVideoAdapter extends SDSimpleRecyclerAdapter<QKSmallVideoListModel>
{

    public QKTabSmallVideoAdapter(List<QKSmallVideoListModel> listModel, Activity activity)
    {
        super(listModel, activity);
    }

    @Override
    public int getLayoutId(ViewGroup parent, int viewType)
    {
        return R.layout.qk_item_tab_small_video;
    }

    @Override
    public void onBindData(SDRecyclerViewHolder<QKSmallVideoListModel> holder, final int position, final QKSmallVideoListModel model)
    {
        FrameLayout fl_root = holder.get(R.id.fl_root);//根布局
        ImageView iv_video_cover = holder.get(R.id.iv_video_cover);//小视频图片
        ImageView iv_avatar = holder.get(R.id.iv_avatar);//用户头像
        TextView tv_name = holder.get(R.id.tv_name);//小视频名字
        TextView tv_play_count = holder.get(R.id.tv_play_count);//观看小视频人数

        GlideUtil.load(model.getPhoto_image()).into(iv_video_cover);
        GlideUtil.loadHeadImage(model.getHead_image()).into(iv_avatar);
        tv_name.setText(model.getNick_name());
        tv_play_count.setText(model.getVideo_count());

        fl_root.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                //fix by xiaolin 小视频判断VIP
                UserModel userModel = UserModelDao.query();
                int is_vip=   userModel.getIs_vip();
                if(is_vip==1){
                    XRActivityLauncher.launchUserDynamicDetailVideo(getActivity(), model.getWeibo_id());
                }else{
                    AppDialogConfirm dialog = new AppDialogConfirm(getActivity());
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setTextContent("开通VIP即可观看小视频，是否购买VIP")
                         .setTextCancel("否").setTextConfirm("是")
                         .setCallback(new ISDDialogConfirm.Callback()
                         {
                             @Override
                             public void onClickCancel(View v, SDDialogBase dialog){
                                 //无动作
                             }

                             @Override
                             public void onClickConfirm(View v, SDDialogBase dialog)
                             {
                                 //跳转开VIP页面
                                 Intent intent = new Intent(getActivity(), LiveRechargeVipActivity.class);
                                 getActivity().startActivity(intent);

                             }
                         }).show();
             }
            }
        });
//        civ_head_image.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                QKActivitylauncher.launchUserHome(getActivity(), model.getUser_id());
//            }
//        });

    }


}
