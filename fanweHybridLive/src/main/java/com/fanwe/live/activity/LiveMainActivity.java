package com.fanwe.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.fanwe.hybrid.activity.BaseActivity;
import com.fanwe.hybrid.app.App;
import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.model.InitActModel;
import com.fanwe.hybrid.service.AppUpgradeHelper;
import com.fanwe.lib.dialog.ISDDialogConfirm;
import com.fanwe.lib.dialog.impl.SDDialogBase;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.appview.main.LiveMainBottomNavigationView;
import com.fanwe.live.appview.main.LiveMainHomeView;
import com.fanwe.live.appview.main.LiveMainMeView;
import com.fanwe.live.appview.main.LiveMainRankingView;
import com.fanwe.live.appview.main.LiveMainRechargeView;
import com.fanwe.live.appview.main.LiveMainTopUpView;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dialog.LevelLoginFirstDialog;
import com.fanwe.live.dialog.LevelUpgradeDialog;
import com.fanwe.live.dialog.common.AppDialogConfirm;
import com.fanwe.live.event.EIMLoginError;
import com.fanwe.live.event.EImOnForceOffline;
import com.fanwe.live.event.EReSelectTabLiveBottom;
import com.fanwe.xianrou.activity.QKCreateEntranceActivity;
import com.fanwe.xianrou.appview.main.QKTabSmallVideoView;
import com.sunday.eventbus.SDEventManager;

public class LiveMainActivity extends BaseActivity
{
    private FrameLayout fl_main_content;
    private LiveMainHomeView mMainHomeView;
    private LiveMainRankingView mMainRankingView;
    private QKTabSmallVideoView mSmallVideoView;
    private LiveMainTopUpView mLiveMainTopUpView;
    private LiveMainMeView mMainMeView;

    private LiveMainBottomNavigationView mBottomNavigationView;

