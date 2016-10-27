package ru.mos.polls.common.controller;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import ru.mos.polls.R;
import ru.mos.polls.common.model.Position;

/**
 * Контроллер для работы с меcтоположением, получает данные на основе google play service
 * Получает координаты раз в 30 сек (UPDATE_INTERVAL_IN_SECONDS)
 */
public class LocationController implements LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private static final int MILLISECONDS_PER_SECOND = 1000;
    public static final int UPDATE_INTERVAL_IN_SECONDS = 30;
    private static final long UPDATE_INTERVAL =
            MILLISECONDS_PER_SECOND * UPDATE_INTERVAL_IN_SECONDS;
    private static final int FASTEST_INTERVAL_IN_SECONDS = 5;
    private static final long FASTEST_INTERVAL =
            MILLISECONDS_PER_SECOND * FASTEST_INTERVAL_IN_SECONDS;

    private static final int RUNTIME_LOCATION_REQUEST = 1;

    private LocationController(Context context) {
        initLocationRequest();
        locationClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .build();
    }

    private static LocationController instance;
    private GoogleApiClient locationClient;
    private LocationRequest locationRequest;
    private OnPositionListener listener;
    private Position currentPosition;

    public static boolean isLocationProviderEnable(final Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        return locationManager != null
                && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static void goToGPSSettings(Context context) {
        Intent intent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

    public static boolean isLocationNetworkProviderEnabled(Context context) {
        LocationManager locationManager = getLocationManaget(context);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }

    public static boolean isLocationGPSProviderEnabled(Context context) {
        LocationManager locationManager = getLocationManaget(context);
        return locationManager != null && locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    private static LocationManager getLocationManaget(Context context) {
        return (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
    }

    public static void showDialog(final Context context, final DialogInterface.OnClickListener cancelListener, boolean isGPSEnabled) {
        if (!isGPSEnabled) {
            showDialogEnableLocation(context, cancelListener, R.string.location_provaders_gps);
        } else {
            showDialogEnableLocation(context, cancelListener, R.string.location_provaders_are_not_available);
        }

//        showDialogEnableGPS(context, null);
    }

    public static void showDialogEnableGPS(final Context context, final DialogInterface.OnClickListener cancelListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(R.string.location_provaders_are_not_available);
        builder.setNegativeButton(R.string.cancel, cancelListener);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.turn_on, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToGPSSettings(context);
            }
        });
        builder.show();
    }


    public static void showDialogEnableLocation(final Context context, final DialogInterface.OnClickListener cancelListener, int dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage(dialogMessage);
        builder.setNegativeButton(R.string.cancel, cancelListener);
        builder.setCancelable(false);
        builder.setPositiveButton(R.string.turn_on, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                goToGPSSettings(context);
            }
        });
        builder.show();
    }

    public static synchronized LocationController getInstance(Context context) {
        if (instance == null) {
            instance = new LocationController(context);
        }
        return instance;
    }

    public boolean onRequestPermissionsResult(Activity activity, int requestCode, int[] grantResults) {
        boolean result = false;
        switch (requestCode) {
            case RUNTIME_LOCATION_REQUEST: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    connect(activity);
                    result = true;
                }
            }
        }
        return result;
    }

    public void connect(Activity context) {
        if (hasFineLocationPermission(context) && hasCoarseLocationPermission(context)) {
            locationClient.connect();
        } else {
            requestAllLocationRuntimePermission(context);
        }
    }

    public void disconnect() {
        if (locationClient != null && locationClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(locationClient, this);
            locationClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        LocationServices.FusedLocationApi.requestLocationUpdates(locationClient, locationRequest, this);
        Location location = LocationServices.FusedLocationApi.getLastLocation(locationClient);
        setCurrentPosition(location);
    }

    @Override
    public void onConnectionSuspended(int i) {
    }

    @Override
    public void onLocationChanged(Location location) {
        setCurrentPosition(location);
    }

    public Position getCurrentPosition() {
        return currentPosition;
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
    }

    public void setOnPositionListener(OnPositionListener listener) {
        if (listener != null) {
            this.listener = listener;
        } else {
            this.listener = OnPositionListener.STUB;
        }
    }

    public void requestAllLocationRuntimePermission(final Activity context) {
        if (!hasFineLocationPermission(context)) {
            ActivityCompat.requestPermissions(context,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    RUNTIME_LOCATION_REQUEST);
        }
    }

    public boolean hasFineLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    public boolean hasCoarseLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    private void initLocationRequest() {
        locationRequest = LocationRequest.create();
        locationRequest.setPriority(
                LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setInterval(UPDATE_INTERVAL);
        locationRequest.setFastestInterval(FASTEST_INTERVAL);
    }

    private void setCurrentPosition(Location location) {
        currentPosition = new Position(location);
        if (listener != null) {
            listener.onGet(currentPosition);
        }
    }

    public interface OnPositionListener {
        OnPositionListener STUB = new OnPositionListener() {
            @Override
            public void onGet(Position position) {
            }
        };

        void onGet(Position position);
    }

}