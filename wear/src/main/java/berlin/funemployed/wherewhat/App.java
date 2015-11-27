package berlin.funemployed.wherewhat;

import android.app.Application;

import berlin.funemployed.wherewhat.dependencygraph.DaggerMainComponent;
import berlin.funemployed.wherewhat.dependencygraph.MainComponent;

public class App extends Application{

    private static MainComponent component;

    @Override
    public void onCreate() {
        super.onCreate();
        component =  DaggerMainComponent.create();
    }

    public static MainComponent component() {
        return component;
    }

}
