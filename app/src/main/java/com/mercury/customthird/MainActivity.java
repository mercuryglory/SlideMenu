package com.mercury.customthird;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.mercury.customthird.view.RefreshListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private RefreshListView mRefreshListView;
    private List<String> mDatas;
    private MyAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initEvent();
        initData();
    }

    private void initEvent() {
        mRefreshListView.setOnFreshListener(new RefreshListView.OnFreshListener() {
            @Override
            public void onDownPull() {
                //去获取数据
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.add(0, "这是下拉刷新的新数据");
                        mAdapter.notifyDataSetChanged();
                        mRefreshListView.onFinish();
                    }
                }, 2000);
            }

            @Override
            public void onLoadMore() {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mDatas.add("这是加载更多的新数据1");
                        mDatas.add("这是加载更多的新数据2");
                        mDatas.add("这是加载更多的新数据3");
                        mAdapter.notifyDataSetChanged();
                        mRefreshListView.onFinish();
                    }
                }, 2000);
            }
        });
    }

    private void initData() {
        mDatas = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            mDatas.add("这是listview的数据" + i);

        }
        mAdapter = new MyAdapter();
        mRefreshListView.setAdapter(mAdapter);

    }

    private void initView() {
        mRefreshListView = (RefreshListView) findViewById(R.id.refreshlistview);
    }

    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return mDatas.size();
        }

        @Override
        public Object getItem(int position) {
            return mDatas.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            TextView textView = new TextView(MainActivity.this);
            textView.setText(mDatas.get(position));
            textView.setTextColor(Color.BLACK);
            textView.setPadding(8, 8, 8, 8);
            return textView;
        }
    }
}
