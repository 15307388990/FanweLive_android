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
import com.fanwe.library.adapter.SDPagerAdapter;
import com.fanwe.library.utils.SDCollectionUtil;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.lib.viewpager.SDViewPager;
import com.fanwe.lib.viewpager.indicator.IPagerIndicatorItem;
import com.fanwe.lib.viewpager.indicator.adapter.PagerIndicatorAdapter;
import com.fanwe.lib.viewpager.indicator.impl.PagerIndicator;
import com.fanwe.library.webview.CustomWebView;
import com.fanwe.live.R;
import com.fanwe.live.activity.LiveChatC2CActivity;
import com.fanwe.live.activity.LiveSearchUserActivity;
import com.fanwe.live.activity.LiveWebViewActivity;
import com.fanwe.live.appview.BaseAppView;
import com.fanwe.live.appview.pagerindicator.LiveHomeTitleTab;
import com.fanwe.live.appview.title.LiveMainHomeTitleView;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.live.dialog.InviteRechargeDialog;
import com.fanwe.live.dialog.LiveSelectLiveDialog;
import com.fanwe.live.dialog.NoticeDialog;
import com.fanwe.live.event.EReSelectTabLiveBottom;
import com.fanwe.live.event.ESelectLiveFinish;
import com.fanwe.live.model.HomeTabTitleModel;
import com.fanwe.live.model.LiveFilterModel;
import com.fanwe.pay.activity.InviteRewardsActivity;
import com.fanwe.xianrou.appview.main.QKTabSmallVideoView;

import java.util.ArrayList;
import java.util.List;

/**
 * 首页-主页view
 */
