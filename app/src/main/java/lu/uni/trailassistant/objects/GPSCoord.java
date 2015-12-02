package lu.uni.trailassistant.objects;

import android.graphics.Path;

/**
 * Created by leandrogil on 11/19/15.
 */
public class GPSCoord {
    private int gpsCoordID;
    private float longitude, lattitude;

    public GPSCoord(int gpsCoordID, float longitude, float lattidude) {
        this.gpsCoordID = gpsCoordID;
        setLongitude(longitude);
        setLattitude(lattidude);
    }

    public float getLongitude() { return longitude; }
    public float getLattitude() { return lattitude; }

    public void setLongitude(float longitude) { this.longitude = longitude; }
    public void setLattitude(float lattitude) { this.lattitude = lattitude; }

    public void generateGoogleMapsDirections() {
        // TODO: generate the Google Maps Direction(s) object through an HTTP request and return it(?)
    }

    public String toString() {
        return "Longitude=" + longitude + ", Lattitude=" + lattitude;
    }
}