package diablo.lib.list.multiple;

import android.annotation.TargetApi;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import diablo.lib.list.R;
import java.util.ArrayList;
import java.util.List;
import ru.vang.progressswitcher.ProgressWidget;

/**
 * Created by Diablo on 16/9/22.
 */

public class MultipleItemFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MultipleItemAdapter adapter;
    private ProgressWidget progressWidget;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multiple_list, container, false);
        initView(view);
        return view;
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initView(View base) {
        progressWidget = (ProgressWidget) base.findViewById(R.id.progress_widget);
        progressWidget.setEmptyText("没有数据了!", R.id.progress_widget_empty_txt);
        progressWidget.findViewById(R.id.progress_widget_error_txt)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onRefresh();
                    }
                });
        progressWidget.showContent();
        recyclerView = (RecyclerView) base.findViewById(R.id.list);
        adapter = new MultipleItemAdapter(getActivity());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        MyScrollListener myScrollListener = new MyScrollListener(adapter);
        myScrollListener.setOnLoadMoreInterface(new MyScrollListener.OnLoadDataInterface() {
            @Override
            public void loadMore() {
                onLoadMore();
            }
        });
        recyclerView.addOnScrollListener(myScrollListener);
        swipeRefreshLayout = (SwipeRefreshLayout) base.findViewById(R.id.swipe_refresh_widget);
        swipeRefreshLayout.setColorSchemeColors(Color.BLUE, Color.RED);
        swipeRefreshLayout.setOnRefreshListener(this);
        onRefresh();
    }

    @Override
    public void onRefresh() {
        progressWidget.showProgress();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> datas = new ArrayList<String>();
                for (int i = 0; i < 15; i++) {
                    datas.add(String.valueOf(i));
                }
                adapter.addRefreshData(MultipleItemTypeData.buildMultipleType(MultipleItemAdapter.FIRST_TYPE, datas));
                swipeRefreshLayout.setRefreshing(false);
                progressWidget.showContent();
            }
        }, 1000);
    }

    private void onLoadMore() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<String> datas = new ArrayList<String>();
                for (int i = 0; i < 5; i++) {
                    datas.add(String.valueOf(i));
                }
                if (datas == null){
                    return;
                }
                adapter.hideLoadMoreInfo();
                adapter.addLoadData(MultipleItemTypeData.buildMultipleType(MultipleItemAdapter.SECOND_TYPE, datas));
            }
        }, 1000);
    }
}
