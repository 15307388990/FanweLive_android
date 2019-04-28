package com.fanwe.live.business;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.fanwe.hybrid.activity.AdImgActivity;
import com.fanwe.hybrid.activity.InitAdvListActivity;
import com.fanwe.hybrid.activity.MainActivity;
import com.fanwe.hybrid.constant.ApkConstant;
import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.map.tencent.SDTencentMapManager;
import com.fanwe.hybrid.model.InitActModel;
import com.fanwe.hybrid.umeng.UmengSocialManager;
import com.fanwe.hybrid.utils.RetryInitWorker;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.lib.cache.SDDisk;
import com.fanwe.library.common.SDActivityManager;
import com.fanwe.lib.looper.impl.SDWaitRunner;
import com.fanwe.library.model.SDDelayRunnable;
import com.fanwe.library.utils.LogUtil;
import com.fanwe.library.utils.SDFileUtil;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveLoginActivity;
import com.fanwe.live.activity.LiveMainActivity;
import com.fanwe.live.activity.LiveMobielRegisterActivity;
import com.fanwe.live.common.AppDiskKey;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.common.HostManager;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.dialog.common.AppDialogProgress;
import com.fanwe.live.model.UserModel;

import java.util.ArrayList;

/**
 * 初始化界面业务
 */
public class InitBusiness extends BaseBusiness
{
    private Activity mActivity;

    public void init(Activity activity)
    {
        this.mActivity = activity;
        if (ApkConstant.DEBUG)
        {
            SDFileUtil.copyAnrToCache(activity);
        }

      UmengSocialManager.init(activity.getApplication());
        SDTencentMapManager.getInstance().startLocation(null);

        int init_delayed_time = activity.getResources().getInteger(R.integer.init_delayed_time);
        mDelayRunnable.runDelay(init_delayed_time);
    }

    private SDDelayRunnable mDelayRunnable = new SDDelayRunnable()
    {
        @Override
        public void run()
        {
            requestInit();
        }
    };

    /**
     * 请求初始化接口
     */
    private void requestInit()
    {
        CommonInterface.requestInit(new AppRequestCallback<InitActModel>()
        {
            @Override
            public String getCancelTag()
            {
                return getHttpCancelTag();
            }

            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk())
                {
                    dealInitLaunchBusiness(mActivity);
                } else
                {
                    onRequestInitError();
                }
            }

            @Override
            protected void onError(SDResponse resp)
            {
                super.onError(resp);
                onRequestInitError();
            }
        });
    }

    /**
     * 初始化接口失败回调
     */
    private void onRequestInitError()
    {
        HostManager.getInstance().findAvailableHost();
        RetryInitWorker.getInstance().start();
    }

    /**
     * 处理初始化成功后启动跳转逻辑
     */
    public static void dealInitLaunchBusiness(Activity activity)
    {
        //启动本地广告图
        boolean isFirstOpenApp = SDDisk.open().getBoolean(AppDiskKey.IS_FIRST_OPEN_APP, true);
        boolean is_open_adv = activity.getResources().getBoolean(R.bool.is_open_adv);
        if (isFirstOpenApp && is_open_adv)
        {
            ArrayList<String> array = new ArrayList<>();
            String[] adv_img_array = activity.getResources().getStringArray(R.array.adv_img_array);
            for (int i = 0; i < adv_img_array.length; i++)
            {
                array.add(adv_img_array[i]);
            }
            startInitAdvList(activity, array);
            return;
        }

        startAdImgActivityOrLiveMainActivity(activity);
    }

    private static void startAdImgActivityOrLiveMainActivity(Activity activity)
    {
        String mImgUrl = "";
        try
        {
            InitActModel model = InitActModelDao.query();
            if (model.getStart_diagram().size() != 0)
            {
                mImgUrl = model.getStart_diagram().get(0).getImage();
            }
        } catch (Exception e)
        {
            e.printStackTrace();
        }

        //如果图片有缓存显示InitActivity，否则显示rLiveMainActivity
        if (!TextUtils.isEmpty(mImgUrl))
        {
            startAdImgActivity(activity);
        } else
        {
            startMainOrLogin(activity);
        }
    }

    /**
     * 启动主界面或者登陆界面
     *
     * @param activity
     */
    public static void startMainOrLogin(Activity activity)
    {
        if (AppRuntimeWorker.getIsOpenWebviewMain())
        {
            Intent intent = new Intent(activity, MainActivity.class);
            activity.startActivity(intent);
            activity.finish();
            return;
        }

        UserModel user = UserModelDao.query();
        if (user != null)
        {
            startMainActivity(activity);
        } else
        {
            startLoginActivity(activity);
        }
    }

    /**
     * 启动主界面
     *
     * @param activity
     */
    public static void startMainActivity(final Activity activity)
    {
        if (ApkConstant.hasUpdateAeskeyHttp())
        {
            startMainActivityInternal(activity);
        } else
        {
            final AppDialogProgress dialogProgress = new AppDialogProgress(activity);
            dialogProgress.setCancelable(false);
            dialogProgress.setCanceledOnTouchOutside(false);
            dialogProgress.show();

            AppRuntimeWorker.startContext();
            new SDWaitRunner().run(new Runnable()
            {
                @Override
                public void run()
                {
                    LogUtil.i("wait aes update success");
                    dialogProgress.dismiss();
                    startMainActivityInternal(activity);
                }
            }).condition(new SDWaitRunner.Condition()
            {
                @Override
                public boolean canRun()
                {
                    LogUtil.i("wait aes update");
                    return ApkConstant.hasUpdateAeskeyHttp();
                }
            }).setTimeout(-1).startWait();
        }
    }

    private static void startMainActivityInternal(Activity activity)
    {
        Intent intent = new Intent(activity, LiveMainActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 启动登陆界面
     *
     * @param activity
     */
    public static void startLoginActivity(Activity activity)
    {
        Intent intent = new Intent(activity, LiveLoginActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    private static void startInitAdvList(Activity activity, ArrayList<String> array)
    {
        Intent intent = new Intent(activity, InitAdvListActivity.class);
        intent.putStringArrayListExtra(InitAdvListActivity.EXTRA_ARRAY, array);
        activity.startActivity(intent);
        activity.finish();
    }

    private static void startAdImgActivity(Activity activity)
    {
        Intent intent = new Intent(activity, AdImgActivity.class);
        activity.startActivity(intent);
        activity.finish();
    }

    /**
     * 结束登录页
     */
    public static void finishLoginActivity()
    {
        SDActivityManager.getInstance().finishActivity(LiveLoginActivity.class);
    }

    public static void finishMobileRegisterActivity()
    {
        SDActivityManager.getInstance().finishActivity(LiveMobielRegisterActivity.class);
    }

    @Override
    protected BaseBusinessCallback getBaseBusinessCallback()
    {
        return null;
    }

    /**
     * 销毁
     */
    public void onDestroy()
    {
        mActivity = null;
        mDelayRunnable.removeDelay();
        super.onDestroy();
    }
}
