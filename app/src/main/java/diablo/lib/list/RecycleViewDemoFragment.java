package diablo.lib.list;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.list.lib.diablo.refreshloadlayout.RefreshLoadMoreLayout;
import java.util.ArrayList;
import java.util.List;
import ru.vang.progressswitcher.ProgressWidget;

/**
 * Created by shenyonghe on 16/8/23.
 */
public class RecycleViewDemoFragment extends Fragment implements RefreshLoadMoreLayout.CallBack {

    private static final int LINE_COUNT = 3;
    private static final int SPACE = 10;
    private static final int DATA_COUNT = 50;
    private static final int ONE_PAGE_NUM = 10;
    private static final int REFRESH_DELAY = 200;
    private static final int LOAD_DELAY = 1000;


    private RefreshLoadMoreLayout refreshLoadMoreLayout;
    private ProgressWidget progressWidget;
    private MyViewAdapter adapter;
    private Handler handler = new Handler();

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_test_recyclerview, container, false);
        refreshLoadMoreLayout = (RefreshLoadMoreLayout) view.findViewById(R.id.rlm);
        progressWidget = (ProgressWidget) view.findViewById(R.id.progress_widget);
        progressWidget.setEmptyText("没有数据了!", R.id.progress_widget_empty_txt);
        progressWidget.findViewById(R.id.progress_widget_error_txt)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onRefresh();
                    }
                });
        progressWidget.showContent();
        /**
         * canRefresh 是否下拉刷新
         * canLoadMore 是否上拉加载更多
         * autoLoadMore 自动加载更多（默认不自动加载更多）
         * showLastRefreshTime 是否显示上次刷新时间（默认不显示）
         * multiTask 下拉刷新上拉加载更多可同时进行（默认下拉刷新和上拉加载更多不能同时进行）
         */
        refreshLoadMoreLayout.init(new RefreshLoadMoreLayout.Config(this).canRefresh(true)
                .canLoadMore(true)
                .autoLoadMore()
                .showLastRefreshTime(RecyclerViewActivity.class, "yyyy-MM-dd")
                .multiTask());
        final RecyclerView recyclerView = (RecyclerView) refreshLoadMoreLayout.getContentView();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(getActivity(), LINE_COUNT);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return 0 == recyclerView.getAdapter().getItemViewType(position) ? 1 : LINE_COUNT;
            }
        });
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter = new MyViewAdapter(getActivity()));
        recyclerView.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent,
                    RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.left = SPACE;
                outRect.top = SPACE;
                outRect.right = SPACE;
                outRect.bottom = SPACE;
            }
        });

        return view;
    }

    @Override
    public void onRefresh() {
        System.out.println("onRefresh");
        progressWidget.showProgress();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int num = adapter.getFirstNumber();
                List<Integer> dataList = new ArrayList<Integer>();
                for (int i = ONE_PAGE_NUM; i > 0; i--) {
                    int tmpNum = num;
                    tmpNum += i;
                    dataList.add(tmpNum);
                }
                adapter.addRefreshData(dataList);
                refreshLoadMoreLayout.stopRefresh();
                System.out.println("onRefresh finish");
                progressWidget.showContent();
            }
        }, REFRESH_DELAY);
    }

    @Override
    public void onLoadMore() {
        System.out.println("onLoadMore");
        progressWidget.showProgress();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                int num = adapter.getLastNumber();
                List<Integer> dataList = new ArrayList<Integer>();
                for (int i = 0; i < ONE_PAGE_NUM; i++) {
                    --num;
                    dataList.add(num);
                }
                adapter.addLoadData(dataList);
                refreshLoadMoreLayout.stopLoadMoreNoData(
                        adapter.getItemCount() >= DATA_COUNT); //依然可以上拉，显示没有更多数据
                progressWidget.showContent();
            }
        }, LOAD_DELAY);
    }

    private class MyViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
        private Context context;

        private List<Integer> mdatalist = new ArrayList<Integer>();

        public MyViewAdapter(Context context) {
            this.context = context;
        }

        public void addRefreshData(List<Integer> dataList) {
            this.mdatalist.addAll(0, dataList);
            notifyDataSetChanged();
        }

        public void addLoadData(List<Integer> dataList) {
            this.mdatalist.addAll(dataList);
            notifyDataSetChanged();
        }

        public int getFirstNumber() {
            return 0 == mdatalist.size() ? 0 : mdatalist.get(0);
        }

        public int getLastNumber() {
            return 0 == mdatalist.size() ? 0 : mdatalist.get(mdatalist.size() - 1);
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (0 == viewType) {
                View view = LayoutInflater.from(context)
                        .inflate(R.layout.adapter_item, parent, false);
                view.setBackgroundColor(Color.parseColor("#aabbcc"));
                return new MyViewHolder(view);
            }
            return null;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
            if (0 == getItemViewType(position)) {
                MyViewHolder holder1 = (MyViewHolder) holder;
                holder1.mtxt.setText(mdatalist.get(position) + "");
            }
        }

        @Override
        public int getItemCount() {
            return mdatalist.size();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView mtxt;

        public MyViewHolder(View itemView) {
            super(itemView);
            mtxt = (TextView) itemView.findViewById(R.id.tv);
        }
    }
}
