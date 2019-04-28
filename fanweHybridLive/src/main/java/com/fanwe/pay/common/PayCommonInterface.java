package com.fanwe.pay.common;

import com.fanwe.hybrid.http.AppHttpUtil;
import com.fanwe.hybrid.http.AppRequestCallback;
import com.fanwe.hybrid.http.AppRequestParams;
import com.fanwe.pay.model.AlipayWithdrawalModel;
import com.fanwe.pay.model.AppLivePayContActModel;
import com.fanwe.pay.model.App_live_live_payActModel;
import com.fanwe.pay.model.App_live_live_pay_agreeActModel;
import com.fanwe.pay.model.App_live_live_pay_deductActModel;
import com.fanwe.pay.model.IntoAccountModel;
import com.fanwe.pay.model.InvitePeopleModel;
import com.fanwe.pay.model.InviteRewoardModel;

/**
 * Created by Administrator on 2016/11/30.
 */

public class PayCommonInterface
{
    /**
     * 切换付费接口
     *
     * @param listener
     */
    public static void requestLiveLivePay(int live_fee, int live_pay_type, int room_id, int is_mention, AppRequestCallback<App_live_live_payActModel> listener)
    {
        AppRequestParams params = new AppRequestParams();
        params.putCtl("live");
        params.putAct("live_pay");
        params.put("live_fee", live_fee);
        params.put("live_pay_type", live_pay_type);
        params.put("room_id", room_id);
        params.put("is_mention", is_mention);
        AppHttpUtil.getInstance().post(params, listener);
    }
    /**
     * 支付宝提现
     * @param money  提现金额
     * @param alipay_user_id    支付宝帐号
     * @param alipay_user_name         收款人姓名
     */

    public static void requestAlipayWithdrawal( String money, String alipay_user_id,   String alipay_user_name,AppRequestCallback<AlipayWithdrawalModel> listener) {
        AppRequestParams params = new AppRequestParams();
        params.putCtl("invite");
        params.putAct("userCarry");
        params.put("money", money);
        params.put("alipay_user_id", alipay_user_id);
        params.put("alipay_user_name", alipay_user_name);
        params.setNeedShowActInfo(false);
        AppHttpUtil.getInstance().post(params, listener);
    }
    /**
     * 同意扣费接口
     *
     * @param listener
     */
    public static void requestLiveLivePayAgree(int room_id, AppRequestCallback<App_live_live_pay_agreeActModel> listener)
    {
        AppRequestParams params = new AppRequestParams();
        params.putCtl("live");
        params.putAct("live_pay_agree");
        params.put("room_id", room_id);
        AppHttpUtil.getInstance().post(params, listener);
    }
    /**
     * 兑换
     * @param money
     */
    public static void requestIntoAccount( String money,AppRequestCallback<IntoAccountModel> listener) {
        AppRequestParams params = new AppRequestParams();
        params.putCtl("invite");
        params.putAct("diamondToUser");
        params.put("money",money);
        params.setNeedShowActInfo(false);
        AppHttpUtil.getInstance().post(params, listener);
    }
    /**
     * 扣费接口
     *
     * @param listener
     */
    public static void requestLivelivePayDeduct(int room_id, boolean is_agree, AppRequestCallback<App_live_live_pay_deductActModel> listener)
    {
        AppRequestParams params = new AppRequestParams();
        params.putCtl("live");
        params.putAct("live_pay_deduct");
        params.put("room_id", room_id);
        if (is_agree)
        {
            params.put("is_agree", 1);
        } else
        {
            params.put("is_agree", 0);
        }

        AppHttpUtil.getInstance().post(params, listener);
    }

    /**
     * @param p
     * @param type          0 消费 1 收费
     * @param live_pay_type 0 按时 1 按场
     * @param listener
     */
    public static void requestLivePayCont(int p, int type, int live_pay_type, AppRequestCallback<AppLivePayContActModel> listener)
    {
        AppRequestParams params = new AppRequestParams();
        params.putCtl("live");
        params.putAct("pay_cont");
        params.put("p", p);
        params.put("type", type);
        params.put("live_pay_type", live_pay_type);
        AppHttpUtil.getInstance().post(params, listener);
    }
    /**
     * 邀请奖励
     */
    public static void requestToGetInviteRewardInfo(AppRequestCallback<InviteRewoardModel> listener) {
        AppRequestParams params = new AppRequestParams();
        params.putCtl("invite");
        params.putAct("getInvite");
        params.setNeedShowActInfo(false);
        AppHttpUtil.getInstance().post(params, listener);
    }
    /**
     * 邀请人
     */
    public static void requestToInvitePeopleList(long date,AppRequestCallback<InvitePeopleModel> listener) {
        AppRequestParams params = new AppRequestParams();
        params.putCtl("invite");
        params.putAct("getInviteInfo");
        params.put("date_time",date);
        params.setNeedShowActInfo(false);
        AppHttpUtil.getInstance().post(params, listener);
    }
}
