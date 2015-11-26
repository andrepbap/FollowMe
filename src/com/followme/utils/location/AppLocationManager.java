package com.followme.utils.location;

import android.content.Context;
import android.location.*;
import android.os.Bundle;

public class AppLocationManager implements LocationListener {

    private LocationManager locationManager;
    private double latitude;
    private double longitude;
    private Criteria criteria;
    private String provider;

    public AppLocationManager(Context context) {
        locationManager = (LocationManager) context
                .getSystemService(Context.LOCATION_SERVICE);
        criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        provider = locationManager.getBestProvider(criteria, true);
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1,
                0, this);
        setMostRecentLocation(locationManager.getLastKnownLocation(provider));

    }

    private void setMostRecentLocation(Location lastKnownLocation) {
    	// do nothing
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.location.LocationListener#onLocationChanged(android.location.
     * Location)
     */
    @Override
    public void onLocationChanged(Location location) {
        latitude = location.getLongitude();/// * 1E6);
        longitude = location.getLatitude();// * 1E6);
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.location.LocationListener#onProviderDisabled(java.lang.String)
     */
    @Override
    public void onProviderDisabled(String arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * android.location.LocationListener#onProviderEnabled(java.lang.String)
     */
    @Override
    public void onProviderEnabled(String arg0) {
        // TODO Auto-generated method stub

    }

    /*
     * (non-Javadoc)
     * 
     * @see android.location.LocationListener#onStatusChanged(java.lang.String,
     * int, android.os.Bundle)
     */
    @Override
    public void onStatusChanged(String arg0, int arg1, Bundle arg2) {
        // TODO Auto-generated method stub

    }
    

}