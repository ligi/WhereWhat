package berlin.funemployed.wherewhat.common;

/**
 * Shared constants
 */
public interface Constants {

    // Radius of POIs to be requested

    int LOCATION_RADIUS = 3600;

    // Maximum # of POIs to show at once

    int MAX_RESPONSE_COUNT = 15;

    // Wear paths

    String OVERPASS_RESPONSE_SUCCESS_PATH = "/success";

    String OVERPASS_RESPONSE_FAILURE_PATH = "/failure";

    String OVERPASS_REQUEST_FEATURES_PATH = "/features";

}
