package com.example.zotee.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import com.example.zotee.R;
import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.NoteEntity;

import java.sql.Timestamp;
import java.util.Date;
// import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NotificationService extends Service {

    @Inject
    DataRepository dataRepository;

    private static final String CHANNEL_ID = "NOTIFICATION_CHANNEL";
    private static final String GROUP_KEY_WORK_EVENT = "com.android.example.WORK_EVENT";
    private static final int NOTIFICATION_ID = 1000;
    public int counter=0;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private void pushNotification(String address, String time, Integer id)
    {
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        StringBuilder message = new StringBuilder();
        message.append("You have an appointment at ").append(time).append(" at ").append(address);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, CHANNEL_ID);

        Notification notification = mBuilder
                .setSmallIcon(R.drawable.ic_clock)
                .setContentTitle("Zotee")
                .setContentText(message)
                .setPriority(NotificationManager.IMPORTANCE_MIN)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setGroup(GROUP_KEY_WORK_EVENT)
                .setAutoCancel(true)
                .build();
        startForeground(id, notification);
    }

    private void createNotificationChannel() {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(com.example.zotee.R.string.app_name);
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(CHANNEL_ID, name, importance);
            channel.setDescription(description);
            // Register the channel with the system; you can't change the importance
            // or other notification behaviors after this
            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);
        startTimer();
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();

        /*Intent lintent = new Intent(this, LocationService.class);
        context.stopService(lintent);*/
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, NotificationBroadCastReceiver.class);
        this.sendBroadcast(broadcastIntent);
    }

    private Timer timer;
    private TimerTask timerTask;
    private Handler handler;
    public void startTimer() {
        timer = new Timer();
        timerTask = new TimerTask() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    createNotificationWithData();
                }
            }
        };
        timer.schedule(timerTask, 0, 1000 * 5);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private  void createNotificationWithData() {

        createNotificationChannel();

        Calendar calCurrentTime = Calendar.getInstance();
        Calendar calDepreciationTime = Calendar.getInstance();
        Calendar calEndTime = Calendar.getInstance();
        calEndTime.add(Calendar.MINUTE, 30);
        calCurrentTime.add(Calendar.MINUTE, -1);
        calDepreciationTime.add(Calendar.MINUTE, 1);

        // Time before 30 minutes
        Date currentTime = new Date(calCurrentTime.getTime().getTime());
        Date endTime = new Date(calEndTime.getTime().getTime());
        Date depreciationTime = new Date(calDepreciationTime.getTime().getTime());

        List<NoteEntity> lstData =  dataRepository.loadNotes();
        List<NoteEntity> filterData = filterNoteWithTime(currentTime, endTime, lstData);
        NoteEntity currentData = getNoteCurrentTime(currentTime, depreciationTime, lstData);

        Log.i("Start notification", " Notification " + counter++);
        for (NoteEntity item : filterData){
            int id = NOTIFICATION_ID;
            id++;
            pushNotification(item.getLocationName(), item.getTimeText(), id);
        }

        /*if(counter % 3 == 0) {
            NotificationManager mNotificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.deleteNotificationChannel(CHANNEL_ID);
        }*/
    }

    private List<NoteEntity> filterNoteWithTime(Date startTime, Date endTime, List<NoteEntity> lstData){
        List<NoteEntity> result = new ArrayList<>();
        if(lstData.size() == 0) return result;

        Timestamp currentTimestamp = new Timestamp(startTime.getTime());
        Timestamp endTimestamp = new Timestamp(endTime.getTime());
        for (NoteEntity item : lstData) {
            Date date = item.getDate();
            if(date == null) continue;

            Timestamp timestamp = new Timestamp(date.getTime());
            if(timestamp.getTime() > currentTimestamp.getTime() && timestamp.getTime() <= endTimestamp.getTime()){
                result.add(item);
            }
        }
        return result;
    }

    private NoteEntity getNoteCurrentTime(Date currentTime, Date depreciationTime, List<NoteEntity> lstData){
       if(lstData.size() == 0) return null;

        Timestamp currentTimestamp = new Timestamp(currentTime.getTime());
        Timestamp depreciationTimestamp = new Timestamp(depreciationTime.getTime());
        for (NoteEntity item : lstData) {

            Date date = item.getDate();
            if(date == null) continue;

            Timestamp timestamp = new Timestamp(date.getTime());
            if(timestamp.getTime() > currentTimestamp.getTime() && timestamp.getTime() <= depreciationTimestamp.getTime()){
               return item;
            }
        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void cancelNotification(Context ctx, int notifyId) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.deleteNotificationChannel(CHANNEL_ID);
    }

    public void stoptimertask() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
