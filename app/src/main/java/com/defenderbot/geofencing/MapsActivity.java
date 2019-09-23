package com.defenderbot.geofencing;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.defenderbot.R;
import com.defenderbot.activity.ChildLocationListActivity;
import com.defenderbot.fragment.ChildListFragment;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.Autocomplete;
import com.google.android.libraries.places.widget.AutocompleteActivity;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.android.PolyUtil;
import com.google.maps.errors.ApiException;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.TravelMode;


import org.joda.time.DateTime;
import org.xml.sax.ErrorHandler;


import java.io.IOException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import pl.charmas.android.reactivelocation.ReactiveLocationProvider;
import rx.Observable;
import rx.Subscription;
import rx.functions.Action1;
import rx.subscriptions.CompositeSubscription;

import static com.defenderbot.activity.ChildLocationListActivityKt.ChildLocationBool;
import static com.defenderbot.fragment.ChildListFragmentKt.coordList;
import static com.defenderbot.fragment.ChildListFragmentKt.location_id;
import static com.defenderbot.fragment.ChildListFragmentKt.location_name;
import static com.defenderbot.fragment.ChildListFragmentKt.destinationLat;
import static com.defenderbot.util.duonavigationdrawer.ConstantKt.desti_address;
import static com.defenderbot.util.duonavigationdrawer.ConstantKt.destinationLatlong;
import static com.defenderbot.fragment.ChildListFragmentKt.destinationlong;
import static com.defenderbot.fragment.ChildListFragmentKt.originLat;
import static com.defenderbot.util.duonavigationdrawer.ConstantKt.originLatlong;
import static com.defenderbot.fragment.ChildListFragmentKt.originlong;




public class MapsActivity extends BaseActivity implements OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,View.OnClickListener {
    public final static String MAP_OPTION = "map_option";
    public final static int REQUEST_CHECK_SETTINGS = 0;
    public static final String POINTS = "coordList";
    private final static String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Location currentLocation;
    private List<Marker> markerList = new ArrayList<>();
    private Polygon polygon;
    private Polyline polyline;
    private ReactiveLocationProvider locationProvider;
    private Observable<Location> lastKnownLocationObservable;
    private Observable<Location> locationUpdatesObservable;
    private Subscription lastKnownLocationSubscription;
    private Subscription updatableLocationSubscription;
    private Marker currentMarker;
    private CompositeSubscription compositeSubscription;
    private boolean isGPSOn = false;
    private GoogleApiClient mGoogleApiClient;
    private DrawingOption drawingOption;
    private View calLayout;
    private TextView areaTextView;
    private TextView lengthTextView;
    FloatingActionButton btnDone;
    FloatingActionButton btnEdit;
    FloatingActionButton btnUndo;
    FloatingActionButton btnList;
    private LinearLayout lay_droplocation;
    private TextView tv_droplocation;
    private EditText et_droptitel;
    ImageView btnConfirm,btncancel;
    LinearLayout layaddgeo;
    final int AUTOCOMPLETE_REQUEST_CODE = 101;
    List<Place.Field> fields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
    boolean mapCall=true;


    private static Bitmap getBitmapFromDrawable(Context context, int icon) {
        Drawable drawable = ContextCompat.getDrawable(context, icon);
        Bitmap obm = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(obm);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);
        return obm;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        drawingOption = getIntent().getParcelableExtra(MAP_OPTION);
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        setViewIDs();
        setOnClick();
        setPlace();

        setUpCalculateLayout();
        initRequestingLocation();
        if (drawingOption.getRequestGPSEnabling())
            requestActivatingGPS();
    }

   void setPlace(){


       if (!Places.isInitialized()) {
           Places.initialize(getApplicationContext(), getString(R.string.google_maps_key), Locale.US);
       }
       // Initialize the AutocompleteSupportFragment.
       AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
               getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

// Specify the types of place data to return.
       autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS));

