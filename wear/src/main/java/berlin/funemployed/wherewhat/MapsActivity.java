package berlin.funemployed.wherewhat;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import info.metadude.java.library.overpass.ApiModule;
import info.metadude.java.library.overpass.models.Element;
import info.metadude.java.library.overpass.models.OverpassResponse;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MapsActivity extends Activity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener {

    private DismissOverlayView mDismissOverlay;
    private GoogleMap mMap;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        // Set the layout. It only contains a MapFragment and a DismissOverlay.
        setContentView(R.layout.activity_maps);

        // Retrieve the containers for the root of the layout and the map. Margins will need to be
        // set on them to account for the system window insets.
        final FrameLayout topFrameLayout = (FrameLayout) findViewById(R.id.root_container);
        final FrameLayout mapFrameLayout = (FrameLayout) findViewById(R.id.map_container);

        // Set the system view insets on the containers when they become available.
        topFrameLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                // Call through to super implementation and apply insets
                insets = topFrameLayout.onApplyWindowInsets(insets);

                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) mapFrameLayout.getLayoutParams();

                // Add Wearable insets to FrameLayout container holding map as margins
                params.setMargins(
                        insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom());
                mapFrameLayout.setLayoutParams(params);

                return insets;
            }
        });

        // Obtain the DismissOverlayView and display the introductory help text.
        mDismissOverlay = (DismissOverlayView) findViewById(R.id.dismiss_overlay);
        mDismissOverlay.setIntroText(R.string.intro_text);
        //mDismissOverlay.show();

        // Obtain the MapFragment and set the async listener to be notified when the map is ready.
        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void handleMarkerGetFail() {
        mDismissOverlay.setIntroText("no results found");
        mDismissOverlay.show();
    }

    private void addMarkersFromResponse(Response<OverpassResponse> response) {
        final LatLngBounds.Builder latLngBuilder = LatLngBounds.builder();

        final List<Element> elements = response.body().elements;
        for (Element element : elements) {
            final LatLng pos = new LatLng(element.lat, element.lon);
            latLngBuilder.include(pos);
            mMap.addMarker(new MarkerOptions().position(pos).title("title"));

        }
        final LatLngBounds latLngBounds = latLngBuilder.build();

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        final Call<OverpassResponse> overpassResponse = ApiModule.provideOverpassService().getOverpassResponse("[out:json];node(around:600,52.516667,13.383333)[\"amenity\"=\"post_box\"];out;");

        overpassResponse.enqueue(new Callback<OverpassResponse>() {
            @Override
            public void onResponse(Response<OverpassResponse> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    addMarkersFromResponse(response);
                } else {
                    handleMarkerGetFail();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                handleMarkerGetFail();
            }
        });

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        // Display the dismiss overlay with a button to exit this activity.
        mDismissOverlay.show();
    }
}
