package berlin.funemployed.wherewhat.dependencygraph;

import javax.inject.Singleton;

import berlin.funemployed.wherewhat.FeatureAdapter;
import dagger.Component;


@Singleton
@Component(modules = {MainModule.class})
public interface MainComponent {

    void inject(FeatureAdapter featureAdapter);
}
