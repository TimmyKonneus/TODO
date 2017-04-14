package com.timmykonneus.todoapp;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import java.util.List;


public class TaskListActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



   // private List<Task> tasks = new LinkedList<>();

    @RequiresApi(api = Build.VERSION_CODES.N)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.drawer, R.string.drawerclose);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        if (savedInstanceState == null) {
            TaskStorageHelper.getInstance().initStorage(this);
            Fragment fragment = new TASKLIST();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.container_view, fragment, null)
                    .commit();
        }
    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_tasks) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_view, new TASKLIST(), null)
                    .commit();
        } else if (id == R.id.nav_stats) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_view, new STATISTICS(), null)
                    .commit();
        } else if (id == R.id.nav_info) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.container_view, new INFO(), null)
                    .commit();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