    @Override
    protected int onCreateContentView()
    {
        return R.layout.act_live_main;
    }

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);
        fl_main_content = (FrameLayout) findViewById(R.id.fl_main_content);
        mIsExitApp = true;
        checkUpdate();
        AppRuntimeWorker.startContext();
        CommonInterface.requestUser_apns(null);
        CommonInterface.requestMyUserInfo(null);

        checkVideo();

        initTabs();

        initUpgradeDialog();
        initLoginfirstDialog();
    }

    @Override
    protected void onNewIntent(Intent intent)
    {
        super.onNewIntent(intent);

        initUpgradeDialog();
        initLoginfirstDialog();
    }

    private void initUpgradeDialog()
    {
        LevelUpgradeDialog.check(this);
    }

    //首次登陆奖励和升级提示
    private void initLoginfirstDialog()
    {
        LevelLoginFirstDialog.check(this);
    }

    private void checkUpdate()
    {
        new AppUpgradeHelper(this).check(0);
    }

    private void initTabs()
    {
        mBottomNavigationView = (LiveMainBottomNavigationView) findViewById(R.id.view_bottom_navigation);
        mBottomNavigationView.setCallback(new LiveMainBottomNavigationView.Callback()
        {
            @Override
            public void onTabSelected(int index)
            {
                switch (index)
                {
                    case LiveMainBottomNavigationView.INDEX_HOME:
                        onSelectTabHome();
                        break;
                    case LiveMainBottomNavigationView.INDEX_RANKING:
                        onSelectTabRanking();
                        break;
                    case LiveMainBottomNavigationView.INDEX_SMALL_VIDEO:
                        onSelectTabSmallVideo();
                        break;
                    case LiveMainBottomNavigationView.INDEX_ME:
                        onSelectTabMe();
                        break;
                }
            }

            @Override
            public void onTabReselected(int index)
            {
                EReSelectTabLiveBottom event = new EReSelectTabLiveBottom();
                event.index = index;
                SDEventManager.post(event);
            }

            @Override
            public void onClickCreateLive(View view)
            {
                LiveMainActivity.this.onClickCreateLive();
            }
        });

        mBottomNavigationView.selectTab(LiveMainBottomNavigationView.INDEX_HOME);
    }

    public LiveMainHomeView getMainHomeView()
    {
        if (mMainHomeView == null)
        {
            mMainHomeView = new LiveMainHomeView(this);
        }
        return mMainHomeView;
    }

    public LiveMainRankingView getMainRankingView()
    {
        if (mMainRankingView == null)
        {
            mMainRankingView = new LiveMainRankingView(this);
        }
        return mMainRankingView;
    }

    public QKTabSmallVideoView getSmallVideoView()
    {
        if (mSmallVideoView == null)
        {
            mSmallVideoView = new QKTabSmallVideoView(this);
        }
        return mSmallVideoView;
    }

    public LiveMainTopUpView getMainTopUpView()
    {
        if (mLiveMainTopUpView == null)
        {
            mLiveMainTopUpView = new LiveMainTopUpView(this);
        }
        return mLiveMainTopUpView;
    }

    public LiveMainMeView getMainMeView()
    {
        if (mMainMeView == null)
        {
            mMainMeView = new LiveMainMeView(this);
        }
        return mMainMeView;
    }

    /**
     * 主页
     */
    protected void onSelectTabHome()
    {
        SDViewUtil.toggleView(fl_main_content, getMainHomeView());
    }

    /**
     * 排行榜
     */
    protected void onSelectTabRanking()
    {
        SDViewUtil.toggleView(fl_main_content, getMainRankingView());
    }

    /**
     * 小视频
     */
    protected void onSelectTabSmallVideo()
    {
        //SDViewUtil.toggleView(fl_main_content, getSmallVideoView());
        //fix by dog
        //Toast.makeText(this,"点击小视频按钮",Toast.LENGTH_SHORT).show();

        //Intent intent = new Intent(LiveMainActivity.this, LiveRechargeDiamondsActivity.class);
        //startActivity(intent);

        InitActModel model = InitActModelDao.query();
        int isOpen=model.getIs_open_smallvideo();

//        if(isOpen==1){
//            SDViewUtil.toggleView(fl_main_content, getSmallVideoView());
//        }else{
            SDViewUtil.toggleView(fl_main_content, getMainTopUpView());
//        }
    }

    /**
     * 个人中心
     */
    protected void onSelectTabMe()
    {
        SDViewUtil.toggleView(fl_main_content, getMainMeView());
    }

    private void onClickCreateLive()
    {
        startActivity(new Intent(LiveMainActivity.this, QKCreateEntranceActivity.class));
    }

    public void onEventMainThread(EIMLoginError event)
    {
        AppDialogConfirm dialog = new AppDialogConfirm(this);
        dialog.setCancelable(false);
        dialog.setCanceledOnTouchOutside(false);
        String content = "登录IM失败";
        if (!TextUtils.isEmpty(event.errMsg))
        {
            content = content + (event.errCode + event.errMsg);
        }
        dialog.setTextContent(content).setTextCancel("退出").setTextConfirm("重试");
        dialog.setCallback(new ISDDialogConfirm.Callback()
        {
            @Override
            public void onClickCancel(View v, SDDialogBase dialog)
            {
                App.getApplication().logout(false);
            }

            @Override
            public void onClickConfirm(View v, SDDialogBase dialog)
            {
                AppRuntimeWorker.startContext();
            }
        }).show();
    }

    /**
     * 异地登录
     *
     * @param event
     */
    public void onEventMainThread(EImOnForceOffline event)
    {
        AppDialogConfirm dialog = new AppDialogConfirm(this);
        dialog.setTextContent("你的帐号在另一台手机上登录");
        dialog.setTextCancel("退出");
        dialog.setTextConfirm("重新登录");
        dialog.setCallback(new ISDDialogConfirm.Callback()
        {
            @Override
            public void onClickCancel(View v, SDDialogBase dialog)
            {
                App.getApplication().logout(true);
            }

            @Override
            public void onClickConfirm(View v, SDDialogBase dialog)
            {
                AppRuntimeWorker.startContext();
            }
        }).show();
    }
}
