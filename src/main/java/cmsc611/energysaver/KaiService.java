package cmsc611.energysaver;

import android.app.ActivityManager;
import android.app.IntentService;
import android.bluetooth.BluetoothAdapter;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.PowerManager;
import android.provider.Settings;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

/**
 * Created by abhi on 12/3/2015.
 */
public class KaiService extends IntentService {
    private static final double threshold = 0.35;
    public static HashMap<Integer, HashMap<Integer, HourSplit>> sensors =
            new HashMap<Integer, HashMap<Integer, HourSplit>>(7);
    public KaiService() {
        super("KaiService");
    }
    /*
            For Initial Hashing for days:
            0 = Monday
            1 = Tuesday
            2 = Wednesday
            3 = Thursday
            4 = Friday
            5 = Saturday
            6 = Sunday

            For sensors:
            0 = Auto-Sync
            1 = WiFi
            2 = Bluetooth
            3 = Sound Profile
            4 = Brightness
            5 = Screen on/off

    */
    @Override
    protected void onHandleIntent(Intent intent) {
        //do work here when the service is created
        //must perform update on sensors everytime it is called
        //initialize date variables
        if(!sensors.containsKey(Calendar.MONDAY)){
            //setup the sensors
            HourSplit minuteWithSensors = new HourSplit();
            //setup the hours in the day
            HashMap<Integer, HourSplit> daysToSensors = new HashMap<Integer, HourSplit>();
            for(int x = 0; x < 24; x++){
                daysToSensors.put(x, minuteWithSensors);
            }
            //set the days of the week
            sensors.put(Calendar.MONDAY, daysToSensors);
            sensors.put(Calendar.TUESDAY, daysToSensors);
            sensors.put(Calendar.WEDNESDAY, daysToSensors);
            sensors.put(Calendar.THURSDAY, daysToSensors);
            sensors.put(Calendar.FRIDAY, daysToSensors);
            sensors.put(Calendar.SATURDAY, daysToSensors);
            sensors.put(Calendar.SUNDAY, daysToSensors);

        }
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        int day = calendar.get(Calendar.DAY_OF_WEEK);
        //initialize empty array for new values
        double[] newValues = getSensorState();
        //a debugging view tool
        System.out.println("Current Sensor Values: " + arrayToString(newValues));
        //compute the mean with the current value
        ArrayList<Double> currentValues = sensors.get(day).get(hour).getSensorList(minute);
        //perform some loop unrolling as opposed to using a loop for optimization purposes
        currentValues.set(0, new Double((currentValues.get(0)+newValues[0])/(2.0)));
        currentValues.set(1, new Double((currentValues.get(1)+newValues[1])/(2.0)));
        currentValues.set(2, new Double((currentValues.get(2)+newValues[2])/(2.0)));
        currentValues.set(3, new Double((currentValues.get(3) + newValues[3]) / (2.0)));
        currentValues.set(4, new Double((currentValues.get(4) + newValues[4]) / (2.0)));
        currentValues.set(5, new Double((currentValues.get(5) + newValues[5]) / (2.0)));
        //set th new values in the data structure
        sensors.get(day).get(hour).setNewValues(minute, currentValues);
        System.out.println("Updated Values: " + sensors.get(day).get(hour).getSensorList(minute));

        //set the sensors accordingly
        setSensors(currentValues.toArray());
    }

    private void setSensors(Object[] newValues){
        //manages bluetooth
        BluetoothAdapter bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        //manages wifi
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        //manages sound profile
        AudioManager sound = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);

        //sets the auto-sync value
        if((double)newValues[0] > threshold){
            ContentResolver.setMasterSyncAutomatically(true);
        }

        if(wifiManager != null){
            //sets the wifi state
            if((double)newValues[1] > threshold){
                wifiManager.setWifiEnabled(true);
            }
        }

        if(bluetoothAdapter != null){
            //sets the bluetooth state
            if((double)newValues[2] > threshold){
                bluetoothAdapter.enable();
            }
        }

        //does not follow threshold for ringer mode
        //sets the ringer mode
        if(sound != null){
            //sets it to silent (not vibrate)
            double soundProfile = (double)newValues[3];
            if(soundProfile < AudioManager.RINGER_MODE_VIBRATE){
                sound.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            } else if(soundProfile > AudioManager.RINGER_MODE_VIBRATE){
                sound.setRingerMode(AudioManager.RINGER_MODE_NORMAL);
            } else {
                sound.setRingerMode(AudioManager.RINGER_MODE_VIBRATE);
            }
        }

        //sets the screen brightness
        int brightness = ((new Double((double)newValues[4])).intValue());
        Settings.System.putInt(getContentResolver(),
                Settings.System.SCREEN_BRIGHTNESS, brightness);

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

    private String arrayToString(double[] x){
        String finalStr = "";
        for(int i = 0; i < x.length; i++){
            finalStr += x[i] + ", ";
        }
        return finalStr;
    }

    public double[] getSensorState(){
        //manages bluetooth
        BluetoothAdapter bluetoothAdapter= BluetoothAdapter.getDefaultAdapter();
        //manages wifi
        WifiManager wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        //manages sound profile
        AudioManager sound = (AudioManager) this.getSystemService(Context.AUDIO_SERVICE);
        //used to manage whether the screen is on or off
        PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);

        //initialize arrays to 0
        double [] sensorStates = {0, 0, 0, 0, 0, 0};

        //checks if the ContentResolver can provide whether Auto-Sync for the system is on
        if(ContentResolver.getMasterSyncAutomatically()) {
            sensorStates[0] = 1;
        }
        if(wifiManager != null){
            //check if WiFi is on
            if(wifiManager.isWifiEnabled()){
                sensorStates[1] = 1;
            }
        }
        if(bluetoothAdapter != null){
            //check if bluetooth is on
            if(bluetoothAdapter.isEnabled()){
                sensorStates[2] = 1;
            }
        }
        if(sound != null){
            //gets the ringer mode
            sensorStates[3] = (double)sound.getRingerMode();
        }
        //retrieves the brightness value
        try {
            sensorStates[4] = Settings.System.getInt(getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
        } catch (Settings.SettingNotFoundException e) {
            e.printStackTrace();
        }
        if(pm != null){
            if(pm.isScreenOn()){
                sensorStates[5] = 1;
            }
        }

        return sensorStates;
    }

}
