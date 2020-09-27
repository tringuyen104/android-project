package com.example.zotee.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import com.example.zotee.CloudEventDetailsActivity;
import com.example.zotee.EditDetailsActivity;
import com.example.zotee.EventDetailsActivity;
import com.example.zotee.R;
import com.example.zotee.activity.HomeActivity;
import com.example.zotee.activity.VibratorNotification;
import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.NoteEntity;

import java.sql.Array;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
// import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.stream.Collectors;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class NotificationService extends Service {

    @Inject
    DataRepository dataRepository;

    private static final String CHANNEL_ID = "NOTIFICATION_CHANNEL";
    private static final int NOTIFICATION_ID = 1000;
    private int counter=0;
    private List<NotificationModel> _lstNoteDisplay = new ArrayList<>();
    private List<Integer> _minutes = new ArrayList<Integer>()
    {{
        for(int i=0; i< 20; i++){ if(i%2==0) add(i); }
    }} ;



    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("notification", "Oncreated called");
        // createNotificationChannel(CHANNEL_ID); o dau
    }

    private void pushNotification(NoteEntity noteEntity, String time, Integer id)
    {
        String channelId = CHANNEL_ID;
        createNotificationChannel(channelId);
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());
        StringBuilder message = new StringBuilder();
        message.append("Bạn còn ").append(time).append(" phút để đến cuộc hẹn tại ").append(noteEntity.getLocationName());
        NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this, channelId);

        Intent notifyIntent = new Intent(this, CloudEventDetailsActivity.class);
        Bundle bundle = new Bundle();
        bundle.putInt("id", noteEntity.getId());
        bundle.putString("event_name", noteEntity.getTitle());
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy    HH:mm");
        String d = simpleDateFormat.format(noteEntity.getDate());
        bundle.putString("Date", d);
        bundle.putString("DesName", noteEntity.getLocationName());
        bundle.putString("Content", noteEntity.getContent());
        notifyIntent.putExtras(bundle);

        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent notifyPendingIntent = PendingIntent.getActivity(
                this, 0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(notifyPendingIntent);
        Notification notification = mBuilder
                .setSmallIcon(R.drawable.ic_clock)
                .setContentTitle("Zotee")
                .setContentText(message)
                .setPriority(NotificationManager.IMPORTANCE_HIGH)
                .setCategory(Notification.CATEGORY_SERVICE)
                .setAutoCancel(true)
                .setGroup("com.example.zotee.notification.REMIND")
                .build();

        notificationManager.notify(id, notification);
    }

    private void createNotificationChannel(String channelId) {
        // Create the NotificationChannel, but only on API 26+ because
        // the NotificationChannel class is new and not in the support library
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = getString(com.example.zotee.R.string.app_name);
            String description = "description";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;
            NotificationChannel channel = new NotificationChannel(channelId, name, importance);
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
        Log.d("noti", "OnStart called");
        return START_STICKY;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        stoptimertask();
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
                    createNotificationWithData();
            }
        };
        timer.schedule(timerTask, 0, 1000*60);
    }

    private  void createNotificationWithData() {
        Calendar calCurrentTime = Calendar.getInstance();/*
        Calendar calDepreciationTime = Calendar.getInstance();*/
        Calendar calEndTime = Calendar.getInstance();
        calEndTime.add(Calendar.MINUTE, 30);
        calCurrentTime.add(Calendar.MINUTE, -1);/*
        calDepreciationTime.add(Calendar.MINUTE, 1);*/

        // Time before 30 minutes
        Date currentTime = new Date(calCurrentTime.getTime().getTime());
        Date endTime = new Date(calEndTime.getTime().getTime());

        List<NoteEntity> lstData =  dataRepository.loadNotes();
        List<NoteEntity> filterData = null;
        NoteEntity currentEvent;
        if(lstData.size() > 0) {
            Log.i("Start notification", " Notification " + counter++);
            filterData = filterNoteWithTime(currentTime, endTime, lstData);

            if(filterData.size() == 0) return;
            for (NoteEntity item : filterData) {

                Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                long subTime = item.getDate().getTime() - currentTime.getTime();
                Date date = new Date(subTime);

                int minutes = date.getMinutes();
                int notId = (int) timestamp.getTime();
                NotificationModel notificationModel = new NotificationModel();
                notificationModel.setDisplay(true);
                notificationModel.setId(item.getId());
                notificationModel.setMinutes(minutes);
                notificationModel.setNotificationId(notId);

                NotificationStatus status =  isDisplayNotification(notificationModel);
              /*  sendMessageToActivity(item.getTitle());
                Intent dialogIntent = new Intent(this, VibratorNotification.class);
                dialogIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplication().startActivity(dialogIntent);*/
                /*Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                } else {
                    //deprecated in API 26
                    v.vibrate(1000);
                }*/
                if(NotificationStatus.DISPLAY == status)
                    pushNotification(item, String.valueOf(minutes), notificationModel.getNotificationId());
                else if(NotificationStatus.REMOVE == status){
                    pushNotification(item, String.valueOf(minutes), notId);
                    removeNotificationItem(notificationModel.getId());
                   /* Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        v.vibrate(VibrationEffect.createOneShot(1000, VibrationEffect.DEFAULT_AMPLITUDE));
                    } else {
                        //deprecated in API 26
                        v.vibrate(1000);
                    }*/
                }
            }
        }
    }

    private  void sendMessageToActivity(String msg) {
        Intent intent = new Intent("AppointmentStart");
        // You can also include some extra data.
        intent.putExtra("Message", msg);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    private void removeNotificationItem(int id){
        int index = 0;
        for (int i = 0; i <  _lstNoteDisplay.size(); i++){
            NotificationModel item = _lstNoteDisplay.get(i);
            if(item.getId() == id)
            {
                index = i;
                break;
            }
        }
        _lstNoteDisplay.remove(index);
    }
    private NotificationStatus isDisplayNotification(NotificationModel model){
        boolean check = false;
        for(NotificationModel notificationModel : _lstNoteDisplay) {
            if(notificationModel.getId() == model.getId()) {
                check = true;
                break;
            }
        }

        if(!check) {
            _lstNoteDisplay.add(model);
            return NotificationStatus.DISPLAY;
        }

        if(_minutes.contains(model.getMinutes()))
            return NotificationStatus.DISPLAY;
        if (model.getMinutes() == 3 )
            return NotificationStatus.REMOVE;
        return NotificationStatus.NONE;
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
