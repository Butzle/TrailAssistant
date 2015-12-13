package lu.uni.trailassistant.objects;

import android.graphics.Path;

/**
 * Created by leandrogil on 11/19/15.
 */
public class GPSCoord {
    private int gpsCoordID;
    private double longitude, lattitude;

    public GPSCoord(int gpsCoordID, double longitude, double lattidude) {
        this.gpsCoordID = gpsCoordID;
        setLongitude(longitude);
        setLattitude(lattidude);
    }

    public GPSCoord() {
        gpsCoordID = 0;
        longitude = 0;
        lattitude = 0;
    }

    public double getLongitude() { return longitude; }
    public double getLattitude() { return lattitude; }
    public int getGPSCoordID() { return gpsCoordID; }

    public void setLongitude(double longitude) { this.longitude = longitude; }
    public void setLattitude(double lattitude) { this.lattitude = lattitude; }

    public String toString() {
        return "Longitude=" + longitude + ", Lattitude=" + lattitude;
    }
}