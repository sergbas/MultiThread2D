package my.apps.multithread2d;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();
    public static Bitmap bitmap;
    private ResponseReceiver receiver;
    private EditText et;
    public static ImageView image;
    private Context context;
    private int state = 0;

    public class ResponseReceiver extends BroadcastReceiver {
        public static final String ACTION_RESP = "my.apps.multithread2d.intent.action.MESSAGE_PROCESSED";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.e(TAG, "ResponseReceiver.onReceive");
            TextView result = (TextView) findViewById(R.id.text);
            String text = intent.getStringExtra(RenderIntentService.PARAM_OUT_MSG);
            result.setText(text);

            image.setImageBitmap(bitmap);

        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        context = this;

        IntentFilter filter = new IntentFilter(ResponseReceiver.ACTION_RESP);
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        receiver = new ResponseReceiver();
        registerReceiver(receiver, filter);

        et = (EditText)findViewById(R.id.msg);
        image = (ImageView) findViewById(R.id.image);

        Button btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                state = 1 - state;
                Log.e(TAG, "onClick:" + state);
                Intent msgIntent = new Intent(context, RenderIntentService.class);
                msgIntent.putExtra(RenderIntentService.PARAM_IN_MSG, et.getText().toString());
                msgIntent.putExtra(RenderIntentService.PARAM_STATUS_MSG, state == 0 ? "stop" : "start");
                startService(msgIntent);
            }
        });

    }

    @Override
    protected void onResume(){
        super.onResume();
        if(image != null && bitmap != null)
            image.setImageBitmap(bitmap);
    }


}
