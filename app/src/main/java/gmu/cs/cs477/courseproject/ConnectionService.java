package gmu.cs.cs477.courseproject;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ConnectionService {
    private static Location lastKnownLocation = null;
    Context c;

    public ConnectionService(Context c){
        this.c = c;
    }

    public  boolean isGPSEnabled(){
        LocationManager locationManager = (LocationManager) c.getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public  boolean isInternetEnabled(){
        try {
            InetAddress ipAddr = InetAddress.getByName("google.com");
            return ipAddr.equals("");

        } catch (Exception e) {
            return false;
        }
    }

    public void registerForUpdates(){
        GPSTracker tracker = new GPSTracker();
        LocationManager locationManager = (LocationManager)
                c.getSystemService(Context.LOCATION_SERVICE);
        try {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, tracker);
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, tracker);
        } catch(SecurityException se){
        }
    }

    public  Location getLocation(){

        Long t = Calendar.getInstance().getTimeInMillis();
        while ( lastKnownLocation== null && Calendar.getInstance().getTimeInMillis()-t<30000) {
            try{ Thread.sleep(1000);}
            catch (InterruptedException ie){}
        };

        return lastKnownLocation;
    }

    public Location getLastKnownLocation(){
        return lastKnownLocation;
    }

    public String getLastKnownAddress(){
        Geocoder geocoder;
        List<Address> addresses = null;
        geocoder = new Geocoder(c, Locale.getDefault());
        try {
            addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (IOException e){

        }
        String address = addresses.get(0).getAddressLine(0); // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
        String city = addresses.get(0).getLocality();
        String state = addresses.get(0).getAdminArea();
        String country = addresses.get(0).getCountryName();
        String postalCode = addresses.get(0).getPostalCode();
        String knownName = addresses.get(0).getFeatureName(); // Only if available else return NULL
        knownName = (knownName == null) ? "" : knownName;
        return address + " " + city + ", " + state + ", "  + country + " " + postalCode + " " + knownName;
    }

    private class GPSTracker implements LocationListener{

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

}
