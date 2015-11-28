package berlin.funemployed.wherewhat;

import android.app.Application;

import berlin.funemployed.wherewhat.dependencygraph.DaggerMainComponent;
import berlin.funemployed.wherewhat.dependencygraph.MainComponent;

public class App extends Application {

    private static MainComponent component;

    public static MainComponent component() {
        if (component == null) {
            component = DaggerMainComponent.create();
        }
        return component;
    }

}
