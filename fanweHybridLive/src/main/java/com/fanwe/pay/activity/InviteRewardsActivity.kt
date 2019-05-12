package com.fanwe.pay.activity

import android.content.Intent
import android.os.Bundle
import com.fanwe.hybrid.activity.BaseTitleActivity
import com.fanwe.hybrid.http.AppRequestCallback
import com.fanwe.library.adapter.http.model.SDResponse
import com.fanwe.library.utils.LogUtil
import com.fanwe.library.utils.SDToast
import com.fanwe.live.R
import com.fanwe.pay.common.PayCommonInterface
import com.fanwe.pay.event.InviteRewardsEvent
import com.fanwe.pay.model.InviteRewoardModel
import com.hn.library.extension.setThrottleClickListener
import kotlinx.android.synthetic.main.activity_invite_rewards.*

/**
 * 项目名称：
 * 类描述：邀请奖励界面
 * 创建人：mj
 * 创建时间：2018/12/20 20:22
 * 修改人：
 * 修改时间：
 * 修改备注：
 * Version:
 *
 */
class InviteRewardsActivity : BaseTitleActivity() {

    /**邀请奖励数据实体*/
    private  var mInviteRewoardModel:InviteRewoardModel?=null

    private  var  isFirst=true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_rewards)
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
        mTitle.setMiddleTextTop(getString(R.string.invite_rewards))
    }
    /**
     * 初始化数据
     */
    private fun initData() {
        PayCommonInterface.requestToGetInviteRewardInfo(object : AppRequestCallback<InviteRewoardModel>() {
            override fun onSuccess(p0: SDResponse?) {
                super.onSuccessBefore(p0)
                if(actModel.isOk){
                    isFirst=false
                    mInviteRewoardModel=actModel
                    updateUI()
                }
            }

            override fun onError(resp: SDResponse?) {
                super.onError(resp)
                if(isFirst) {
                    isFirst=false
                    val str = String.format(getString(R.string.successfully_invited), "0", "0")
                    tvSuccessfullyInvited.text = str
                }
            }
        })

    }

    /**
     * 邀请数据
     */
    private fun updateUI() {
        mInviteRewoardModel?.let {
            //邀请链接
            tvInviteUrl.text=it.inviteUrl
            //钻石
            tvDiamond.text=it.money
            //邀请人数
            val  str=String.format(getString(R.string.successfully_invited),it.inviteNumber)
            tvSuccessfullyInvited.text=str
        }
    }

    /**
     * 事件监听
     */
    private fun initListener() {
     val ob =   setThrottleClickListener(btn_into_account,btn_alipay_withdrawal,btn_invite_url,btn_my_invite)
                .subscribe {
                    when(it) {
                        btn_into_account -> {  //转入我的帐户
                            mInviteRewoardModel?.let {
                                val bundle=Bundle()
                                bundle.putString("money",it.money)
                                bundle.putString("diamonds",it.diamonds)
                                startActivity(Intent(this@InviteRewardsActivity, IntoAccountActivity::class.java).putExtras(bundle))
                            }
                        }
                        btn_alipay_withdrawal -> { //支付宝提现
                            mInviteRewoardModel?.let {
                                val bundle=Bundle()
                                bundle.putString("data",it.money)
                                startActivity(Intent(this@InviteRewardsActivity, AlipayWithdrawalActivity::class.java).putExtras(bundle))
                            }
                        }
                        btn_invite_url->{//复制邀请链接
                            mInviteRewoardModel?.let {
                                Utils.clipboardManager(it.inviteUrl, this@InviteRewardsActivity)
                                SDToast.showToast(getString(R.string.copy_success))
                            }
                        }
                        btn_my_invite ->{//我邀请的人
                            startActivity(Intent(this@InviteRewardsActivity, InvitePeopleActivity::class.java))

                        }
                    }
                }
    }

    /**
     * eventbus  更新界面
     */
    fun  onEventMainThread(event: InviteRewardsEvent){
        initData()
    }
}
