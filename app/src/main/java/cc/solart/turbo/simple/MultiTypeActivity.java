package cc.solart.turbo.simple;

import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import cc.solart.turbo.OnLoadMoreListener;
import cc.solart.turbo.TurboRecyclerView;
import cc.solart.turbo.decoration.BaseItemDecoration;
import cc.solart.turbo.simple.adapter.MultiTypeAdapter;
import cc.solart.turbo.decoration.LinearOffsetsItemDecoration;
import cc.solart.turbo.simple.model.MultiModel;

public class MultiTypeActivity extends AppCompatActivity {

    TurboRecyclerView mRecyclerView;
    MultiTypeAdapter mAdapter;
    SwipeRefreshLayout mRefreshLayout;
    Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multi_type);
        mRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.refresh);
        mRefreshLayout.setColorSchemeColors(Color.parseColor("#4E76A8"));
        mRecyclerView = (TurboRecyclerView) findViewById(R.id.rv_multi);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        LinearOffsetsItemDecoration decoration =new LinearOffsetsItemDecoration(BaseItemDecoration.VERTICAL);
        decoration.setItemOffsets(getResources().getDimensionPixelOffset(R.dimen.dp_10));
        mRecyclerView.addItemDecoration(decoration);


        mAdapter = new MultiTypeAdapter(this);
        mRecyclerView.setAdapter(mAdapter);
        mAdapter.addData(obtainData());

        mRecyclerView.setLoadMoreEnabled(true);

        mRecyclerView.addOnLoadingMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadingMore() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mRecyclerView.loadMoreComplete(obtainData());
                    }
                }, 2000);
            }
        });

        mRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.resetData(obtainNewData());
                        mRefreshLayout.setRefreshing(false);
                    }
                }, 2000);
            }
        });
    }

    private List<MultiModel> obtainData(){
        ArrayList<MultiModel> list = new ArrayList<>();
        int i = 0;
        while (i < 10){
            MultiModel multiModel = new MultiModel();
            multiModel.setmType(i%3);
            list.add(multiModel);
            i++;
        }
        return list;
    }

    private List<MultiModel> obtainNewData(){
        ArrayList<MultiModel> list = new ArrayList<>();
        int i = 1;
        while (i < 10){
            MultiModel multiModel = new MultiModel();
            multiModel.setmType(i%3);
            list.add(multiModel);
            i++;
        }
        return list;
    }
}
