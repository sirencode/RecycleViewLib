package diablo.lib.list.multiple;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Diablo on 16/9/22.
 */

public class MultipleItemTypeData {

    private int itemType;

    private Object data;

    public MultipleItemTypeData(int itemType, Object data) {
        this.itemType = itemType;
        this.data = data;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public static List<MultipleItemTypeData> buildMultipleType(int type, List<?> objects) {
        List<MultipleItemTypeData> datas = new ArrayList<>();
        MultipleItemTypeData multipleItemTypeData;
        if (objects == null || objects.size() == 0) {
            return datas;
        }
        for (Object object : objects) {
            multipleItemTypeData = new MultipleItemTypeData(type, object);
            datas.add(multipleItemTypeData);
        }

        return datas;
    }
}
