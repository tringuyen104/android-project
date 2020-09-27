package com.example.zotee.activity;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;

import com.example.zotee.EventDetailsActivity;
import com.example.zotee.activity.fragment.CloudItemsFragment;
import com.example.zotee.activity.fragment.ItemListFragment;
import com.example.zotee.notification.NotificationBroadCastReceiver;
import com.example.zotee.notification.NotificationService;
import com.example.zotee.storage.DataRepository;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.RequiresApi;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.zotee.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class HomeActivity extends FirebaseAuthenticationActivity {

    @Inject
    DataRepository dataRepository;
    Intent mServiceIntent;
    private NotificationService mNotificationService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        instanceService();
        Intent in = getIntent();
        Boolean showSecondTab = in.getBooleanExtra("showGlobal", false);
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        View.OnClickListener localFabClick = view -> {
            AsyncTask.execute(() -> {
                Intent intent = new Intent(HomeActivity.this, EventDetailsActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("Key_1", "fab");
                intent.putExtras(bundle);
                startActivity(intent);
            });
        };

        View.OnClickListener onlineFabClick = view -> {
            Intent intent = new Intent(HomeActivity.this, EventDetailsActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("Key_2", "fab");
            intent.putExtras(bundle);
            startActivity(intent);
        };

        //
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            private final Fragment[] fragments = new Fragment[]{
                    new ItemListFragment(),
                    new CloudItemsFragment(),
            };
            private final String[] fragmentNames = new String[]{
                    getString(R.string.individual_note),
                    getString(R.string.group_note)
            };

            @Override
            public Fragment getItem(int position) {
                Fragment fragment = fragments[position];
                return fragment;
            }

            @Override
            public int getCount() {
                return fragments.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return fragmentNames[position];
            }
        };

        ViewPager viewPager = findViewById(R.id.container);
        viewPager.setAdapter(pagerAdapter);

        TabLayout tabLayout = findViewById(R.id.tabs);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int position = tab.getPosition();
                FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
                if(position == 1 ) {
                    fab.setOnClickListener(onlineFabClick);
                    if (firebaseUser == null) {
                        fab.hide();
                    }
                } else {
                    fab.setOnClickListener(localFabClick);
                    fab.show();
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        if(showSecondTab){
            tabLayout.getTabAt(1).select();
        }
    }

    private void instanceService() {
        mNotificationService = new NotificationService();
        mServiceIntent = new Intent(this, mNotificationService.getClass());
        if (!isMyServiceRunning(mNotificationService.getClass())) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                this.startForegroundService(mServiceIntent);
            } else {
                startService(mServiceIntent);
            }
        }
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                Log.i ("Service status", "Running");
                return true;
            }
        }
        Log.i ("Service status", "Not running");
        return false;
    }

    @Override
    void onLoggedIn() {

    }

    @Override
    void onLoggedOut() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        boolean logged = isLogged();
        MenuItem accountItem = menu.findItem(R.id.accountMenu);
        accountItem.setVisible(logged);
        if(logged) accountItem.setTitle("Hello, "+getUser().getDisplayName());
        MenuItem signInItem = menu.findItem(R.id.sign_in_action_bar);
        signInItem.setVisible(!logged);
        MenuItem signOutItem =  menu.findItem(R.id.sign_out_action_bar);
        signOutItem.setVisible(logged);
        return true;
    }

    @Override
    protected void onDestroy() {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction("restartservice");
        broadcastIntent.setClass(this, NotificationBroadCastReceiver.class);
        this.sendBroadcast(broadcastIntent);
        super.onDestroy();
    }
}