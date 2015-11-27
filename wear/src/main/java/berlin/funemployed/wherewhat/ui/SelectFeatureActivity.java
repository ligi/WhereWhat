package berlin.funemployed.wherewhat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import berlin.funemployed.wherewhat.R;
import butterknife.Bind;
import butterknife.ButterKnife;

public class SelectFeatureActivity extends Activity {

    @Bind(R.id.recycler)
    RecyclerView recyclerView;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.activity_select);

        ButterKnife.bind(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(new FeatureAdapter());
    }

}
