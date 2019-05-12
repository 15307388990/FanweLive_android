package com.fanwe.hybrid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.fanwe.hybrid.event.ERetryInitSuccess;
import com.fanwe.live.R;
import com.fanwe.live.business.InitBusiness;
import com.fanwe.xianrou.activity.QKMySmallVideoActivity;
import com.fm.openinstall.OpenInstall;
import com.fm.openinstall.listener.AppWakeUpAdapter;
import com.fm.openinstall.model.AppData;

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

        //获取唤醒参数
        OpenInstall.getWakeUp(getIntent(), wakeUpAdapter);
    }

    public void onEventMainThread(ERetryInitSuccess event)
    {
        InitBusiness.dealInitLaunchBusiness(this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // 此处要调用，否则App在后台运行时，会无法截获
        OpenInstall.getWakeUp(intent, wakeUpAdapter);
    }

    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        mInitBusiness.onDestroy();
        mInitBusiness = null;
        wakeUpAdapter = null;
    }
    AppWakeUpAdapter wakeUpAdapter = new AppWakeUpAdapter() {
        @Override
        public void onWakeUp(AppData appData) {
            //获取渠道数据
            String channelCode = appData.getChannel();
            //获取绑定数据
            String bindData = appData.getData();
            Log.d("OpenInstall", "getWakeUp : wakeupData = " + appData.toString());
        }
    };
}
