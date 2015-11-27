package berlin.funemployed.wherewhat.dependencygraph;

import javax.inject.Singleton;

import berlin.funemployed.wherewhat.ui.FeatureAdapter;
import berlin.funemployed.wherewhat.ui.FeatureViewHolder;
import berlin.funemployed.wherewhat.ui.MapsActivity;
import berlin.funemployed.wherewhat.ui.FeatureDetailsActivity;
import dagger.Component;


@Singleton
@Component(modules = {MainModule.class})
public interface MainComponent {

    void inject(FeatureAdapter featureAdapter);

    void inject(FeatureViewHolder featureViewHolder);

    void inject(MapsActivity mapsActivity);

    void inject(FeatureDetailsActivity featureDetailsActivity);
}
