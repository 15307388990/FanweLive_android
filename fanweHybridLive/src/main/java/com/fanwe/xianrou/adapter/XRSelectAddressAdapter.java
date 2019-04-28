package com.fanwe.xianrou.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.fanwe.library.adapter.SDSimpleRecyclerAdapter;
import com.fanwe.library.adapter.viewholder.SDRecyclerViewHolder;
import com.fanwe.library.utils.SDViewBinder;
import com.fanwe.live.R;
import com.fanwe.xianrou.interfaces.XRCommonItemClickCallback;
import com.tencent.lbssearch.object.result.SuggestionResultObject;

/**
 * Created by Administrator on 2017/4/4 0004.
 */

public class XRSelectAddressAdapter extends SDSimpleRecyclerAdapter<SuggestionResultObject.SuggestionData>
{
    public XRSelectAddressAdapter(Activity activity)
    {
        super(activity);
    }

    @Override
    public int getLayoutId(ViewGroup parent, int viewType)
    {
        return R.layout.xr_item_select_address;
    }

    @Override
    public void onBindData(final SDRecyclerViewHolder<SuggestionResultObject.SuggestionData> holder, final int position, final SuggestionResultObject.SuggestionData model)
    {
        TextView tv_address = holder.get(R.id.tv_address);
        SDViewBinder.setTextView(tv_address, model.title);

        holder.itemView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                if (itemClickCallback != null)
                {
                    itemClickCallback.onItemClick(holder.itemView, model, position);
                }
            }
        });
    }

    public XRCommonItemClickCallback<SuggestionResultObject.SuggestionData> itemClickCallback;

    public void setItemClickCallback(XRCommonItemClickCallback<SuggestionResultObject.SuggestionData> itemClickCallback)
    {
        this.itemClickCallback = itemClickCallback;
    }
}
