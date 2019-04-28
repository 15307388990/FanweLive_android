package com.fanwe.pay.activity

import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.widget.EditText
import com.fanwe.hybrid.activity.BaseTitleActivity
import com.fanwe.hybrid.http.AppRequestCallback
import com.fanwe.library.adapter.http.model.SDResponse
import com.fanwe.library.utils.SDToast
import com.fanwe.live.R
import com.fanwe.pay.common.PayCommonInterface
import com.fanwe.pay.event.InviteRewardsEvent
import com.fanwe.pay.model.AlipayWithdrawalModel
import com.sunday.eventbus.SDEventManager
import kotlinx.android.synthetic.main.activity_alipay_withdrawal.*

/**
 *
 *
 * 项目名称：
 * 类描述：支付宝提现界面
 * 创建人：mj
 * 创建时间：2018/12/20 20:22
 * 修改人：
 * 修改时间：
 * 修改备注：
 * Version:
 *
 */
class AlipayWithdrawalActivity : BaseTitleActivity(), TextWatcher {

    private val money by lazy {
        intent.getStringExtra("data")
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alipay_withdrawal)
        //初始化标题栏
        initTitle()
        //设置监听
        initListener()
    }

    /**
     * 初始化标题
     */
    private fun initTitle() {
        mTitle.setMiddleTextTop(getString(R.string.alipay_withdrawal))
        tv_amount_available.text = money
    }

    /**
     * 事件监听
     */
    private fun initListener() {
        cet_withdrawal_amount.addTextChangedListener(this)
        //提现
        btn_sure.setOnClickListener {
            if (TextUtils.isEmpty(money) || money.toDouble() < 30) {
                SDToast.showToast("余额不足")
                return@setOnClickListener
            }
            if (money.toDouble() <= 0) {
                return@setOnClickListener
            }
            //可用金额
            val amountAvailable = tv_amount_available.text.toString()
            if (amountAvailable.isEmpty()) {
                SDToast.showToast(getString(R.string.input_amount_available))
                return@setOnClickListener
            }

            //提现金额
            val withdrawalAmount = cet_withdrawal_amount.text.toString()
            if (withdrawalAmount.isEmpty() || withdrawalAmount.toDouble() <= 0) {
                SDToast.showToast(getString(R.string.input_withdrawal_amount))
                return@setOnClickListener
            }
            if (money.toDouble() < withdrawalAmount.toDouble()) {
                SDToast.showToast(String.format(getString(R.string.input_available_diamond), money))
                return@setOnClickListener
            }

            //支付宝帐号
            val amountOfAlipay = et_amount_of_alipay.text.toString()
            if (amountOfAlipay.isEmpty()) {
                SDToast.showToast(getString(R.string.input_alipay_account))
                return@setOnClickListener
            }
            //收款人姓名
            val payeeName = et_payee_name.text.toString()
            if (payeeName.isEmpty()) {
                SDToast.showToast(getString(R.string.input_payee_name))
                return@setOnClickListener
            }
            showProgressDialog("加载中...")
            PayCommonInterface.requestAlipayWithdrawal(withdrawalAmount, amountOfAlipay, payeeName, object : AppRequestCallback<AlipayWithdrawalModel>() {
                override fun onSuccess(p0: SDResponse?) {
                    dismissProgressDialog()
                    if (actModel.isOk) {
                        SDToast.showToast(getString(R.string.withdrawal_success))
                        SDEventManager.post(InviteRewardsEvent())
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
        }
    }

    override fun afterTextChanged(s: Editable?) {
        setDecimalFormat(cet_withdrawal_amount)
    }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

    /**
     * 保留两位小数
     */
    fun setDecimalFormat(view: EditText) {
        val value = view.text.toString()
        if (!value.isEmpty()) {
            val index = value.lastIndexOf(".")
            if (index != -1) {
                val length = value.substring(index + 1, value.length).length
                if (length > 2) {
                    view.setText(value.substring(0, index + 3))
                    view.setSelection(view.text.length)
                }
            }
        }
    }
}
