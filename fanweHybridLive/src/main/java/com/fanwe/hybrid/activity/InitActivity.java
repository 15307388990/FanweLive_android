package com.fanwe.hybrid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import com.fanwe.hybrid.event.ERetryInitSuccess;
import com.fanwe.live.R;
import com.fanwe.live.business.InitBusiness;
import com.fanwe.xianrou.activity.QKMySmallVideoActivity;

/**
 * @author 作者 E-mail:
 * @version 创建时间：2015-12-16 下午4:39:42 类说明 启动页
 */
public class InitActivity extends BaseActivity
{
    private InitBusiness mInitBusiness;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setFullScreen(true);
        setContentView(R.layout.act_init);

        mInitBusiness = new InitBusiness();
        mInitBusiness.init(this);
    }

    public void onEventMainThread(ERetryInitSuccess event)
    {
        InitBusiness.dealInitLaunchBusiness(this);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mInitBusiness.onDestroy();
        mInitBusiness = null;
    }
}
