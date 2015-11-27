package berlin.funemployed.wherewhat.model;

import android.support.annotation.DrawableRes;
import android.support.annotation.StringRes;

public class FeatureType {
    public final String osm_tag;

    @DrawableRes
    public final int drawableRes;

    @StringRes
    public final int descriptionRes;

    public FeatureType(String osm_tag, int drawableRes, int descriptionRes) {
        this.osm_tag = osm_tag;
        this.drawableRes = drawableRes;
        this.descriptionRes = descriptionRes;
    }
}
