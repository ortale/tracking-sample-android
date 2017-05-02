package br.com.rodolfoortale.myroutes.view.dialog;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

import java.util.List;

import br.com.rodolfoortale.myroutes.R;
import br.com.rodolfoortale.myroutes.controller.JourneysController;
import br.com.rodolfoortale.myroutes.controller.PositionsController;
import br.com.rodolfoortale.myroutes.model.Journey;
import br.com.rodolfoortale.myroutes.util.ProjectUtil;
import br.com.rodolfoortale.myroutes.view.activities.MapsActivity;

/**
 * Dialog shows details of a journey such as name, date and time.
 */

public class JourneyDetailDialog extends DialogFragment implements OnMapReadyCallback {
    private static final float ZOOM = 14.0f;
    private Journey journey;
    private SupportMapFragment mapFragment;

    /**
     * Pass journey object as argument.
     *
     * @param journey
     */
    public static JourneyDetailDialog newInstance(Journey journey) {
        JourneyDetailDialog journeyDetailDialog = new JourneyDetailDialog();

        Bundle args = new Bundle();
        args.putSerializable(ProjectUtil.keyJourney, journey);
        journeyDetailDialog.setArguments(args);

        return journeyDetailDialog;
    }

    /**
     * Get object argument.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        journey = (Journey) getArguments().getSerializable(ProjectUtil.keyJourney);
    }

    /**
     * Add map fragment.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_journey_detail, null);

        ImageButton btnClose = (ImageButton) view.findViewById(R.id.btn_close);
        TextView lbJourneyName = (TextView) view.findViewById(R.id.lb_journey_name);
        TextView lbJourneyStart = (TextView) view.findViewById(R.id.lb_journey_start);
        TextView lbJourneyEnd = (TextView) view.findViewById(R.id.lb_journey_end);

        lbJourneyName.setText(journey.getName());
        lbJourneyStart.setText(ProjectUtil.parseDateTime(journey.getStartTime()));
        lbJourneyEnd.setText(ProjectUtil.parseDateTime(journey.getEndTime()));

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        mapFragment = (SupportMapFragment) getFragmentManager().findFragmentById(R.id.map_detail);
        mapFragment.getMapAsync(this);

        return view;
    }

    /**
     * Remove map fragment to avoid double fragment inflate.
     *
     * @param dialog
     */
    @Override
    public void onDismiss(DialogInterface dialog) {
        super.onDismiss(dialog);

        if (mapFragment != null) {
            getFragmentManager().beginTransaction().remove(mapFragment).commit();
        }
    }

    /**
     * On map ready, plots points to map.
     *
     * @param googleMap
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        PositionsController positionsController = new PositionsController(getActivity());

        final List<LatLng> positions = positionsController.getAll(journey);

        if (positions != null && !positions.isEmpty()) {
            PolylineOptions polylineOptions = new PolylineOptions()
                    .width(5)
                    .geodesic(true)
                    .visible(true)
                    .color(Color.RED);
            polylineOptions.addAll(positions);
            googleMap.addPolyline(polylineOptions);

            // Getting the last position
            LatLng latLngFinish = positions.get(positions.size() - 1);

            // Getting the initial position
            LatLng latLngStart = positions.get(0);

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngFinish, ZOOM));

            googleMap.addMarker(new MarkerOptions().position(latLngStart).title(getString(R.string.lb_start_place)));
            googleMap.addMarker(new MarkerOptions().position(latLngFinish).title(getString(R.string.lb_finish_place)));
        }
    }
}