package diablo.lib.list.multiple;

/**
 * Created by Diablo on 16/9/22.
 */

public enum SampleItemEum {

    FirstItem(1),
    SecondItem(2),
    ThirdItem(3),
    LoadMoreItem(4),
    LoadMoreNoData(5);

    private int value = 0;

    private SampleItemEum(int value){
        this.value = value;
    }

    public int getValue(){
        return this.value;
    }
}
