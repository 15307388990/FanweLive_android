package com.fanwe.pay.activity

import android.app.Dialog
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.fanwe.hybrid.activity.BaseTitleActivity
import com.fanwe.hybrid.http.AppRequestCallback
import com.fanwe.library.adapter.http.model.SDResponse
import com.fanwe.library.utils.SDToast
import com.fanwe.live.R
import com.fanwe.live.dialog.LiveTimePickerDialog
import com.fanwe.live.view.pulltorefresh.IPullToRefreshViewWrapper
import com.fanwe.pay.adapter.InvitePeopleAdapter
import com.fanwe.pay.common.PayCommonInterface
import com.fanwe.pay.model.InvitePeopleModel
import com.jzxiang.pickerview.data.Type
import com.jzxiang.pickerview.listener.OnDateSetDialogListener
import kotlinx.android.synthetic.main.activity_invite_people.*
import java.text.SimpleDateFormat
import java.util.*


/**
 * 项目名称：
 * 类描述：邀请人界面
 * 创建人：mj
 * 创建时间：2018/12/20 20:22
 * 修改人：
 * 修改时间：
 * 修改备注：
 * Version:
 *
 */
class InvitePeopleActivity : BaseTitleActivity(), OnDateSetDialogListener {

    /**页数*/
    private var mPage = 1
    /**适配器*/
    private val mAdapter: InvitePeopleAdapter by lazy {
        InvitePeopleAdapter()
    }
    /**时间*/
    private var tvTime: TextView? = null
    /**总金额*/
    private var tvAllMoney: TextView? = null
    private var invitePeopleDate: Long = 0
    private val mHeaderView: View  by lazy {
        LayoutInflater.from(this).inflate(R.layout.header_invite_people_layout, null)
    }

    private val mRightView: View  by lazy {
        LayoutInflater.from(this).inflate(R.layout.invite_people_right_layout, null)

    }

    /**时间选择器*/
    private var mTimePicker: LiveTimePickerDialog? = null//时间选择器

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invite_people)
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
        mTitle.setMiddleTextTop(getString(R.string.my_invite))
        mTitle.setCustomViewRight(mRightView).setOnClickListener {
            //            if (mTimePicker == null) {
//                initTimePicker()
//            }
//            mTimePicker?.showBottom()
        }
    }

    private fun initData() {
        view_state_layout.showContent()
        pullToRefreshViewWrapper.pullToRefreshView = view_pull_to_refresh
        lv_content.layoutManager = LinearLayoutManager(this) as RecyclerView.LayoutManager?
        lv_content.adapter = mAdapter
        tvTime = mHeaderView.findViewById(R.id.tvTime) as TextView?
        tvTime?.text = getFormatTime(Date().time)
        tvAllMoney = mHeaderView.findViewById(R.id.tvAllMoney) as TextView?
        mAdapter.addHeaderView(mHeaderView)
        mHeaderView.findViewById(R.id.click_layout).setOnClickListener {
            if (mTimePicker == null) {
                initTimePicker()
            }
            mTimePicker?.showBottom()
        }
        mPage = 1
        requestData()
    }


    /**
     * 设置监听
     */
    private fun initListener() {
        pullToRefreshViewWrapper.setOnRefreshCallbackWrapper(object : IPullToRefreshViewWrapper.OnRefreshCallbackWrapper {
            override fun onRefreshingFromHeader() {
                mPage = 1
                requestData()
            }

            override fun onRefreshingFromFooter() {
                mPage += 1
                requestData()
            }
        })

    }

    /**
     * 请求数据
     */
    private fun requestData() {
//        onRefreshComplete()
        if (invitePeopleDate == 0.toLong())
            invitePeopleDate = System.currentTimeMillis()
        PayCommonInterface.requestToInvitePeopleList(invitePeopleDate, object : AppRequestCallback<InvitePeopleModel>() {
            override fun onSuccess(p0: SDResponse?) {
                super.onSuccessBefore(p0)
                onRefreshComplete()
                if (actModel.isOk) {
                    if (mPage == 1) {
                        tvAllMoney?.setText(actModel.today_total)
                        if (actModel?.p_user_info?.isEmpty()!!) {
                            stateLayout.showEmpty()
                        } else {
                            mAdapter.setNewData(actModel.p_user_info)
                        }
                    } else {
                        if (actModel?.p_user_info?.isEmpty()!!) {
                            mAdapter.addData(actModel.p_user_info)
                        }
                    }

                } else {
                    SDToast.showToast(actModel.error);
                }
            }


            override fun onError(resp: SDResponse?) {
                super.onError(resp)
                onRefreshComplete()
            }

        })

    }

    protected fun onRefreshComplete() {
        if (lv_content != null) {
            pullToRefreshViewWrapper.stopRefreshing()
        }
    }

    /**
     * 初始化时间选择器
     */
    private fun initTimePicker() {
        mTimePicker = LiveTimePickerDialog.Builder(activity)
                .setCallBack(this)
                .setTitleStringId("日期")
                .setCyclic(false)
                .setType(Type.YEAR_MONTH_DAY)
                .setMinMillseconds(1)//1970年1月1日的时间戳
                .setMaxMillseconds(System.currentTimeMillis())
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(resources.getColor(R.color.res_main_color))
                .setWheelItemTextNormalColor(resources.getColor(R.color.res_text_gray_s))
                .setWheelItemTextSelectorColor(resources.getColor(R.color.res_main_color))
                .build()
    }

    override fun onDateSet(p0: Dialog?, p1: Long) {
        tvTime?.text = getFormatTime(p1)
        mPage = 1
        invitePeopleDate = p1
        requestData()
    }

    fun getFormatTime(time: Long): String {
        val date = Date()
        date.time = time
        return SimpleDateFormat("yyyy-MM-dd").format(date)
    }

}