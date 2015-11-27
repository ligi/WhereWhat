package berlin.funemployed.wherewhat;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import berlin.funemployed.wherewhat.model.FeatureType;
import butterknife.Bind;
import butterknife.ButterKnife;

class FeatureViewHolder extends RecyclerView.ViewHolder {

    @Bind(R.id.icon)
    ImageView icon;

    @Bind(R.id.text)
    TextView text;

    public FeatureViewHolder(View itemView) {
        super(itemView);
        ButterKnife.bind(this,itemView);
    }

    public void bind(FeatureType featureType) {
        icon.setImageResource(featureType.drawableRes);
        text.setText(featureType.descriptionRes);

    }
}
