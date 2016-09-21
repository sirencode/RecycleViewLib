package diablo.lib.list;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diablo on 16/9/20.
 */
public class SwipeRefreshFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {

    private List<String> datas = new ArrayList<>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MyViewAdapter adapter;
    private TextView load;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.swipe_refresh_lay, container, false);
        initView(view);
        return view;
    }

    private void initDatas() {
        datas.add("New York");
        datas.add("Bei Jing");
        datas.add("Boston");
        datas.add("London");
        datas.add("San Francisco");
        datas.add("Chicago");
        datas.add("Shang Hai");
        datas.add("Tian Jin");
        datas.add("Zheng Zhou");
        datas.add("Hang Zhou");
        datas.add("Guang Zhou");
        datas.add("Fu Gou");
        datas.add("Zhou Kou");
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initView(View base) {
        initDatas();
        load = (TextView) base.findViewById(R.id.loading);
        recyclerView = (RecyclerView) base.findViewById(R.id.list);
        adapter = new MyViewAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        StaggeredGridLayoutManager
                layout = new StaggeredGridLayoutManager(2,StaggeredGridLayoutManager.VERTICAL);
        layout.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_NONE);
        recyclerView.setLayoutManager(layout);
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MyScrollListener myScrollListener = new MyScrollListener(adapter);
        myScrollListener.setOnLoadMoreInterface(new MyScrollListener.onLoadDataInterface() {
            @Override
            public void loadMore() {
                onLoadMore();
            }

        });
        recyclerView.addOnScrollListener(myScrollListener);
        swipeRefreshLayout = (SwipeRefreshLayout) base.findViewById(R.id.swipe_refresh_widget);
        // 设置小圆圈的颜色
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED);
        // 设置刷新监听
        swipeRefreshLayout.setOnRefreshListener(this);

        onRefresh();
    }

    @Override
    public void onRefresh() {
        // 为演示设置一个5秒定时器（还是在主线程）
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 取消掉小圆圈，不然会一直显示
                datas.add(0, "refreshItem");
                adapter.addRefreshData(datas);
                swipeRefreshLayout.setRefreshing(false);
            }
        }, 1000);
    }

    private void onLoadMore() {
        // 为演示设置一个5秒定时器（还是在主线程）
        load.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                load.setVisibility(View.GONE);
                List<String> data = new ArrayList<String>();
                for (int i = 0; i < 4; i++) {
                    data.add("addItem" + i);
                }
                adapter.addLoadData(data);
                adapter.setCanLoad(true);
            }
        }, 1000);
    }
}
