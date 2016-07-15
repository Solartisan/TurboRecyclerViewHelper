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
import android.database.Cursor;
import android.util.Log;
import android.widget.Filter;
import android.widget.FilterQueryProvider;
import android.widget.Filterable;

/**
 * Adapter that exposes data from a {@link android.database.Cursor Cursor} to a
 * {@link android.support.v7.widget.RecyclerView RecyclerView} widget.
 * <p/>
 * The Cursor must include a column named "_id" or this class will not work.
 * Additionally, using {@link android.database.MergeCursor} with this class will
 * not work if the merged Cursors have overlapping values in their "_id"
 * columns.
 * <p/>
 * author: imilk
 * https://github.com/Solartisan/TurboRecyclerViewHelper
 */
public abstract class BaseCursorAdapter<VH extends BaseViewHolder> extends AbsTurboAdapter<Cursor, BaseViewHolder>
        implements Filterable, CursorFilter.CursorFilterClient {

    protected static final String TAG = "BaseCursorAdapter";

    protected Cursor mCursor;
    protected CursorFilter mCursorFilter;
    protected FilterQueryProvider mFilterQueryProvider;
    protected boolean mDataValid;
    protected int mRowIDColumn;
    private boolean mEmptyEnable;

    /**
     * Constructor that disallows control over auto-requery. As an alternative,
     * use {@link android.app.LoaderManager}/{@link android.support.v4.app.LoaderManager}
     * with a {@link android.content.CursorLoader}/{@link android.support.v4.content.CursorLoader}
     *
     * @param c       The cursor from which to get the data.
     * @param context The context
     */
    public BaseCursorAdapter(Context context, Cursor c) {
        super(context);
        boolean cursorPresent = c != null;
        mCursor = c;
        mDataValid = cursorPresent;
        mRowIDColumn = cursorPresent ? c.getColumnIndexOrThrow("_id") : -1;
    }

    @Override
    public Cursor getItem(int position) {
        if (mDataValid && mCursor != null) {
            mCursor.moveToPosition(position);
            return mCursor;
        } else {
            return null;
        }
    }

    @Override
    public int getItemCount() {

        int count = 0;
        if (mDataValid && mCursor != null) {
            count = mCursor.getCount() + getHeaderViewCount() + getFooterViewCount();
        }

        mEmptyEnable = false;
        if (count == 0) {
            mEmptyEnable = true;
            count += getEmptyViewCount();
        }
        return count;
    }

    @Override
    public long getItemId(int position) {
        if (mDataValid && mCursor != null) {
            if (mCursor.moveToPosition(position)) {
                return mCursor.getLong(mRowIDColumn);
            } else {
                return -1;
            }
        } else {
            return -1;
        }
    }

    @Override
    protected boolean isEmpty() {
        return getHeaderViewCount() + getFooterViewCount() + mCursor.getCount() == 0;
    }

    @Override
    public final int getItemViewType(int position) {
        if (mHeaderView != null && position == 0) {
            return TYPE_HEADER_VIEW;
        } else if (mEmptyView != null && getItemCount() == 1 && mEmptyEnable) {
            return TYPE_EMPTY_VIEW;
        } else if (mDataValid && mCursor != null && mFooterView != null
                && position == mCursor.getCount() + getHeaderViewCount()) {
            return TYPE_FOOTER_VIEW;
        }
        return getDefItemViewType(position);
    }

    @Override
    protected final void bindHolder(BaseViewHolder holder, int position) {
        if (!mDataValid) {
            throw new IllegalStateException("this should only be called when the cursor is valid");
        }
        if (!mCursor.moveToPosition(position - getHeaderViewCount())) {
            throw new IllegalStateException("couldn't move cursor to position " + position);
        }
        convert((VH) holder, mCursor);
    }


    /**
     * Implement this method and use the helper to adapt the view to the given item.
     *
     * @param holder A fully initialized helper.
     * @param cursor The cursor that needs to be displayed.
     */
    abstract protected void convert(VH holder, Cursor cursor);


    @Override
    public Filter getFilter() {
        if (mCursorFilter == null) {
            mCursorFilter = new CursorFilter(this);
        }
        return mCursorFilter;
    }

    /**
     * <p>Converts the cursor into a CharSequence. Subclasses should override this
     * method to convert their results. The default implementation returns an
     * empty String for null values or the default String representation of
     * the value.</p>
     *
     * @param cursor the cursor to convert to a CharSequence
     * @return a CharSequence representing the value
     */
    @Override
    public CharSequence convertToString(Cursor cursor) {
        return cursor == null ? "" : cursor.toString();
    }

    /**
     * Runs a query with the specified constraint. This query is requested
     * by the filter attached to this adapter.
     * <p/>
     * The query is provided by a
     * {@link android.widget.FilterQueryProvider}.
     * If no provider is specified, the current cursor is not filtered and returned.
     * <p/>
     * After this method returns the resulting cursor is passed to {@link #changeCursor(Cursor)}
     * and the previous cursor is closed.
     * <p/>
     * This method is always executed on a background thread, not on the
     * application's main thread (or UI thread.)
     * <p/>
     * Contract: when constraint is null or empty, the original results,
     * prior to any filtering, must be returned.
     *
     * @param constraint the constraint with which the query must be filtered
     * @return a Cursor representing the results of the new query
     * @see #getFilter()
     * @see #getFilterQueryProvider()
     * @see #setFilterQueryProvider(android.widget.FilterQueryProvider)
     */
    @Override
    public Cursor runQueryOnBackgroundThread(CharSequence constraint) {
        if (mFilterQueryProvider != null) {
            return mFilterQueryProvider.runQuery(constraint);
        }

        return mCursor;
    }


    /**
     * Returns the query filter provider used for filtering. When the
     * provider is null, no filtering occurs.
     *
     * @return the current filter query provider or null if it does not exist
     * @see #setFilterQueryProvider(android.widget.FilterQueryProvider)
     * @see #runQueryOnBackgroundThread(CharSequence)
     */
    public FilterQueryProvider getFilterQueryProvider() {
        return mFilterQueryProvider;
    }

    /**
     * Sets the query filter provider used to filter the current Cursor.
     * The provider's
     * {@link android.widget.FilterQueryProvider#runQuery(CharSequence)}
     * method is invoked when filtering is requested by a client of
     * this adapter.
     *
     * @param filterQueryProvider the filter query provider or null to remove it
     * @see #getFilterQueryProvider()
     * @see #runQueryOnBackgroundThread(CharSequence)
     */
    public void setFilterQueryProvider(FilterQueryProvider filterQueryProvider) {
        mFilterQueryProvider = filterQueryProvider;
    }

    @Override
    public Cursor getCursor() {
        return mCursor;
    }

    /**
     * Change the underlying cursor to a new cursor. If there is an existing cursor it will be
     * closed.
     *
     * @param cursor The new cursor to be used
     */
    @Override
    public void changeCursor(Cursor cursor) {
        Cursor old = swapCursor(cursor);
        if (old != null) {
            old.close();
        }
    }

    /**
     * Swap in a new Cursor, returning the old Cursor.  Unlike
     * {@link #changeCursor(Cursor)}, the returned old Cursor is <em>not</em>
     * closed.
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set Cursor, or null if there was not one.
     * If the given new Cursor is the same instance is the previously set
     * Cursor, null is also returned.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;

        mCursor = newCursor;
        if (newCursor != null) {
            mRowIDColumn = newCursor.getColumnIndexOrThrow("_id");
            mDataValid = true;
            // notify the observers about the new cursor
            notifyDataSetChanged();
        } else {
            mRowIDColumn = -1;
            mDataValid = false;
            // notify the observers about the lack of a data set
            notifyDataSetChanged();
//            notifyItemRangeRemoved(0, oldCursor.getCount() - 1);
        }
        return oldCursor;
    }

}
