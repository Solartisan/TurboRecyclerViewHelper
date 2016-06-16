package cc.solart.turbo.simple;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.ViewGroup;

import cc.solart.turbo.TurboRecyclerView;
import cc.solart.turbo.simple.adapter.SimpleAdapter;

public class EmptyActivity extends AppCompatActivity {

    TurboRecyclerView mRecyclerView;
    SimpleAdapter mAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty);

        mRecyclerView = (TurboRecyclerView) findViewById(R.id.rv_empty);

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        mAdapter = new SimpleAdapter(this);
        // you must call inflate(@LayoutRes int resource, @Nullable ViewGroup root, boolean attachToRoot)
        mAdapter.setEmptyView(getLayoutInflater().inflate(R.layout.empty, (ViewGroup) mRecyclerView.getParent(),false));
        mRecyclerView.setAdapter(mAdapter);
    }
}
