package com.example.timeleftnotifier;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.temporal.ChronoField;
import java.util.Calendar;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Icon;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {
    private static final int NOTIFICATION_ID = 1945;
    private static final String CHANNEL_ID = "TimeLeftNotifier";
    final Handler handler=new Handler();
    Notification.Builder builder = null;
    NotificationManagerCompat notificationManagerCompat = null;
    TextView textView;
    int startHour, endHour;
    Boolean showNotification = true;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = (TextView) findViewById(R.id.TView);
        builder = new Notification.Builder(this, CHANNEL_ID);
        builder.setOngoing(true);
        notificationManagerCompat = NotificationManagerCompat.from(this);

            //update current time view after every 1 seconds
            handler.postDelayed(updateTask,1000);
    }

    public void displayNotification(String shortText, String fullText) {
        //convert text to bitmap
        Bitmap bitmap = createBitmapFromString(shortText.trim());

        //setting bitmap to staus bar icon.
        builder.setSmallIcon(Icon.createWithBitmap(bitmap));

        builder.setContentText(fullText);

        notificationManagerCompat.notify(NOTIFICATION_ID, builder.build());


        createNotificationChannel();
    }

    private void createNotificationChannel() {
        CharSequence name = "testing";
        String description = "i'm testing this notification";
        int importance = NotificationManager.IMPORTANCE_LOW;
        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
        channel.setDescription(description);
        // Register the channel with the system; you can't change the importance
        // or other notification behaviors after this
        NotificationManager notificationManager = getSystemService(NotificationManager.class);
        assert notificationManager != null;
        notificationManager.createNotificationChannel(channel);
    }

    private Bitmap createBitmapFromString(String inputNumber) {
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        paint.setTextSize(100);
        paint.setTextAlign(Paint.Align.CENTER);
        paint.setColor(Color.WHITE);
        Rect textBounds = new Rect();
        paint.getTextBounds(inputNumber, 0, inputNumber.length(), textBounds);

        Bitmap bitmap = Bitmap.createBitmap(textBounds.width() + 10, 70,
                Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        canvas.drawText(inputNumber, textBounds.width() / 2 + 5, 70, paint);
        return bitmap;
    }


    final Runnable updateTask=new Runnable() {
        @Override
        public void run() {
            if(showNotification)
                updateCurrentTime();
            handler.postDelayed(this,1000);
        }
    };



    public void updateCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        startHour = 4;
        endHour = 20;

        int startSecond = hourToSeconds(startHour);
        int endSecond = hourToSeconds(endHour);
        long currentSecond =  LocalTime.now().get(ChronoField.SECOND_OF_DAY);//.of(4, 0).get(ChronoField.SECOND_OF_DAY) + 6;
        String result = "Done";
        if(currentSecond >= startSecond || currentSecond <= endSecond){

            int secondDiff = startSecond - endSecond;
            double secondValue = (double) 100 / secondDiff;

            double currentSecondPercent = 100 - (secondValue * (startSecond - currentSecond));

            if(currentSecondPercent > 0)
            {
                showNotification = true;
                result = String.format("%.2f", currentSecondPercent) + "%";
                displayNotification(result.substring(0,2) + "%", result);
            }
            else
                showNotification = false;
        }

        textView.setText(result);
    }

    public int hourToSeconds(int hour){
        return hour * 60 * 60;
    }

}