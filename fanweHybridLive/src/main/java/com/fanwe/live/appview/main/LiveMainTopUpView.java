package com.fanwe.live.appview.main;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.fanwe.hybrid.dao.InitActModelDao;
import com.fanwe.hybrid.event.ERetryInitSuccess;
import com.fanwe.hybrid.model.InitActModel;
import com.fanwe.lib.viewpager.SDViewPager;
import com.fanwe.lib.viewpager.indicator.IPagerIndicatorItem;
import com.fanwe.lib.viewpager.indicator.adapter.PagerIndicatorAdapter;
import com.fanwe.lib.viewpager.indicator.impl.PagerIndicator;
import com.fanwe.library.adapter.SDPagerAdapter;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveChatC2CActivity;
import com.fanwe.live.activity.LiveSearchUserActivity;
import com.fanwe.live.appview.BaseAppView;
import com.fanwe.live.appview.pagerindicator.LiveTopUpTitleTab;
import com.fanwe.live.appview.title.LiveMainHomeTitleView;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.dialog.LiveSelectLiveDialog;
import com.fanwe.live.dialog.NoticeDialog;
import com.fanwe.live.event.EReSelectTabLiveBottom;
import com.fanwe.live.event.ESelectLiveFinish;
import com.fanwe.live.model.HomeTabTitleModel;
import com.fanwe.live.model.LiveFilterModel;
import com.fanwe.pay.activity.InviteRewardsActivity;

import java.util.ArrayList;
import java.util.List;
/**
 * @author luoming
 *created at 2019/4/16 7:14 PM
 * 充值
*/
public class LiveMainTopUpView extends BaseAppView {
    public LiveMainTopUpView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveMainTopUpView(Context context) {
        super(context);
        init();
    }

    private LiveMainHomeTitleView view_title;

    private SDViewPager vpg_content;
    private PagerIndicator view_pager_indicator;

    private List<HomeTabTitleModel> mListModel = new ArrayList<>();

    private SparseArray<LiveTabBaseView> mArrContentView = new SparseArray<>();

    private HomeTabTitleModel mSelectTitleModel;

    protected void init() {
        setContentView(R.layout.view_live_main_top_up);

        vpg_content = (SDViewPager) findViewById(R.id.vpg_content);
        view_pager_indicator = (PagerIndicator) findViewById(R.id.view_pager_indicator);

        vpg_content.addOnPageChangeListener(mOnPageChangeListener);

        initTabsData();
        initViewPagerIndicator();
        initViewPager();
    }


    public void onEventMainThread(ERetryInitSuccess event) {
        initTabsData();
        mPagerAdapter.notifyDataSetChanged();
        dealLastSelected();
    }

    private void dealLastSelected() {
        int index = mListModel.indexOf(mSelectTitleModel);
        if (index < 0) {
            index = 1;
        }

        vpg_content.setCurrentItem(index);
    }

    private void initTabsData() {
        mListModel.clear();

        HomeTabTitleModel tabFollow = new HomeTabTitleModel();
        tabFollow.setTitle("钻石充值");

        HomeTabTitleModel tabNearby = new HomeTabTitleModel();
        tabNearby.setTitle("VIP会员");
        mListModel.add(tabFollow);
        mListModel.add(tabNearby);

    }

    private void initViewPager() {
        vpg_content.setOffscreenPageLimit(2);
        vpg_content.setAdapter(mPagerAdapter);
        vpg_content.setCurrentItem(0);
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mSelectTitleModel = mListModel.get(position);

        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }
    };

    private SDPagerAdapter mPagerAdapter = new SDPagerAdapter<HomeTabTitleModel>(mListModel, getActivity()) {
        @Override
        public View getView(ViewGroup container, int position) {
            LiveTabBaseView view = null;
            //后台自定义分类 修改过by:
            switch (position) {
                case 0://钻石会员
                    view = new LvieRechargeView(getActivity());
                    break;
                case 1://VIP会员
                    view = new LvieVipView(getActivity());
                    break;
            }
            if (view != null) {
                mArrContentView.put(position, view);
                view.setParentViewPager(vpg_content);
            }

            return view;
        }
    };

    private void initViewPagerIndicator() {
        view_pager_indicator.setViewPager(vpg_content);
        view_pager_indicator.setAdapter(new PagerIndicatorAdapter() {
            @Override
            protected IPagerIndicatorItem onCreatePagerIndicatorItem(int position, ViewGroup viewParent) {
                LiveTopUpTitleTab item = new LiveTopUpTitleTab(getActivity());
                HomeTabTitleModel model = SDCollectionUtil.get(mListModel, position);
                item.setData(model);
                return item;
            }
        });
    }

    public void onEventMainThread(ESelectLiveFinish event) {
        String text = event.model.getCity();

        IPagerIndicatorItem item = view_pager_indicator.getPagerIndicatorItem(1);
        if (item != null) {
            LiveTopUpTitleTab tab = (LiveTopUpTitleTab) item;
            HomeTabTitleModel model = tab.getData();
            model.setTitle(text);
            tab.setData(model);
        }
    }

    public void onEventMainThread(EReSelectTabLiveBottom event) {
        if (event.index == 0) {
            int index = vpg_content.getCurrentItem();
            LiveTabBaseView view = mArrContentView.get(index);
            if (view != null) {
                view.scrollToTop();
            }
        }
    }


}
