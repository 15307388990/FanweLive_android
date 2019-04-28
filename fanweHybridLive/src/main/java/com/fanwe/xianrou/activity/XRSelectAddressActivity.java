package com.fanwe.xianrou.activity;

import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.fanwe.hybrid.activity.BaseTitleActivity;
import com.fanwe.hybrid.map.tencent.SDTencentGeoCode;
import com.fanwe.hybrid.map.tencent.SDTencentMapManager;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.library.view.SDRecyclerView;
import com.fanwe.live.R;
import com.fanwe.live.common.AppRuntimeWorker;
import com.fanwe.xianrou.adapter.XRSelectAddressAdapter;
import com.fanwe.xianrou.common.XRAppRumTimeData;
import com.fanwe.xianrou.dialog.XRSelectAddressDialog;
import com.fanwe.xianrou.event.XRESelectAddressSuccess;
import com.fanwe.xianrou.interfaces.XRCommonItemClickCallback;
import com.sunday.eventbus.SDEventManager;
import com.tencent.lbssearch.object.result.Geo2AddressResultObject;
import com.tencent.lbssearch.object.result.SuggestionResultObject;
import com.tencent.map.geolocation.TencentLocation;
import com.tencent.map.geolocation.TencentLocationListener;
import com.tencent.mapsdk.raster.model.LatLng;

import org.xutils.view.annotation.ViewInject;

import java.util.List;

/**
 * Created by yhz on 2017/4/4 0004.选择位置
 */

public class XRSelectAddressActivity extends BaseTitleActivity
{
    @ViewInject(R.id.ll_dialog)
    private LinearLayout ll_dialog;
    @ViewInject(R.id.et_search)
    private EditText et_search;
    @ViewInject(R.id.ll_is_show_location)
    private LinearLayout ll_is_show_location;
    @ViewInject(R.id.ll_city)
    private LinearLayout ll_city;
    @ViewInject(R.id.tv_city)
    private TextView tv_city;
    @ViewInject(R.id.rv_content)
    private SDRecyclerView rv_content;

    private SDTencentGeoCode mGeoCode;
    private List<SuggestionResultObject.SuggestionData> mListModel;
    private XRSelectAddressAdapter adapter;
    private String mProvince;
    private String mCity;
    private String mDistrict;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.xr_act_select_address);
        init();
    }

    private void init()
    {
        initTitle();
        initView();
        initData();
        initLocation();
        initSearchGeoCode();
    }

    private void initTitle()
    {
        mTitle.setMiddleTextTop("所在位置");
    }

    private void initView()
    {
        ll_city.setOnClickListener(this);
        ll_is_show_location.setOnClickListener(this);
        et_search.setOnClickListener(this);
        et_search.setFocusable(false);
    }

    private void initData()
    {
        adapter = new XRSelectAddressAdapter(getActivity());
        rv_content.setAdapter(adapter);
        adapter.setItemClickCallback(new XRCommonItemClickCallback<SuggestionResultObject.SuggestionData>()
        {
            @Override
            public void onItemClick(View itemView, SuggestionResultObject.SuggestionData entity, int position)
            {
                if (!TextUtils.isEmpty(mCity))
                {
                    XRESelectAddressSuccess event = new XRESelectAddressSuccess();
                    event.province=mProvince;
                    event.city=mCity;
                    event.district=mDistrict;
                    event.address = entity.title;
                    SDEventManager.post(event);
                    finish();
                }
            }
        });
        if (XRAppRumTimeData.getInstance().mListModel != null)
        {
            this.mListModel = XRAppRumTimeData.getInstance().mListModel;
            adapter.updateData(mListModel);
        }
    }

    private void initSearchGeoCode()
    {
        mGeoCode = new SDTencentGeoCode(getActivity());
    }

    @Override
    public void onClick(View v)
    {
        super.onClick(v);
        if (v == ll_is_show_location)
        {
            clickLlIsShowLocation();
        } else if (v == ll_city)
        {
            clickLlCity();
        } else if (v == et_search)
        {
            clickEtSearch();
        }
    }

    private void clickLlIsShowLocation()
    {
        XRESelectAddressSuccess event = new XRESelectAddressSuccess();
        event.isShowLocation=false;
        SDEventManager.post(event);
        finish();
    }

    private void clickLlCity()
    {
        if (!TextUtils.isEmpty(mCity))
        {
            XRESelectAddressSuccess event = new XRESelectAddressSuccess();
            event.province=mProvince;
            event.city=mCity;
            event.district=mDistrict;
            event.address = "";
            SDEventManager.post(event);
            finish();
        }
    }

    private void clickEtSearch()
    {
        SDViewUtil.setGone(ll_dialog);
        XRSelectAddressDialog dialog = new XRSelectAddressDialog(getActivity());
        dialog.setOnDismissListener(new DialogInterface.OnDismissListener()
        {
            @Override
            public void onDismiss(DialogInterface dialogInterface)
            {
                SDViewUtil.setVisible(ll_dialog);
            }
        });
        dialog.setSelectAddressListener(new XRSelectAddressDialog.SelectAddressListener()
        {
            @Override
            public void onSuccessText(String text)
            {
                searchSuggesstion(text);
            }
        });
        dialog.showTop();
    }

    private void initLocation()
    {
        SDTencentMapManager.getInstance().startLocation(new TencentLocationListener()
        {
            @Override
            public void onLocationChanged(TencentLocation tencentLocation, int error, String s)
            {
                if (TencentLocation.ERROR_OK == error)
                {
                    new SDTencentGeoCode(getActivity()).location(new LatLng(tencentLocation.getLatitude(), tencentLocation.getLongitude())).geo2address(new SDTencentGeoCode.Geo2addressListener()
                    {
                        @Override
                        public void onSuccess(Geo2AddressResultObject.ReverseAddressResult result)
                        {
                            mCity = result.ad_info.city;
                            mProvince = result.ad_info.province;
                            mDistrict = result.ad_info.district;
                            tv_city.setText(result.ad_info.city);
                        }

                        @Override
                        public void onFailure(String msg)
                        {
                            tv_city.setText("无法获取到位置");
                        }
                    });

                } else
                {
                    tv_city.setText("无法获取到位置");
                }
            }

            @Override
            public void onStatusUpdate(String s, int i, String s1)
            {

            }
        });
    }

    private void searchSuggesstion(String content)
    {
        if (!TextUtils.isEmpty(content))
        {
            mGeoCode.region(AppRuntimeWorker.getCity_name()).keyword(content).suggestion(new SDTencentGeoCode.SuggestionListener()
            {

                @Override
                public void onSuccess(SuggestionResultObject result)
                {
                    mListModel = result.data;
                    XRAppRumTimeData.getInstance().mListModel = mListModel;
                    adapter.updateData(mListModel);
                }

                @Override
                public void onFailure(String msg)
                {

                }
            });
        }
    }
}
