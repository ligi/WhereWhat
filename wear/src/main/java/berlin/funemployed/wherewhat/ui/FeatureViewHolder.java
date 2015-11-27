package berlin.funemployed.wherewhat.ui;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.ligi.axt.AXT;

import javax.inject.Inject;

import berlin.funemployed.wherewhat.App;
import berlin.funemployed.wherewhat.R;
import berlin.funemployed.wherewhat.model.FeatureType;
import berlin.funemployed.wherewhat.model.UserContext;
import butterknife.Bind;
import butterknife.ButterKnife;

public class FeatureViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.icon)
    ImageView icon;

    @Bind(R.id.text)
    TextView text;

    @Inject
    UserContext userContext;

    public FeatureViewHolder(final View itemView) {
        super(itemView);
        ButterKnife.bind(this, itemView);
        App.component().inject(this);
    }

    public void bind(final FeatureType featureType) {

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                userContext.currentFeatureType = featureType;
                AXT.at(itemView.getContext()).startCommonIntent().activityFromClass(MapsActivity.class);
            }
        });

        icon.setImageResource(featureType.drawableRes);
        text.setText(featureType.descriptionRes);

    }
}
