package berlin.funemployed.wherewhat.model;

import java.util.ArrayList;
import java.util.List;

import berlin.funemployed.wherewhat.R;

public class FeatureTypes {

    public List<FeatureType> get() {
        return new ArrayList<FeatureType>() {
            {

                add(new FeatureType("WIFI",R.drawable.ic_action_wifi,R.string.wifi));

                add(new FeatureType("library",R.drawable.ic_action_book,R.string.library));
                add(new FeatureType("coffee",R.drawable.ic_action_book,R.string.coffee));

                add(new FeatureType("post", R.drawable.ic_action_mail,R.string.mailbox));

                add(new FeatureType("bike parking",R.drawable.ic_action_bike,R.string.bikeparking));

                add(new FeatureType("bus",R.drawable.ic_action_bus,R.string.bus));
                add(new FeatureType("train",R.drawable.ic_action_train,R.string.train));

                add(new FeatureType("trash",R.drawable.ic_action_trash,R.string.trash));
            }
        };
    }
}
