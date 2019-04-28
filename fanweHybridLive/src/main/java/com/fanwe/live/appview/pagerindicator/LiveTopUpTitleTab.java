package com.fanwe.live.appview.pagerindicator;

import android.content.Context;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.fanwe.lib.viewpager.indicator.IPagerIndicatorItem;
import com.fanwe.lib.viewpager.indicator.model.PositionData;
import com.fanwe.library.utils.SDResourcesUtil;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.model.HomeTabTitleModel;
import com.fanwe.live.view.LiveTabUnderline;

/**
 * 充值分类标题Item
 */
public class LiveTopUpTitleTab extends LiveTabUnderline implements IPagerIndicatorItem {
    public LiveTopUpTitleTab(Context context) {
        super(context);
        init();
    }

    public LiveTopUpTitleTab(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private HomeTabTitleModel mData;

    private void init() {
       // setMinimumWidth(SDViewUtil.dp2px(23));
        int padding = SDViewUtil.dp2px(40);
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT,FrameLayout.LayoutParams.MATCH_PARENT);
        layoutParams.setMargins(padding, 0, padding, 0);
        getTextViewTitle().setLayoutParams(layoutParams);
        getTextViewTitle().setTextSize(16);
        getViewUnderline().setMinimumWidth(SDViewUtil.dp2px(23));

    }

    public void setData(HomeTabTitleModel data) {
        mData = data;
        if (data != null) {
            getTextViewTitle().setText(data.getTitle());

        }
    }

    public HomeTabTitleModel getData() {
        return mData;
    }

    @Override
    public void onSelectedChanged(boolean selected) {
        setSelected(selected);
    }

    @Override
    public void onShowPercent(float showPercent, boolean isEnter, boolean isMoveLeft) {

    }

    @Override
    public PositionData getPositionData() {
        return null;
    }
}
