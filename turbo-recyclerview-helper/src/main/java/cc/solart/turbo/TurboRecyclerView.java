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

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.support.annotation.Nullable;
import android.support.v4.view.MotionEventCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ViewConfiguration;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;

import java.util.ArrayList;
import java.util.List;

/**
 * A subclass of RecyclerView responsible for providing views that refresh new data set.
 *
 * author: imilk
 * https://github.com/Solartisan/TurboRecyclerViewHelper
 */
public class TurboRecyclerView extends RecyclerView {
    private static final String TAG = "TurboRecyclerView";

    private static final int DRAG_MAX_DISTANCE = 100;
    private static final float DRAG_RATE = .5f;
    private static final int INVALID_POINTER = -1;

    private final ArrayList<OnItemTouchListener> mOnItemTouchListeners =
            new ArrayList<>();
    private final ArrayList<OnLoadMoreListener> mOnLoadMoreListeners =
            new ArrayList<>();

    private OnItemTouchListener mActiveOnItemTouchListener;

    private int mInitialMotionX, mInitialMotionY;
    private int mTouchSlop;

    private int mLastVisibleItemPosition;
    private int[] mLastPositions;

    private int mTotalDragDistance;

    private boolean mIsLoading;
    private boolean mLoadEnabled;

    private int mActivePointerId = INVALID_POINTER;

    private ObjectAnimator mResetAnimator;
    private Interpolator mInterpolator = new DecelerateInterpolator();

