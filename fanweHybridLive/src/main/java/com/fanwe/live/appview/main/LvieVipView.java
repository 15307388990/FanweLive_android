package com.fanwe.live.appview.main;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fanwe.hybrid.activity.AppWebViewActivity;
import com.fanwe.hybrid.common.CommonOpenSDK;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.listner.PayResultListner;
import com.fanwe.hybrid.model.PaySdkModel;
import com.fanwe.lib.gridlayout.SDGridLayout;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.common.SDSelectManager;
import com.fanwe.library.customview.SDGridLinearLayout;
import com.fanwe.library.listener.SDItemClickCallback;
import com.fanwe.library.utils.SDResourcesUtil;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.utils.SDTypeParseUtil;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.adapter.LiveRechargePaymentAdapter;
import com.fanwe.live.adapter.LiveRechrgeDiamondsPaymentRuleAdapter;
import com.fanwe.live.adapter.LiveRechrgeVipPaymentRuleAdapter;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.dao.UserModelDao;
import com.fanwe.live.model.App_UserVipPurchaseActModel;
import com.fanwe.live.model.App_payActModel;
import com.fanwe.live.model.App_rechargeActModel;
import com.fanwe.live.model.PayItemModel;
import com.fanwe.live.model.PayModel;
import com.fanwe.live.model.RuleItemModel;
import com.fanwe.live.model.UserModel;
import com.fanwe.live.view.pulltorefresh.IPullToRefreshViewWrapper;
import com.fanwei.jubaosdk.shell.OnPayResultListener;

