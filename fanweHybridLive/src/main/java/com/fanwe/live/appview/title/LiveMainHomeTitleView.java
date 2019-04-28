package com.fanwe.live.appview.title;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.TextView;

import com.fanwe.live.R;
import com.fanwe.live.appview.BaseAppView;

/**
 * 首页-主页标题栏view
 */
public class LiveMainHomeTitleView extends BaseAppView
{
    public LiveMainHomeTitleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public LiveMainHomeTitleView(Context context)
    {
        super(context);
        init();
    }

    private View view_search;
    private TextView tv_title_name;
    private View view_select_live;
    private View view_new_msg;

    private Callback mCallback;

    public void setCallback(Callback callback)
    {
        mCallback = callback;
    }

    private void init()
    {
        setContentView(R.layout.view_live_main_home_title);
        view_search = findViewById(R.id.view_search);
        tv_title_name = (TextView) findViewById(R.id.tv_title_name);
        view_select_live = findViewById(R.id.view_select_live);
        view_new_msg = findViewById(R.id.view_new_msg);

        view_search.setOnClickListener(this);
        view_select_live.setOnClickListener(this);
        view_new_msg.setOnClickListener(this);
    }

    public View getViewSelectLive()
    {
        return view_select_live;
    }

    public TextView getTextViewTitleName()
    {
        return tv_title_name;
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        if (v == view_search)
        {
            getCallback().onClickSearch(v);
        } else if (v == view_select_live)
        {
            getCallback().onClickSelectLive(v);
        } else if (v == view_new_msg)
        {
            getCallback().onClickNewMsg(v);
        }
    }

    public Callback getCallback()
    {
        if (mCallback == null)
        {
            mCallback = new Callback()
            {
                @Override
                public void onClickSearch(View v)
                {

                }

                @Override
                public void onClickSelectLive(View v)
                {

                }

                @Override
                public void onClickNewMsg(View v)
                {

                }
            };
        }
        return mCallback;
    }

    public interface Callback
    {
        void onClickSearch(View v);

        void onClickSelectLive(View v);

        void onClickNewMsg(View v);
    }
}
