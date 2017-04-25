package br.com.rodolfoortale.myroutes.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;

import java.util.List;

import br.com.rodolfoortale.myroutes.R;
import br.com.rodolfoortale.myroutes.model.Journey;
import br.com.rodolfoortale.myroutes.util.ProjectUtil;

/**
 * Dialog shows a list of journeys by the user.
 */

public class JourneysListDialog extends Dialog {
    private Dialog dialog;

    /**
     * Sets default dialog properties and layout.
     *
     * @param activity
     * @param journeyList
     */
    public JourneysListDialog(final Activity activity, final List<Journey> journeyList) {
        super(activity);

        dialog = ProjectUtil.getDefaultDialogProperties(this, R.layout.dialog_journeys_list);
        dialog.setCancelable(false);

        ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.btn_close);
        final ListView lvJourneys = (ListView) dialog.findViewById(R.id.lv_journeys);

        ArrayAdapter adapter = new ArrayAdapter<>(activity, android.R.layout.simple_list_item_1, journeyList);
        lvJourneys.setAdapter(adapter);

        lvJourneys.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Journey journey = (Journey) lvJourneys.getItemAtPosition(i);
                new JourneyDetailDialog(activity, journey);
            }
        });

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
