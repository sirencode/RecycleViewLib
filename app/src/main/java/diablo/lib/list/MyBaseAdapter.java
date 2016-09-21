package diablo.lib.list;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

/**
 * Created by Diablo on 16/9/21.
 */
public class MyBaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private boolean canLoad = true;

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    protected boolean canLoad(){
        return canLoad;
    }

    protected void setCanLoad(boolean canLoad){
        this.canLoad = canLoad;
    }
}
