package hydrocoolapps.waterwise.activity;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import hydrocoolapps.waterwise.R;

public class SystemActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private String fragmentName;
    private Toolbar myToolbar;
    private FragmentDrawer drawerFragment;
    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system);

        myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayShowTitleEnabled(true);

        fragmentManager = getFragmentManager();

        drawerFragment = (FragmentDrawer)
                getFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
        drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), myToolbar);
        drawerFragment.setDrawerListener(this);

        //displayView will choose an item from the menu and bring up that fragment
        displayView(0); // First menu item is what I would like to use as the main page
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) { return super.onCreateOptionsMenu(menu); }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) { return super.onOptionsItemSelected(item); }

    @Override
    public void onDrawerItemSelected(View view, final int position) { displayView(position); }

    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);

        //Going to use switch statements to grab the fragment requested
        switch (position) {

            case 0:
                title = getString(R.string.title_system);
                fragment = new SystemFragment();
                break;

            case 1:
                title = getString(R.string.title_plant_info);
                fragment = new PlantFragment();
                break;

            case 2:
                title = getString(R.string.title_sensors);
                fragment = new SensorsFragment();
                break;

            case 3:
                title = getString(R.string.title_account);
                fragment = new AccountFragment();
                break;

            case 4:
                title = getString(R.string.title_settings);
                fragment = new SettingsFragment();
                break;

            case 5:
                title = getString(R.string.title_help);
                fragment = new HelpFragment();
                break;
        }

        if (fragment != null) {

            fragmentTransaction = fragmentManager.beginTransaction();

            fragmentTransaction.replace(R.id.container_body, fragment).addToBackStack(fragment.getClass().getName());
            fragmentTransaction.commit();

            getSupportActionBar().setTitle(title);
        }
    }

    @Override
    public void onBackPressed() {

        if (fragmentManager.getBackStackEntryCount() > 1)
            fragmentManager.popBackStack();

        else
            moveTaskToBack(true);
    }



}
