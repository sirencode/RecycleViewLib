package diablo.lib.list;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * @author shenyonghe
 */
public class RecyclerViewActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_container);
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.fragment_container, new RecycleViewDemoFragment())
                .commit();
    }
}
