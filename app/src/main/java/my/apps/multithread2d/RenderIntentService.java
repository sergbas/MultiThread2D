package my.apps.multithread2d;

import android.app.IntentService;
import android.content.Intent;
import android.os.SystemClock;
import android.text.format.DateFormat;
import android.util.Log;

public class RenderIntentService extends IntentService {
    public static final String PARAM_IN_MSG = "imsg";
    public static final String PARAM_STATUS_MSG = "status";
    public static final String PARAM_OUT_MSG = "omsg";
    private static final String TAG = RenderIntentService.class.getSimpleName();

    private boolean isWorking = false;

    public RenderIntentService() {
        super("RenderIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e(TAG, "onHandleIntent");
        String msg = intent.getStringExtra(PARAM_IN_MSG);
        String state = intent.getStringExtra(PARAM_STATUS_MSG);

        if(state.equals("start")) isWorking = true;
        if(state.equals("stop")) isWorking = false;

        Log.e(TAG, "state:" + state + "; isWorking:" + isWorking);

        for(int i=0; i<10; i++) {
            SystemClock.sleep(1000); // 30 seconds
            String resultTxt = msg + " "
                    + DateFormat.format("yyyy/MM/dd h:mm:ss", System.currentTimeMillis());

            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
            broadcastIntent.putExtra(PARAM_OUT_MSG, resultTxt);
            sendBroadcast(broadcastIntent);
        }
    }

}