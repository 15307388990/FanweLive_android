package com.fanwe.pay.adapter

import com.chad.library.adapter.base.BaseQuickAdapter
import com.chad.library.adapter.base.BaseViewHolder
import com.fanwe.live.R
import com.fanwe.pay.model.InvitePeopleModel


/**
 * 项目名称：
 * 类描述：邀请人适配器
 * 创建人：mj
 * 创建时间：2018/12/20 20:22
 * 修改人：
 * 修改时间：
 * 修改备注：
 * Version:
 *
 */
class InvitePeopleAdapter  : BaseQuickAdapter<InvitePeopleModel.InvitePeopleBean, BaseViewHolder>(R.layout.item_invite_people_layout) {
    override fun convert(helper: BaseViewHolder, item: InvitePeopleModel.InvitePeopleBean) {
        //id
        helper.setText(R.id.tvID,item.user_id)
        //昵称
        helper.setText(R.id.tvName,item.nick_name)
        //充值金额
        helper.setText(R.id.tvRechargeAmount,item.chongzhi_total)
        //消费金额
        helper.setText(R.id.tvConsumptionAmount,item.diamonds_total)

    }
}