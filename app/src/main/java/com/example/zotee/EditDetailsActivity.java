package com.example.zotee;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.Manifest;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.zotee.activity.HomeActivity;
import com.example.zotee.activity.fragment.model.NoteListViewModel;
import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.NoteEntity;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.SimpleFormatter;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EditDetailsActivity extends AppCompatActivity implements DatePickerDialog.OnDateSetListener, TimePickerDialog.OnTimeSetListener {
    @Inject
    DataRepository dataRepository;

    Button btnSetPath;
    Button btnCheckMap;
    Button btnDeleteNode;
    Button btnBack;
    EditText eventName;
    EditText txtTime;
    EditText source;
    // EditText source;
    EditText des;
    EditText Content;
    int day, month, year, hour, minute, t;
    int myDay, myMonth, myYear, myHour, myMinute;
    private FusedLocationProviderClient client;
    private Location currentLocation;
    private static final int REQUEST_CODE = 101;
    private String sSource = "", date = "",date1 = "";
    private Date d1 = null, d2 = null;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_edit_detail_activity);

        btnSetPath = (Button) findViewById(R.id.bt_edit_track_path);
        btnCheckMap = (Button) findViewById(R.id.btn_check_map);
        btnDeleteNode = (Button) findViewById(R.id.btn_delete_note);
        btnBack = (Button) findViewById(R.id.bt_back);
        eventName = (EditText) findViewById(R.id.event_edit_name);
        txtTime = (EditText) findViewById(R.id.time_edit);
        des = (EditText) findViewById(R.id.etDestination_edit);
        Content = (EditText) findViewById(R.id.content_edit);
        Bundle bundle = getIntent().getExtras();
        if(bundle != null)
        {
            eventName.setText(bundle.getString("event_name"));
            txtTime.setText(bundle.getString("Date"));
            date1 = bundle.getString("Date");
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy    HH:mm");
            try {
                d1 = simpleDateFormat.parse(bundle.getString("Date"));
            }
            catch (Exception e)
            {
                e.getMessage();
            }
            des.setText(bundle.getString("DesName"));
            Content.setText(bundle.getString("Content"));
        }
        txtTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Calendar calendar = Calendar.getInstance();
                year = calendar.get(Calendar.YEAR);
                month = calendar.get(Calendar.MONTH);
                day = calendar.get(Calendar.DAY_OF_MONTH);
                DatePickerDialog datePickerDialog = new DatePickerDialog(EditDetailsActivity.this, EditDetailsActivity.this, year, month, day);
                datePickerDialog.show();
            }
        });

        client = LocationServices.getFusedLocationProviderClient(this);
        getCurrentLocation();

        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(view.getContext(), HomeActivity.class);
                view.getContext().startActivity(intent);
            }
        });

        btnCheckMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String sDes = des.getText().toString().trim();
                if (sDes.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter both location!", Toast.LENGTH_SHORT).show();
                } else {
                    getCurrentLocation();
                    DisplayTrack(sSource, sDes);
                }
            }
        });

        btnDeleteNode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Xác nhận xóa");
                builder.setMessage("Bạn có muốn xóa lịch hẹn " + bundle.getString("event_name") + " này?");
                builder.setIcon(R.drawable.ic_baseline_remove_circle_24);
                builder.setPositiveButton("Xóa", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();

                    AsyncTask.execute(() -> {
                        NoteEntity entity = new NoteEntity();
                        entity.setId(bundle.getInt("id"));

                        dataRepository.delete(entity);
                    });
                    Intent intent= new Intent(view.getContext(), HomeActivity.class);
                    view.getContext().startActivity(intent);
                    }

                });
                builder.setNegativeButton("Hủy", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
                AlertDialog alert = builder.create();
                alert.show();
            }
        });

        btnSetPath.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //get value from edit text
                // sSource = source.getText().toString().trim();
                String sDes = des.getText().toString().trim();

                //check condition
                if (sDes.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Please enter both location!", Toast.LENGTH_SHORT).show();
                } else {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy    HH:mm");
                    if(myDay == 0 && myMonth == 0 && myYear == 0 && myHour == 0 && myMinute == 0)
                    {
                        date = date1;
                        AsyncTask.execute(() -> {
                            NoteEntity entity = new NoteEntity();
                            entity.setId(bundle.getInt("id"));
                            entity.setTitle(eventName.getText().toString());
                            try {
                                entity.setDate(simpleDateFormat.parse(date));
                            }
                            catch (Exception e)
                            {
                                e.getMessage();
                            }
                            entity.setLocationName(sDes);
                            entity.setContent(Content.getText().toString());
                            dataRepository.update(entity);
                            Log.d("TAG", "On Create: " + entity.getDateText() + ", " + entity.getTimeText() + ", " + date + ", " + entity.getDate());
                        });
                        Intent intent= new Intent(view.getContext(), HomeActivity.class);
                        startActivity(intent);
                    }
                    else
                    {
                        date = myDay + "/" + (myMonth + 1) + "/" + myYear + "    " + myHour + ":" + myMinute;
                        Date today = new Date();
                        try {
                            if(simpleDateFormat.parse(date).after(today) || simpleDateFormat.parse(date).equals(today))
                            {
                                AsyncTask.execute(() -> {
                                    NoteEntity entity = new NoteEntity();
                                    entity.setId(bundle.getInt("id"));
                                    entity.setTitle(eventName.getText().toString());
                                    try {
                                        entity.setDate(simpleDateFormat.parse(date));
                                    }
                                    catch (Exception e)
                                    {
                                        e.getMessage();
                                    }
                                    entity.setLocationName(sDes);
                                    entity.setContent(Content.getText().toString());
                                    dataRepository.update(entity);
                                    Log.d("TAG", "On Create: " + entity.getDateText() + ", " + entity.getTimeText() + ", " + date + ", " + entity.getDate());
                                });
                                Intent intent= new Intent(view.getContext(), HomeActivity.class);
                                startActivity(intent);
                            }
                            else
                            {
                                Toast.makeText(getApplicationContext(), "Bạn hãy nhập lại ngày lớn hơn hoặc bằng ngày hiện tại!", Toast.LENGTH_SHORT).show();
                            }
                        }
                        catch (Exception e)
                        {
                            e.getMessage();
                        }
                    }
                }
            }
        });
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        myYear = year;
        myDay = dayOfMonth;
        myMonth = month;
        Calendar c = Calendar.getInstance();
        hour = c.get(Calendar.HOUR);
        minute = c.get(Calendar.MINUTE);
        TimePickerDialog timePickerDialog = new TimePickerDialog(EditDetailsActivity.this, EditDetailsActivity.this, hour, minute, true);
        timePickerDialog.show();
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        myHour = hourOfDay;
        myMinute = minute;
        txtTime.setText(myDay + "/" + (myMonth + 1) + "/" + myYear + "   " + myHour + ":" + myMinute);
    }

    private void getCurrentLocation() {
        //Check permission
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[] {
                    Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
            return;
        }
        //initialize task location
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    currentLocation = location;
                    sSource = currentLocation.getLatitude()+ "," + currentLocation.getLongitude();
                }
            }
        });
    }

    private void DisplayTrack(String sSource, String sDes) {
        try {
            //initialize uri
            Uri uri = Uri.parse("https://www.google.co.in/maps/dir/" + sSource + "/" + sDes
            );
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            uri = null;
            //set package
            intent.setPackage("com.google.android.apps.maps");
            //set flag
            //intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            //start activity
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            //when google map is not installed, request install for launch app
            Uri uri = Uri.parse("https://play.google.com/store/apps/details?id=com.google.android.apps.maps");
            //initialize intent with action view
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            //start activity
            startActivity(intent);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ( keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0 ) {
            Intent intent= new Intent(this, HomeActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}