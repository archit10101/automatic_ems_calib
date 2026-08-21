package com.example.othermaps;

import static com.mapbox.maps.plugin.animation.CameraAnimationsUtils.getCamera;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.addOnMapClickListener;
import static com.mapbox.maps.plugin.gestures.GesturesUtils.getGestures;
import static com.mapbox.maps.plugin.locationcomponent.LocationComponentUtils.getLocationComponent;
import static com.mapbox.navigation.base.extensions.RouteOptionsExtensions.applyDefaultNavigationOptions;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.PowerManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mapbox.android.core.location.LocationEngine;
import com.mapbox.android.core.location.LocationEngineCallback;
import com.mapbox.android.core.location.LocationEngineProvider;
import com.mapbox.android.core.location.LocationEngineResult;
import com.mapbox.android.gestures.MoveGestureDetector;
import com.mapbox.api.directions.v5.DirectionsCriteria;
import com.mapbox.api.directions.v5.models.Bearing;
import com.mapbox.api.directions.v5.models.LegStep;
import com.mapbox.api.directions.v5.models.RouteOptions;
import com.mapbox.api.directions.v5.models.VoiceInstructions;
import com.mapbox.bindgen.Expected;
import com.mapbox.geojson.Point;
import com.mapbox.maps.CameraOptions;
import com.mapbox.maps.EdgeInsets;
import com.mapbox.maps.MapView;
import com.mapbox.maps.Style;
import com.mapbox.maps.extension.style.layers.properties.generated.TextAnchor;
import com.mapbox.maps.plugin.animation.MapAnimationOptions;
import com.mapbox.maps.plugin.annotation.AnnotationPlugin;
import com.mapbox.maps.plugin.annotation.AnnotationPluginImplKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManagerKt;
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions;
import com.mapbox.maps.plugin.gestures.OnMapClickListener;
import com.mapbox.maps.plugin.gestures.OnMoveListener;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentConstants;
import com.mapbox.maps.plugin.locationcomponent.LocationComponentPlugin;
import com.mapbox.maps.plugin.locationcomponent.generated.LocationComponentSettings;
import com.mapbox.navigation.base.options.NavigationOptions;
import com.mapbox.navigation.base.route.NavigationRoute;
import com.mapbox.navigation.base.route.NavigationRouterCallback;
import com.mapbox.navigation.base.route.RouterFailure;
import com.mapbox.navigation.base.route.RouterOrigin;
import com.mapbox.navigation.base.trip.model.RouteProgress;
import com.mapbox.navigation.base.trip.model.RouteStepProgress;
import com.mapbox.navigation.core.MapboxNavigation;
import com.mapbox.navigation.core.directions.session.RoutesObserver;
import com.mapbox.navigation.core.directions.session.RoutesUpdatedResult;
import com.mapbox.navigation.core.lifecycle.MapboxNavigationApp;
import com.mapbox.navigation.core.replay.MapboxReplayer;
import com.mapbox.navigation.core.replay.ReplayLocationEngine;
import com.mapbox.navigation.core.replay.history.ReplayEventUpdateLocation;
import com.mapbox.navigation.core.replay.route.ReplayRouteMapper;
import com.mapbox.navigation.core.trip.session.LocationMatcherResult;
import com.mapbox.navigation.core.trip.session.LocationObserver;
import com.mapbox.navigation.core.trip.session.RouteProgressObserver;
import com.mapbox.navigation.core.trip.session.VoiceInstructionsObserver;
import com.mapbox.navigation.ui.base.util.MapboxNavigationConsumer;
import com.mapbox.navigation.ui.maps.location.NavigationLocationProvider;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineApi;
import com.mapbox.navigation.ui.maps.route.line.api.MapboxRouteLineView;
import com.mapbox.navigation.ui.maps.route.line.model.MapboxRouteLineOptions;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineError;
import com.mapbox.navigation.ui.maps.route.line.model.RouteLineResources;
import com.mapbox.navigation.ui.maps.route.line.model.RouteSetValue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Objects;