// Set up a PlaceSelectionListener to handle the response.
       autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
           @Override
           public void onPlaceSelected(Place place) {
               // TODO: Get info about the selected place.
               Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
           }

           @Override
           public void onError(Status status) {
               // TODO: Handle the error.
               Log.i(TAG, "An error occurred: " + status);
           }
       });
   }

    public void setViewIDs(){
        btnEdit = (FloatingActionButton) findViewById(R.id.btnEdit);
        btnUndo = (FloatingActionButton) findViewById(R.id.btnUndo);
        btnDone = (FloatingActionButton) findViewById(R.id.btnDone);
        btnList = (FloatingActionButton) findViewById(R.id.btnList);
        lay_droplocation= (LinearLayout) findViewById(R.id.lay_droplocation);
        tv_droplocation= (TextView) findViewById(R.id.tv_droplocation);
        et_droptitel= (EditText) findViewById(R.id.et_droptitel);
        btnConfirm = (ImageView) findViewById(R.id.btnConfirm);
        btncancel = (ImageView) findViewById(R.id.btncancel);
        layaddgeo= (LinearLayout) findViewById(R.id.layaddgeo);

    }

    public void setOnClick(){
        btnEdit.setOnClickListener(this);
        btnUndo.setOnClickListener(this);
        btnDone.setOnClickListener(this);
        btnList.setOnClickListener(this);
        btnConfirm.setOnClickListener(this);
        btncancel.setOnClickListener(this);
        tv_droplocation.setOnClickListener(this);
        layaddgeo.setOnClickListener(this);
    }



    private void setUpCalculateLayout() {
        calLayout = findViewById(R.id.calculate_layout);
        calLayout.setVisibility(drawingOption.getEnableCalculateLayout() ? View.VISIBLE : View.GONE);
        areaTextView = (TextView) findViewById(R.id.areaTextView);
        lengthTextView = (TextView) findViewById(R.id.lengthTextView);
    }

    private void initRequestingLocation() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();
        mGoogleApiClient.connect();
        compositeSubscription = new CompositeSubscription();
        locationProvider = new ReactiveLocationProvider(getApplicationContext());
        lastKnownLocationObservable = locationProvider.getLastKnownLocation();
    }

    private void requestActivatingGPS() {
        LocationRequest locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setNumUpdates(5)
                .setInterval(100);
        locationUpdatesObservable = locationProvider.getUpdatedLocation(locationRequest);
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest).setAlwaysShow(true);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient, builder.build());
        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult locationSettingsResult) {
                final Status status = locationSettingsResult.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        getLastKnowLocation();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this,
                                    REQUEST_CHECK_SETTINGS);
                        } catch (IntentSender.SendIntentException e) {
                            e.printStackTrace();
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        Log.e(TAG, "Error happen during show Dialog for Turn of GPS");
                        break;
                }
            }
        });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnEdit:
                lay_droplocation.setVisibility(View.VISIBLE);
                btnEdit.setVisibility(View.GONE);
                btnUndo.setVisibility(View.GONE);
                btnDone.setVisibility(View.GONE);
                btnList.setVisibility(View.GONE);
                break;
            case R.id.btnUndo:
                if (coordList.size() > 0) {
                    Marker marker = markerList.get(markerList.size() - 1);
                    marker.remove();
                    markerList.remove(marker);
                    coordList.remove(coordList.size() - 1);
                    if (coordList.size() > 0) {
                        if (drawingOption.getDrawingType() == DrawingOption.DrawingType.POLYGON) {
                            drawPolygon(coordList);
                        }
//            else if (drawingOption.getDrawingType() == DrawingOption.DrawingType.POLYLINE) {
//              drawPolyline(coordList);
//            }
                    }
                }
                break;
            case R.id.btnDone:
                returnCurrentPosition();
                break;
            case R.id.btnList:
                startActivity(new Intent(this, ChildLocationListActivity.class));
                break;
            case R.id.btncancel:
                lay_droplocation.setVisibility(View.GONE);
                btnEdit.setVisibility(View.VISIBLE);
                btnUndo.setVisibility(View.VISIBLE);
                btnDone.setVisibility(View.VISIBLE);
                btnList.setVisibility(View.VISIBLE);
                break;
            case R.id.btnConfirm:
                if(!et_droptitel.getText().toString().equalsIgnoreCase("")) {
                    lay_droplocation.setVisibility(View.GONE);
                    btnEdit.setVisibility(View.VISIBLE);
                    btnUndo.setVisibility(View.VISIBLE);
                    btnDone.setVisibility(View.VISIBLE);
                    btnList.setVisibility(View.VISIBLE);
                    location_name =  et_droptitel.getText().toString();
                }else{
                    Toast.makeText(this, "Please add title", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.layaddgeo:
                if(markerList.size()>0){

                    new AlertDialog.Builder(this)
                            .setTitle("Clear entry")
                            .setMessage("Are you sure you want to clear current geo fencing?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    location_id = 0;
                                    location_name = "";
                                    markerList.clear();
                                    mMap.clear();
                                    coordList.clear();
                                    polygon.remove();
                                    setAreaLength(coordList);
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }else{
                    location_id = 0;
                    location_name = "";
                    markerList.clear();
                    mMap.clear();
                    coordList.clear();
                    if(polygon!=null)
                    polygon.remove();
                    setAreaLength(coordList);
                }

                break;
            case R.id.tv_droplocation:
                // Start the autocomplete intent.
                if(markerList.size()>0){

                    new AlertDialog.Builder(this)
                            .setTitle("Delete entry")
                            .setMessage("Are you sure you want to delete current geo fencing?")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    markerList.clear();
                                    mMap.clear();
                                    coordList.clear();
                                    polygon.remove();
                                    setAreaLength(coordList);
                                    Intent intent = new Autocomplete.IntentBuilder(
                                            AutocompleteActivityMode.FULLSCREEN, fields)
                                            .build(MapsActivity.this);
                                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();

                }else{
                    Intent intent = new Autocomplete.IntentBuilder(
                            AutocompleteActivityMode.FULLSCREEN, fields)
                            .build(MapsActivity.this);
                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                }
                break;

        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(ChildLocationBool){
            mMap.clear();
            markerList.clear();
            ChildLocationBool=false;
            if (coordList.size() > 0) {
                mapCall=false;
                if (drawingOption.getDrawingType() == DrawingOption.DrawingType.POLYGON) {
                    drawPolygon(coordList);
                    @SuppressLint("ResourceType") @IdRes int icon = R.drawable.ic_add_location_light_green_500_36dp;
                    BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(MapsActivity.this, icon));

                    for(int i=0;i<coordList.size();i++){
                        Marker marker = mMap.addMarker(new MarkerOptions().position(coordList.get(i)).icon(bitmap).draggable(true));
                        marker.setTag(coordList.get(i));
                        markerList.add(marker);
                    }
                    if(destinationLat!=0.1 && destinationlong!=0.1) {
                        showRoute();
                    }else{
                        new AlertDialog.Builder(this)
                                .setTitle("Add entry")
                                .setMessage("You need to add destination location for create route.")

                                // Specifying a listener allows you to take an action before dismissing the dialog.
                                // The dialog is automatically dismissed when a dialog button is clicked.
                                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Continue with delete operation
                                        markerList.clear();
                                        mMap.clear();
                                        coordList.clear();
                                        polygon.remove();
                                        setAreaLength(coordList);
                                        Intent intent = new Autocomplete.IntentBuilder(
                                                AutocompleteActivityMode.FULLSCREEN, fields)
                                                .build(MapsActivity.this);
                                        startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                                    }
                                })

                                // A null listener allows the button to dismiss the dialog and take no further action.
                                .setNegativeButton(android.R.string.no, null)
                                .setIcon(android.R.drawable.ic_dialog_alert)
                                .show();
                    }
                }
            }
            destinationLat = 0.1;
                    destinationlong =0.1;
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        LatLng center = new LatLng(drawingOption.getLocationLatitude(), drawingOption.getLocationLongitude());
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(center, drawingOption.getZoom()));
        @SuppressLint("ResourceType") @IdRes int iconHome = R.drawable.ic_home_black_24dp;
        BitmapDescriptor bitmapHome = BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(MapsActivity.this,iconHome ));
        mMap.addMarker(new MarkerOptions().position(center).icon(bitmapHome).draggable(true));
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                @SuppressLint("ResourceType") @IdRes int icon = R.drawable.ic_add_location_light_green_500_36dp;
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(MapsActivity.this, icon));
                Marker marker = mMap.addMarker(new MarkerOptions().position(latLng).icon(bitmap).draggable(true));
                marker.setTag(latLng);
                markerList.add(marker);
                coordList.add(latLng);
                if (drawingOption.getDrawingType() == DrawingOption.DrawingType.POLYGON) {
                    drawPolygon(coordList);
                    setAreaLength(coordList);
                }
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {
                updateMarkerLocation(marker, false);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                updateMarkerLocation(marker, true);
            }
        });
        if (coordList.size() > 0 && mapCall) {
            mapCall=false;
            if (drawingOption.getDrawingType() == DrawingOption.DrawingType.POLYGON) {
                drawPolygon(coordList);
                @SuppressLint("ResourceType") @IdRes int icon = R.drawable.ic_add_location_light_green_500_36dp;
                BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(MapsActivity.this, icon));

                for(int i=0;i<coordList.size();i++){
                    Marker marker = mMap.addMarker(new MarkerOptions().position(coordList.get(i)).icon(bitmap).draggable(true));
                    marker.setTag(coordList.get(i));
                    markerList.add(marker);
                }
                if(destinationLat!=0.1 && destinationlong!=0.1) {
                    showRoute();
                }else{
                    new AlertDialog.Builder(this)
                            .setTitle("Add entry")
                            .setMessage("You need to add destination location for create route.")

                            // Specifying a listener allows you to take an action before dismissing the dialog.
                            // The dialog is automatically dismissed when a dialog button is clicked.
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    // Continue with delete operation
                                    markerList.clear();
                                    mMap.clear();
                                    coordList.clear();
                                    polygon.remove();
                                    setAreaLength(coordList);
                                    Intent intent = new Autocomplete.IntentBuilder(
                                            AutocompleteActivityMode.FULLSCREEN, fields)
                                            .build(MapsActivity.this);
                                    startActivityForResult(intent, AUTOCOMPLETE_REQUEST_CODE);
                                }
                            })

                            // A null listener allows the button to dismiss the dialog and take no further action.
                            .setNegativeButton(android.R.string.no, null)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .show();
                }
            }
        }else{
            if(destinationLat!=0.1 && destinationlong!=0.1) {
                showRoute();
            }else{
                new AlertDialog.Builder(this)
                        .setTitle("Add entry")
                        .setMessage("You need to add destination location for create route.")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                lay_droplocation.setVisibility(View.VISIBLE);
                                btnEdit.setVisibility(View.GONE);
                                btnUndo.setVisibility(View.GONE);
                                btnDone.setVisibility(View.GONE);
                                btnList.setVisibility(View.GONE);
                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        }
    }

    private void updateMarkerLocation(Marker marker, boolean calculate) {
        LatLng latLng = (LatLng) marker.getTag();
        int position = coordList.indexOf(latLng);
        coordList.set(position, marker.getPosition());
        marker.setTag(marker.getPosition());
        if (drawingOption.getDrawingType() == DrawingOption.DrawingType.POLYGON) {
            drawPolygon(coordList);
            if (calculate)
                setAreaLength(coordList);
        }

    }

