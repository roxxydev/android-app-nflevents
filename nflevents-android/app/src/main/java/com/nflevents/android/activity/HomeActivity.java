package com.nflevents.android.activity;

import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.nflevents.android.R;
import com.nflevents.android.base.BaseActivity;
import com.nflevents.android.core.entity.venue.Venue;
import com.nflevents.android.fragment.VenueDetailsFragment;
import com.nflevents.android.fragment.VenueListFragment;
import com.nflevents.android.fragment.VenueListFragment.OnVenueListItemClick;

public class HomeActivity extends BaseActivity implements FragmentManager.OnBackStackChangedListener {

    private static final String TAG = HomeActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private ProgressBar mProgressBar;

    private boolean mIsTwoPane = false;

    private VenueListFragment venueListFragment;
    private VenueDetailsFragment venueDetailsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        mIsTwoPane = findViewById(R.id.venue_detail_container) != null ? true : false;

        venueListFragment = VenueListFragment.getInstance();
        venueDetailsFragment = VenueDetailsFragment.getInstance();

        initListItemClickListener();

        if (mIsTwoPane) {
            // layout view is in landscape
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.venue_list_container, venueListFragment)
                    .commit();
        } else {
            // replace with Venue List
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.venue_container, venueListFragment)
                    .commit();
        }

        setUpToolbar();

        ActivityCompat.invalidateOptionsMenu(HomeActivity.this);
        supportInvalidateOptionsMenu();
    }

    @Override
    public void onBackStackChanged() {
        // Recreate actionbar menu if navigating from Venue details screen in portrait orientation
        ActivityCompat.invalidateOptionsMenu(HomeActivity.this);
        supportInvalidateOptionsMenu();
    }

    /**
     * Show progressbar in actionbar
     */
    public void showPgBarVisibility(boolean isVisible) {
        if (mProgressBar != null) {
            int visibility = isVisible ? View.VISIBLE : View.GONE;
            mProgressBar.setVisibility(visibility);
        }
    }

    // Setup Toolbar as actionbar
    private void setUpToolbar() {
        mProgressBar = (ProgressBar) findViewById(R.id.toolbar_progressbar);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        mToolbar.setTitleTextColor(getResources().getColor(android.R.color.black));
        mToolbar.setBackgroundColor(getResources().getColor(R.color.actionbar_background_color));
        mToolbar.setTitleTextColor(getResources().getColor(R.color.actionbar_menu_text_color));
        setSupportActionBar(mToolbar);

        getSupportActionBar().show();
        getSupportActionBar().setTitle(getString(R.string.app_name));
        getSupportActionBar().setIcon(R.drawable.ic_launcher);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    // Initialize Venue list item click listener
    private void initListItemClickListener() {
        OnVenueListItemClick onVenueListItemClick = new OnVenueListItemClick() {
            @Override
            public void onClick(Venue venueData) {
                venueDetailsFragment.setVenueData(venueData);
                ActivityCompat.invalidateOptionsMenu(HomeActivity.this);
                supportInvalidateOptionsMenu();
                if (mIsTwoPane) {
                    getSupportFragmentManager().beginTransaction()
                            .detach(venueDetailsFragment)
                            .replace(R.id.venue_detail_container, venueDetailsFragment)
                            .attach(venueDetailsFragment)
                            .commit();

                    // disable home up button if Venue details screen is in landscape
                    getSupportActionBar().setDisplayHomeAsUpEnabled(false);
                } else {
                    getSupportFragmentManager().addOnBackStackChangedListener(HomeActivity.this);
                    getSupportFragmentManager()
                            .beginTransaction()
                            .detach(venueDetailsFragment)
                            .replace(R.id.venue_container, venueDetailsFragment)
                            .attach(venueDetailsFragment)
                            .addToBackStack(VenueDetailsFragment.class.getSimpleName())
                            .commit();

                    // only show actionbar up if not Venue details screen is in portrait orientation
                    getSupportActionBar().setTitle(getString(R.string.ab_venue_details_title));
                    getSupportActionBar().setDisplayShowHomeEnabled(true);
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                }
            }
        };
        venueListFragment.setOnListItemClick(onVenueListItemClick);
    }
}
