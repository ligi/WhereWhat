package berlin.funemployed.wherewhat.dependencygraph;

import javax.inject.Singleton;

import berlin.funemployed.wherewhat.model.FeatureTypes;
import dagger.Module;
import dagger.Provides;

@Module
public class MainModule {

    @Provides
    @Singleton
    public FeatureTypes provideFeatures() {
        return new FeatureTypes();
    }
}
