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
    private Canvas canvas;

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

        drawRegion(0, 0, 320, 320);
   }

    private void drawRegion(int x1, int y1, int x2, int y2) {
        image = MainActivity.image;
        MainActivity.bitmap = Bitmap.createBitmap(320, 320, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(MainActivity.bitmap);

        int sizeX = 4;
        int sizeY = 4;

        int dx = 320 / sizeX;
        int dy = 320 / sizeY;

        for(int j=0; j<sizeY; j++)
            for(int i=0; i<sizeX; i++)
                new MyRunnable(i*dx, j*dy, (i+1)*dx, (j+1)*dy);
    }


    class MyRunnable implements Runnable {
        Thread thread;
        private int x1, y1, x2, y2;

        // Конструктор
        MyRunnable(int x1, int y1, int x2, int y2) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;

            thread = new Thread(this, "Поток");
            thread.start();
        }

        public void run() {
            try {
                final Paint p = new Paint();
                p.setAntiAlias(false);
                p.setStyle(Paint.Style.FILL_AND_STROKE);
                p.setStrokeWidth(1);

                for (int j = y1; j < y2; j++) {
                    for (int i = x1; i < x2; i++) {
                        int col = (int)System.currentTimeMillis();

                        //будем рисовать красный круг на синем фоне
                        int dx = i - 160;
                        int dy = j - 160;
                        double d = Math.sqrt(dx*dx + dy*dy);
                        if(d<120) col = Color.RED;
                        else col = Color.BLUE;

                        p.setColor(col);

                        canvas.drawPoint(i, j, p);

                        if(i % 4 == 0 && j % 4 == 0) {
                            Intent broadcastIntent = new Intent();
                            broadcastIntent.setAction(MainActivity.ResponseReceiver.ACTION_RESP);
                            broadcastIntent.addCategory(Intent.CATEGORY_DEFAULT);
                            broadcastIntent.putExtra(PARAM_OUT_MSG, i + ", " + j);
                            sendBroadcast(broadcastIntent);
                        }
                    }
                }
            } catch (Exception e) {
                Log.i(TAG, "Второй поток прерван");
            }
        }
    }

}