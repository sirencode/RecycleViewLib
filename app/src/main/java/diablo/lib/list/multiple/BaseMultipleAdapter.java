package diablo.lib.list.multiple;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import diablo.lib.list.R;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Diablo on 16/10/1.
 */

public abstract class BaseMultipleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    //加载更多的itemtype
    private static final int LOAD_MORE_TYPE = 1001;
    //没有更多数据的itemtype
    private static final int LOAD_NODATA_TYPE = 1002;
    private boolean canLoad = true;

    public Context context;
    public List<MultipleItemTypeData> datas =
            Collections.synchronizedList(new ArrayList<MultipleItemTypeData>());

    public BaseMultipleAdapter(Context context) {
        this.context = context;
    }

    /**
     * 添加刷新数据
     */
    public void addRefreshData(List<MultipleItemTypeData> dataList) {
        this.datas.clear();
        this.datas.addAll(0, dataList);
        notifyDataSetChanged();
        setCanLoad(true);
    }

    /**
     * 添加加载数据
     */
    public void addLoadData(List<MultipleItemTypeData> dataList) {
        if (dataList != null) {
            if (dataList.size() == 0) {
                showLoadMoreNoDataInfo();
                return;
            }
            this.datas.addAll(dataList);
            notifyDataSetChanged();
            setCanLoad(true);
        }
    }

    public List<MultipleItemTypeData> getCurrentDatas(){
        return datas;
    }

    /**
     * 判断是否可以加载更多
     *
     * @return 是否可加载
     */
    public boolean canLoad() {
        return canLoad;
    }

    /**
     * 设置是否可以加载
     */
    public void setCanLoad(boolean canLoad) {
        this.canLoad = canLoad;
    }

    /**
     * 显示加载更多提示
     */
    public void showLoadMoreInfo() {
        datas.add(new MultipleItemTypeData(LOAD_MORE_TYPE, null));
        notifyDataSetChanged();
    }

    /**
     * 隐藏加载更多提示
     */
    public void hideLoadMoreInfo() {
        if (datas.size() > 0 && datas.get(getItemCount()-1).getItemType() == LOAD_MORE_TYPE) {
            datas.remove(getItemCount() - 1);
            notifyDataSetChanged();
        }
    }

    /**
     * 显示没有更多数据
     */
    public void showLoadMoreNoDataInfo() {
        datas.add(new MultipleItemTypeData(LOAD_NODATA_TYPE, null));
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return datas == null ? 0 : datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).getItemType();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (LOAD_MORE_TYPE == viewType) {
            View view = LayoutInflater.from(context).inflate(R.layout.loadmoe_item, parent, false);
            return new ItemLoadMoreHolder(view);
        } else if (LOAD_NODATA_TYPE == viewType) {
            View view = LayoutInflater.from(context)
                    .inflate(R.layout.loadmore_nodata_item, parent, false);
            return new ItemLoadMoreHolder(view);
        }
        return onCreateMyViewHolder(parent, viewType);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    /**
     * 绑定自己的Item数据
     *
     * @return ViewHolder
     */
    public abstract RecyclerView.ViewHolder onCreateMyViewHolder(ViewGroup parent, int viewType);

    private static class ItemLoadMoreHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public ItemLoadMoreHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.loadmore_item_title);
        }
    }
}
