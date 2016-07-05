/*
 * This code is cloned from LinearDividerItemDecoration provided by RecyclerItemDecoration
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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;
import android.util.SparseIntArray;
import android.view.View;

/**
 * This class can only be used in the RecyclerView which use a LinearLayoutManager or
 * its subclass.
 */
public class LinearDividerItemDecoration extends BaseItemDecoration {
    private static final int[] ATTRS = new int[]{
            android.R.attr.listDivider
    };

    private final SparseIntArray mDividerOffsets = new SparseIntArray();
    private final SparseArray<IDrawableCreator> mTypeDrawableFactories = new SparseArray<>();

    private Drawable mDivider;

    public LinearDividerItemDecoration(Context context, int orientation) {
        super(orientation);
        resolveDivider(context);
    }

    private void resolveDivider(Context context) {
        final TypedArray a = context.obtainStyledAttributes(ATTRS);
        mDivider = a.getDrawable(0);
        a.recycle();
    }

    public void setDivider(Drawable divider) {
        this.mDivider = divider;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        if (mOrientation == VERTICAL) {
            drawVerticalDividers(c, parent);
        } else {
            drawHorizontalDividers(c, parent);
        }
    }

    public void drawVerticalDividers(Canvas c, RecyclerView parent) {
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final Drawable divider = getDivider(parent, params.getViewAdapterPosition());
            final int top = child.getBottom() + params.bottomMargin + Math.round(ViewCompat.getTranslationY(child));
            final int bottom = top + divider.getIntrinsicHeight();

            mDividerOffsets.put(params.getViewAdapterPosition(), divider.getIntrinsicHeight());

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    public void drawHorizontalDividers(Canvas c, RecyclerView parent) {
        final int top = parent.getPaddingTop();
        final int bottom = parent.getHeight() - parent.getPaddingBottom();

        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            final Drawable divider = getDivider(parent, params.getViewAdapterPosition());
            final int left = child.getRight() + params.rightMargin + Math.round(ViewCompat.getTranslationX(child));
            final int right = left + divider.getIntrinsicHeight();

            mDividerOffsets.put(params.getViewAdapterPosition(), divider.getIntrinsicHeight());

            divider.setBounds(left, top, right, bottom);
            divider.draw(c);
        }
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        final int adapterPosition = parent.getChildAdapterPosition(view);
        if (adapterPosition == parent.getAdapter().getItemCount() - 1) {
            return;
        }

        if (mDividerOffsets.indexOfKey(adapterPosition) < 0) {
            mDividerOffsets.put(adapterPosition, getDivider(parent, adapterPosition).getIntrinsicHeight());
        }

        if (mOrientation == VERTICAL) {
            outRect.set(0, 0, 0, mDividerOffsets.get(parent.getChildAdapterPosition(view)));
        } else {
            outRect.set(0, 0, mDividerOffsets.get(parent.getChildAdapterPosition(view)), 0);
        }
    }

    private Drawable getDivider(RecyclerView parent, int adapterPosition) {
        final RecyclerView.Adapter adapter = parent.getAdapter();
        final int itemType = adapter.getItemViewType(adapterPosition);
        final IDrawableCreator drawableCreator = mTypeDrawableFactories.get(itemType);

        if (drawableCreator != null) {
            return drawableCreator.create(parent, adapterPosition);
        }

        return mDivider;
    }

    public void registerTypeDrawable(int itemType, IDrawableCreator drawableCreator) {
        mTypeDrawableFactories.put(itemType, drawableCreator);
    }

    public interface IDrawableCreator {
        Drawable create(RecyclerView parent, int adapterPosition);
    }
}
