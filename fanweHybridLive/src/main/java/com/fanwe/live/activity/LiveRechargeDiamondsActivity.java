package com.fanwe.live.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.fanwe.hybrid.activity.AppWebViewActivity;
import com.fanwe.hybrid.activity.BaseTitleActivity;
import com.fanwe.hybrid.common.CommonOpenSDK;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.listner.PayResultListner;
import com.fanwe.hybrid.utils.IntentUtil;
import com.fanwe.library.adapter.http.model.SDResponse;
import com.fanwe.library.common.SDSelectManager;
import com.fanwe.lib.gridlayout.SDGridLayout;
import com.fanwe.library.listener.SDItemClickCallback;
import com.fanwe.lib.pulltorefresh.SDPullToRefreshView;
import com.fanwe.library.utils.SDToast;
import com.fanwe.library.utils.SDTypeParseUtil;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.live.R;
import com.fanwe.live.adapter.LiveRechargePaymentAdapter;
import com.fanwe.live.adapter.LiveRechrgeDiamondsPaymentRuleAdapter;
import com.fanwe.live.common.CommonInterface;
import com.fanwe.live.model.App_payActModel;
import com.fanwe.live.model.App_rechargeActModel;
import com.fanwe.live.model.PayItemModel;
import com.fanwe.live.model.RuleItemModel;
import com.fanwe.live.view.pulltorefresh.IPullToRefreshViewWrapper;
import com.fanwei.jubaosdk.shell.OnPayResultListener;

import java.math.BigDecimal;
import java.util.List;

/**
 * 个人中心-账户-充值界面
 * Created by Administrator on 2016/7/6.
 */
public class LiveRechargeDiamondsActivity extends BaseTitleActivity
{
    private TextView tv_user_money;

    private View ll_payment;
    private SDGridLayout lv_payment;

    private View ll_payment_rule;
    private SDGridLayout lv_payment_rule;

    private View ll_other_ticket_exchange;
    private EditText et_money;
    private TextView tv_money_to_diamonds;
    private TextView tv_exchange;

    private LiveRechargePaymentAdapter mAdapterPayment;

    private LiveRechrgeDiamondsPaymentRuleAdapter mAdapterPaymentRule;
    private List<RuleItemModel> mListCommonPaymentRule;

    /**
     * 支付方式id
     */
    private int mPaymentId;
    /**
     * 支付金额选项id
     */
    private int mPaymentRuleId;
    /**
     * 要兑换的金额
     */
    private int mExchangeMoney;
    /**
     * 兑换比例
     */
    private int mExchangeRate = 1;

    @Override
    protected void init(Bundle savedInstanceState)
    {
        super.init(savedInstanceState);
        setContentView(R.layout.act_live_recharge_diamonds);
        getPullToRefreshViewWrapper().setPullToRefreshView((SDPullToRefreshView) findViewById(R.id.view_pull_to_refresh));
        tv_user_money = (TextView) findViewById(R.id.tv_user_money);
        ll_payment = findViewById(R.id.ll_payment);
        lv_payment = (SDGridLayout) findViewById(R.id.lv_payment);
        ll_payment_rule = findViewById(R.id.ll_payment_rule);
        lv_payment_rule = (SDGridLayout) findViewById(R.id.lv_payment_rule);
        ll_other_ticket_exchange = findViewById(R.id.ll_other_ticket_exchange);
        et_money = (EditText) findViewById(R.id.et_money);
        tv_money_to_diamonds = (TextView) findViewById(R.id.tv_money_to_diamonds);
        tv_exchange = (TextView) findViewById(R.id.tv_exchange);

        mTitle.setMiddleTextTop("充值");

        initPayment(); //支付方式
        initPaymentRule(); //支付金额选项
        initOtherExchange(); //其他金额
        initPullToRefresh();
    }

