package berlin.funemployed.wherewhat.ui;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Map;

import javax.inject.Inject;

import berlin.funemployed.wherewhat.App;
import berlin.funemployed.wherewhat.R;
import berlin.funemployed.wherewhat.model.UserContext;
import butterknife.Bind;
import butterknife.ButterKnife;
import info.metadude.java.library.overpass.models.Element;

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

        final Element currentSelectedElement = userContext.currentSelectedElement;

        if (currentSelectedElement == null) {
            Log.w(getClass().getSimpleName(), "opened without a selected element");
            finish();
            return;
        }

        final LayoutInflater layoutInflater = LayoutInflater.from(this);


        for (Map.Entry<String, String> stringStringEntry : currentSelectedElement.tags.entrySet()) {

            final View itemView = layoutInflater.inflate(R.layout.item_detail, container, false);

            final TextView keyTextView = (TextView) itemView.findViewById(R.id.key);
            final TextView valueTextView = (TextView) itemView.findViewById(R.id.value);

            keyTextView.setText(stringStringEntry.getKey());
            valueTextView.setText(stringStringEntry.getValue());

            container.addView(itemView);

        }
    }

}
