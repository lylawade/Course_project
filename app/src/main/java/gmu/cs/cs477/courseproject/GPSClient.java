package gmu.cs.cs477.courseproject;


public interface GPSClient {
    void onGPSDisabled();
    void onGPSEnabled();
    void onLocationFound();
    void onLocationNotFound();
}
