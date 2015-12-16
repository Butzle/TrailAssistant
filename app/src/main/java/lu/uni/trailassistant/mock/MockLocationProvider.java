package lu.uni.trailassistant.mock;

import android.content.Context;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;

/**
 * Created by Jo on 06/12/15.
 * source: https://mobiarch.wordpress.com/2012/07/17/testing-with-mock-location-data-in-android/
 */
public class MockLocationProvider {
    String providerName;
    Context ctx;
    LocationManager lm;
    boolean providerExists;

    public MockLocationProvider(String name, Context ctx) {
        this.providerName = name;
        this.ctx = ctx;
        this.providerExists = false;

        lm = (LocationManager) ctx.getSystemService(
                Context.LOCATION_SERVICE);

        // check if the provider name already exists (is only destroyed when the phone gets turned off)
        for(String provider: lm.getAllProviders()){
            if(provider.equals(providerName)){
                providerExists = true;
            }
        }
        if(!providerExists) {
            lm.addTestProvider(providerName, false, false, false, false, false,
                    true, true, 0, 5);
        }
        lm.setTestProviderEnabled(providerName, true);
    }


    public void pushLocation(double lat, double lon) {

        Location mockLocation = new Location(providerName);
        mockLocation.setLatitude(lat);
        mockLocation.setLongitude(lon);
        mockLocation.setAltitude(0);
        mockLocation.setAccuracy(Criteria.ACCURACY_FINE);
        mockLocation.setElapsedRealtimeNanos(1000);
        mockLocation.setTime(System.currentTimeMillis());
        lm.setTestProviderLocation(providerName, mockLocation);
    }
}