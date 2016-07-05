/*
 * This code is cloned from LinearOffsetsItemDecoration provided by RecyclerItemDecoration
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
 * This class can only be used in the RecyclerView which use a LinearLayoutManager or
 * its subclass.
 */
public class LinearOffsetsItemDecoration extends BaseItemDecoration {

    private final SparseArray<IOffsetsCreator> mTypeOffsetsFactories = new SparseArray<>();

    private int mItemOffsets;

    public LinearOffsetsItemDecoration(int orientation) {
       super(orientation);
    }

    public void setItemOffsets(int itemOffsets) {
        this.mItemOffsets = itemOffsets;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int adapterPosition = parent.getChildAdapterPosition(view);
        if (adapterPosition == parent.getAdapter().getItemCount() - 1) {
            return;
        }

        if (mOrientation == HORIZONTAL) {
            outRect.right = getDividerOffsets(parent, view);
        } else {
            outRect.bottom = getDividerOffsets(parent, view);
        }
    }

    private int getDividerOffsets(RecyclerView parent, View view) {
        if (mTypeOffsetsFactories.size() == 0) {
            return mItemOffsets;
        }

        final int adapterPosition = parent.getChildAdapterPosition(view);
        final int itemType = parent.getAdapter().getItemViewType(adapterPosition);
        final IOffsetsCreator offsetsCreator = mTypeOffsetsFactories.get(itemType);

        if (offsetsCreator != null) {
            return offsetsCreator.create(parent, adapterPosition);
        }

        return 0;
    }

    public void registerTypeOffsets(int itemType, IOffsetsCreator offsetsCreator) {
        mTypeOffsetsFactories.put(itemType, offsetsCreator);
    }

    public interface IOffsetsCreator {
        int create(RecyclerView parent, int adapterPosition);
    }
}
