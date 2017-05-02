package br.com.rodolfoortale.myroutes.util;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.view.KeyEvent;
import android.view.Window;
import android.widget.LinearLayout;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * Class with methods for utilities in the project.
 */

public class ProjectUtil {
    private static final String DATETIME_PATTERN = "yyyy-MM-dd HH:mm:ss";
    private static final String OUTPUT_PATTERN = "dd/MM/yyyy HH:mm:ss";

    public static final float ZOOM = 18.0f;

    // Fragments' params
    public static final String keyJourney = "journey";
    public static final String keyJourneyList = "journeyList";

    /**
     * Sets default properties of a dialog.
     *
     * @param dialog
     * @param layout
     * @return
     */
    public static Dialog getDefaultDialogProperties(Dialog dialog, int layout) {
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(layout);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
        dialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);

        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    dialog.dismiss();
                    return true;
                }
                return false;
            }
        });

        return dialog;
    }

    /**
     * Returns date String in DateTime format to store on database.
     *
     * @return
     */
    public static String getCurrentDateTimeToSave() {
        Calendar calendar = Calendar.getInstance();

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATETIME_PATTERN);

        return simpleDateFormat.format(calendar.getTime());
    }

    /**
     * Returns date String in format dd/MM/yyyy HH:mm:ss to show on view.
     *
     * @param strDate
     * @return
     */
    public static String parseDateTime(String strDate) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(DATETIME_PATTERN);
        try {
            Date date = simpleDateFormat.parse(strDate);

            SimpleDateFormat fmtOut = new SimpleDateFormat(OUTPUT_PATTERN);
            return fmtOut.format(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return null;
    }

}
