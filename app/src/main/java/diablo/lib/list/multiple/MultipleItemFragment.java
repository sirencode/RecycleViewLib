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
import diablo.lib.list.swip.MyScrollListener;
import java.util.ArrayList;
import java.util.List;
import ru.vang.progressswitcher.ProgressWidget;

/**
 * Created by Diablo on 16/9/22.
 */

public class MultipleItemFragment extends Fragment implements SwipeRefreshLayout.OnRefreshListener {
    private List<MultipleItemTypeData> datas = new ArrayList<MultipleItemTypeData>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private MultipleItemAdapter adapter;
    private ProgressWidget progressWidget;
    private RecycleItemTypeData itemTypeData1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.multiple_list, container, false);
        initView(view);
        return view;
    }

    private void initDatas() {
        datas.clear();
        itemTypeData1 = new RecycleItemTypeData(SampleItemEum.FirstItem.getValue(),
                R.layout.multiple_item1);
        RecycleItemTypeData itemTypeData2 =
                new RecycleItemTypeData(SampleItemEum.SecondItem.getValue(),
                        R.layout.multiple_item2);
        RecycleItemTypeData itemTypeData3 =
                new RecycleItemTypeData(SampleItemEum.ThirdItem.getValue(),
                        R.layout.multiple_item3);

        datas.add(new MultipleItemTypeData(itemTypeData1, "New York"));
        datas.add(new MultipleItemTypeData(itemTypeData2, "San Francisco"));
        datas.add(new MultipleItemTypeData(itemTypeData1, "Bei Jing"));
        datas.add(new MultipleItemTypeData(itemTypeData1, "Boston"));
        datas.add(new MultipleItemTypeData(itemTypeData1, "London"));

        datas.add(new MultipleItemTypeData(itemTypeData2, "Chicago"));
        datas.add(new MultipleItemTypeData(itemTypeData2, "Shang Hai"));
        datas.add(new MultipleItemTypeData(itemTypeData3, "Bei Jing"));
        datas.add(new MultipleItemTypeData(itemTypeData2, "Tian Jin"));

        datas.add(new MultipleItemTypeData(itemTypeData3, "New York"));
        datas.add(new MultipleItemTypeData(itemTypeData2, "San Francisco"));
        datas.add(new MultipleItemTypeData(itemTypeData3, "Boston"));
        datas.add(new MultipleItemTypeData(itemTypeData3, "London"));
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void initView(View base) {
        initDatas();
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
        initDatas();
        progressWidget.showProgress();
        // 为演示设置一个5秒定时器（还是在主线程）
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                // 取消掉小圆圈，不然会一直显示
                MultipleItemTypeData data = new MultipleItemTypeData(itemTypeData1, "Tian Jin");
                datas.add(0, data);
                adapter.addRefreshData(datas);
                swipeRefreshLayout.setRefreshing(false);
                progressWidget.showContent();
            }
        }, 1000);
    }

    private void onLoadMore() {
        // 为演示设置一个5秒定时器（还是在主线程）
        adapter.showLoadMoreInfo();
        recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                List<MultipleItemTypeData> data = new ArrayList<MultipleItemTypeData>();
                //for (int i = 0; i < 4; i++) {
                //    MultipleItemTypeData tem = new MultipleItemTypeData(itemTypeData1, "item" + i);
                //    data.add(tem);
                //}
                adapter.hideLoadMoreInfo();
                if (data != null || data.size() == 0) {
                    adapter.showLoadMoreNoDataInfo();
                }else {
                    adapter.addLoadData(data);
                    adapter.setCanLoad(true);
                }
            }
        }, 1000);
    }
}
