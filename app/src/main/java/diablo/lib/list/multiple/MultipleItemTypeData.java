package diablo.lib.list.multiple;

/**
 * Created by Diablo on 16/9/22.
 */

public class MultipleItemTypeData {

    private RecycleItemTypeData recycleItemTypeData;

    private String data;

    public MultipleItemTypeData(RecycleItemTypeData itemType, String data) {
        this.recycleItemTypeData = itemType;
        this.data = data;
    }

    public RecycleItemTypeData getRecycleItemTypeData() {
        return recycleItemTypeData;
    }

    public void setRecycleItemTypeData(RecycleItemTypeData recycleItemTypeData) {
        this.recycleItemTypeData = recycleItemTypeData;
    }

    public String getData() {
        return data;
    }

    public void setData(String data) {
        this.data = data;
    }
}
