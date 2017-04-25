package br.com.rodolfoortale.myroutes.view.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.List;

import br.com.rodolfoortale.myroutes.R;
import br.com.rodolfoortale.myroutes.model.Journey;
import br.com.rodolfoortale.myroutes.util.ProjectUtil;

/**
 * Dialog shows details of a journey such as name, date and time.
 */

public class JourneyDetailDialog extends Dialog {
    private Dialog dialog;

    /**
     * Sets default dialog properties and layout.
     *
     * @param activity
     * @param journey
     */
    public JourneyDetailDialog(final Activity activity, Journey journey) {
        super(activity);

        dialog = ProjectUtil.getDefaultDialogProperties(this, R.layout.dialog_journey_detail);
        dialog.setCancelable(false);

        ImageButton btnClose = (ImageButton) dialog.findViewById(R.id.btn_close);
        TextView lbJourneyName = (TextView) dialog.findViewById(R.id.lb_journey_name);
        TextView lbJourneyStart = (TextView) dialog.findViewById(R.id.lb_journey_start);
        TextView lbJourneyEnd = (TextView) dialog.findViewById(R.id.lb_journey_end);

        lbJourneyName.setText(journey.getName());
        lbJourneyStart.setText(ProjectUtil.parseDateTime(journey.getStartTime()));
        lbJourneyEnd.setText(ProjectUtil.parseDateTime(journey.getEndTime()));

        btnClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}