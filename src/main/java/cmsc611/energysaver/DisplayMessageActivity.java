package cmsc611.energysaver;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;


public class DisplayMessageActivity extends AppCompatActivity {
    public static final String TAG = "TESTMESSAGE: ";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_message);
        //get the intent that initiated this activity
        Intent intent = getIntent();

        //get the message from the intent (like a key-value storage style)
        String message = intent.getStringExtra(Energy.EXTRA_MESSAGE);
        //Log.d(TAG, message);
        //create and edit the text view
        TextView textView = (TextView) findViewById(R.id.display_text);
        //Log.d(TAG, textView.toString());
        float size = 40;
        textView.setTextSize(size);

        //set the text view
        textView.setText(message);
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
