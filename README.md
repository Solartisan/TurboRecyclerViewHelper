TurboRecyclerViewHelper project（[中文文档](https://github.com/Solartisan/TurboRecyclerViewHelper/blob/master/README-cn.md)）
=======================================================================================================================

Travis master: [![Build Status](https://api.travis-ci.org/Solartisan/TurboRecyclerViewHelper.svg?branch=master)](https://travis-ci.org/Solartisan/TurboRecyclerViewHelper)

A library that powerful and flexible RecyclerView.
Like us if you love our work. your support is great power for us.


Features（[download apk](https://github.com/Solartisan/TurboRecyclerViewHelper/raw/master/preview/turbo_demo.apk)）
-----------------------------------------------------------------------------------------------------------------
* **RecyclerView upward sliding/left sliding load**
* **Supports setEmptyView**
* **Supports add HeaderView and FooterView**
* **Supports item click and long click**
* **Supports custom load view**
* **Supports loading database data**

<img src="./preview/simple.gif">
<img src="./preview/multi.gif">

Gradle
---
```
dependencies {
    ...
    compile 'cc.solart:turbo-recyclerview-helper:1.1.0'
}
```

Usage
---
#### **use `TurboRecyclerView` in the layout file**

```xml
    <cc.solart.turbo.TurboRecyclerView
        android:id="@+id/rv_list"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        app:maxDragDistance="100"
        app:enableLoad="true" />
```

#### **create a simple adapter extends `BaseTurboAdapter`**

```java
public class SimpleAdapter extends BaseTurboAdapter<String, SimpleAdapter.SimpleViewHolder> {
    
    public SimpleAdapter(Context context) {
        super(context);
    }

    public SimpleAdapter(Context context, List<String> data) {
        super(context, data);
    }

    @Override
    protected SimpleViewHolder onCreateDefViewHolder(ViewGroup parent, int viewType) {
        return new SimpleViewHolder(inflateItemView(R.layout.item_simple, parent));
    }

    @Override
    protected void convert(SimpleViewHolder holder, String item) {
        holder.tv.setText(item);
    }


    class SimpleViewHolder extends BaseViewHolder {

        TextView tv;

        protected SimpleViewHolder(View view) {
            super(view);
            tv = findViewById(R.id.simple_text);
        }
    }
}
```

#### **add HeaderView and FooterView**

```java
        mAdapter = new SimpleAdapter(this, Arrays.asList(sCheeseStrings));
        View header = LayoutInflater.from(this).inflate(R.layout.item_header, (ViewGroup) mRecyclerView.getParent(), false);
        mAdapter.addHeaderView(header);
        View footer = LayoutInflater.from(this).inflate(R.layout.item_footer, (ViewGroup) mRecyclerView.getParent(), false);
        mAdapter.addFooterView(footer);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLoadMoreEnabled(true);
```

#### **add `OnItemClickListener`**
        
```java
        mAdapter.addOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                Toast.makeText(SimpleActivity.this, "您点击了第" + position + "个item", Toast.LENGTH_SHORT).show();
            }
        });
```

#### **enable loading more and add `OnLoadingMoreListener`**

```java
		mRecyclerView.setLoadMoreEnabled(true);//or use enableLoad in xml
    	mRecyclerView.addOnLoadingMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadingMore() {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.loadMoreComplete(Arrays.asList(sCheeseStrings));
                    }
                }, 2000);
            }
   	    });
```

Changelog
---
* **1.0.0-beta**
    * Initial release
* **1.0.1-beta**
    * Add styleable
* **1.0.2-beta**
    * Fix empty state can be refreshed
    * Modify click/long click 
* **1.0.3-beta**
    * Fix missing generic type
* **1.0.4-beta**
    * Fix ClassCastException in isEmpty()
* **1.0.5-beta**
    * Fix setSpanSizeLookup invalid
* **1.0.6-beta**
    * Fix incorrect slide direction cause refresh
* **1.0.7-beta**
    * Fix AdapterView setOnClick exception
* **1.0.8-beta**
    * Fix TurboRecyclerView sliding in the end does not respond to click events
* **1.0.9-beta**
    * add removeData(List<T> data)
* **1.1.0**
    * refactor Adapter, add BaseCursorAdapter

Thanks
------
[base-adapter-helper](https://github.com/JoanZapata/base-adapter-helper)

[RecyclerItemDecoration](https://github.com/dinuscxj/RecyclerItemDecoration)
    
License
---

    Copyright 2015 - 2016 solartisan/imilk

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.