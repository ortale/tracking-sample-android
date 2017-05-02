package br.com.rodolfoortale.myroutes.view.dialog;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.ArrayList;

import br.com.rodolfoortale.myroutes.R;
import br.com.rodolfoortale.myroutes.model.Journey;
import br.com.rodolfoortale.myroutes.util.ProjectUtil;

/**
 * Dialog shows a list of journeys by the user.
 */

public class JourneysListDialogFragment extends DialogFragment {
    private ArrayList<Journey> journeyList;

    /**
     * Pass journey list as argument.
     *
     * @param journeyList
     */
    public static JourneysListDialogFragment newInstance(ArrayList<Journey> journeyList) {
        JourneysListDialogFragment journeysListDialogFragment = new JourneysListDialogFragment();

        Bundle args = new Bundle();
        args.putSerializable(ProjectUtil.keyJourneyList, journeyList);
        journeysListDialogFragment.setArguments(args);

        return journeysListDialogFragment;
    }

    /**
     * Get list argument.
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        journeyList = (ArrayList<Journey>) getArguments().getSerializable(ProjectUtil.keyJourneyList);
    }

    /**
     * On list view item selected, call dialog to show its details.
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialog_journeys_list, null);

        ImageButton btnClose = (ImageButton) view.findViewById(R.id.btn_close);
        final ListView lvJourneys = (ListView) view.findViewById(R.id.lv_journeys);

        ArrayAdapter adapter = new ArrayAdapter<>(getActivity(), android.R.layout.simple_list_item_1, journeyList);
        lvJourneys.setAdapter(adapter);

        lvJourneys.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Journey journey = (Journey) lvJourneys.getItemAtPosition(i);
                JourneyDetailDialog journeyDetailDialog = JourneyDetailDialog.newInstance(journey);
                journeyDetailDialog.setRetainInstance(true);
                journeyDetailDialog.show(getFragmentManager(), JourneyDetailDialog.class.getSimpleName());
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        return view;
    }
}
