package berlin.funemployed.wherewhat.ui;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowInsets;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResolvingResultCallbacks;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.wearable.CapabilityApi;
import com.google.android.gms.wearable.CapabilityInfo;
import com.google.android.gms.wearable.MessageApi;
import com.google.android.gms.wearable.MessageEvent;
import com.google.android.gms.wearable.Node;
import com.google.android.gms.wearable.Wearable;
import com.squareup.moshi.JsonAdapter;
import com.squareup.moshi.Moshi;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.inject.Inject;

import berlin.funemployed.wherewhat.App;
import berlin.funemployed.wherewhat.R;
import berlin.funemployed.wherewhat.common.Constants;
import berlin.funemployed.wherewhat.model.UserContext;
import berlin.funemployed.wherewhat.util.TitleFromTagExtractor;
import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import info.metadude.java.library.overpass.models.Element;
import info.metadude.java.library.overpass.utils.DataQuery;

public class MapsActivity extends Activity implements OnMapReadyCallback,
        GoogleMap.OnMapLongClickListener, MessageApi.MessageListener, LocationListener {

    @Override
    protected void onResume() {
        super.onResume();
        markerToElementMap.clear();
        userContext.currentSelectedElement = null;
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mGoogleApiClient.disconnect();
    }

    public final static String TAG = "MapsActivity";

    @Bind(R.id.root_container)
    FrameLayout topFrameLayout;

    @Bind(R.id.map_container)
    FrameLayout mapFrameLayout;

    private GoogleApiClient mGoogleApiClient;

    @OnClick(R.id.navigate_to_button)
    void navigateTo() {
        try {
            final Element currentSelectedElement = userContext.currentSelectedElement;
            String uri = "google.navigation:q=" + String.valueOf(currentSelectedElement.lat) + "," + String.valueOf(currentSelectedElement.lon);
            Intent mapsIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            mapsIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(mapsIntent);
        } catch (ActivityNotFoundException e) {
            Toast.makeText(this, "You need to install navigation app", Toast.LENGTH_LONG).show();
        }
    }

    @Bind(R.id.navigate_to_button)
    View navigateToButton;

    @Inject
    UserContext userContext;

    private Map<Marker, Element> markerToElementMap = new HashMap<>();

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

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle connectionHint) {
                        requestLocation();
                    }

                    @Override
                    public void onConnectionSuspended(int cause) {
                        Log.d(TAG, "onConnectionSuspended: " + cause);
                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(ConnectionResult result) {
                        Log.d(TAG, "onConnectionFailed: " + result);
                    }
                })
                // Request access only to the Wearable API
                .addApi(Wearable.API)
                .addApi(LocationServices.API)

                .build();

    }

    private void requestFeatures(final Location location) {
        Wearable.MessageApi.addListener(mGoogleApiClient, MapsActivity.this);

        final PendingResult<CapabilityApi.GetAllCapabilitiesResult> pendingCapabilityResult =
                Wearable.CapabilityApi.getAllCapabilities(
                        mGoogleApiClient,
                        CapabilityApi.FILTER_REACHABLE);

        pendingCapabilityResult.setResultCallback(new ResolvingResultCallbacks<CapabilityApi.GetAllCapabilitiesResult>(MapsActivity.this, 100) {
            @Override
            public void onSuccess(CapabilityApi.GetAllCapabilitiesResult getAllCapabilitiesResult) {
                Log.d(TAG, "onCap: " + getAllCapabilitiesResult.getAllCapabilities().size());
                final Map<String, CapabilityInfo> allCapabilities = getAllCapabilitiesResult.getAllCapabilities();
                for (Map.Entry<String, CapabilityInfo> stringCapabilityInfoEntry : allCapabilities.entrySet()) {
                    Log.d(TAG, "onCap nodes: " + stringCapabilityInfoEntry.getKey() + "->" + stringCapabilityInfoEntry.getValue().getNodes().size());

                    for (Node node : stringCapabilityInfoEntry.getValue().getNodes()) {
                        Map<String, String> osmTags = userContext.currentFeatureType.osmTags;
                        DataQuery dataQuery = new DataQuery(3600, location.getLatitude(),location.getLongitude(), osmTags, true, Constants.MAX_RESPONSE_COUNT);

                        final PendingResult<MessageApi.SendMessageResult> sendMessageResultPendingResult = Wearable.MessageApi.sendMessage(mGoogleApiClient, node.getId(), "/features", dataQuery.getFormattedDataQuery().getBytes());

                        sendMessageResultPendingResult.setResultCallback(new ResolvingResultCallbacks<MessageApi.SendMessageResult>(MapsActivity.this, 1001) {
                            @Override
                            public void onSuccess(MessageApi.SendMessageResult sendMessageResult) {
                                Log.d(TAG, "onCap succ: " + sendMessageResult);
                            }

                            @Override
                            public void onUnresolvableFailure(Status status) {
                                Log.d(TAG, "onCap fail: " + status);
                            }
                        });

                    }
                }

            }

            @Override
            public void onUnresolvableFailure(Status status) {

            }
        });
    }

    private void handleMarkerGetFail() {
        Toast.makeText(this, "no results found", Toast.LENGTH_LONG).show();
    }

    private void addMarkersFromResponse(Element[] elements) {
        final LatLngBounds.Builder latLngBuilder = LatLngBounds.builder();

        if (elements.length == 0) {
            Toast.makeText(this, "nothing found", Toast.LENGTH_LONG).show();
            return;
        }

        markerToElementMap.clear();


        for (Element element : elements) {
            final LatLng pos = new LatLng(element.lat, element.lon);
            latLngBuilder.include(pos);
            final String fallback = TitleFromTagExtractor.getTitleFromTagMap(element.tags, "fallback");
            final Marker marker = mMap.addMarker(new MarkerOptions().position(pos).title(fallback));
            markerToElementMap.put(marker, element);
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
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        finish();
    }

    @Override
    public void onMessageReceived(MessageEvent messageEvent) {
        final String jsonResponse = new String(messageEvent.getData());

        final Moshi moshi = new Moshi.Builder().build();
        final JsonAdapter<Element[]> adapter = moshi.adapter(Element[].class);

        try {
            final Element[] list = adapter.fromJson(jsonResponse);
            addMarkersFromResponse(list);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void requestLocation() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(100)
                .setFastestInterval(100);

        LocationServices.FusedLocationApi
                .requestLocationUpdates(mGoogleApiClient, locationRequest, this)
                .setResultCallback(new ResultCallback<Status>() {

                    @Override
                    public void onResult(Status status) {
                        if (status.getStatus().isSuccess()) {
                            Log.d(TAG, "Successfully requested location updates");
                        } else {
                            Log.e(TAG,
                                    "Failed in requesting location updates, "
                                            + "status code: "
                                            + status.getStatusCode()
                                            + ", message: "
                                            + status.getStatusMessage());
                        }
                    }
                });
    }

    @Override
    public void onLocationChanged(Location location) {
        if (markerToElementMap.isEmpty()) { // only once after location
            requestFeatures(location);
        }
    }


}
