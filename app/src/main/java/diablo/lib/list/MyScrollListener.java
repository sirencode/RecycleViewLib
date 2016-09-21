package diablo.lib.list;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by Diablo on 16/9/21.
 */
public class MyScrollListener extends RecyclerView.OnScrollListener {

    private boolean isSlidingToLast = false;
    private MyBaseAdapter adapter;
    private onLoadDataInterface onLoadMoreInterface;

    public MyScrollListener(MyBaseAdapter adapter) {
        this.adapter = adapter;
    }

    public void setOnLoadMoreInterface(onLoadDataInterface loadMoreData) {
        this.onLoadMoreInterface = loadMoreData;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        //用来标记是否正在向最后一个滑动，既是否向下滑动
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int firstPosition = 0;
        int lastPosition = 0;
        //判断是当前layoutManager是否为LinearLayoutManager
        // 只有LinearLayoutManager才有查找第一个和最后一个可见view位置的方法

        if (layoutManager instanceof LinearLayoutManager) {
            LinearLayoutManager linearManager = (LinearLayoutManager) layoutManager;
            firstPosition = linearManager.findFirstVisibleItemPosition();
            lastPosition = linearManager.findLastVisibleItemPosition();
        } else if (layoutManager instanceof StaggeredGridLayoutManager) {
            StaggeredGridLayoutManager manager =
                    (StaggeredGridLayoutManager) recyclerView.getLayoutManager();
            //获取最后一个完全显示的ItemPosition
            int[] lastVisiblePositions =
                    manager.findLastVisibleItemPositions(new int[manager.getSpanCount()]);
            int[] firstVisiblePositions =
                    manager.findFirstVisibleItemPositions(new int[manager.getSpanCount()]);
            lastPosition = getMaxElem(lastVisiblePositions);
            firstPosition = getMinElem(firstVisiblePositions);
        }

        //if (newState == RecyclerView.SCROLL_STATE_IDLE){
        //    //第一个位置
        //    if (firstPosition == 0 && !isSlidingToLast && adapter.canRefresh()) {
        //        adapter.setCanRefresh(false);
        //        refreshData();
        //    }
        //
        //}

        //获取最后一个可见view的位
        if (lastPosition == layoutManager.getItemCount() - 1 && adapter.canLoad()
                && isSlidingToLast) {
            adapter.setCanLoad(false);
            loadData();
        }
    }

    private void loadData() {
        if (onLoadMoreInterface != null) {
            onLoadMoreInterface.loadMore();
        }
    }

    //private void refreshData() {
    //    if (onLoadMoreInterface != null) {
    //        onLoadMoreInterface.refresh();
    //    }
    //}

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        super.onScrolled(recyclerView, dx, dy);
        //dx用来判断横向滑动方向，dy用来判断纵向滑动方向
        if (dy > 0) {
            //大于0表示，正在向下滚动
            isSlidingToLast = true;
        } else {
            //小于等于0 表示停止或向上滚动
            isSlidingToLast = false;
        }
    }

    private int getMaxElem(int[] arr) {
        int size = arr.length;
        int maxVal = Integer.MIN_VALUE;
        for (int i = 0; i < size; i++) {
            if (arr[i] > maxVal) {
                maxVal = arr[i];
            }
        }
        return maxVal;
    }

    private int getMinElem(int[] arr) {
        int size = arr.length;
        int mainVal = Integer.MAX_VALUE;
        for (int i = 0; i < size; i++) {
            if (arr[i] < mainVal) {
                mainVal = arr[i];
            }
        }
        return mainVal;
    }

    public interface onLoadDataInterface {
        void loadMore();

//        void refresh();
    }
}
