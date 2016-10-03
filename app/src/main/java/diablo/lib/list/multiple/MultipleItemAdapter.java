package diablo.lib.list.multiple;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import diablo.lib.list.R;

/**
 * Created by Diablo on 16/9/22.
 */

public class MultipleItemAdapter extends BaseMultipleAdapter {
    public static final int FIRST_TYPE = 1;
    public static final int SECOND_TYPE = 2;

    public MultipleItemAdapter(Context context) {
        super(context);
    }

    @Override
    public RecyclerView.ViewHolder onCreateMyViewHolder(ViewGroup parent, int viewType) {
        if (FIRST_TYPE == viewType) {
            View view =
                    LayoutInflater.from(context).inflate(R.layout.multiple_item1, parent, false);
            return new ItemOneHolder(view);
        } else if (SECOND_TYPE == viewType) {
            View view =
                    LayoutInflater.from(context).inflate(R.layout.multiple_item2, parent, false);
            return new ItemTwoHolder(view);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (datas.get(position).getItemType() == FIRST_TYPE) {
            ItemOneHolder holder1 = (ItemOneHolder) holder;
            String data = (String) datas.get(position).getData();
            holder1.title.setText(data);
        } else if (datas.get(position).getItemType() == SECOND_TYPE) {
            ItemTwoHolder holder1 = (ItemTwoHolder) holder;
            String data = (String) datas.get(position).getData();
            holder1.title.setText(data);
        }
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
}
