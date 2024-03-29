package gmu.cs.cs477.courseproject;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

final class GPSLocator  extends AsyncTask<Void, Void, Void>{
    private static LocationTracker tracker = new LocationTracker();
    private final Context context;
    private final GPSClient client;

    public GPSLocator(@NonNull final Context context, @NonNull final GPSClient client){
        this.context = context;
        this.client = client;
    }

    @Override
    protected void onPreExecute(){
        if (!Utils.isGPSEnabled(context)){
            Toast.makeText(context, "GPS is disabled", Toast.LENGTH_SHORT).show();
            this.cancel(true);
            client.onGPSDisabled();
            return;
        }
        client.onGPSEnabled();

        if (tracker.getLastKnownLocation() == null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, tracker);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, tracker);
//                locationManager.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, 0, 0, tracker);
//                List<String> providers = locationManager.getAllProviders();
//                for (String provider: providers){
//                    tracker.setLastKnownLocation(locationManager.getLastKnownLocation(provider));
//                }
            } catch (SecurityException se) {
                // Not gonna happen
            }
        }

    }

    @Override
    protected Void doInBackground(Void... params) {
        Location lastKnownLocation = tracker.getLastKnownLocation();
        if (lastKnownLocation == null || isTooOld(lastKnownLocation) ) {
            waitForLocation();
        }
        return null;
    }

    private boolean isTooOld(@NonNull final Location location){
        long howOldIsTooOld = TimeUnit.MINUTES.toMillis(10);
        long howOld = Calendar.getInstance().getTimeInMillis() - location.getTime();
        return howOld > howOldIsTooOld;
    }

    private void waitForLocation(){
        Location lastKnownLocation = tracker.getLastKnownLocation();
        // Wait for GPS location
        Long t = Calendar.getInstance().getTimeInMillis();
        while (lastKnownLocation == null && Calendar.getInstance().getTimeInMillis() - t < 10000) {
            try {
                Thread.sleep(100);
            } catch (InterruptedException ie) {}
            lastKnownLocation = tracker.getLastKnownLocation();
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        if (tracker.getLastKnownLocation() == null){
            Toast.makeText(context, "Could not retrieve location", Toast.LENGTH_SHORT).show();
            client.onLocationNotFound();
        } else{
            Toast.makeText(context, prettyPrintLocation(tracker.getLastKnownLocation()), Toast.LENGTH_LONG).show();
            client.onLocationFound();
        }
    }

    // Testing methods

    private String prettyPrintLocation(@NonNull Location location){
        String prettyPrint = "Lat: " + location.getLatitude() + ", long: " + location.getLongitude();
        prettyPrint += " from " + location.getProvider();
        return prettyPrint;
    }

    //Use if you want to get the location in address form

//    private String getLastKnownAddress(){
//        Location lastKnownLocation  = tracker.getLastKnownLocation();
//        Geocoder geocoder;
//        List<Address> addresses = null;
//        geocoder = new Geocoder(context, Locale.getDefault());
//        try {
//            addresses = geocoder.getFromLocation(lastKnownLocation.getLatitude(), lastKnownLocation.getLongitude(), 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
//        } catch (IOException e){
//        }
//        if ((addresses == null) || (addresses.size() == 0)){
//            return "Could not get address, but location found";
//        }
//        String address = addresses.get(0).getAddressLine(0);
//        String city = addresses.get(0).getLocality();
//        String state = addresses.get(0).getAdminArea();
//        String country = addresses.get(0).getCountryName();
//        String postalCode = addresses.get(0).getPostalCode();
//        return address + " " + city + ", " + state + ", "  + country + " " + postalCode;
//    }
}
