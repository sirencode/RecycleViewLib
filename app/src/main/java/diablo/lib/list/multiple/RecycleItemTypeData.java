package diablo.lib.list.multiple;

/**
 * Created by Diablo on 16/9/22.
 */

public class RecycleItemTypeData {

    //recycleview item的类型
    private int itemType;

    //recycleview item当前类型对应的res资源
    private int typeRes;

    public RecycleItemTypeData(int itemType, int typeRes) {
        this.itemType = itemType;
        this.typeRes = typeRes;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public int getTypeRes() {
        return typeRes;
    }

    public void setTypeRes(int typeRes) {
        this.typeRes = typeRes;
    }
}
