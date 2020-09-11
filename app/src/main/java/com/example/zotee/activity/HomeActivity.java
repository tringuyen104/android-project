package com.example.zotee.activity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;

import com.example.zotee.activity.fragment.CloudItemsFragment;
import com.example.zotee.activity.fragment.ItemListFragment;
import com.example.zotee.storage.DataRepository;
import com.example.zotee.storage.entity.NoteEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.zotee.R;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Date;

import javax.inject.Inject;

import dagger.hilt.android.AndroidEntryPoint;


@AndroidEntryPoint
public class HomeActivity extends BaseActivity {

    @Inject
    DataRepository dataRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        Intent in = getIntent();
        Boolean showSecondTab = in.getBooleanExtra("showGlobal", false);
        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        View.OnClickListener localFabClick = view -> {
            AsyncTask.execute(() -> {
                NoteEntity entity = new NoteEntity();
                entity.setLocationName("SaiGon");
                entity.setTitle("Test");
                entity.setContent("Content");
                entity.setDate(new Date());
                entity.setLat("North");
                entity.setLng("17");
                dataRepository.insert(entity, true);
            });
        };

        View.OnClickListener onlineFabClick = view -> {
            NoteEntity entity = new NoteEntity();
            entity.setLocationName("SaiGon");
            entity.setTitle("Online Test");
            entity.setContent("Content");
            entity.setDate(new Date());
            entity.setLat("North");
            entity.setLng("17");
            dataRepository.createCloudNote(getUserId(), entity);
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
                return fragments[position];
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}