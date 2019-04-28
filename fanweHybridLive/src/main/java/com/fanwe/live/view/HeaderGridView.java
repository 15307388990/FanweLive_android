package com.fanwe.live.view;

//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//


import android.content.Context;
import android.database.DataSetObservable;
import android.database.DataSetObserver;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.MeasureSpec;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ListAdapter;
import android.widget.WrapperListAdapter;
import java.util.ArrayList;
import java.util.Iterator;

public class HeaderGridView extends GridView {
    private static final String TAG = "HeaderGridView";
    private ArrayList<HeaderGridView.FixedViewInfo> mHeaderViewInfos = new ArrayList();

    private void initHeaderGridView() {
        super.setClipChildren(false);
    }

    public HeaderGridView(Context context) {
        super(context);
        this.initHeaderGridView();
    }

    public HeaderGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initHeaderGridView();
    }

    public HeaderGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.initHeaderGridView();
    }

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        ListAdapter adapter = this.getAdapter();
        if (adapter != null && adapter instanceof HeaderGridView.HeaderViewGridAdapter) {
            ((HeaderGridView.HeaderViewGridAdapter)adapter).setNumColumns(this.getNumColumns());
        }

    }

    public void setClipChildren(boolean clipChildren) {
    }

    public void addHeaderView(View v, Object data, boolean isSelectable) {
        ListAdapter adapter = this.getAdapter();
        if (adapter != null && !(adapter instanceof HeaderGridView.HeaderViewGridAdapter)) {
            throw new IllegalStateException("Cannot add header view to grid -- setAdapter has already been called.");
        } else {
            HeaderGridView.FixedViewInfo info = new HeaderGridView.FixedViewInfo();
            FrameLayout fl = new HeaderGridView.FullWidthFixedViewLayout(this.getContext());
            fl.addView(v);
            info.view = v;
            info.viewContainer = fl;
            info.data = data;
            info.isSelectable = isSelectable;
            this.mHeaderViewInfos.add(info);
            if (adapter != null) {
                ((HeaderGridView.HeaderViewGridAdapter)adapter).notifyDataSetChanged();
            }

        }
    }

    public void addHeaderView(View v) {
        this.addHeaderView(v, (Object)null, true);
    }

    public int getHeaderViewCount() {
        return this.mHeaderViewInfos.size();
    }

    public boolean removeHeaderView(View v) {
        if (this.mHeaderViewInfos.size() > 0) {
            boolean result = false;
            ListAdapter adapter = this.getAdapter();
            if (adapter != null && ((HeaderGridView.HeaderViewGridAdapter)adapter).removeHeader(v)) {
                result = true;
            }

            this.removeFixedViewInfo(v, this.mHeaderViewInfos);
            return result;
        } else {
            return false;
        }
    }

    private void removeFixedViewInfo(View v, ArrayList<HeaderGridView.FixedViewInfo> where) {
        int len = where.size();

        for(int i = 0; i < len; ++i) {
            HeaderGridView.FixedViewInfo info = (HeaderGridView.FixedViewInfo)where.get(i);
            if (info.view == v) {
                where.remove(i);
                break;
            }
        }

    }

    public void setAdapter(ListAdapter adapter) {
        if (this.mHeaderViewInfos.size() > 0) {
            HeaderGridView.HeaderViewGridAdapter hadapter = new HeaderGridView.HeaderViewGridAdapter(this.mHeaderViewInfos, adapter);
            int numColumns = this.getNumColumns();
            if (numColumns > 1) {
                hadapter.setNumColumns(numColumns);
            }

            super.setAdapter(hadapter);
        } else {
            super.setAdapter(adapter);
        }

    }

    private static class HeaderViewGridAdapter implements WrapperListAdapter, Filterable {
        private final DataSetObservable mDataSetObservable = new DataSetObservable();
        private final ListAdapter mAdapter;
        private int mNumColumns = 1;
        ArrayList<HeaderGridView.FixedViewInfo> mHeaderViewInfos;
        boolean mAreAllFixedViewsSelectable;
        private final boolean mIsFilterable;

        public HeaderViewGridAdapter(ArrayList<HeaderGridView.FixedViewInfo> headerViewInfos, ListAdapter adapter) {
            this.mAdapter = adapter;
            this.mIsFilterable = adapter instanceof Filterable;
            if (headerViewInfos == null) {
                throw new IllegalArgumentException("headerViewInfos cannot be null");
            } else {
                this.mHeaderViewInfos = headerViewInfos;
                this.mAreAllFixedViewsSelectable = this.areAllListInfosSelectable(this.mHeaderViewInfos);
            }
        }

        public int getHeadersCount() {
            return this.mHeaderViewInfos.size();
        }

        public boolean isEmpty() {
            return (this.mAdapter == null || this.mAdapter.isEmpty()) && this.getHeadersCount() == 0;
        }

        public void setNumColumns(int numColumns) {
            if (numColumns < 1) {
                throw new IllegalArgumentException("Number of columns must be 1 or more");
            } else {
                if (this.mNumColumns != numColumns) {
                    this.mNumColumns = numColumns;
                    this.notifyDataSetChanged();
                }

            }
        }

        private boolean areAllListInfosSelectable(ArrayList<HeaderGridView.FixedViewInfo> infos) {
            if (infos != null) {
                Iterator var2 = infos.iterator();

                while(var2.hasNext()) {
                    HeaderGridView.FixedViewInfo info = (HeaderGridView.FixedViewInfo)var2.next();
                    if (!info.isSelectable) {
                        return false;
                    }
                }
            }

            return true;
        }

        public boolean removeHeader(View v) {
            for(int i = 0; i < this.mHeaderViewInfos.size(); ++i) {
                HeaderGridView.FixedViewInfo info = (HeaderGridView.FixedViewInfo)this.mHeaderViewInfos.get(i);
                if (info.view == v) {
                    this.mHeaderViewInfos.remove(i);
                    this.mAreAllFixedViewsSelectable = this.areAllListInfosSelectable(this.mHeaderViewInfos);
                    this.mDataSetObservable.notifyChanged();
                    return true;
                }
            }

            return false;
        }

        public int getCount() {
            return this.mAdapter != null ? this.getHeadersCount() * this.mNumColumns + this.mAdapter.getCount() : this.getHeadersCount() * this.mNumColumns;
        }

        public boolean areAllItemsEnabled() {
            if (this.mAdapter == null) {
                return true;
            } else {
                return this.mAreAllFixedViewsSelectable && this.mAdapter.areAllItemsEnabled();
            }
        }

        public boolean isEnabled(int position) {
            int numHeadersAndPlaceholders = this.getHeadersCount() * this.mNumColumns;
            if (position >= numHeadersAndPlaceholders) {
                int adjPosition = position - numHeadersAndPlaceholders;
                if (this.mAdapter != null) {
                    int adapterCount = this.mAdapter.getCount();
                    if (adjPosition < adapterCount) {
                        return this.mAdapter.isEnabled(adjPosition);
                    }
                }

                throw new ArrayIndexOutOfBoundsException(position);
            } else {
                return position % this.mNumColumns == 0 && ((HeaderGridView.FixedViewInfo)this.mHeaderViewInfos.get(position / this.mNumColumns)).isSelectable;
            }
        }

        public Object getItem(int position) {
            int numHeadersAndPlaceholders = this.getHeadersCount() * this.mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                return position % this.mNumColumns == 0 ? ((HeaderGridView.FixedViewInfo)this.mHeaderViewInfos.get(position / this.mNumColumns)).data : null;
            } else {
                int adjPosition = position - numHeadersAndPlaceholders;
                if (this.mAdapter != null) {
                    int adapterCount = this.mAdapter.getCount();
                    if (adjPosition < adapterCount) {
                        return this.mAdapter.getItem(adjPosition);
                    }
                }

                throw new ArrayIndexOutOfBoundsException(position);
            }
        }

        public long getItemId(int position) {
            int numHeadersAndPlaceholders = this.getHeadersCount() * this.mNumColumns;
            if (this.mAdapter != null && position >= numHeadersAndPlaceholders) {
                int adjPosition = position - numHeadersAndPlaceholders;
                int adapterCount = this.mAdapter.getCount();
                if (adjPosition < adapterCount) {
                    return this.mAdapter.getItemId(adjPosition);
                }
            }

            return -1L;
        }

        public boolean hasStableIds() {
            return this.mAdapter != null ? this.mAdapter.hasStableIds() : false;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            int numHeadersAndPlaceholders = this.getHeadersCount() * this.mNumColumns;
            if (position < numHeadersAndPlaceholders) {
                View headerViewContainer = ((HeaderGridView.FixedViewInfo)this.mHeaderViewInfos.get(position / this.mNumColumns)).viewContainer;
                if (position % this.mNumColumns == 0) {
                    return headerViewContainer;
                } else {
                    if (convertView == null) {
                        convertView = new View(parent.getContext());
                    }

                    convertView.setVisibility(View.INVISIBLE);
                    convertView.setMinimumHeight(headerViewContainer.getHeight());
                    return convertView;
                }
            } else {
                int adjPosition = position - numHeadersAndPlaceholders;
                if (this.mAdapter != null) {
                    int adapterCount = this.mAdapter.getCount();
                    if (adjPosition < adapterCount) {
                        return this.mAdapter.getView(adjPosition, convertView, parent);
                    }
                }

                throw new ArrayIndexOutOfBoundsException(position);
            }
        }

        public int getItemViewType(int position) {
            int numHeadersAndPlaceholders = this.getHeadersCount() * this.mNumColumns;
            if (position < numHeadersAndPlaceholders && position % this.mNumColumns != 0) {
                return this.mAdapter != null ? this.mAdapter.getViewTypeCount() : 1;
            } else {
                if (this.mAdapter != null && position >= numHeadersAndPlaceholders) {
                    int adjPosition = position - numHeadersAndPlaceholders;
                    int adapterCount = this.mAdapter.getCount();
                    if (adjPosition < adapterCount) {
                        return this.mAdapter.getItemViewType(adjPosition);
                    }
                }

                return -2;
            }
        }

        public int getViewTypeCount() {
            return this.mAdapter != null ? this.mAdapter.getViewTypeCount() + 1 : 2;
        }

        public void registerDataSetObserver(DataSetObserver observer) {
            this.mDataSetObservable.registerObserver(observer);
            if (this.mAdapter != null) {
                this.mAdapter.registerDataSetObserver(observer);
            }

        }

        public void unregisterDataSetObserver(DataSetObserver observer) {
            this.mDataSetObservable.unregisterObserver(observer);
            if (this.mAdapter != null) {
                this.mAdapter.unregisterDataSetObserver(observer);
            }

        }

        public Filter getFilter() {
            return this.mIsFilterable ? ((Filterable)this.mAdapter).getFilter() : null;
        }

        public ListAdapter getWrappedAdapter() {
            return this.mAdapter;
        }

        public void notifyDataSetChanged() {
            this.mDataSetObservable.notifyChanged();
        }
    }

    private class FullWidthFixedViewLayout extends FrameLayout {
        public FullWidthFixedViewLayout(Context context) {
            super(context);
        }

        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            int targetWidth = HeaderGridView.this.getMeasuredWidth() - HeaderGridView.this.getPaddingLeft() - HeaderGridView.this.getPaddingRight();
            widthMeasureSpec = MeasureSpec.makeMeasureSpec(targetWidth, MeasureSpec.getMode(widthMeasureSpec));
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
    }

    private static class FixedViewInfo {
        public View view;
        public ViewGroup viewContainer;
        public Object data;
        public boolean isSelectable;

        private FixedViewInfo() {
        }
    }
}
