package com.example.zotee.activity;

import android.os.AsyncTask;
import android.os.Bundle;

import com.example.zotee.activity.fragment.CloudItemFragment;
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

import com.example.zotee.R;
import com.google.android.material.tabs.TabLayout;

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

        //Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //
        FragmentPagerAdapter pagerAdapter = new FragmentPagerAdapter(getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
            private final Fragment[] fragments = new Fragment[]{
                    new ItemListFragment(),
                    new CloudItemFragment(),
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
        tabLayout.setupWithViewPager(viewPager);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(view -> {

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
                });


//        // Add product list note if this is first creation
//        if (savedInstanceState == null) {
//            ItemListFragment fragment = new ItemListFragment();
//
//            getSupportFragmentManager().beginTransaction()
//                    .add(R.id.fragment_container, fragment, "ItemListFragment").commit();
//        }
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