    private void initPayment()
    {
        mAdapterPayment = new LiveRechargePaymentAdapter(null, this);
        mAdapterPayment.getSelectManager().setMode(SDSelectManager.Mode.SINGLE_MUST_ONE_SELECTED);
        mAdapterPayment.getSelectManager().addSelectCallback(new SDSelectManager.SelectCallback<PayItemModel>()
        {
            @Override
            public void onNormal(int index, PayItemModel item)
            {
            }

            @Override
            public void onSelected(int index, PayItemModel item)
            {
                List<RuleItemModel> listPaymentRule = item.getRule_list();
                if (listPaymentRule != null && !listPaymentRule.isEmpty())
                {
                    mAdapterPaymentRule.updateData(listPaymentRule);
                } else
                {
                    mAdapterPaymentRule.updateData(mListCommonPaymentRule);
                }
            }
        });
        mAdapterPayment.setItemClickCallback(new SDItemClickCallback<PayItemModel>()
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
                mAdapterPayment.getSelectManager().performClick(item);
            }
        });
        lv_payment.setAdapter(mAdapterPayment);
    }

    private void initPaymentRule()
    {
        mAdapterPaymentRule = new LiveRechrgeDiamondsPaymentRuleAdapter(null, this);
        mAdapterPaymentRule.setItemClickCallback(new SDItemClickCallback<RuleItemModel>()
        {
            @Override
            public void onItemClick(int position, RuleItemModel item, View view)
            {
                mPaymentRuleId = item.getId();

                if (!validatePayment())
                {
                    return;
                }
                mExchangeMoney = 0;
                requestPay();
            }
        });
        lv_payment_rule.setAdapter(mAdapterPaymentRule);
    }

    private void initOtherExchange()
    {
        et_money.addTextChangedListener(new TextWatcher()
        {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after)
            {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count)
            {
            }

            @Override
            public void afterTextChanged(Editable s)
            {
                mExchangeMoney = SDTypeParseUtil.getInt(s.toString());

                int diamonds = mExchangeMoney * mExchangeRate;
                String strDiamonds = new BigDecimal(diamonds).toPlainString();
                tv_money_to_diamonds.setText(strDiamonds);
            }
        });

        //兑换
        tv_exchange.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                clickExchange();
            }
        });
    }

    private void initPullToRefresh()
    {
        getPullToRefreshViewWrapper().setModePullFromHeader();
        getPullToRefreshViewWrapper().setOnRefreshCallbackWrapper(new IPullToRefreshViewWrapper.OnRefreshCallbackWrapper()
        {
            @Override
            public void onRefreshingFromHeader()
            {
                requestData();
            }

            @Override
            public void onRefreshingFromFooter()
            {

            }
        });
    }

    private void requestData()
    {
        CommonInterface.requestRecharge(new AppRequestCallback<App_rechargeActModel>()
        {
            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk())
                {
                    mListCommonPaymentRule = actModel.getRule_list();
                    mExchangeRate = actModel.getRate();

                    SDViewUtil.setVisible(ll_payment);
                    SDViewUtil.setVisible(ll_payment_rule);
                    SDViewBinder.setTextView(tv_user_money, String.valueOf(actModel.getDiamonds()));

                    mAdapterPayment.updateData(actModel.getPay_list());

                    SDViewUtil.setVisibleOrGone(ll_other_ticket_exchange, actModel.getShow_other() == 1);

                    selectPaymentIfNeed(actModel.getPay_list());
                } else
                {
                    errorUi();
                }
            }

            @Override
            protected void onError(SDResponse resp)
            {
                super.onError(resp);
                errorUi();
            }

            @Override
            protected void onFinish(SDResponse resp)
            {
                getPullToRefreshViewWrapper().stopRefreshing();
                super.onFinish(resp);
            }
        });
    }

    /**
     * 选中支付方式
     *
     * @param listPayment
     */
    private void selectPaymentIfNeed(List<PayItemModel> listPayment)
    {
        int selectIndex = -1;
        if (listPayment != null && listPayment.size() > 0)
        {
            for (int i = 0; i < listPayment.size(); i++)
            {
                PayItemModel item = listPayment.get(i);
                if (mPaymentId == item.getId())
                {
                    selectIndex = i;
                    break;
                }
            }
        } else
        {
            mAdapterPaymentRule.updateData(null);
        }

        if (selectIndex < 0)
        {
            selectIndex = 0;
            mPaymentId = 0;
        }
        mAdapterPayment.getSelectManager().setSelected(selectIndex, true);
    }

    private void errorUi()
    {
        SDViewBinder.setTextView(tv_user_money, "获取数据失败");
        SDViewUtil.setGone(ll_payment);
        SDViewUtil.setGone(ll_payment_rule);
        SDViewUtil.setGone(ll_other_ticket_exchange);
    }

    /**
     * 输入金额支付
     */
    private void clickExchange()
    {
        mPaymentRuleId = 0;
        if (!validatePayment())
        {
            return;
        }

        if (mExchangeMoney <= 0)
        {
            SDToast.showToast("请输入兑换金额");
            return;
        }

        requestPay();
    }

    private void requestPay()
    {
        CommonInterface.requestPay(mPaymentId, mPaymentRuleId, mExchangeMoney, new AppRequestCallback<App_payActModel>()
        {
            @Override
            protected void onStart()
            {
                super.onStart();
                showProgressDialog("正在启动插件");
            }

            @Override
            protected void onFinish(SDResponse resp)
            {
                super.onFinish(resp);
                dismissProgressDialog();
            }

            @Override
            protected void onSuccess(SDResponse resp)
            {
                if (actModel.isOk())
                {
                    CommonOpenSDK.dealPayRequestSuccess(actModel, getActivity(), payResultListner, jbfPayResultListener);
                }
            }
        });
    }

    private PayResultListner payResultListner = new PayResultListner()
    {
        @Override
        public void onSuccess()
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    et_money.setText("");
                }
            });
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

    private OnPayResultListener jbfPayResultListener = new OnPayResultListener()
    {

        @Override
        public void onSuccess(Integer integer, String s, String s1)
        {
            runOnUiThread(new Runnable()
            {
                @Override
                public void run()
                {
                    et_money.setText("");
                }
            });
        }

        @Override
        public void onFailed(Integer integer, String s, String s1)
        {
        }
    };

    private boolean validatePayment()
    {
        PayItemModel payment = mAdapterPayment.getSelectManager().getSelectedItem();
        if (payment == null)
        {
            SDToast.showToast("请选择支付方式");
            return false;
        }
        mPaymentId = payment.getId();

        return true;
    }

    @Override
    protected void onResume()
    {
        requestData();
        super.onResume();
    }
}