import kotlin.Unit;
import kotlin.jvm.functions.Function1;

public class MainActivity extends AppCompatActivity {
    MapView mapView;
    FloatingActionButton focusLocationBtn;
    private final NavigationLocationProvider navigationLocationProvider = new NavigationLocationProvider();
    private MapboxRouteLineView routeLineView;
    private MapboxRouteLineApi routeLineApi;
    private Location location;

    private Bluetooth bluetoothEMS;
    private PowerManager.WakeLock wakeLock;


    private final LocationObserver locationObserver = new LocationObserver() {
        @Override
        public void onNewRawLocation(@NonNull Location location) {

        }

        @Override
        public void onNewLocationMatcherResult(@NonNull LocationMatcherResult locationMatcherResult) {
            Location location = locationMatcherResult.getEnhancedLocation();
            navigationLocationProvider.changePosition(location, locationMatcherResult.getKeyPoints(), null, null);
            if (focusLocation) {
                updateCamera(Point.fromLngLat(location.getLongitude(), location.getLatitude()), (double) location.getBearing());
            }
        }
    };
    private final RoutesObserver routesObserver = new RoutesObserver() {
        @Override
        public void onRoutesChanged(@NonNull RoutesUpdatedResult routesUpdatedResult) {
            routeLineApi.setNavigationRoutes(routesUpdatedResult.getNavigationRoutes(), new MapboxNavigationConsumer<Expected<RouteLineError, RouteSetValue>>() {
                @Override
                public void accept(Expected<RouteLineError, RouteSetValue> routeLineErrorRouteSetValueExpected) {
                    Style style = mapView.getMapboxMap().getStyle();
                    if (style != null) {
                        routeLineView.renderRouteDrawData(style, routeLineErrorRouteSetValueExpected);
                    }
                }
            });
        }
    };
    boolean focusLocation = true;
    private MapboxNavigation mapboxNavigation;

    private void updateCamera(Point point, Double bearing) {
        MapAnimationOptions animationOptions = new MapAnimationOptions.Builder().duration(1500L).build();
        CameraOptions cameraOptions = new CameraOptions.Builder().center(point).zoom(18.0).bearing(bearing).pitch(0.0)
                .padding(new EdgeInsets(0.0, 0.0, 0.0, 0.0)).build();

        getCamera(mapView).flyTo(cameraOptions, animationOptions);
    }

    private final OnMoveListener onMoveListener = new OnMoveListener() {
        @Override
        public void onMoveBegin(@NonNull MoveGestureDetector moveGestureDetector) {
            focusLocation = false;
            getGestures(mapView).removeOnMoveListener(this);
            focusLocationBtn.show();
        }

        @Override
        public boolean onMove(@NonNull MoveGestureDetector moveGestureDetector) {
            return false;
        }

        @Override
        public void onMoveEnd(@NonNull MoveGestureDetector moveGestureDetector) {

        }
    };
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                Toast.makeText(MainActivity.this, "Permission granted! Restart this app", Toast.LENGTH_SHORT).show();
            }
        }
    });


    private boolean righting = false;
    private boolean lefting = false;

    private ImageView displayTurn;
    private ImageView displayTurnColor;

    private void replayOriginLocation() {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location lastKnownLocation = result.getLastLocation();
                if (lastKnownLocation != null) {
                    location = lastKnownLocation;
                    List<ReplayEventUpdateLocation> locationUpdates = new ArrayList<>();
                    locationUpdates.add(ReplayRouteMapper.mapToUpdateLocation(new Date().getTime(), Point.fromLngLat(location.getLongitude(), location.getLatitude())));

                    mapboxReplayer.pushEvents(locationUpdates);
                    mapboxReplayer.playFirstLocation();
                    mapboxReplayer.playbackSpeed(5.0);
                } else {
                    Log.e("ReplayLocation", "Last known location is null");
                }
            }

            @Override
            public void onFailure(@NonNull Exception exception) {
                Log.e("ReplayLocation", "Failed to get last known location: " + exception.getMessage());
            }
        });
    }




    private boolean tapper = false;
    //end
    private MapboxReplayer mapboxReplayer = new MapboxReplayer();
    private ReplayLocationEngine replayLocationEngine = new ReplayLocationEngine(mapboxReplayer);

    private SingletonData mySingletonData;
    private VoiceInstructionsObserver voiceInstructionsObserver;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK, "MyApp::MyWakelockTag");
        wakeLock.acquire();
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);


        Singleton mySingleton = Singleton.getInstanceBLE(this);
        bluetoothEMS = mySingleton.getMyBLEObject();


        mySingletonData = SingletonData.getInstance();
        bluetoothEMS = mySingleton.getMyBLEObject();

        mapView = findViewById(R.id.mapView);
        focusLocationBtn = findViewById(R.id.focusLocation);
        //Start copy
