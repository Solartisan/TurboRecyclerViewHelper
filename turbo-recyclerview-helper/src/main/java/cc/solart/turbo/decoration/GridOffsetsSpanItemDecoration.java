/*
 * Copyright 2015 - 2016 solartisan/imilk
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
public class GridOffsetsSpanItemDecoration extends GridOffsetsItemDecoration{
    private final SparseIntArray mFullSpanCounts = new SparseIntArray();
    private int mFullSpanCount;

    public GridOffsetsSpanItemDecoration(@Orientation int orientation) {
        super(orientation);
    }

    private int findFullSpanCountByPosition(int position) {
        for (int i = position; i >= 0; position--) {
            int target = mFullSpanCounts.get(position,-1);
            if (target != -1) {
                return target;
            }
        }
        return -1;
    }

    private boolean isFullSpan(RecyclerView parent, int position, int spanCount) {
        RecyclerView.LayoutManager manager = parent.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager.SpanSizeLookup lookup = ((GridLayoutManager) manager).getSpanSizeLookup();
            int spanSize = lookup.getSpanSize(position);
            if (spanSize == spanCount) {
                if (mFullSpanCounts.get(position, -1) == -1
                        && parent.getAdapter().getItemViewType(position) != BaseTurboAdapter.TYPE_FOOTER_VIEW
                        && parent.getAdapter().getItemViewType(position) != BaseTurboAdapter.TYPE_LOADING_VIEW) {
                    mFullSpanCount ++;
                    mFullSpanCounts.put(position, mFullSpanCount);
                }
                return true;
            }
        } else if (manager instanceof StaggeredGridLayoutManager) {
            View view = manager.findViewByPosition(position);
            RecyclerView.ViewHolder holder = parent.getChildViewHolder(view);
            ViewGroup.LayoutParams layoutParams = holder.itemView.getLayoutParams();
            if (layoutParams instanceof StaggeredGridLayoutManager.LayoutParams) {
                mFullSpanCount++;
                return ((StaggeredGridLayoutManager.LayoutParams) layoutParams).isFullSpan();
            }
        }
        return false;
    }

    @Override
    protected boolean isFirstColumn(RecyclerView parent, int position, int spanCount, int childCount) {
        if (mOrientation == GRID_OFFSETS_VERTICAL) {
            boolean isFirst = isFullSpan(parent, position, spanCount);
            if (isFirst) {
                return isFirst;
            }
            int count = findFullSpanCountByPosition(position);
            return ((position - count) % spanCount == 0);
        } else {
            //TODO Consider full-span factors
            return position < spanCount;
        }
    }

    @Override
    protected boolean isLastColumn(RecyclerView parent, int position, int spanCount, int childCount) {
        if (mOrientation == GRID_OFFSETS_VERTICAL) {
            boolean isLast = isFullSpan(parent, position, spanCount);
            if (isLast) {
                return isLast;
            }
            int count = findFullSpanCountByPosition(position);
            return ((position + 1 - count) % spanCount == 0);
        } else {
            //TODO Consider full-span factors
            int lastColumnCount = childCount % spanCount;
            lastColumnCount = lastColumnCount == 0 ? spanCount : lastColumnCount;
            return position >= childCount - lastColumnCount;
        }
    }
}
