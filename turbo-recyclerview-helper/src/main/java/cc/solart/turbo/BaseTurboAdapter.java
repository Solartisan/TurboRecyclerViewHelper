/*
 * Copyright (C) 2016 solartisan/imilk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package cc.solart.turbo;

import android.content.Context;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * author: imilk
 * https://github.com/Solartisan/TurboRecyclerViewHelper
 */
public abstract class BaseTurboAdapter<T, VH extends BaseViewHolder> extends AbsTurboAdapter<T, BaseViewHolder> implements OnLoadMoreListener {

    protected static final String TAG = "BaseTurboAdapter";

    protected List<T> mData;
    private boolean mLoading = false;
    private boolean mEmptyEnable;


    public BaseTurboAdapter(Context context) {
        this(context, null);
    }

    /**
     * initialization
     *
     * @param context The context.
     * @param data    A new list is created out of this one to avoid mutable list
     */
    public BaseTurboAdapter(Context context, List<T> data) {
        super(context);
        this.mData = data == null ? new ArrayList<T>() : new ArrayList<T>(data);
    }

    public void add(T item) {
        boolean isAdd = mData.add(item);
        if (isAdd)
            notifyItemInserted(mData.size() + getHeaderViewCount());
    }

    public void add(int position, T item) {
        if (position < 0 || position > mData.size()) {
            Log.e(TAG, "add position = " + position + ", IndexOutOfBounds, please check your code!");
            return;
        }
        mData.add(position, item);
        notifyItemInserted(position + getHeaderViewCount());
    }

    public void remove(T item) {
        int index = mData.indexOf(item);
        boolean isRemoved = mData.remove(item);
        if (isRemoved)
            notifyItemRemoved(index + getHeaderViewCount());
    }

    public void remove(int position) {
        if (position < 0 || position >= mData.size()) {
            Log.e(TAG, "remove position = " + position + ", IndexOutOfBounds, please check your code!");
            return;
        }
        mData.remove(position);
        notifyItemRemoved(position + getHeaderViewCount());
    }

    /**
     * additional data;
     *
     * @param data
     */
    public void addData(List<T> data) {
        if (data != null) {
            this.mData.addAll(data);
            notifyDataSetChanged();
        }
    }

    public void removeData(List<T> data) {
        if (data != null) {
            this.mData.removeAll(data);
            notifyDataSetChanged();
        }
    }

    public void resetData(List<T> data) {
        mData.clear();
        addData(data);
    }


    public List<T> getData() {
        return mData;
    }


    /**
     * Get the data item associated with the specified position in the data set.
     *
     * @param position Position of the item whose data we want within the adapter's
     *                 data set.
     * @return The data at the specified position.
     */
    @Override
    public T getItem(int position) {
        if (position < 0 || position >= mData.size()) {
            Log.e(TAG, "getItem position = " + position + ", IndexOutOfBounds, please check your code!");
            return null;
        }
        return mData.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    /**
     * Whether there is data exists
     *
     * @return
     */
    @Override
    protected boolean isEmpty() {
        return getHeaderViewCount() + getFooterViewCount() + getData().size() == 0;
    }

    @Override
    public int getItemCount() {

        int count;
        if (mLoading) { //if loading ignore footer view
            count = mData.size() + 1 + getHeaderViewCount();
        } else {
            count = mData.size() + getHeaderViewCount() + getFooterViewCount();
        }
        mEmptyEnable = false;
        if (count == 0) {
            mEmptyEnable = true;
            count += getEmptyViewCount();
        }
        return count;
    }


    @Override
    public final int getItemViewType(int position) {
        if (mHeaderView != null && position == 0) {
            return TYPE_HEADER_VIEW;
        } else if (mEmptyView != null && getItemCount() == 1 && mEmptyEnable) {
            return TYPE_EMPTY_VIEW;
        } else if (position == mData.size() + getHeaderViewCount()) {
            if (mLoading) {
                return TYPE_LOADING_VIEW;
            } else if (mFooterView != null) {
                return TYPE_FOOTER_VIEW;
            }
        }
        return getDefItemViewType(position);
    }

    @Override
    protected final void bindHolder(BaseViewHolder holder, int position) {
        convert((VH) holder, mData.get(holder.getLayoutPosition() - getHeaderViewCount()));
    }

    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param holder A fully initialized helper.
     * @param item   The item that needs to be displayed.
     */
    abstract protected void convert(VH holder, T item);

    @Override
    public void onLoadingMore() {
        if (!mLoading) {
            mLoading = true;
            notifyDataSetChanged();
        }
    }

    void loadingMoreComplete(List<T> data) {
        mLoading = false;
        addData(data);
    }

}