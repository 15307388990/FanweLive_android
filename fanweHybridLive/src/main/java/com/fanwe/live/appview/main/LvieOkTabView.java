package com.fanwe.live.appview.main;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.fanwe.hybrid.activity.AppWebViewActivity;
import com.fanwe.hybrid.common.CommonOpenSDK;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.listner.PayResultListner;
import com.fanwe.lib.gridlayout.SDGridLayout;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.common.SDSelectManager;
import com.fanwe.library.listener.SDItemClickCallback;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.utils.SDTypeParseUtil;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveChatC2CActivity;
import com.fanwe.live.activity.LiveSearchUserActivity;
import com.fanwe.live.adapter.LiveRechargePaymentAdapter;
import com.fanwe.live.adapter.LiveRechrgeDiamondsPaymentRuleAdapter;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.model.App_payActModel;
import com.fanwe.live.model.App_rechargeActModel;
import com.fanwe.live.model.PayItemModel;
import com.fanwe.live.model.RuleItemModel;
import com.fanwe.live.view.pulltorefresh.IPullToRefreshViewWrapper;
import com.fanwe.xianrou.appview.QKSmallVideoListView;
import com.fanwei.jubaosdk.shell.OnPayResultListener;

import org.xutils.view.annotation.ViewInject;

import java.math.BigDecimal;
import java.util.List;

public class LvieOkTabView extends LiveTabBaseView {
    public LvieOkTabView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LvieOkTabView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LvieOkTabView(Context context) {
        super(context);
        init();
    }
    @ViewInject(R.id.ll_search)
    private LinearLayout ll_search;// 搜索

    @ViewInject(R.id.ll_chat)
    private RelativeLayout ll_chat;

    @ViewInject(R.id.fl_small_video)
    private FrameLayout fl_small_video;

    private QKSmallVideoListView smallVideoListView;

    private void init()
    {
        setContentView(R.layout.qk_frag_tab_small_video);
        setBtnOnClick();
        fl_small_video.addView(getSmallVideoListView());
    }

    private void setBtnOnClick()
    {
        ll_search.setOnClickListener(this);
        ll_chat.setOnClickListener(this);
    }

    private QKSmallVideoListView getSmallVideoListView()
    {
        if (null == smallVideoListView)
        {
            smallVideoListView = new QKSmallVideoListView(getActivity());
        }
        return smallVideoListView;
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        switch (v.getId())
        {
            case R.id.ll_search:
                clickLLSearch();
                break;
            case R.id.ll_chat:
                clickLlChat();
                break;
            default:
                break;
        }
    }

    // 搜索
    private void clickLLSearch()
    {
        Intent intent = new Intent(getActivity(), LiveSearchUserActivity.class);
        getActivity().startActivity(intent);
    }

    //聊天
    private void clickLlChat()
    {
        Intent intent = new Intent(getActivity(), LiveChatC2CActivity.class);
        getActivity().startActivity(intent);
    }

    @Override
    protected void onRoomClosed(int roomId) {

    }

    @Override
    public void scrollToTop() {

    }

    @Override
    protected void onLoopRun() {

    }
}
