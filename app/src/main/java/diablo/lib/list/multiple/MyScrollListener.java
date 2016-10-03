package diablo.lib.list.multiple;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;

/**
 * Created by Diablo on 16/9/21.
 */
public class MyScrollListener extends RecyclerView.OnScrollListener {

    private boolean isSlidingToLast = false;
    private BaseMultipleAdapter adapter;
    private OnLoadDataInterface onLoadMoreInterface;

    public MyScrollListener(BaseMultipleAdapter adapter) {
        this.adapter = adapter;
    }

    public void setOnLoadMoreInterface(OnLoadDataInterface loadMoreData) {
        this.onLoadMoreInterface = loadMoreData;
    }

    @Override
    public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
        super.onScrollStateChanged(recyclerView, newState);
        //用来标记是否正在向最后一个滑动，既是否向下滑动
        RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
        int firstPosition = 0;
        int lastPosition = 0;
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

        if (lastPosition == layoutManager.getItemCount() - 1 && adapter.canLoad()
                && isSlidingToLast) {
            adapter.setCanLoad(false);
            loadData();
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    private void loadData() {
        if (onLoadMoreInterface != null) {
            adapter.showLoadMoreInfo();
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

    /**
     * 加载事件回调
     */
    public interface OnLoadDataInterface {
        /**
         * 加载更多
         */
        void loadMore();
        //        void refresh();
    }
}
