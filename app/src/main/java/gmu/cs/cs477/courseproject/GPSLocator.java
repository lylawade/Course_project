package gmu.cs.cs477.courseproject;


import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.widget.Toast;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

final class GPSLocator  extends AsyncTask<Void, Void, Void>{
    private static LocationTracker tracker = new LocationTracker();
    private Context context;
    private Runnable onSuccess, onError;

    public GPSLocator(@NonNull final Context context, @NonNull final Runnable onSuccess, @NonNull final Runnable onError){
        this.context = context;
        this.onSuccess = onSuccess;
        this.onError = onError;
    }

    @Override
    protected void onPreExecute(){
        if (!Utils.isGPSEnabled(context)){
            Toast.makeText(context, "GPS is disabled", Toast.LENGTH_SHORT).show();
            this.cancel(true);
            onError.run();
            return;
        }

        if (tracker.getLastKnownLocation() == null) {
            LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
            try {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, tracker);
                locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, tracker);
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
            } catch (InterruptedException ie) {
            }
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        if (tracker.getLastKnownLocation() == null){
            Toast.makeText(context, "Could not retrieve location", Toast.LENGTH_SHORT).show();
            onError.run();
        } else{
            Toast.makeText(context, prettyPrintLocation(tracker.getLastKnownLocation()), Toast.LENGTH_LONG).show();
            onSuccess.run();
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
