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

import android.support.v7.widget.RecyclerView;
import android.widget.LinearLayout;

/**
 * author: imilk
 * https://github.com/Solartisan/TurboRecyclerViewHelper
 */
public abstract class BaseItemDecoration extends RecyclerView.ItemDecoration {
    protected static final String TAG = "BaseItemDecoration";
    public static final int HORIZONTAL = LinearLayout.HORIZONTAL;
    public static final int VERTICAL = LinearLayout.VERTICAL;
    protected int mOrientation;

    public BaseItemDecoration(int orientation) {
        setOrientation(orientation);
    }

    public void setOrientation(int orientation) {
        this.mOrientation = orientation;
    }
}
