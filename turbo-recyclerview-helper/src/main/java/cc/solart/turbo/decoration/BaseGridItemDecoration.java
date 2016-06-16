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

/**
 * author: imilk
 * https://github.com/Solartisan/TurboRecyclerViewHelper
 */
public abstract class BaseGridItemDecoration extends BaseItemDecoration {

    public BaseGridItemDecoration(int orientation) {
        super(orientation);
    }


    protected boolean isFirstColumn(RecyclerView parent, int position, int spanCount, int childCount) {
        if (mOrientation == VERTICAL) {
            return position % spanCount == 0;
        } else {
            return position < spanCount;
        }
    }

    protected boolean isLastColumn(RecyclerView parent, int position, int spanCount, int childCount) {
        if (mOrientation == VERTICAL) {
            return (position + 1) % spanCount == 0;
        } else {
            int lastColumnCount = childCount % spanCount;
            lastColumnCount = lastColumnCount == 0 ? spanCount : lastColumnCount;
            return position >= childCount - lastColumnCount;
        }
    }

    protected boolean isFirstRow(RecyclerView parent, int position, int spanCount, int childCount) {
        if (mOrientation == VERTICAL) {
            return position < spanCount;
        } else {
            return position % spanCount == 0;
        }
    }

    protected boolean isLastRow(RecyclerView parent, int position, int spanCount, int childCount) {
        if (mOrientation == VERTICAL) {
            int lastColumnCount = childCount % spanCount;
            lastColumnCount = lastColumnCount == 0 ? spanCount : lastColumnCount;
            return position >= childCount - lastColumnCount;
        } else {
            return (position + 1) % spanCount == 0;
        }
    }


    protected int getSpanCount(RecyclerView parent) {
        RecyclerView.LayoutManager layoutManager = parent.getLayoutManager();

        if (layoutManager instanceof GridLayoutManager) {
            return ((GridLayoutManager) layoutManager).getSpanCount();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            return ((StaggeredGridLayoutManager) layoutManager).getSpanCount();
        } else {
            throw new UnsupportedOperationException("the +" + getClass().getSimpleName() + " can only be used in " +
                    "the RecyclerView which use a GridLayoutManager or StaggeredGridLayoutManager");
        }
    }
}
