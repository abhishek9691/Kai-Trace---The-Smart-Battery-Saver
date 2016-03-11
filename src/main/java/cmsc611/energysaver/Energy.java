package cmsc611.energysaver;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.media.AudioManager;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.TextView;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;


public class Energy extends AppCompatActivity {

    public final static String EXTRA_MESSAGE = "cmsc611.energysaver.MESSAGE";
    boolean serviceTurnedOn = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_energy);
        TextView toggle = (TextView) findViewById(R.id.toggle_service_text);
        String serviceText = "Is the service on? -> ";

        Intent intent = new Intent(getApplicationContext(), KaiAlarm.class);
        serviceTurnedOn =  (PendingIntent.getBroadcast(this, KaiAlarm.REQUEST_CODE,
                intent, PendingIntent.FLAG_NO_CREATE) != null);
        if(serviceTurnedOn){
            serviceText += "Yes";
        } else {
            serviceText += "No";
        }
        toggle.setText(serviceText);

    }

    /*public void sendMessage(View view){
        //Do something in response to the button
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }*/

    public void toggleService(View view){
        Intent backgroundIntent = new Intent(this, KaiService.class);
        backgroundIntent.setData(Uri.parse("Abhishek.Sethi"));
        Intent intent = new Intent(getApplicationContext(), KaiAlarm.class);
        //a pendingIntent that will be triggered when the alarm goes off
        final PendingIntent pendingIntent = PendingIntent.getBroadcast(this, KaiAlarm.REQUEST_CODE,
                intent, PendingIntent.FLAG_UPDATE_CURRENT);
        AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);

        if(!serviceTurnedOn) {
            //Setup a periodic alarm
            //the time when the alarm should start (immediate in this case)
            long millis = System.currentTimeMillis();
            //interval from start time/last alarm till next occurence
            long millisInterval = 1200000;
            //set the alarm
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, millis,
                    millisInterval, pendingIntent);
            serviceTurnedOn = true;
        } else {
            //first turn off the alarm
            alarm.cancel(pendingIntent);
            serviceTurnedOn = false;
        }

        TextView toggle = (TextView) findViewById(R.id.toggle_service_text);
        String serviceText = "Is the service on? -> ";
        if(serviceTurnedOn){
            serviceText += "Yes";
        } else {
            serviceText += "No";
        }
        toggle.setText(serviceText);
    }

    public void minimizeAllSensors(View view){
        //manages bluetooth
        BluetoothAdapter bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        //manages wifi
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        //manages sound profile
        AudioManager sound = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        //sets the screen brightness
        Settings.System.putInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS, 20);

        if(bluetoothAdapter != null){
            //device does support Bluetooth
            /*if(!bluetoothAdapter.isEnabled()){
                bluetoothAdapter.enable();
            } else {
                bluetoothAdapter.disable();
            }*/
            if(bluetoothAdapter.isEnabled()){
                bluetoothAdapter.disable();
            }
        }
        if(wifiManager != null){
            //able to get a wifimanager
            if(wifiManager.isWifiEnabled()){
                wifiManager.setWifiEnabled(false);
            }
        }
        if(sound != null){
            //sets it to silent (not vibrate)
            sound.setRingerMode(AudioManager.RINGER_MODE_SILENT);
        }

        if(ContentResolver.getMasterSyncAutomatically()) {
            ContentResolver.setMasterSyncAutomatically(false);
        }

        //kills all the background processes of other apps
        List<ApplicationInfo> packages;
        PackageManager pm;
        pm = getPackageManager();
        //get a list of installed apps.
        packages = pm.getInstalledApplications(0);

        ActivityManager mActivityManager = (ActivityManager)this.getSystemService(Context.ACTIVITY_SERVICE);

        for (ApplicationInfo packageInfo : packages) {
            if((packageInfo.flags & ApplicationInfo.FLAG_SYSTEM)==1) continue;
            if(packageInfo.packageName.equals("mypackage")) continue;
            mActivityManager.killBackgroundProcesses(packageInfo.packageName);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_energy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