import org.xutils.view.annotation.ViewInject;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class LvieVipView extends LiveTabBaseView {
    public LvieVipView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public LvieVipView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LvieVipView(Context context) {
        super(context);
        init();
    }

    @ViewInject(R.id.tv_user_vip_deadline)
    private TextView tv_user_vip_deadline;

    @ViewInject(R.id.ll_payment)
    private SDGridLinearLayout ll_payment;

    @ViewInject(R.id.ll_payment_rule)
    private SDGridLinearLayout ll_payment_rule;

    private LiveRechargePaymentAdapter adapterPayment;
    private List<PayItemModel> listPayment = new ArrayList<>( );

    private LiveRechrgeVipPaymentRuleAdapter adapterPaymentRule;
    private List<RuleItemModel> listPaymentRule = new ArrayList<>( );


    private int pay_id;
    private int rule_id;

    @Override
    protected int onCreateContentView()
    {
        return R.layout.act_live_recharge_vip;
    }

    protected void init()
    {

        //getPullToRefreshViewWrapper().setPullToRefreshView((SDPullToRefreshView) findViewById(R.id.view_pull_to_refresh));



        //支付方式
        adapterPayment = new LiveRechargePaymentAdapter(listPayment, getActivity());
        adapterPayment.setItemClickCallback(new SDItemClickCallback<PayItemModel>( )
        {
            @Override
            public void onItemClick(int position, PayItemModel item, View view)
            {
                if(item.isIskefu()){
                    Intent intent = new Intent(getActivity(), AppWebViewActivity.class);
                    intent.putExtra(AppWebViewActivity.EXTRA_URL, item.getClass_name());
                    getActivity().startActivity(intent);
                    return;
                }
                adapterPayment.getSelectManager( ).performClick(item);
            }
        });
        ll_payment.setAdapter(adapterPayment);

        //支付金额
        adapterPaymentRule = new LiveRechrgeVipPaymentRuleAdapter(listPaymentRule, getActivity());
        adapterPaymentRule.setItemClickCallback(new SDItemClickCallback<RuleItemModel>( )
        {
            @Override
            public void onItemClick(int position, RuleItemModel item, View view)
            {
                rule_id = item.getId( );
                clickPaymentRule(item);
            }
        });
        ll_payment_rule.setAdapter(adapterPaymentRule);
        requestVipData();
        initPullToRefresh();
    }


    private void initPullToRefresh()
    {
        getPullToRefreshViewWrapper().setModePullFromHeader();
        getPullToRefreshViewWrapper().setOnRefreshCallbackWrapper(new IPullToRefreshViewWrapper.OnRefreshCallbackWrapper()
        {
            @Override
            public void onRefreshingFromHeader()
            {
                requestVipData();
            }

            @Override
            public void onRefreshingFromFooter()
            {

            }
        });
    }
    /**
     * 购买会员套餐
     * @param model
     */
    private void clickPaymentRule(RuleItemModel model)
    {
        if (!validatePayment( ))
        {
            return;
        }
        requestPay( );
    }

    private void requestPay()
    {
        CommonInterface.requestPayVip(pay_id, rule_id, new AppRequestCallback<App_payActModel>( )
        {
            @Override
            protected void onStart()
            {
                super.onStart( );
                showProgressDialog("正在启动插件");
            }

            @Override
            protected void onFinish(SDResponse resp)
            {
                super.onFinish(resp);
                dismissProgressDialog( );
            }

            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk( ))
                {
                    PayModel payModel = actModel.getPay( );
                    if (payModel != null)
                    {
                        PaySdkModel paySdkModel = payModel.getSdk_code( );
                        if (paySdkModel != null)
                        {
                            CommonOpenSDK.dealPayRequestSuccess(actModel, getActivity( ), payResultListner, jbfPayResultListener);
                        }
                    }
                }
            }
        });
    }

    private PayResultListner payResultListner = new PayResultListner( )
    {
        @Override
        public void onSuccess()
        {

        }

        @Override
        public void onDealing()
        {

        }

        @Override
        public void onFail()
        {

        }

        @Override
        public void onCancel()
        {

        }

        @Override
        public void onNetWork()
        {

        }

        @Override
        public void onOther()
        {

        }
    };

    private OnPayResultListener jbfPayResultListener = new OnPayResultListener( )
    {

        @Override
        public void onSuccess(Integer integer, String s, String s1)
        {

        }

        @Override
        public void onFailed(Integer integer, String s, String s1)
        {
        }
    };

    private boolean validatePayment()
    {
        PayItemModel payment = adapterPayment.getSelectManager( ).getSelectedItem( );
        if (payment == null)
        {
            SDToast.showToast("请选择支付方式");
            return false;
        }
        pay_id = payment.getId( );

        return true;
    }

    private void requestVipData()
    {
        CommonInterface.requestVipPurchase(new AppRequestCallback<App_UserVipPurchaseActModel>( )
        {
            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk( ))
                {

                    if (actModel.getIs_vip( ) == 0)
                    {
                        tv_user_vip_deadline.setTextColor(SDResourcesUtil.getColor(R.color.res_text_gray_m));
                    } else
                    {
                        tv_user_vip_deadline.setTextColor(SDResourcesUtil.getColor(R.color.res_main_color));
                    }
                    UserModel userModel = UserModelDao.query();
                    userModel.setIs_vip(actModel.getIs_vip());
                    UserModelDao.insertOrUpdate(userModel);

                    SDViewBinder.setTextView(tv_user_vip_deadline, actModel.getVip_expire_time( ));
                    adapterPayment.updateData(actModel.getPay_list( ));
                    adapterPaymentRule.updateData(actModel.getRule_list( ));

                    int defaultPayIndex = -1;
                    List<PayItemModel> listPay = actModel.getPay_list( );
                    if (listPay != null)
                    {
                        int i = 0;
                        for (PayItemModel pay : listPay)
                        {
                            if (pay_id == pay.getId( ))
                            {
                                defaultPayIndex = i;
                                break;
                            }
                            i++;
                        }
                        if (defaultPayIndex < 0)
                        {
                            defaultPayIndex = 0;
                            pay_id = 0;
                        }
                    }
                    adapterPayment.getSelectManager( ).setSelected(defaultPayIndex, true);
                }
            }

            @Override
            protected void onFinish(SDResponse resp)
            {
                getPullToRefreshViewWrapper().stopRefreshing();
                super.onFinish(resp);
            }
        });
    }


    @Override
    protected void onRoomClosed(int roomId) {

    }

    @Override
    public void scrollToTop() {

    }

    @Override
    protected void onLoopRun() {

    }
}
