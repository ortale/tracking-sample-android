package br.com.rodolfoortale.myroutes.view.activities;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.ArrayList;
import java.util.List;

import br.com.rodolfoortale.myroutes.R;
import br.com.rodolfoortale.myroutes.controller.JourneysController;
import br.com.rodolfoortale.myroutes.controller.PositionsController;
import br.com.rodolfoortale.myroutes.model.Journey;
import br.com.rodolfoortale.myroutes.service.LocalService;
import br.com.rodolfoortale.myroutes.util.ProjectUtil;
import br.com.rodolfoortale.myroutes.view.dialog.JourneysListDialogFragment;

/**
 * MapsActivity is responsible to provide all its map activity and user interactions.
 */
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    private final String TAG = MapsActivity.class.getSimpleName();

    // Location and Map
    private GoogleMap mMap;
    private LatLng latLng;
    private LatLng oldLatLng;
    private GoogleApiClient mGoogleApiClient;
    private PolylineOptions polylineOptions;

    // Service
    private BroadcastReceiver broadcastReceiver;
    private Intent intentService;

    // Views
    private Button btnNewJourney;
    private Button btnFinishJourney;
    private Button btnShowJourneys;
    private LinearLayout lineTrackStatus;
    private Switch swtTrack;
    private TextView txtTrack;

    // Model / Controller
    private Journey journey;
    private JourneysController journeysController;

    // Listener
    private CompoundButton.OnCheckedChangeListener onCheckedChangeListener;

    /**
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        btnNewJourney = (Button) findViewById(R.id.btn_new_journey);
        btnFinishJourney = (Button) findViewById(R.id.btn_finish_journey);
        btnShowJourneys = (Button) findViewById(R.id.btn_show_journeys);
        swtTrack = (Switch) findViewById(R.id.swt_track);
        txtTrack = (TextView) findViewById(R.id.txt_track);
        lineTrackStatus = (LinearLayout) findViewById(R.id.line_track_status);

        onCheckedChangeListener = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
                journeysController = new JourneysController(MapsActivity.this);
                journeysController.setTracking(checked);

                if (checked) {
                    onTrackingOnUI();
                    startTrackingService();
                }

                else {
                    onTrackingOffUI();
                    stopTrackingService();
                }
            }
        };
        swtTrack.setOnCheckedChangeListener(onCheckedChangeListener);

        btnNewJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog builder = new AlertDialog.Builder(MapsActivity.this).create();
                builder.setTitle(getString(R.string.tt_new_journey));
                builder.setMessage(getString(R.string.msg_new_journey));

                final EditText input = new EditText(MapsActivity.this);
                builder.setView(input);

                builder.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.lb_cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                });
                builder.setButton(DialogInterface.BUTTON_POSITIVE, getString(R.string.lb_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        swtTrack.setChecked(true);

                        String journeyName = input.getText().toString();
                        createNewJourney(journeyName);
                    }
                });

                builder.show();
            }
        });

        btnFinishJourney.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finishJourney();

                swtTrack.setChecked(false);
            }
        });

        btnShowJourneys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                journeysController = new JourneysController(MapsActivity.this);
                ArrayList<Journey> journeyList = journeysController.getAll();

                if (journeyList == null || journeyList.isEmpty()) {
                    Toast.makeText(MapsActivity.this, getString(R.string.msg_no_journey), Toast.LENGTH_SHORT).show();
                }

                else {
                    JourneysListDialogFragment journeysListDialogFragment = JourneysListDialogFragment.newInstance(journeyList);
                    journeysListDialogFragment.show(getSupportFragmentManager(), JourneysListDialogFragment.class.getSimpleName());
                }
            }
        });

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Starts Intent Service to save locations from a journey in background
     */
    private void startTrackingService() {
        intentService = new Intent(MapsActivity.this, LocalService.class);
        startService(intentService);

        finish();
    }

    /**
     * Stops Intent Service to save locations from a journey in background
     */
    private void stopTrackingService() {
        intentService = new Intent(MapsActivity.this, LocalService.class);
        stopService(intentService);
    }

    /**
     * Creates a new journey based on its name and start date and time.
     *
     * @param journeyName
     */
    private void createNewJourney(String journeyName) {
        lineTrackStatus.setVisibility(View.VISIBLE);
        btnNewJourney.setEnabled(false);
        btnFinishJourney.setEnabled(true);

        journey = new Journey();
        journey.setName(journeyName);

        String formattedDate = ProjectUtil.getCurrentDateTimeToSave();

        journey.setStartTime(formattedDate);

        journeysController = new JourneysController(MapsActivity.this);
        Long id = journeysController.createNew(journey);
        journey.setId(id);

        journeysController.setJourneyLive(true);
    }

    /**
     * Finish the journey updating the current with its ending date and time.
     *
     */
    private void finishJourney() {
        lineTrackStatus.setVisibility(View.GONE);
        btnFinishJourney.setEnabled(false);
        btnNewJourney.setEnabled(true);

        journeysController = new JourneysController(MapsActivity.this);

        String formattedDate = ProjectUtil.getCurrentDateTimeToSave();

        journey.setEndTime(formattedDate);
        journeysController.updateJourney(journey);

        journeysController.setJourneyLive(false);
    }

    /**
     * On application resume, BroadcastReceiver starts receiving data from Intent Service such as current
     * user' location (latitude and longitude).
     */
    @Override
    protected void onResume() {
        super.onResume();
        journeysController = new JourneysController(MapsActivity.this);

        swtTrack.setOnCheckedChangeListener(null);
        swtTrack.setChecked(journeysController.isTracking());
        swtTrack.setOnCheckedChangeListener(onCheckedChangeListener);

        if (journeysController.isTracking()) {
            onTrackingOnUI();
        }

        else {
            onTrackingOffUI();
        }

        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                Double latitude = (Double) intent.getExtras().get(getString(R.string.key_latitude));
                Double longitude = (Double) intent.getExtras().get(getString(R.string.key_longitude));

                latLng = new LatLng(latitude, longitude);

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, ProjectUtil.ZOOM));

                if (oldLatLng == null) {
                    oldLatLng = latLng;
                }

                if (journeysController.isTracking()) {
                    onTrackingOnUI();

                    polylineOptions = new PolylineOptions()
                            .width(5)
                            .geodesic(true)
                            .visible(true)
                            .color(Color.RED);
                    polylineOptions.add(oldLatLng, latLng);
                    oldLatLng = latLng;
                }
                mMap.addPolyline(polylineOptions);
            }
        };
        registerReceiver(broadcastReceiver,new IntentFilter(getString(R.string.key_location_update)));
    }

    /**
     * When application is destroyed, BroadcastReceiver is unregistered.
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(broadcastReceiver != null){
            unregisterReceiver(broadcastReceiver);
        }
    }

    /**
     * Handle UI information when track is on.
     */
    private void onTrackingOnUI() {
        txtTrack.setText(getString(R.string.lb_on));
        txtTrack.setTextColor(Color.RED);
    }

    /**
     * Handle UI information when track is off.
     * Minimizes application
     */
    private void onTrackingOffUI() {
        txtTrack.setText(getString(R.string.lb_off));
        txtTrack.setTextColor(Color.BLACK);
    }

    /**
     * On map ready, it sets user's current location and initial position.
     * Calls Google Api Client's listeners and services.
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMyLocationEnabled(true);

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    /**
     * On connected to Google Api Client.
     * Gets all positions stored of a specific journey to show on map.
     * @param bundle
     */
    @Override
    public void onConnected(Bundle bundle) {
        Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);

        if (mLastLocation != null) {
            double latitude = mLastLocation.getLatitude();
            double longitude = mLastLocation.getLongitude();

            LatLng location = new LatLng(latitude, longitude);
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, ProjectUtil.ZOOM));

            journeysController = new JourneysController(MapsActivity.this);

            if (journeysController.isJourneyLive()) {
                lineTrackStatus.setVisibility(View.VISIBLE);
                btnNewJourney.setEnabled(false);
                btnFinishJourney.setEnabled(true);
            }

            PositionsController positionsController = new PositionsController(MapsActivity.this);
            journeysController = new JourneysController(MapsActivity.this);
            journey = journeysController.getLast();

            if (journey != null) {
                final List<LatLng> positions = positionsController.getAll(journey);

                if (positions != null && !positions.isEmpty()) {
                    polylineOptions = new PolylineOptions()
                            .width(5)
                            .geodesic(true)
                            .visible(true)
                            .color(Color.RED);
                    polylineOptions.addAll(positions);
                    mMap.addPolyline(polylineOptions);

                    if (oldLatLng == null) {
                        oldLatLng = positions.get(positions.size() - 1);
                    }
                }
            }
        }
    }

    /**
     *
     * @param i
     */
    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     *
     * @param connectionResult
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
