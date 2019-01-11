package com.wrms.vodafone.live_cotton;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.wrms.vodafone.R;
import com.wrms.vodafone.home.HomeActivity;
import com.wrms.vodafone.home.NavigationDrawerActivity;
import com.wrms.vodafone.utils.AppConstant;


/**
 * Created by Admin on 24-08-2017.
 */
public class LiveCottonActivity extends AppCompatActivity implements
        NavigationView.OnNavigationItemSelectedListener
{

    SharedPreferences prefs;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.livecotton_activity);

        Toolbar toolbar = (Toolbar) findViewById(R.id.tool_bar); // Attaching the layout to the toolbar object
        TextView farmInfo = (TextView) findViewById(R.id.live_logo);
        Typeface tf = Typeface.createFromAsset(getAssets(), "fonts/kaushan_script_regular.otf");
        farmInfo.setTypeface(tf);
        farmInfo.setText("Plant Doctor");
        farmInfo.setTextColor(Color.WHITE);
        setSupportActionBar(toolbar);



     /*   FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
*/


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.live_drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.live_nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        String role = AppConstant.role;
        Log.v("roleeeeeelllll",role+"");

        if (role.equalsIgnoreCase("Admin") || role.equalsIgnoreCase("Moderator") || role.equalsIgnoreCase("Expert")){
            Menu menu =navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.live_resolve_resolution);
            target.setVisible(true);
            MenuItem target1 = menu.findItem(R.id.live_report);
            target1.setVisible(true);
        }else {
            Menu menu =navigationView.getMenu();
            MenuItem target = menu.findItem(R.id.live_resolve_resolution);
            target.setVisible(false);
            MenuItem target1 = menu.findItem(R.id.live_report);
            target1.setVisible(false);
        }


        Fragment fragment = new LogOldFragment();
        getSupportFragmentManager().beginTransaction().replace(R.id.live_fragmentContainer, fragment).commit();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.live_drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment frg = getSupportFragmentManager().findFragmentById(R.id.live_fragmentContainer);
            if (frg instanceof LogOldFragment) {    // do something with f

                finish();

            } else {

                Fragment fragment = new LogOldFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.live_fragmentContainer, fragment).commit();
            }

        }
    }

    private void exitMethod() {

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(LiveCottonActivity.this);
        builder.setTitle("EXIT").
                setMessage("Do You want to exit?").
                setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                        finish();
                    }
                }).
                setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.cancel();
                    }
                });

        android.support.v7.app.AlertDialog dialog = builder.create();
        dialog.show();

    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.live_navigation_drawer, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

*/
    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();


        if (id == R.id.live_log_requested) {
            Fragment fragment = new LogOldFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.live_fragmentContainer, fragment).commit();

        } else if (id == R.id.live_log_new_request) {
            Fragment fragment = new LogNewFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.live_fragmentContainer, fragment).commit();

        } else if (id == R.id.live_resolve_resolution) {

            Fragment fragment = new ResolveRequests();
            getSupportFragmentManager().beginTransaction().replace(R.id.live_fragmentContainer, fragment).commit();

        } else if (id == R.id.live_send_resolution) {

        } else if (id == R.id.live_on_hold) {

        } else if (id == R.id.live_report) {

            Fragment fragment = new ReportFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.live_fragmentContainer, fragment).commit();

        } else if (id == R.id.live_draft) {

            Toast.makeText(getApplicationContext(), "Under Maintenance", Toast.LENGTH_SHORT).show();
        } else if (id == R.id.live_add_crop) {

        } else if (id == R.id.live_update_location) {

        } else if (id == R.id.live_search) {

        } else if (id == R.id.live_logout) {
            accountAlert();
        }else if (id == R.id.live_home) {

            Intent intent = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.live_drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void accountAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(AppConstant.visible_Name);
        builder.setPositiveButton("LOGOUT", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {


                if (prefs == null) {
                    prefs = getSharedPreferences(AppConstant.SHARED_PREFRENCE_NAME, MODE_PRIVATE);
                }
                boolean isLogin = prefs.getBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, false);
                SharedPreferences.Editor editor = prefs.edit();
                AppConstant.isLogin = false;
                editor.putString(AppConstant.PREFRENCE_KEY_USER_ID, "");
                editor.putString(AppConstant.PREFRENCE_KEY_VISIBLE_NAME, "");
                editor.putBoolean(AppConstant.PREFRENCE_KEY_ISLOGIN, false);
                editor.commit();


                Intent in = new Intent(getApplicationContext(),HomeActivity.class);
                in.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(in);
                finish();
            }
        });
        builder.show();
    }


    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        View view = getCurrentFocus();
        boolean ret = super.dispatchTouchEvent(event);

        if (view instanceof EditText) {
            View w = getCurrentFocus();
            int scrcoords[] = new int[2];
            w.getLocationOnScreen(scrcoords);
            float x = event.getRawX() + w.getLeft() - scrcoords[0];
            float y = event.getRawY() + w.getTop() - scrcoords[1];

            if (event.getAction() == MotionEvent.ACTION_UP
                    && (x < w.getLeft() || x >= w.getRight()
                    || y < w.getTop() || y > w.getBottom())) {

                try {
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    // TODO: handle exception
                }

            }
        }
        return ret;
    }
}