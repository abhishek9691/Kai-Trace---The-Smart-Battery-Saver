package cmsc611.energysaver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;
import java.util.HashMap;

/**
 * Created by abhi on 12/3/2015.
 */
public class KaiAlarm extends BroadcastReceiver {

    public static final int REQUEST_CODE = 12345;
    public static final String ACTION = "cmsc611.energysaver";

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent i = new Intent(context, KaiService.class);
        i.putExtra("TEST", "Every 30 for now");
        context.startService(i);
    }
}
