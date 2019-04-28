package com.fanwe.live.appview.main;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.GridView;

import com.fanwe.hybrid.activity.MainActivity;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.jshandler.AppJsHandler;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.common.SDHandlerManager;
import com.fanwe.library.model.SDTaskRunnable;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.library.webview.CustomWebView;
import com.fanwe.live.R;
import com.fanwe.live.adapter.LiveTabCategoryAdapter;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.model.HomeTabTitleModel;
import com.fanwe.live.model.Index_new_videoActModel;
import com.fanwe.live.model.LiveRoomModel;
import com.fanwe.live.view.pulltorefresh.IPullToRefreshViewWrapper;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * 首页直播分类view
 */
public class LiveTabCategoryWebviewView extends LiveTabBaseView
{
    public LiveTabCategoryWebviewView(Context context,String url)
    {
        super(context);
        this.url=url;
        init();
    }


    private CustomWebView mWeb;
    private boolean isScaleToShowAll=false;
    private String url;

    private void init()
    {
        setContentView(R.layout.act_app_webview);

        mWeb = (CustomWebView) findViewById(R.id.webview);

        initWebView();
    }

    private void initWebView()
    {
        mWeb.addJavascriptInterface(new AppJsHandler(getActivity()));
        mWeb.setScaleToShowAll(isScaleToShowAll);

        mWeb.setWebChromeClient(new WebChromeClient()
        {

            @Override
            public void onReceivedTitle(WebView view, String title)
            {
               //mTitle.setMiddleTextTop(view.getTitle());
            }

        });

        if (!TextUtils.isEmpty(url))
        {
            mWeb.get(url);
        }
        /*else if (!TextUtils.isEmpty(mHttmContent))
        {
            mWeb.loadData(mHttmContent, "text/html", "utf-8");
        }*/
    }


    @Override
    public void onActivityResumed(Activity activity)
    {
        super.onActivityResumed(activity);
       // initWebView();
    }

    /**
     * 当有直播间被关闭的时候回调
     *
     * @param roomId
     */
    protected  void onRoomClosed(int roomId){

    }

    @Override
    protected void onLoopRun()
    {

    }


    @Override
    public void scrollToTop()
    {

    }

}
