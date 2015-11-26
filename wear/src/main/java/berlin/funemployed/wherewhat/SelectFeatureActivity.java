package berlin.funemployed.wherewhat;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import org.ligi.axt.AXT;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SelectFeatureActivity extends Activity {

    @OnClick(R.id.button_post_box)
    void onPostBoxClick() {
        AXT.at(this).startCommonIntent().activityFromClass(MapsActivity.class);
    }

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        setContentView(R.layout.activity_select);

        ButterKnife.bind(this);
    }

}
