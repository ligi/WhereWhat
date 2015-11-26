package berlin.funemployed.wherewhat;

import java.util.ArrayList;
import java.util.Map;

public class TitleFromTagExtractor {

    private final static ArrayList<String> keys = new ArrayList<String>() {
        {
            add("collection_times");
            add("ref");
            add("amenity");
        }
    };

    public static String getTitleFromTagMap(Map<String,String> map,final String fallback) {

        for (String key : keys) {
            if (map.containsKey(key)) {
                return map.get(key);
            }
        }

        return fallback;
    }

}
