package lu.uni.trailassistant.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;

import com.directions.route.Route;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;

import lu.uni.trailassistant.R;

/**
 * Created by Jo on 26/11/15.
 */
/*
Defines the general properties of the two Map Activities
 */
public abstract class AbstractRouteActivity extends FragmentActivity implements RoutingListener {

    protected ArrayList<Polyline> polylines;
    private static final String TAG = "MapsActivity";
    protected GoogleMap map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        map = null;
        polylines = new ArrayList<>();
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

    // trace the route of the user
    protected void traceRoute(MarkerOptions origin, MarkerOptions destination) {
        // initialize an async. request using the direction api
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(this)
                .waypoints(origin.getPosition(), destination.getPosition())
                .build();

        // launch the request
        routing.execute();
    }
}
