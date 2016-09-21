package diablo.lib.list;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diablo on 16/9/21.
 */
public class MyViewAdapter extends MyBaseAdapter {
    private Context context;
    private List<String> datas = new ArrayList<String>();

    public MyViewAdapter(Context context) {
        this.context = context;
    }


    public List<String> getListData() {
        return datas;
    }

    public void addRefreshData(List<String> dataList) {
        this.datas.addAll(0, dataList);
        notifyDataSetChanged();
    }

    public void addLoadData(List<String> dataList) {
        this.datas.addAll(dataList);
        notifyDataSetChanged();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (0 == viewType) {
            View view =
                    LayoutInflater.from(context).inflate(R.layout.adapter_item, parent, false);
            view.setBackgroundColor(Color.parseColor("#aabbcc"));
            return new MyViewHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (0 == getItemViewType(position)) {
            MyViewHolder holder1 = (MyViewHolder) holder;
            holder1.title.setText(datas.get(position));
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    @Override
    public int getItemViewType(int position) {
        return 0;
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView title;

        public MyViewHolder(View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.tv);
        }
    }
}


