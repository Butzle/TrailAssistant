package lu.uni.trailassistant.objects;

import android.graphics.Path;

/**
 * Created by leandrogil on 11/19/15.
 */
public class GPSCoord {
    private double longitude, lattitude;

    public GPSCoord(double longitude, double lattidude) {
        setLongitude(longitude);
        setLattitude(lattidude);
    }

    public double getLongitude() { return longitude; }
    public double getLattitude() { return lattitude; }

    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setLattitude(double lattitude) { this.lattitude = lattitude; }

    public void generateGoogleMapsDirections() {
        // TODO: generate the Google Maps Direction(s) object through an HTTP request and return it(?)
    }

    public String toString() {
        return "Longitude=" + longitude + ", Lattitude=" + lattitude;
    }
}