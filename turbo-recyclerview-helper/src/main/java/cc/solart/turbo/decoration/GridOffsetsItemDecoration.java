/*
 * This code is cloned from GridOffsetsItemDecoration provided by RecyclerItemDecoration
 * Copyright (C) 2016 dinus
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


import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.view.View;

/**
 * This class can only be used in the RecyclerView which use a GridLayoutManager
 * or StaggeredGridLayoutManager, but it's not always work for the StaggeredGridLayoutManager,
 * because we can't figure out which position should belong to the last column or the last row
 */
public class GridOffsetsItemDecoration extends BaseGridItemDecoration {

    private final SparseArray<IOffsetsCreator> mTypeOffsetsFactories = new SparseArray<>();

    private int mVerticalItemOffsets;
    private int mHorizontalItemOffsets;

    public GridOffsetsItemDecoration(int orientation) {
        super(orientation);
    }

    public void setVerticalItemOffsets(int verticalItemOffsets) {
        this.mVerticalItemOffsets = verticalItemOffsets;
    }

    public void setHorizontalItemOffsets(int horizontalItemOffsets) {
        this.mHorizontalItemOffsets = horizontalItemOffsets;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int spanCount = getSpanCount(parent);
        int childCount = parent.getAdapter().getItemCount();
        int adapterPosition = parent.getChildAdapterPosition(view);

        outRect.set(getHorizontalOffsets(parent, view) / 2, 0, getHorizontalOffsets(parent, view) / 2, getVerticalOffsets(parent, view));

        if (isFirstColumn(parent, adapterPosition, spanCount, childCount)) {
            outRect.left = getHorizontalOffsets(parent, view);
        }

        if (isLastColumn(parent, adapterPosition, spanCount, childCount)) {
            outRect.right = getHorizontalOffsets(parent, view);
        }

        if (isLastRow(parent, adapterPosition, spanCount, childCount)) {
            outRect.bottom = 0;
        }
    }

    protected int getHorizontalOffsets(RecyclerView parent, View view) {
        if (mTypeOffsetsFactories.size() == 0) {
            return mHorizontalItemOffsets;
        }

        final int adapterPosition = parent.getChildAdapterPosition(view);
        final int itemType = parent.getAdapter().getItemViewType(adapterPosition);
        final IOffsetsCreator offsetsCreator = mTypeOffsetsFactories.get(itemType);

        if (offsetsCreator != null) {
            return offsetsCreator.createHorizontal(parent, adapterPosition);
        }

        return mHorizontalItemOffsets;
    }

    protected int getVerticalOffsets(RecyclerView parent, View view) {
        if (mTypeOffsetsFactories.size() == 0) {
            return mVerticalItemOffsets;
        }

        final int adapterPosition = parent.getChildAdapterPosition(view);
        final int itemType = parent.getAdapter().getItemViewType(adapterPosition);
        final IOffsetsCreator offsetsCreator = mTypeOffsetsFactories.get(itemType);

        if (offsetsCreator != null) {
            return offsetsCreator.createVertical(parent, adapterPosition);
        }

        return mVerticalItemOffsets;
    }

    public void registerTypeOffsets(int itemType, IOffsetsCreator offsetsCreator) {
        mTypeOffsetsFactories.put(itemType, offsetsCreator);
    }

    public interface IOffsetsCreator {
        int createVertical(RecyclerView parent, int adapterPosition);

        int createHorizontal(RecyclerView parent, int adapterPosition);
    }

}
