package lu.uni.trailassistant.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.directions.route.Route;
import com.directions.route.Routing;

import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import lu.uni.trailassistant.R;


public class GoogleMapsDefineRouteActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, RoutingListener {

    private static final String TAG = "MapsActivity";

    private GoogleMap map;
    private MarkerOptions origin;
    private MarkerOptions destination;
    private ArrayList<Polyline> polylines;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_google_maps_define_route);

        // initialize object attributes
        map = null;
        origin = null;
        destination = null;
        polylines = new ArrayList<>();

        // setup the map fragment widget
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        // fetch the map async.
        mapFragment.getMapAsync(this);
    }

    @Override
    public void onMapReady(GoogleMap map) {
        // setup the map attribute
        this.map = map;
        map.setOnMapLongClickListener(this);

        // create the origin marker
        LatLng point = new LatLng(49.626271, 6.158536);
        origin = new MarkerOptions().position(point).title("Origin");
        origin.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));

        // add the marker to the map
        createOriginMarker();
    }

    @Override
    public void onMapLongClick(LatLng point) {
        // remove all markers (origin and destination)
        map.clear();

        // create the destination marker
        destination = new MarkerOptions().position(point).title("Destination");
        destination.icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green));

        // add the both markers to the map
        createOriginMarker();
        createDestinationMarker();

        // display the route and log the duration/distance
        traceRoute(origin, destination);
    }

    private void createOriginMarker() {
        // safe guard
        if (origin == null)
            return;

        // add the marker to the map
        map.addMarker(origin);

        // center the camera with a zoom of 15
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(origin.getPosition(), 15));
    }

    private void createDestinationMarker() {
        // safe guard
        if (destination == null)
            return;

        // add the marker to the map
        map.addMarker(destination);

        // center the camera with a zoom of 15
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(destination.getPosition(), 15));
    }

    private void traceRoute(MarkerOptions origin, MarkerOptions destination) {
        // initialize an async. request using the direction api
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(this)
                .waypoints(origin.getPosition(), destination.getPosition())
                .build();

        // launch the request
        routing.execute();
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        // remove the previous polylines
        if (polylines.size() > 0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }

        // create a new array of polylines
        polylines = new ArrayList<>();

        // select the shortest path
        Route path = route.get(shortestRouteIndex);

        // add polylines to the map and the array
        PolylineOptions polyOptions = new PolylineOptions();
        polyOptions.color(R.color.colorPrimary);
        polyOptions.width(13);
        polyOptions.addAll(path.getPoints());
        Polyline polyline = map.addPolyline(polyOptions);
        polylines.add(polyline);

        // log the distance and duration
        Log.i(TAG, "[Route] distance: " + path.getDistanceText() + ", duration: " + path.getDurationValue() + " sec");
    }

    @Override
    public void onRoutingFailure() {

    }

    @Override
    public void onRoutingStart() {

    }

    @Override
    public void onRoutingCancelled() {

    }
}
