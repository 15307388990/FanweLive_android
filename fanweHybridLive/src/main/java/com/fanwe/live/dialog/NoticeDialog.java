package com.fanwe.live.dialog;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageView;
import android.widget.TextView;

import com.fanwe.hybrid.http.AppHttpUtil;
import com.fanwe.lib.dialog.impl.SDDialogBase;
import com.fanwe.library.utils.SDViewUtil;
import com.fanwe.library.webview.CustomWebView;
import com.fanwe.library.webview.DefaultWebViewClient;
import com.fanwe.live.R;
import com.fanwe.live.appview.H5AppViewWeb;
import com.fanwe.live.utils.GlideUtil;
import com.fanwe.o2o.jshandler.O2OShoppingLiveJsHander;

import org.xutils.view.annotation.ViewInject;
import org.xutils.x;

import static com.fanwe.live.appview.H5AppViewWeb.no_network_url;

/**
 * @author luoming
 * created at 2019/4/14 7:46 PM
 * 系统公告
 */
public class NoticeDialog extends SDDialogBase {

    @ViewInject(R.id.tv_title)
    protected TextView tv_title;//窗口标题
    @ViewInject(R.id.cus_webview)
    protected WebView customWebView;// 显示内容
    @ViewInject(R.id.tv_share)
    protected TextView tv_share;//知道了

    private String mUrl;
    private Context mContext;

    public NoticeDialog(Activity activity, String mUrl) {
        super(activity);
        this.mUrl = mUrl;
        this.mContext=activity;
        init();

    }

    public NoticeDialog(Activity activity, int theme) {
        super(activity, theme);

        init();
    }

    private void init() {
        setCanceledOnTouchOutside(true);
        setContentView(R.layout.dialog_notice);
        paddings(SDViewUtil.dp2px(30));
        x.view().inject(this, getContentView());
        setOnClicks();
        initWebView();
    }

    private void initWebView() {
        customWebView.getSettings().setBuiltInZoomControls(false);
        customWebView.setWebViewClient(new WebViewClient() {
            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, WebResourceRequest request) {
                Intent intent = new Intent();
                intent.setAction("android.intent.action.VIEW");
                Uri content_url = request.getUrl();
                intent.setData(content_url);
                mContext.startActivity(intent);
                //如果不需要其他对点击链接事件的处理返回true，否则返回false
                return true;
            }
        });


        customWebView.loadUrl(mUrl);
    }


    public void setTitle(String text) {
        tv_title.setText(text);
    }


    private void setOnClicks() {
        tv_share.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        super.onClick(v);
        switch (v.getId()) {
            case R.id.tv_share:
                if (null != onClick) {
                    onClick.shareOnClick();
                }
                dismiss();
                break;
            default:
                break;
        }
    }

    private InviteRechargeOnClick onClick = null;

    public void setInviteRechargeOnClick(InviteRechargeOnClick onClick) {
        this.onClick = onClick;
    }

    public interface InviteRechargeOnClick {
        public void shareOnClick();
    }

}