    private static int convertDpToPixel(Context context, int dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    public TurboRecyclerView(Context context) {
        this(context, null);
    }

    public TurboRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TurboRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mTouchSlop = ViewConfiguration.get(context).getScaledTouchSlop();
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.TurboRecyclerView);
        int max = ta.getInteger(R.styleable.TurboRecyclerView_maxDragDistance, DRAG_MAX_DISTANCE);
        mTotalDragDistance = convertDpToPixel(context, max);
        mLoadEnabled = ta.getBoolean(R.styleable.TurboRecyclerView_enableLoad, false);
        ta.recycle();
    }

    @Override
    public void addOnItemTouchListener(OnItemTouchListener listener) {
        super.addOnItemTouchListener(listener);
        mOnItemTouchListeners.add(listener);
    }

    @Override
    public void removeOnItemTouchListener(OnItemTouchListener listener) {
        super.removeOnItemTouchListener(listener);
        mOnItemTouchListeners.remove(listener);
        if (mActiveOnItemTouchListener == listener) {
            mActiveOnItemTouchListener = null;
        }
    }

    public void addOnLoadingMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListeners.add(listener);
    }

    public void removeOnLoadingMoreListener(OnLoadMoreListener listener) {
        mOnLoadMoreListeners.remove(listener);
    }

    public void setLoadMoreEnabled(boolean enabled) {
        mLoadEnabled = enabled;
    }

    public boolean isLoadMoreEnabled() {
        return mLoadEnabled;
    }

    @Override
    public void setItemAnimator(ItemAnimator animator) {
        super.setItemAnimator(animator);
    }

    @Override
    public void setAdapter(Adapter adapter) {
        if (adapter instanceof OnLoadMoreListener) {
            addOnLoadingMoreListener((OnLoadMoreListener) adapter);
        }
        super.setAdapter(adapter);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        if (getLayoutManager() instanceof LinearLayoutManager) {
            mLastVisibleItemPosition = ((LinearLayoutManager) getLayoutManager())
                    .findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof GridLayoutManager) {
            mLastVisibleItemPosition = ((GridLayoutManager) getLayoutManager())
                    .findLastVisibleItemPosition();
        } else if (getLayoutManager() instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager staggeredGridLayoutManager
                    = (StaggeredGridLayoutManager) getLayoutManager();
            if (mLastPositions == null) {
                mLastPositions = new int[staggeredGridLayoutManager.getSpanCount()];
            }
            staggeredGridLayoutManager.findLastVisibleItemPositions(mLastPositions);
            mLastVisibleItemPosition = findMax(mLastPositions);
        } else {
            throw new RuntimeException(
                    "Unsupported LayoutManager used. Valid ones are LinearLayoutManager, GridLayoutManager and StaggeredGridLayoutManager");
        }
    }

    private int findMax(int[] lastPositions) {
        int max = lastPositions[0];
        for (int value : lastPositions) {
            if (value > max) {
                max = value;
            }
        }
        return max;
    }

    private int getMotionEventX(MotionEvent e, int pointerIndex) {
        return (int) (MotionEventCompat.getX(e, pointerIndex) + 0.5f);
    }

    private int getMotionEventY(MotionEvent e, int pointerIndex) {
        return (int) (MotionEventCompat.getY(e, pointerIndex) + 0.5f);
    }

    /**
     * @return Whether it is possible for the child view of this layout to
     * scroll up. Override this if the child view is a custom view.
     */
    private boolean canScrollEnd() {
        return ViewCompat.canScrollVertically(this, 1) || ViewCompat.canScrollHorizontally(this, 1);
    }


    private boolean dispatchOnItemTouchIntercept(MotionEvent e) {
        final int action = e.getAction();
        if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_DOWN) {
            mActiveOnItemTouchListener = null;
        }

        final int listenerCount = mOnItemTouchListeners.size();
        for (int i = 0; i < listenerCount; i++) {
            final OnItemTouchListener listener = mOnItemTouchListeners.get(i);
            if (listener.onInterceptTouchEvent(this, e) && action != MotionEvent.ACTION_CANCEL) {
                mActiveOnItemTouchListener = listener;
                return true;
            }
        }
        return false;
    }

    private boolean dispatchOnItemTouch(MotionEvent e) {
        final int action = e.getAction();
        if (mActiveOnItemTouchListener != null) {
            if (action == MotionEvent.ACTION_DOWN) {
                // Stale state from a previous gesture, we're starting a new one. Clear it.
                mActiveOnItemTouchListener = null;
            } else {
                mActiveOnItemTouchListener.onTouchEvent(this, e);
                if (action == MotionEvent.ACTION_CANCEL || action == MotionEvent.ACTION_UP) {
                    // Clean up for the next gesture.
                    mActiveOnItemTouchListener = null;
                }
                return true;
            }
        }

        // Listeners will have already received the ACTION_DOWN via dispatchOnItemTouchIntercept
        // as called from onInterceptTouchEvent; skip it.
        if (action != MotionEvent.ACTION_DOWN) {
            final int listenerCount = mOnItemTouchListeners.size();
            for (int i = 0; i < listenerCount; i++) {
                final OnItemTouchListener listener = mOnItemTouchListeners.get(i);
                if (listener.onInterceptTouchEvent(this, e)) {
                    mActiveOnItemTouchListener = listener;
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent e) {
        if (!mLoadEnabled || canScrollEnd() || mIsLoading || isEmpty()) {
            return super.onInterceptTouchEvent(e);
        }

        if (dispatchOnItemTouchIntercept(e)) {
            return true;
        }

        final int action = MotionEventCompat.getActionMasked(e);
        final int actionIndex = MotionEventCompat.getActionIndex(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                mActivePointerId = MotionEventCompat.getPointerId(e, 0);
                mInitialMotionX = getMotionEventX(e, actionIndex);
                mInitialMotionY = getMotionEventY(e, actionIndex);
            }
            break;

            case MotionEvent.ACTION_POINTER_DOWN: {
                mActivePointerId = MotionEventCompat.getPointerId(e, actionIndex);
                mInitialMotionX = getMotionEventX(e, actionIndex);
                mInitialMotionY = getMotionEventY(e, actionIndex);
            }
            break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = INVALID_POINTER;
                break;
            case MotionEventCompat.ACTION_POINTER_UP: {
                onPointerUp(e);
            }
            break;
        }
        return super.onInterceptTouchEvent(e);
    }

    private void onPointerUp(MotionEvent e) {
        final int actionIndex = MotionEventCompat.getActionIndex(e);
        if (MotionEventCompat.getPointerId(e, actionIndex) == mActivePointerId) {
            // Pick a new pointer to pick up the slack.
            final int newIndex = actionIndex == 0 ? 1 : 0;
            mActivePointerId = MotionEventCompat.getPointerId(e, newIndex);
            mInitialMotionX = getMotionEventX(e, newIndex);
            mInitialMotionY = getMotionEventY(e, newIndex);
        }
    }

    private boolean isEmpty() {
        if (getAdapter() == null || !(getAdapter() instanceof AbsTurboAdapter)) {
            return true;
        }
        return ((AbsTurboAdapter) getAdapter()).isEmpty();
    }

    @Override
    public boolean onTouchEvent(MotionEvent e) {
        if (!mLoadEnabled || canScrollEnd() || mIsLoading || isEmpty()) {
            return super.onTouchEvent(e);
        }

        if (dispatchOnItemTouch(e)) {
            return true;
        }

        if (getLayoutManager() == null) {
            return false;
        }

        final boolean canScrollHorizontally = getLayoutManager().canScrollHorizontally();
        final boolean canScrollVertically = getLayoutManager().canScrollVertically();

        final int action = MotionEventCompat.getActionMasked(e);
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int index = MotionEventCompat.getActionIndex(e);
                mActivePointerId = MotionEventCompat.getPointerId(e, 0);
                mInitialMotionX = getMotionEventX(e, index);
                mInitialMotionY = getMotionEventY(e, index);
            }
            break;

            case MotionEventCompat.ACTION_POINTER_DOWN: {
                final int index = MotionEventCompat.getActionIndex(e);
                mActivePointerId = MotionEventCompat.getPointerId(e, index);
                mInitialMotionX = getMotionEventX(e, index);
                mInitialMotionY = getMotionEventY(e, index);
            }
            break;

            case MotionEvent.ACTION_MOVE: {

                final int index = MotionEventCompat.findPointerIndex(e, mActivePointerId);
                if (index < 0) {
                    Log.w(TAG, "pointer index for id " + index + " not found. return super");
                    return super.onTouchEvent(e);
                }

                final int x = getMotionEventX(e, index);
                final int y = getMotionEventY(e, index);

                int deltaY = y - mInitialMotionY;
                if (canScrollVertically && Math.abs(deltaY) > mTouchSlop && deltaY < 0) {
                    float targetEnd = -dampAxis(deltaY);
                    setTranslationY(targetEnd);
                    return true;
                }

                int deltaX = x - mInitialMotionX;
                if (canScrollHorizontally && Math.abs(deltaX) > mTouchSlop && deltaX < 0) {
                    float targetEnd = -dampAxis(deltaX);
                    setTranslationX(targetEnd);
                    return true;
                }
            }
            break;
            case MotionEventCompat.ACTION_POINTER_UP: {
                onPointerUp(e);
            }
            break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL: {
                if (canScrollHorizontally)
                    animateOffsetToEnd("translationX", mInterpolator, 0f);
                if (canScrollVertically)
                    animateOffsetToEnd("translationY", mInterpolator, 0f);
                final int index = MotionEventCompat.findPointerIndex(e, mActivePointerId);
                if (index < 0) {
                    Log.e(TAG, "Got ACTION_UP event but don't have an active pointer id.");
                    return super.onTouchEvent(e);
                }

                final int y = getMotionEventY(e, index);
                final int x = getMotionEventX(e, index);
                final float overScrollBottom = (mInitialMotionY - y) * DRAG_RATE;
                final float overScrollRight = (mInitialMotionX - x) * DRAG_RATE;

                if ((canScrollVertically && overScrollBottom > mTotalDragDistance)
                        || (canScrollHorizontally && overScrollRight > mTotalDragDistance)) {
                    Log.i(TAG, "refreshing...");
                    mIsLoading = true;
                    dispatchOnLoadingMoreListeners();
                    smoothScrollToPosition(mLastVisibleItemPosition + 1);
                    mActivePointerId = INVALID_POINTER;
                    return true;
                } else {
                    mIsLoading = false;
                    mActivePointerId = INVALID_POINTER;
                }

            }
            break;
        }
        return super.onTouchEvent(e);
    }

    private void dispatchOnLoadingMoreListeners() {
        if (mOnLoadMoreListeners != null) {
            for (int i = 0; i < mOnLoadMoreListeners.size(); i++) {
                final OnLoadMoreListener listener = mOnLoadMoreListeners.get(i);
                if (listener != null) {
                    listener.onLoadingMore();
                }
            }
        }
    }

    //Calculating the damping distance of any axis
    private float dampAxis(int delta) {
        final float scrollEnd = delta * DRAG_RATE;
        float mCurrentDragPercent = scrollEnd / mTotalDragDistance;
        float boundedDragPercent = Math.min(1f, Math.abs(mCurrentDragPercent));
        float extraOS = Math.abs(scrollEnd) - mTotalDragDistance;
        float slingshotDist = mTotalDragDistance;
        float tensionSlingshotPercent = Math.max(0,
                Math.min(extraOS, slingshotDist * 2) / slingshotDist);
        float tensionPercent = (float) ((tensionSlingshotPercent / 4) -
                Math.pow((tensionSlingshotPercent / 4), 2)) * 2f;
        float extraMove = (slingshotDist) * tensionPercent / 2;
        float targetEnd = (slingshotDist * boundedDragPercent) + extraMove;
        return targetEnd;
    }

    private void animateOffsetToEnd(final String propertyName, final Interpolator interpolator, float... value) {
        if (mResetAnimator == null) {
            mResetAnimator = new ObjectAnimator();
            mResetAnimator.setTarget(this);
        }
        mResetAnimator.cancel();
        mResetAnimator.setPropertyName(propertyName);
        mResetAnimator.setFloatValues(value);
        mResetAnimator.setInterpolator(interpolator);
        mResetAnimator.start();
    }

    public void loadMoreComplete(List<?> data) {
        if (mIsLoading) {
            mIsLoading = false;
            Adapter adapter = getAdapter();
            if (adapter instanceof BaseTurboAdapter) {
                ((BaseTurboAdapter) adapter).loadingMoreComplete(data);
            } else {
                Log.e(TAG, "Cannot callback adapter.");
            }
        }
    }


}
