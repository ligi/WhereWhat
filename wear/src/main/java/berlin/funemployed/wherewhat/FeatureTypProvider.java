package berlin.funemployed.wherewhat;

import java.util.ArrayList;
import java.util.List;

public class FeatureTypProvider {

    public List<String> getFeatureTypes() {
        return new ArrayList<String>() {
            {
                add("postbox");
                add("toilet");
                add("recycling");
            }
        };
    }
}
