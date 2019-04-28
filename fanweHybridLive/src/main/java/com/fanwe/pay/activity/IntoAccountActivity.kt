package com.fanwe.pay.activity

import android.os.Bundle
import android.text.TextUtils
import com.fanwe.hybrid.activity.BaseTitleActivity
import com.fanwe.hybrid.http.AppRequestCallback
import com.fanwe.library.adapter.http.model.SDResponse
import com.fanwe.library.utils.SDToast
import com.fanwe.live.R
import com.fanwe.pay.common.PayCommonInterface
import com.fanwe.pay.event.InviteRewardsEvent
import com.fanwe.pay.model.IntoAccountModel
import com.sunday.eventbus.SDEventManager
import kotlinx.android.synthetic.main.activity_into_account.*

/**
 * 项目名称：
 * 类描述：转入帐户界面
 * 创建人：mj
 * 创建时间：2018/12/20 20:22
 * 修改人：
 * 修改时间：
 * 修改备注：
 * Version:
 *
 */
class IntoAccountActivity : BaseTitleActivity() {

    private val money by lazy {
        intent.getStringExtra("money")
    }
    private val diamonds by lazy {
        intent.getStringExtra("diamonds")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_into_account)
        //初始化标题栏
        initTitle()
        //初始化数据
        initData()
        //设置监听
        initListener()
    }

    /**
     * 初始化标题
     */
    private fun initTitle() {
        mTitle.setMiddleTextTop(getString(R.string.transfer_to_my_account))
    }

    /**
     * 初始哈数据
     */
    private fun initData() {
        val bundle = intent.extras
        if (bundle != null) {
            if (!TextUtils.isEmpty(diamonds)) {
                val dimoand = (diamonds.toDouble() * 8).toInt()
                if (dimoand >= 1) {
                    tv_available_diamond.text = dimoand.toString()
                }
            }

        }
    }

    /**
     * 事件监听
     */
    private fun initListener() {
        var etdiamonds = et_available_diamond.text.toString();
        //转入帐户
        btn_sure.setOnClickListener {
            if (!TextUtils.isEmpty(etdiamonds) && diamonds.toDouble() > etdiamonds.toDouble()) {
                showProgressDialog("加载中...")
                PayCommonInterface.requestIntoAccount(etdiamonds, object : AppRequestCallback<IntoAccountModel>() {
                    override fun onSuccess(p0: SDResponse?) {
                        dismissProgressDialog()
                        if (actModel.isOk) {
                            SDEventManager.post(InviteRewardsEvent())
                            SDToast.showToast(getString(R.string.transfer_to_success))
                            finish()
                        } else {
                            SDToast.showToast(actModel.error);
                        }
                    }

                    override fun onFinish(resp: SDResponse?) {
                        super.onFinish(resp)
                        dismissProgressDialog()
                    }
                })
            } else {
                SDToast.showToast("兑换数量不能超出可兑换钻石数量")
            }
        }
    }
}
