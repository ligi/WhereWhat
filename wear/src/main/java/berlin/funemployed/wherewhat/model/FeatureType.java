package berlin.funemployed.wherewhat.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

import java.util.HashMap;
import java.util.Map;

public class FeatureType {

    public final Map<String, String> osmTags;

    @DrawableRes
    public final int drawableRes;

    @StringRes
    public final int descriptionRes;

    public FeatureType(Map<String, String> osmTags, int drawableRes, int descriptionRes) {
        this.osmTags = osmTags;
        this.drawableRes = drawableRes;
        this.descriptionRes = descriptionRes;
    }

    public FeatureType(final String amenityTagValue, int drawableRes, int descriptionRes) {
        this(new HashMap<String, String>() {{
            put("amenity", amenityTagValue);
        }}, drawableRes, descriptionRes);
    }

}
