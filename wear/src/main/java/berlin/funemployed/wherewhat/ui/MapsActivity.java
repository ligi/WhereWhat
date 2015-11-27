package berlin.funemployed.wherewhat.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.wearable.view.DismissOverlayView;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.Interceptor;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import berlin.funemployed.wherewhat.App;
import berlin.funemployed.wherewhat.BuildConfig;
import berlin.funemployed.wherewhat.R;
import berlin.funemployed.wherewhat.model.UserContext;
import berlin.funemployed.wherewhat.util.TitleFromTagExtractor;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.metadude.java.library.overpass.ApiModule;
import info.metadude.java.library.overpass.models.Element;
import info.metadude.java.library.overpass.models.OverpassResponse;
import info.metadude.java.library.overpass.utils.DataQuery;
import retrofit.Call;
import retrofit.Callback;
import retrofit.Response;
import retrofit.Retrofit;

public class MapsActivity extends Activity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener {

    @Bind(R.id.root_container)
    FrameLayout topFrameLayout;

    @Bind(R.id.map_container)
    FrameLayout mapFrameLayout;

    @OnClick(R.id.navigate_to_button)
    void navigateTo() {
        try {
            final Element currentSelectedElement = userContext.currentSelectedElement;
            String uri = "google.navigation:q=" + String.valueOf(currentSelectedElement.lat) + "," + String.valueOf(currentSelectedElement.lon);
            Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mapsIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this,"You need to install navigation app",Toast.LENGTH_LONG).show();
        }
    }

    @Bind(R.id.navigate_to_button)
    View navigateToButton;

    @Inject
    UserContext userContext;

    private Map<Marker,Element> markerToElementMap = new HashMap<>();

    private GoogleMap mMap;

    public void onCreate(Bundle savedState) {
        super.onCreate(savedState);

        App.component().inject(this);

        setContentView(R.layout.activity_maps);

        ButterKnife.bind(this);

        topFrameLayout.setOnApplyWindowInsetsListener(new View.OnApplyWindowInsetsListener() {
            @Override
            public WindowInsets onApplyWindowInsets(View v, WindowInsets insets) {
                insets = topFrameLayout.onApplyWindowInsets(insets);

                FrameLayout.LayoutParams params =
                        (FrameLayout.LayoutParams) mapFrameLayout.getLayoutParams();

                params.setMargins(
                        insets.getSystemWindowInsetLeft(),
                        insets.getSystemWindowInsetTop(),
                        insets.getSystemWindowInsetRight(),
                        insets.getSystemWindowInsetBottom());
                mapFrameLayout.setLayoutParams(params);

                return insets;
            }
        });

        MapFragment mapFragment = (MapFragment) getFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void handleMarkerGetFail() {
        Toast.makeText(this,"no results found",Toast.LENGTH_LONG).show();
    }

    private void addMarkersFromResponse(Response<OverpassResponse> response) {
        final LatLngBounds.Builder latLngBuilder = LatLngBounds.builder();

        final List<Element> elements = response.body().elements;

        if (elements.isEmpty()) {
            Toast.makeText(this,"nothing found",Toast.LENGTH_LONG).show();
            return;
        }

        markerToElementMap.clear();

        for (Element element : elements) {

            final LatLng pos = new LatLng(element.lat, element.lon);
            latLngBuilder.include(pos);
            final String fallback = TitleFromTagExtractor.getTitleFromTagMap(element.tags, "fallback");
            final Marker marker = mMap.addMarker(new MarkerOptions().position(pos).title(fallback));

            markerToElementMap.put(marker,element);
        }

        final LatLngBounds latLngBounds = latLngBuilder.build();

        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(latLngBounds, 100));

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                userContext.currentSelectedElement = markerToElementMap.get(marker);
                navigateToButton.setVisibility(View.VISIBLE);
                return false;
            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMapLongClickListener(this);

        final String tagValue = userContext.currentFeatureType.osm_tag;
        Map<String, String> tags =  new HashMap<String, String>() {
            {
                put("amenity", tagValue);
            }
        };
        DataQuery dataQuery = new DataQuery(3600, 52.516667, 13.383333, tags, true, 13);
        HttpLoggingInterceptor httpLoggingInterceptor = new HttpLoggingInterceptor();
        httpLoggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BASIC);
        List<Interceptor> interceptors = new ArrayList<>(1);
        if (BuildConfig.DEBUG) {
            interceptors.add(httpLoggingInterceptor);
        }
        final Call<OverpassResponse> overpassResponse = ApiModule
                .provideOverpassService(interceptors)
                .getOverpassResponse(dataQuery.getFormattedDataQuery());
        overpassResponse.enqueue(new Callback<OverpassResponse>() {
            @Override
            public void onResponse(Response<OverpassResponse> response, Retrofit retrofit) {
                if (response.isSuccess()) {
                    addMarkersFromResponse(response);
                } else {
                    Log.e(getClass().getName(), response.errorBody().toString());
                    handleMarkerGetFail();
                }
            }

            @Override
            public void onFailure(Throwable t) {
                Log.e(getClass().getName(), t.getMessage());
                handleMarkerGetFail();
            }
        });

    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        finish();
    }
}
