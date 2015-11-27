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
import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
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

public class FreeTrailActivity extends AbstractRouteActivity implements LocationListener{


    // where the user is located when he starts the free trail activity. This will be taken as start
    // position for his training program.
    private Location startLocation;
    private MarkerOptions startMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_trail);

        map = null;
        startLocation = null;

        // Getting Google Play availability status
        int status = GooglePlayServicesUtil.isGooglePlayServicesAvailable(getBaseContext());

        // Showing status
        if (status != ConnectionResult.SUCCESS) { // Google Play Services are not available

            int requestCode = 10;
            Dialog dialog = GooglePlayServicesUtil.getErrorDialog(status, this, requestCode);
            dialog.show();

        } else { // Google Play Services are available

            // Getting reference to the SupportMapFragment of activity_main.xml
            SupportMapFragment fm = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

            // Getting GoogleMap object from the fragment
            map = fm.getMap();

            Criteria criteria = new Criteria();
            // Getting LocationManager object from System Service LOCATION_SERVICE
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);


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
            // TODO check method 200000 => time and 0 => after so many meters?
            locationManager.requestLocationUpdates(provider, 20000, 0, this);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
    // redraw the markers when get location update.
        Context context = getApplicationContext();
        CharSequence text = "Hello toast!";
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
        drawMarker(location);
        if(startMarker == null){
            startLocation = location;
            startMarker = new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("Start Position");
            startMarker.icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue));
            map.addMarker(startMarker);
            CameraUpdate center = CameraUpdateFactory.newLatLng(new LatLng(getStartLocation().getLatitude(), getStartLocation().getLongitude()));
            CameraUpdate zoom = CameraUpdateFactory.zoomTo(16);
            map.moveCamera(center);
            map.animateCamera(zoom);
        }
        else {
            map.addMarker(startMarker);
            traceRoute(startMarker, new MarkerOptions().position(new LatLng(location.getLatitude(), location.getLongitude())).title("ME"));
        }
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

    private void drawMarker(Location location){
        // Remove any existing markers on the map
        map.clear();
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


}