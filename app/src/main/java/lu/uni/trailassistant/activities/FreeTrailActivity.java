package lu.uni.trailassistant.activities;


import android.Manifest;
import android.app.Dialog;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.widget.Toast;

import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import lu.uni.trailassistant.R;


// http://stackoverflow.com/questions/16005223/android-google-map-api-v2-current-location

/*
 * TODO examine what this class exactly does (copied and modified it a little bit from stackoverflow)
 */
/*
TODO what happens if the user pushes app to background?
 */

public class FreeTrailActivity extends FragmentActivity{

    GoogleMap map;

    // where the user is located when he starts the free trail activity. This will be taken as start
    // position for his training program.
    private Location startLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_trail);

        map = null;
        startLocation = null;

        // Getting reference to the SupportMapFragment of activity_main.xml
        SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

        // Getting GoogleMap object from the fragment
        map = fm.getMap();

        Context context = getApplicationContext();
        Toast.makeText(context, "Test1234", Toast.LENGTH_SHORT).show();

        // get the start position
        LocationManager service = (LocationManager) getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        // Getting LocationManager object from System Service LOCATION_SERVICE
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        // Creating a criteria object to retrieve provider

        // Getting the name of the best provider
        String provider = locationManager.getBestProvider(criteria, true);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        setStartLocation(service.getLastKnownLocation(provider));


        Location location = locationManager.getLastKnownLocation(provider);

        LocationListener locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                // redraw the markers when get location update.
                drawMarker(location);
                MarkerOptions start = new MarkerOptions().position(new LatLng(getStartLocation().getLatitude(), getStartLocation().getLongitude())).title("Start Position");
                start.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
                map.addMarker(start);
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(location!=null){
            //PLACE THE INITIAL MARKER
            drawMarker(location);
        }

        // TODO check method 200000 => time and 0 => after so many meters?
        locationManager.requestLocationUpdates(provider,20000,0,locationListener);
    }



    private void drawMarker(Location location){
        // Remove any existing markers on the map
        //map.clear();
        LatLng currentPosition = new LatLng(location.getLatitude(),location.getLongitude());
        map.addMarker(new MarkerOptions().position(currentPosition).title("ME"));

        // http://stackoverflow.com/questions/18425141/android-google-maps-api-v2-zoom-to-current-location
        CameraUpdate center=CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude()));
        CameraUpdate zoom=CameraUpdateFactory.zoomTo(16);
        map.moveCamera(center);
        map.animateCamera(zoom);
    }

    public void setStartLocation(Location startLocation){
      this.startLocation = startLocation;
    }

    public Location getStartLocation(){
        return startLocation;
    }

    /*private void traceRoute(MarkerOptions start, MarkerOptions currentPosition) {
        // initialize an async. request using the direction api
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.WALKING)
                .withListener(this)
                .waypoints(start.getPosition(), currentPosition.getPosition())
                .build();

        // launch the request
        routing.execute();
    }*/

}