package gmu.cs.cs477.courseproject;


import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

public class LocationTracker implements LocationListener {
    private Location lastKnownLocation;


    public Location getLastKnownLocation(){
        return lastKnownLocation;
    }

    @Override
    public void onLocationChanged(Location location) {
        lastKnownLocation = location;
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
}