public class LiveMainHomeView extends BaseAppView {
    public LiveMainHomeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LiveMainHomeView(Context context) {
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
        setContentView(R.layout.view_live_main_home);
        view_title = (LiveMainHomeTitleView) findViewById(R.id.view_title);

        vpg_content = (SDViewPager) findViewById(R.id.vpg_content);
        view_pager_indicator = (PagerIndicator) findViewById(R.id.view_pager_indicator);

        vpg_content.addOnPageChangeListener(mOnPageChangeListener);

        ImageView imageInvite = (ImageView) findViewById(R.id.image_invite);
        final InitActModel model = InitActModelDao.query();
        if (model.getInviter_is_open() == 1) {
            imageInvite.setVisibility(View.VISIBLE);
        } else {
            imageInvite.setVisibility(View.GONE);
        }
        Glide.with(getActivity()).load(model.getInviter_img_url()).asGif().into(imageInvite);
        imageInvite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(), AppWebViewActivity.class);
//                intent.putExtra(AppWebViewActivity.EXTRA_URL, model.getInvite_url());
//               getActivity().startActivity(intent);
                getActivity().startActivity(new Intent(getContext(), InviteRewardsActivity.class));
            }
        });

        if (model.getNotice_is_open() == 1) {
            showInviteRechargeDialog(model.getNotice_url());
        }
        initTitle();
        initTabsData();
        initViewPagerIndicator();
        initViewPager();
    }

    /**
     * 显示系统公告
     */
    private void showInviteRechargeDialog(String url) {
        NoticeDialog mDialog = new NoticeDialog(getActivity(), url);
        mDialog.show();
    }


    private void initTitle() {
        view_title.setCallback(new LiveMainHomeTitleView.Callback() {
            @Override
            public void onClickSearch(View v) {
                clickSearch();
            }

            @Override
            public void onClickSelectLive(View v) {
                LiveSelectLiveDialog dialog = new LiveSelectLiveDialog(getActivity());
                dialog.show();
            }

            @Override
            public void onClickNewMsg(View v) {
                clickChatList();
            }
        });
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
        tabFollow.setTitle("关注");

        HomeTabTitleModel tabHot = new HomeTabTitleModel();
        LiveFilterModel model = LiveFilterModel.get();
        String city = model.getCity();
        tabHot.setTitle(city);

        HomeTabTitleModel tabNearby = new HomeTabTitleModel();
        tabNearby.setTitle("附近");

        HomeTabTitleModel smallVdeo = new HomeTabTitleModel();
        smallVdeo.setTitle(AppRuntimeWorker.getSmallvideoName());

        HomeTabTitleModel tabClub = new HomeTabTitleModel();
        tabClub.setTitle(AppRuntimeWorker.getSociatyNmae());

        mListModel.add(tabFollow);
        mListModel.add(tabHot);
        mListModel.add(tabNearby);

        if (AppRuntimeWorker.getSmallvideoIsOpen() == 1
                && !TextUtils.isEmpty(AppRuntimeWorker.getSmallvideoName())) {
            mListModel.add(smallVdeo);
        }

        if (AppRuntimeWorker.getOpen_sociaty_module() == 1
                && !TextUtils.isEmpty(AppRuntimeWorker.getSociatyNmae())) {
            mListModel.add(tabClub);
        }

        InitActModel initActModel = InitActModelDao.query();
        if (initActModel != null) {
            List<HomeTabTitleModel> listTab = initActModel.getVideo_classified();
            if (listTab != null && !listTab.isEmpty()) {
                mListModel.addAll(listTab);
            }
        }
    }

    private void initViewPager() {
        vpg_content.setOffscreenPageLimit(2);
        vpg_content.setAdapter(mPagerAdapter);

        vpg_content.setCurrentItem(1);
    }

    private OnPageChangeListener mOnPageChangeListener = new OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        }

        @Override
        public void onPageSelected(int position) {
            mSelectTitleModel = mListModel.get(position);
            if (position == 1) {
                SDViewUtil.setVisible(view_title.getViewSelectLive());
            } else {
                SDViewUtil.setInvisible(view_title.getViewSelectLive());
            }
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
            HomeTabTitleModel model = mListModel.get(position);
            ////
            switch (position) {
                case 0://关注
                    view = new LiveTabFollowView(getActivity());
                    break;
                case 1://热门
                    view = new LiveTabHotView(getActivity());
                    break;
                case 2://附近
                    view = new LiveTabNearbyView(getActivity());
                    break;
                case 3:
                    //后台自定义分类 修改过by:
                    if (model.getType() != null && model.getType() == 1) {
                        LiveTabCategoryWebviewView webview = new LiveTabCategoryWebviewView(getActivity(), model.getUrl());
                        //webview.setUrl(model.getUrl());
                        view = webview;
                        ////
                    } else if (model.getTitle().equals(AppRuntimeWorker.getSmallvideoName())) {
                        view = new LvieOkTabView(getActivity());
                    } else {
                        //默认代码  获取后台设置的 列表名 展示 直播信息
                        if (AppRuntimeWorker.getOpen_sociaty_module() == 1 && !TextUtils.isEmpty(AppRuntimeWorker.getSociatyNmae())) {
                            //获取工会等问题
                            view = new LiveTabClubView(getActivity());
                        } else {
                            LiveTabCategoryView tabView = new LiveTabCategoryView(getActivity());
                            tabView.setTabTitleModel(mListModel.get(position));
                            view = tabView;
                        }
                    }
                    break;
                default:
                    //后台自定义分类
                    //model.getType() 等于0主播列表， 等于1 URL链接
                    if (model.getType() == null || model.getType() == 0) {
                        //默认代码  获取后台设置的 列表名 展示 直播信息
                        LiveTabCategoryView tabView = new LiveTabCategoryView(getActivity());
                        tabView.setTabTitleModel(model);
                        view = tabView;
                    } else if (model.getType() == 1) {
                        //获取后台自定义链接
                        LiveTabCategoryWebviewView webview = new LiveTabCategoryWebviewView(getActivity(), model.getUrl());
                        //webview.setUrl(model.getUrl());
                        //使用浏览器打开
                        view = webview;
                    }
                    ////


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
                LiveHomeTitleTab item = new LiveHomeTitleTab(getActivity());
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
            LiveHomeTitleTab tab = (LiveHomeTitleTab) item;
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

    /**
     * 私聊列表
     */
    private void clickChatList() {
        Intent intent = new Intent(getActivity(), LiveChatC2CActivity.class);
        getActivity().startActivity(intent);
    }

    /**
     * 搜索
     */
    private void clickSearch() {
        Intent intent = new Intent(getActivity(), LiveSearchUserActivity.class);
        getActivity().startActivity(intent);
    }
}
