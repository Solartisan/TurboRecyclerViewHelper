package cc.solart.turbo.simple;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.Arrays;

import cc.solart.turbo.OnItemClickListener;
import cc.solart.turbo.OnLoadMoreListener;
import cc.solart.turbo.TurboRecyclerView;
import cc.solart.turbo.decoration.BaseItemDecoration;
import cc.solart.turbo.decoration.LinearDividerItemDecoration;
import cc.solart.turbo.simple.adapter.SimpleAdapter;

public class SimpleActivity extends AppCompatActivity {

    TurboRecyclerView mRecyclerView;
    SimpleAdapter mAdapter;
    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_simple);

        mRecyclerView = (TurboRecyclerView) findViewById(R.id.rv_list);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.addItemDecoration(new LinearDividerItemDecoration(this, BaseItemDecoration.VERTICAL));

        mAdapter = new SimpleAdapter(this, Arrays.asList(sCheeseStrings));
        View header = LayoutInflater.from(this).inflate(R.layout.item_header, (ViewGroup) mRecyclerView.getParent(),false);
        mAdapter.addHeaderView(header);
        View footer = LayoutInflater.from(this).inflate(R.layout.item_footer, (ViewGroup) mRecyclerView.getParent(),false);
        mAdapter.addFooterView(footer);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setLoadMoreEnabled(true);
        mAdapter.addOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView.ViewHolder vh, int position) {
                Toast.makeText(SimpleActivity.this, "您点击了第" + position + "个item", Toast.LENGTH_SHORT).show();
            }
        });

        mRecyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(RecyclerView rv, MotionEvent e) {
                Toast.makeText(SimpleActivity.this, "拦截touch", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
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
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.simple, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_add) {
            mAdapter.add(mAdapter.getData().size(), "New Item");
            return true;
        }
        if (id == R.id.action_remove) {
            mAdapter.remove(mAdapter.getData().size() - 1);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static final String[] sCheeseStrings = {
            "Activity", "Service", "ContentProvider", "BroadcastReceiver"};
}
