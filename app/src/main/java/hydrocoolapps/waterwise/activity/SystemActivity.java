package hydrocoolapps.waterwise.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import hydrocoolapps.waterwise.R;

public class SystemActivity extends ActionBarActivity implements FragmentDrawer.FragmentDrawerListener {

    private Context context;
    private Toolbar myToolbar;
    private FragmentDrawer drawerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);

        context = getApplicationContext();

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        drawerFragment = (FragmentDrawer)
                getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), myToolbar);
        drawerFragment.setDrawerListener(this);

        //displayView will choose an item from the menu and bring up that fragment
        displayView(1); // Second menu item is what I would like to use as the main page
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_system, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            displayView(6);
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {

        displayView(position);
    }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        //Going to use switch statements to grab the fragment requested
        switch (position) {
            case 0:
                fragment = new AccountFragment();
                title = getString(R.string.title_account);
                break;
            case 1:
                fragment = new SystemFragment();
                title = getString(R.string.title_system);
                break;
            case 2:
                fragment = new PlantFragment();
                title = getString(R.string.title_plant_info);
                break;
            case 3:
                fragment = new SensorsFragment();
                title = getString(R.string.title_sensors);
                break;
            case 4:
                fragment = new HelpFragment();
                title = getString(R.string.title_help);
                break;
            case 5:
                fragment = new AboutFragment();
                title = getString(R.string.title_about);
                break;
            case 6:
                fragment = new SettingsFragment();
                title = getString(R.string.title_settings);
                break;
            default:
                break;
        }

        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            getSupportActionBar().setTitle(title);
        }
    }
}
