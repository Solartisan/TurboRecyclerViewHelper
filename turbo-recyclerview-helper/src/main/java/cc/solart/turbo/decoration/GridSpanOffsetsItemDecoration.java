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
package cc.solart.turbo.decoration;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

import cc.solart.turbo.BaseTurboAdapter;

/**
 * author: imilk
 * https://github.com/Solartisan/TurboRecyclerViewHelper
 */
public class GridSpanOffsetsItemDecoration extends GridOffsetsItemDecoration {
    private SparseIntArray mFullSpanRecorder;

    public GridSpanOffsetsItemDecoration(int orientation) {
        super(orientation);
    }


    private int findFullSpanCountByPosition(int position) {
        if (mFullSpanRecorder == null) {
            return 0;
        }
        int size = mFullSpanRecorder.size();
        for (int i = size - 1; i >= 0; i--) {
            int target = mFullSpanRecorder.keyAt(i);
            if (target < position) { //look up last full span position and get the full span count
                return mFullSpanRecorder.valueAt(i);
            }
        }
        return 0; // if non return zero
    }

    private boolean isFullSpan(RecyclerView parent, int position, int spanCount) {
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager.SpanSizeLookup lookup = ((GridLayoutManager) manager).getSpanSizeLookup();
            int spanSize = lookup.getSpanSize(position);
            if (spanSize > 1 || spanSize == spanCount) {
                if (parent.getAdapter().getItemViewType(position) != BaseTurboAdapter.TYPE_FOOTER_VIEW
                        && parent.getAdapter().getItemViewType(position) != BaseTurboAdapter.TYPE_LOADING_VIEW) {
                    int count = findFullSpanCountByPosition(position);
                    if (mFullSpanRecorder != null) {
                        mFullSpanRecorder.put(position, count + 1);
                    }
                }
                return true;
            } else {
                if (mFullSpanRecorder != null) {
                    mFullSpanRecorder.delete(position);
                }
            }
        } else if (manager instanceof StaggeredGridLayoutManager) {
            View view = manager.findViewByPosition(position);
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                if (((StaggeredGridLayoutManager.LayoutParams) layoutParams).isFullSpan()) {
                    if (parent.getAdapter().getItemViewType(position) != BaseTurboAdapter.TYPE_FOOTER_VIEW
                            && parent.getAdapter().getItemViewType(position) != BaseTurboAdapter.TYPE_LOADING_VIEW) {
                        int count = findFullSpanCountByPosition(position);
                        if (mFullSpanRecorder != null) {
                            mFullSpanRecorder.put(position, count + 1);
                        }
                    }
                    return true;
                } else {
                    if (mFullSpanRecorder != null) {
                        mFullSpanRecorder.delete(position);
                    }
                }
            }
        }else {
            throw new UnsupportedOperationException("the GridDividerItemDecoration can only be used in " +
                    "the RecyclerView which use a GridLayoutManager or StaggeredGridLayoutManager");
        }
        return false;
    }

    @Override
    protected boolean isFirstColumn(RecyclerView parent, int position, int spanCount, int childCount) {
        if (mOrientation == VERTICAL) {
            boolean isFirst = isFullSpan(parent, position, spanCount);
            if (isFirst) {
                return isFirst;
            }
            int count = findFullSpanCountByPosition(position);
            return ((position - count) % spanCount == 0);
        } else {
            if(position == 0){
                boolean isFirst = isFullSpan(parent, position, spanCount);
                if(isFirst){
                    return true;
                }
            }
            return position < spanCount;
        }
    }

    @Override
    protected boolean isLastColumn(RecyclerView parent, int position, int spanCount, int childCount) {
        if (mOrientation == VERTICAL) {
            boolean isLast = isFullSpan(parent, position, spanCount);
            if (isLast) {
                return isLast;
            }
            int count = findFullSpanCountByPosition(position);
            return ((position + 1 - count) % spanCount == 0);
        } else {
            int count = findFullSpanCountByPosition(position);
            int lastColumnCount = (childCount-count) % spanCount;
            lastColumnCount = lastColumnCount == 0 ? spanCount : lastColumnCount;
            return position >= childCount - lastColumnCount;
        }
    }

    protected boolean isFirstRow(RecyclerView parent, int position, int spanCount, int childCount) {
        if (mOrientation == VERTICAL) {
            if(position == 0){
                boolean isFirst = isFullSpan(parent, position, spanCount);
                if(isFirst){
                    return true;
                }
            }
            return position < spanCount;
        } else {
            boolean isFirst = isFullSpan(parent, position, spanCount);
            if (isFirst) {
                return isFirst;
            }
            int count = findFullSpanCountByPosition(position);
            return ((position - count) % spanCount == 0);
        }
    }


    protected boolean isLastRow(RecyclerView parent, int position, int spanCount, int childCount) {
        if (mOrientation == VERTICAL) {
            int count = findFullSpanCountByPosition(position);
            int lastColumnCount = (childCount-count) % spanCount;
            lastColumnCount = lastColumnCount == 0 ? spanCount : lastColumnCount;
            return position >= childCount - lastColumnCount;
        } else {
            boolean isLast = isFullSpan(parent, position, spanCount);
            if (isLast) {
                return isLast;
            }
            int count = findFullSpanCountByPosition(position);
            return ((position + 1 - count) % spanCount == 0);
        }
    }

    public void registerFullSpanRecorder(SparseIntArray fullSpanRecorder) {
        mFullSpanRecorder = fullSpanRecorder;
    }
}
