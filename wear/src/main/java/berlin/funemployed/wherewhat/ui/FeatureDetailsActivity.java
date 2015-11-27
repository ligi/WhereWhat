package berlin.funemployed.wherewhat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import javax.inject.Inject;

import berlin.funemployed.wherewhat.App;
import berlin.funemployed.wherewhat.R;
import berlin.funemployed.wherewhat.model.UserContext;
import butterknife.Bind;
import butterknife.ButterKnife;

public class FeatureDetailsActivity extends Activity {

    @Bind(R.id.container)
    ViewGroup container;

    @Inject
    UserContext userContext;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        App.component().inject(this);

        setContentView(R.layout.activity_details);

        ButterKnife.bind(this);

        for (Map.Entry<String, String> stringStringEntry : userContext.currentSelectedElement.tags.entrySet()) {
            final TextView child = new TextView(this);
            child.setText(stringStringEntry.getKey() + " : " + stringStringEntry.getValue());
            container.addView(child);
        }
    }

}
