package my.apps.multithread2d;

import android.app.IntentService;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.format.DateFormat;
import android.util.Log;
import android.widget.ImageView;

import java.util.Timer;

public class RenderIntentService extends IntentService {
    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_STATUS_MSG = "status";
    public static final String PARAM_OUT_MSG = "omsg";
    private static final String TAG = RenderIntentService.class.getSimpleName();

    private boolean isWorking = false;
    private Handler h;
    private ImageView image;

    public RenderIntentService() {
        super("RenderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent");
        String msg = intent.getStringExtra(PARAM_IN_MSG);
        String state = intent.getStringExtra(PARAM_STATUS_MSG);

        if (state.equals("start")) isWorking = true;
        if (state.equals("stop")) isWorking = false;

        Log.e(TAG, "state:" + state + "; isWorking:" + isWorking);

        image = MainActivity.image;

        MainActivity.bitmap = Bitmap.createBitmap(320, 480, Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(MainActivity.bitmap);


        final Paint p = new Paint();
        p.setAntiAlias(false);
        p.setStyle(Paint.Style.FILL_AND_STROKE);
        p.setStrokeWidth(1);

        for (int j = 0; j < 320; j++) {
            for (int i = 0; i < 320; i++) {
                int col = (int)System.currentTimeMillis();
                p.setColor(col);

                canvas.drawPoint(i, j, p);

                if(i % 8 == 0 && j % 8 == 0) {
                    Intent broadcastIntent = new Intent();
                    broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
                    broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                    broadcastIntent.putExtra(PARAM_OUT_MSG, i + ", " + j);
                    sendBroadcast(broadcastIntent);
                }
            }
        }
    }


}