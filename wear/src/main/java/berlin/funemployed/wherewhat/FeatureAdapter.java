package berlin.funemployed.wherewhat;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import javax.inject.Inject;

import berlin.funemployed.wherewhat.model.FeatureTypes;

public class FeatureAdapter extends RecyclerView.Adapter<FeatureViewHolder> {

    @Inject
    FeatureTypes featureTypes;

    public FeatureAdapter() {
        App.component().inject(this);
    }

    @Override
    public FeatureViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View inflate = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_feature, parent, false);

        return new FeatureViewHolder(inflate);
    }

    @Override
    public void onBindViewHolder(FeatureViewHolder holder, int position) {
        holder.bind(featureTypes.get().get(position));
    }

    @Override
    public int getItemCount() {
        return featureTypes.get().size();
    }
}
