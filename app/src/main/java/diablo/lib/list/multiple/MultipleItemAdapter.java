package diablo.lib.list.multiple;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import diablo.lib.list.R;
import diablo.lib.list.swip.MyBaseAdapter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diablo on 16/9/22.
 */

public class MultipleItemAdapter extends MyBaseAdapter {

    private Context context;
    private List<MultipleItemTypeData> datas = new ArrayList<MultipleItemTypeData>();

    public MultipleItemAdapter(Context context) {
        this.context = context;
    }

    public List<MultipleItemTypeData> getListData() {
        return datas;
    }

    public void addRefreshData(List<MultipleItemTypeData> dataList) {
        this.datas.addAll(0, dataList);
        notifyDataSetChanged();
    }

    public void addLoadData(List<MultipleItemTypeData> dataList) {
        this.datas.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (SampleItemEum.FirstItem.getValue() == viewType) {
            View view =
                    LayoutInflater.from(context).inflate(R.layout.multiple_item1, parent, false);
            return new ItemOneHolder(view);
        } else if (SampleItemEum.SecondItem.getValue() == viewType) {
            View view =
                    LayoutInflater.from(context).inflate(R.layout.multiple_item2, parent, false);
            return new ItemTwoHolder(view);
        } else if (SampleItemEum.ThirdItem.getValue() == viewType) {
            View view =
                    LayoutInflater.from(context).inflate(R.layout.multiple_item3, parent, false);
            return new ItemThreeHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (datas.get(position).getRecycleItemTypeData().getItemType()
                == SampleItemEum.FirstItem.getValue()) {
            ItemOneHolder holder1 = (ItemOneHolder) holder;
            holder1.title.setText(datas.get(position).getData());
        } else if (datas.get(position).getRecycleItemTypeData().getItemType()
                == SampleItemEum.SecondItem.getValue()) {
            ItemTwoHolder holder1 = (ItemTwoHolder) holder;
            holder1.title.setText(datas.get(position).getData());
        } else if (datas.get(position).getRecycleItemTypeData().getItemType()
                == SampleItemEum.ThirdItem.getValue()) {
            ItemThreeHolder holder1 = (ItemThreeHolder) holder;
            holder1.title.setText(datas.get(position).getData());
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return datas.get(position).getRecycleItemTypeData().getItemType();
    }

    private static class ItemOneHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public ItemOneHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item1_title);
        }
    }

    private static class ItemTwoHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public ItemTwoHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item2_title);
        }
    }

    private static class ItemThreeHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public ItemThreeHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.item3_title);
        }
    }
}
