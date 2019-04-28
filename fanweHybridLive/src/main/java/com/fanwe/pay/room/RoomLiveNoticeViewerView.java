package com.fanwe.pay.room;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.CountDownTimer;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.appview.room.RoomView;
import com.fanwe.pay.model.App_live_live_pay_deductActModel;

/**
 * @author luoming
 * created at 2019/4/25 10:57 PM
 */

public class RoomLiveNoticeViewerView extends RoomView {

    private TextView tv_notice;
    private OnClickListener onClickListener;


    public RoomLiveNoticeViewerView(Context context) {
        super(context);
        init();
    }

    public RoomLiveNoticeViewerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public RoomLiveNoticeViewerView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    protected void init() {
        setContentView(R.layout.view_pay_notice);
        tv_notice = find(R.id.tv_notice);
        tv_notice.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                onClickListener.onClick(v);
            }
        });

    }

    public void setOnClickListener(@Nullable OnClickListener l) {
        this.onClickListener = l;
    }

    public void bindData(String notice) {
        SDViewBinder.setTextView(tv_notice, notice);
    }


}