//        end
        MapboxRouteLineOptions options = new MapboxRouteLineOptions.Builder(this).withRouteLineResources(new RouteLineResources.Builder().build())
                .withRouteLineBelowLayerId(LocationComponentConstants.LOCATION_INDICATOR_LAYER).build();
        routeLineView = new MapboxRouteLineView(options);
        routeLineApi = new MapboxRouteLineApi(options);

        displayTurn = findViewById(R.id.imageView);
        displayTurnColor = findViewById(R.id.imageViewUp);

        NavigationOptions navigationOptions = new NavigationOptions.Builder(this).accessToken(getString(R.string.mapbox_access_token))
                .locationEngine(replayLocationEngine)
                        .build();
        replayOriginLocation();
        MapboxNavigationApp.setup(navigationOptions);
        mapboxNavigation = new MapboxNavigation(navigationOptions);

        mapboxNavigation.registerRoutesObserver(routesObserver);

        mapboxNavigation.registerLocationObserver(locationObserver);
        mapboxNavigation.registerRouteProgressObserver(routeProgressObserver);

        //Start copy

        // end

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                activityResultLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }

        if (ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION);
            activityResultLauncher.launch(Manifest.permission.ACCESS_COARSE_LOCATION);
        } else {
            mapboxNavigation.startTripSession();
        }

        focusLocationBtn.hide();
        LocationComponentPlugin locationComponentPlugin = getLocationComponent(mapView);
        getGestures(mapView).addOnMoveListener(onMoveListener);
        //start copy

        //end
        mapView.getMapboxMap().loadStyleUri(Style.MAPBOX_STREETS, new Style.OnStyleLoaded() {
            @Override
            public void onStyleLoaded(@NonNull Style style) {
                mapView.getMapboxMap().setCamera(new CameraOptions.Builder().zoom(18.0).build());
                locationComponentPlugin.setEnabled(true);
                locationComponentPlugin.setLocationProvider(navigationLocationProvider);
                getGestures(mapView).addOnMoveListener(onMoveListener);
                locationComponentPlugin.updateSettings(new Function1<LocationComponentSettings, Unit>() {
                    @Override
                    public Unit invoke(LocationComponentSettings locationComponentSettings) {
                        locationComponentSettings.setEnabled(true);
                        locationComponentSettings.setPulsingEnabled(true);
                        return null;
                    }
                });
                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.location_pin);
                AnnotationPlugin annotationPlugin = AnnotationPluginImplKt.getAnnotations(mapView);
                PointAnnotationManager pointAnnotationManager = PointAnnotationManagerKt.createPointAnnotationManager(annotationPlugin, mapView);
                addOnMapClickListener(mapView.getMapboxMap(), new OnMapClickListener() {
                    @Override
                    public boolean onMapClick(@NonNull Point point) {
                        if (tapper){
                            destReached = false;
                            pointAnnotationManager.deleteAll();
                            PointAnnotationOptions pointAnnotationOptions = new PointAnnotationOptions().withTextAnchor(TextAnchor.CENTER).withIconImage(bitmap)
                                    .withPoint(point);
                            pointAnnotationManager.create(pointAnnotationOptions);
                            fetchRoute(point);
                        }
                        return true;
                    }
                });

                focusLocationBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        focusLocation = true;
                        getGestures(mapView).addOnMoveListener(onMoveListener);
                        focusLocationBtn.hide();
                    }
                });
                // start copy

                //end
            }
        });
        double pointLatitude = getIntent().getDoubleExtra("POINT_LATITUDE", 0);
        double pointLongitude = getIntent().getDoubleExtra("POINT_LONGITUDE", 0);
        if (pointLongitude == 0 && pointLatitude == 0){
            Toast.makeText(MainActivity.this,"Set a manual location by tapping",Toast.LENGTH_SHORT);
            tapper = true;
        }else{
            Point selectedPoint = Point.fromLngLat(pointLongitude, pointLatitude);
            tapper = false;
            fetchRoute(selectedPoint);

        }


        voiceInstructionsObserver = new VoiceInstructionsObserver() {
            @Override
            public void onNewVoiceInstructions(@NonNull VoiceInstructions voiceInstructions) {
                String instructionText = voiceInstructions.announcement();
                Log.d("instruct",instructionText.toLowerCase());
                Log.d("instruct", String.valueOf(instructionText.toLowerCase().contains("turn left")));

                if (instructionText.toLowerCase().contains("turn left")){
                    lefting = true;
                }
                if (instructionText.toLowerCase().contains("turn right")){
                    righting = true;
                }
            }
        };
        mapboxNavigation.registerVoiceInstructionsObserver(voiceInstructionsObserver);

    }

    private final RouteProgressObserver routeProgressObserver = new RouteProgressObserver() {
        @Override
        public void onRouteProgressChanged(RouteProgress routeProgress) {
            String nextManeuver = getNextManeuver(routeProgress);
            if (nextManeuver != null) {
                checkManeuverDisplay(nextManeuver);
            }
        }
    };

    private String getNextManeuver(RouteProgress routeProgress) {
        if (routeProgress != null && routeProgress.getCurrentLegProgress() != null) {
            RouteStepProgress step = routeProgress.getCurrentLegProgress().getCurrentStepProgress();
            LegStep stepNext = routeProgress.getCurrentLegProgress().getUpcomingStep();

            if (step != null) {
                if (!stepNext.maneuver().type().equals("arrive")){
                    Log.d("man", String.valueOf(stepNext));
                    if (stepNext.maneuver().modifier().equals("left")){
                        if (step.getDistanceRemaining() <= mySingletonData.getleft()*5){
                            return stepNext.maneuver().modifier();
                        }else{
                            return "none";
                        }
                    }else if(stepNext.maneuver().modifier().equals("right")){
                        if (step.getDistanceRemaining() <= mySingletonData.getright()*5){
                            return stepNext.maneuver().modifier();
                        }else{
                            return "none";
                        }
                    }
                }else{
                    if (step.getDistanceRemaining() <= 5){
                        Log.d("man", "done");

                        return "done";
                    }else{
                        return "none";
                    }

                }


            }
        }
        return null;
    }

    private boolean currentLeftSig = false;
    private boolean currentRightSig = false;
    private boolean doneCurrent = false;
    private boolean destReached = false;


    private void checkManeuverDisplay(String type) {
        if (type != null) {
            if (type.equals("done")){
                if (!destReached){
                    bluetoothEMS.startAdd("done");
                    displayTurn.setImageResource(R.drawable.thumbs_up);
                    displayTurn.setVisibility(View.VISIBLE);
                    displayTurnColor.setImageResource(R.drawable.thumbs_up_transparent);
                    displayTurnColor.setVisibility(View.VISIBLE);
                    CountDownTimer countDownTimer = new CountDownTimer(mySingletonData.getdone()*1000, 1000) {
                        @Override
                        public void onTick(long millisUntilFinished) {
                        }

                        @Override
                        public void onFinish() {
                            displayTurn.setVisibility(View.GONE);
                            displayTurnColor.setVisibility(View.GONE);
                        }
                    };
                    destReached = true;
                    countDownTimer.start();

                }
            } else if(type.equals("left")) {
                displayTurn.setImageResource(R.drawable.pointing_left);
                displayTurn.setVisibility(View.VISIBLE);
                displayTurnColor.setImageResource(R.drawable.pointing_left_transparent);
                displayTurnColor.setVisibility(View.VISIBLE);
                if (!currentLeftSig){
                    bluetoothEMS.startAdd("left");

                }
                currentLeftSig = true;
                doneCurrent = false;
                currentRightSig = false;
            } else if (type.equals("right")) {
                displayTurn.setImageResource(R.drawable.pointing_right);
                displayTurn.setVisibility(View.VISIBLE);
                displayTurnColor.setImageResource(R.drawable.pointing_right_transparent);
                displayTurnColor.setVisibility(View.VISIBLE);

                if (!currentRightSig){
                    bluetoothEMS.startAdd("right");
                }
                currentRightSig = true;
                doneCurrent = false;
                currentLeftSig = false;
            } else{
                displayTurn.setVisibility(View.GONE);
                displayTurnColor.setVisibility(View.GONE);
                currentLeftSig = false;
                currentRightSig = false;
                if (!doneCurrent){
                    bluetoothEMS.startAdd("stop");

                }
                doneCurrent = true;

            }
        }
    }



    @SuppressLint("MissingPermission")
    private void fetchRoute(Point point) {
        LocationEngine locationEngine = LocationEngineProvider.getBestLocationEngine(MainActivity.this);
        locationEngine.getLastLocation(new LocationEngineCallback<LocationEngineResult>() {
            @Override
            public void onSuccess(LocationEngineResult result) {
                Location location = result.getLastLocation();
                RouteOptions.Builder builder = RouteOptions.builder();
                Point origin = Point.fromLngLat(Objects.requireNonNull(location).getLongitude(), location.getLatitude());
                builder.coordinatesList(Arrays.asList(origin, point));
                builder.alternatives(false);
                builder.profile(DirectionsCriteria.PROFILE_WALKING);
                builder.bearingsList(Arrays.asList(Bearing.builder().angle(location.getBearing()).degrees(45.0).build(), null));
                applyDefaultNavigationOptions(builder);

                mapboxNavigation.requestRoutes(builder.build(), new NavigationRouterCallback() {
                    @Override
                    public void onRoutesReady(@NonNull List<NavigationRoute> list, @NonNull RouterOrigin routerOrigin) {
                        mapboxNavigation.setNavigationRoutes(list);
                        focusLocationBtn.performClick();
                    }

                    @Override
                    public void onFailure(@NonNull List<RouterFailure> list, @NonNull RouteOptions routeOptions) {
                        Toast.makeText(MainActivity.this, "Route request failed", Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCanceled(@NonNull RouteOptions routeOptions, @NonNull RouterOrigin routerOrigin) {

                    }
                });
            }

            @Override
            public void onFailure(@NonNull Exception exception) {

            }
        });


    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapboxNavigation.onDestroy();
        mapboxNavigation.unregisterRoutesObserver(routesObserver);
        mapboxNavigation.unregisterLocationObserver(locationObserver);
        mapboxNavigation.unregisterRouteProgressObserver(routeProgressObserver);

        mapboxNavigation.unregisterVoiceInstructionsObserver(voiceInstructionsObserver);

    }
}