/*  private void drawPolyline(List<LatLng> latLngList) {
    if (polyline != null) {
      polyline.remove();
    }
    PolylineOptions polylineOptions = new PolylineOptions();
    polylineOptions.color(drawingOption.getStrokeColor());
    polylineOptions.width(drawingOption.getStrokeWidth());
    polylineOptions.addAll(latLngList);
    polyline = mMap.addPolyline(polylineOptions);
  }*/

    private void drawPolygon(List<LatLng> latLngList) {
        if (polygon != null) {
            polygon.remove();
        }
        PolygonOptions polygonOptions = new PolygonOptions();
        polygonOptions.fillColor(drawingOption.getFillColor());
        polygonOptions.strokeColor(drawingOption.getStrokeColor());
        polygonOptions.strokeWidth(drawingOption.getStrokeWidth());
        polygonOptions.addAll(latLngList);
        polygon = mMap.addPolygon(polygonOptions);
    }

    @Override
    protected void onLocationPermissionGranted() {
        getLastKnowLocation();
        updateLocation();
    }

    private void updateLocation() {
        if (locationUpdatesObservable != null && compositeSubscription != null) {
            updatableLocationSubscription = locationUpdatesObservable
                    .subscribe(new Action1<Location>() {
                        @Override
                        public void call(Location location) {
                            if (currentLocation == null)
                                moveMapToCenter(location);

                            currentLocation = location;
                            moveMarkerCurrentPosition(location);
                        }
                    }, new ErrorHandler());
            compositeSubscription.add(updatableLocationSubscription);
        }
    }

    private void getLastKnowLocation() {
        if (lastKnownLocationObservable != null && compositeSubscription != null) {
            lastKnownLocationSubscription =
                    lastKnownLocationObservable
                            .subscribe(new Action1<Location>() {
                                @Override
                                public void call(Location location) {
                                    currentLocation = location;
                                    moveMapToCenter(location);
                                }
                            }, new ErrorHandler());
            compositeSubscription.add(lastKnownLocationSubscription);
        }
    }

    public void moveMapToCenter(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null) {
            myLocationMarker(latLng);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
        }
    }

    public void moveMarkerCurrentPosition(Location location) {
        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
        if (mMap != null) {
            myLocationMarker(latLng);
        }
    }

    private void myLocationMarker(LatLng latLng) {
        if (currentMarker != null) {
            currentMarker.setPosition(latLng);
        } else {
            @SuppressLint("ResourceType") @IdRes int icon = R.drawable.ic_navigation_red_a400_36dp;
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(MapsActivity.this, icon));
            currentMarker = mMap.addMarker(new MarkerOptions()
                    .position(latLng)
                    .icon(bitmap)
                    .draggable(false));
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (lastKnownLocationSubscription != null && updatableLocationSubscription != null && compositeSubscription != null) {
            compositeSubscription.unsubscribe();
            compositeSubscription.clear();
            updatableLocationSubscription.unsubscribe();
            lastKnownLocationSubscription.unsubscribe();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case RESULT_OK:
                        // All required changes were successfully made
                        Log.d(TAG, "User enabled location");
                        getLastKnowLocation();
                        updateLocation();
                        isGPSOn = true;
                        break;
                    case RESULT_CANCELED:
                        // The user was asked to change settings, but chose not to
                        Log.d(TAG, "User Cancelled enabling location");
                        isGPSOn = false;
                        break;

                    default:
                        break;
                }
                break;
            case AUTOCOMPLETE_REQUEST_CODE:
                if (resultCode == RESULT_OK) {
                    showRouteNew(data);
                } else if (resultCode == AutocompleteActivity.RESULT_ERROR) {
                    // TODO: Handle the error.
                    Status status = Autocomplete.getStatusFromIntent(data);
                    Log.i(TAG, status.getStatusMessage());
                } else if (resultCode == RESULT_CANCELED) {
                    // The user canceled the operation.
                }
                break;
        }
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    private void addMarkersToMap(DirectionsResult results, GoogleMap mMap) {
        @SuppressLint("ResourceType") @IdRes int iconHome = R.drawable.ic_home_black_24dp;
        BitmapDescriptor bitmapHome = BitmapDescriptorFactory.fromBitmap(getBitmapFromDrawable(MapsActivity.this,iconHome ));
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].startLocation.lat,
                results.routes[0].legs[0].startLocation.lng)).title(results.routes[0].legs[0].startAddress)).setIcon(bitmapHome);
        mMap.addMarker(new MarkerOptions().position(new LatLng(results.routes[0].legs[0].endLocation.lat,
                results.routes[0].legs[0].endLocation.lng))
                .title(results.routes[0].legs[0].startAddress).snippet(getEndLocationTitle(results)));
    }

    private GeoApiContext getGeoContext() {
        GeoApiContext geoApiContext = new GeoApiContext();
        return geoApiContext.setQueryRateLimit(3).setApiKey(getString(R.string.google_maps_key)).setConnectTimeout(1, TimeUnit.SECONDS).
                setReadTimeout(1, TimeUnit.SECONDS).setWriteTimeout(1, TimeUnit.SECONDS);
    }

    private String getEndLocationTitle(DirectionsResult results){
        return  "Time :"+ results.routes[0].legs[0].duration.humanReadable +
                " Distance :" + results.routes[0].legs[0].distance.humanReadable;
    }

    private void addPolyline(DirectionsResult results, GoogleMap mMap) {
        List<LatLng> decodedPath = PolyUtil.decode(results.routes[0].overviewPolyline.getEncodedPath());
        mMap.addPolyline(new PolylineOptions().addAll(decodedPath));
    }

    private void returnCurrentPosition() {
        if (coordList.size() > 0) {
            Intent returnIntent = new Intent();
            LatLng[] latLngs = new LatLng[coordList.size()];
            coordList.toArray(latLngs);
            DataModel dataModel = new DataModel();
            dataModel.setCount(coordList.size());
            dataModel.setPoints(latLngs);
            returnIntent.putExtra(POINTS, dataModel);
            setResult(RESULT_OK, returnIntent);

        } else {
            setResult(RESULT_CANCELED);
        }
        finish();
    }

    private void setAreaLength(List<LatLng> coordList) {
        areaTextView.setText(getString(R.string.area_label) + String.format(Locale.ENGLISH, "%.2f", CalUtils.getArea(coordList)) + getString(R.string.mm_label));
        lengthTextView.setText(getString(R.string.length_label) + String.format(Locale.ENGLISH, "%.2f", CalUtils.getLength(coordList)) + getString(R.string.m_label));
    }

    private void setLength(List<LatLng> coordList) {
        lengthTextView.setText(getString(R.string.length_label) + String.format(Locale.ENGLISH, "%.2f", CalUtils.getLength(coordList)) + getString(R.string.m_label));
    }


    void showRouteNew( Intent data){
        mMap.clear();
        Place place = Autocomplete.getPlaceFromIntent(data);
        LatLng origin1 = new LatLng(originLat, originlong);
//                    LatLng origin1 = new LatLng(22.66215, 75.9035);
        double latOrigin = origin1.latitude;
        double longOrigin = origin1.longitude;

        double latOrigin2 = place.getLatLng().latitude;
        double longOrigin2 = place.getLatLng().longitude;

        tv_droplocation.setText(place.getName());

        originLatlong = origin1.latitude+","+origin1.longitude;
        destinationLatlong = place.getLatLng().latitude+","+place.getLatLng().longitude;

        desti_address = place.getAddress();

        DateTime now = new DateTime();
        DirectionsResult result = null;
        try {
            result = DirectionsApi.newRequest(getGeoContext()).
                    mode(TravelMode.DRIVING).
                    origin(latOrigin+","+longOrigin).
                    destination(latOrigin2+","+longOrigin2).departureTime(now).await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
            if(result!=null) {
                addMarkersToMap(result, mMap);
                addPolyline(result,mMap);
            }
        }catch (Exception exe){
            Toast.makeText(this, "Please update address in child profile", Toast.LENGTH_SHORT).show();
            finish();
        }

//        addMarkersToMap(result,mMap);
//        addPolyline(result,mMap);
//        Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
    }

    void showRoute( ){

        DateTime now = new DateTime();
        DirectionsResult result = null;
        try {
            result = DirectionsApi.newRequest(getGeoContext()).
                    mode(TravelMode.DRIVING).
                    origin(originLat+","+originlong).
                    destination(destinationLat+","+destinationlong).departureTime(now).await();
        } catch (ApiException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try{
        if(result!=null) {
            addMarkersToMap(result, mMap);
            addPolyline(result,mMap);
        }
        }catch (Exception exe){
            Toast.makeText(this, "Please update address in child profile", Toast.LENGTH_SHORT).show();
            finish();
        }


//        Log.i(TAG, "Place: " + place.getName() + ", " + place.getId());
    }

    private class ErrorHandler implements Action1<Throwable> {
        @Override
        public void call(Throwable throwable) {
            Toast.makeText(MapsActivity.this, "Error occurred.", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Error occurred", throwable);
        }
    }

}
