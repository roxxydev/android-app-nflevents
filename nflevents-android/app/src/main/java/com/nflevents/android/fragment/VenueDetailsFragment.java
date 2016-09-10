package com.nflevents.android.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.ShareActionProvider;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nflevents.android.R;
import com.nflevents.android.base.BaseActivity;
import com.nflevents.android.base.BaseFragment;
import com.nflevents.android.core.entity.venue.Venue;
import com.nflevents.android.core.entity.venue.schedule.Schedule;
import com.nflevents.android.core.json.JsonParser;
import com.nflevents.android.core.utils.FieldValidator;
import com.nflevents.android.core.utils.ImageLoaderUtil;
import com.nflevents.android.core.utils.LogMe;
import com.nflevents.android.core.utils.TimestampUtil;

public class VenueDetailsFragment extends BaseFragment {

    private static final String TAG = VenueDetailsFragment.class.getSimpleName();

    private static VenueDetailsFragment venueDetailsFragment;

    private View mRootView;
    private ImageView mImageView;
    private TextView mTvName, mTvAddress, mTvSchedule;

    private Venue venueData;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VenueDetailsFragment() {
    }

    public static VenueDetailsFragment getInstance() {
        if(venueDetailsFragment == null) {
            venueDetailsFragment = new VenueDetailsFragment();
        }
        return venueDetailsFragment;
    }

    /** Set Venue details to be populated to this fragments' view. */
    public void setVenueData(Venue venueData) {
        this.venueData = venueData;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_venue_details, null);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize child views here after mRootView has been created.
        mImageView = (ImageView) mRootView.findViewById(R.id.frag_venue_details_iv);
        mTvName = (TextView) mRootView.findViewById(R.id.frag_venue_details_tv_name);
        mTvAddress = (TextView) mRootView.findViewById(R.id.frag_venue_details_tv_address);
        mTvSchedule = (TextView) mRootView.findViewById(R.id.frag_venue_details_tv_schedule);
        setHasOptionsMenu(true);
        initViewData();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.menu_venue_details, menu);

        // initialize share action provider
        MenuItem shareItem = menu.findItem(R.id.menu_venue_details_share);
        if(shareItem != null) {
            ShareActionProvider mShareActionProvider = new ShareActionProvider(getActivity());
            mShareActionProvider.setShareHistoryFileName(
                    ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
            mShareActionProvider.setShareIntent(getDefaultShareIntent());
            MenuItemCompat.setActionProvider(shareItem, mShareActionProvider);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            getActivity().onBackPressed();// pop backstack of fragment manager
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDestroy() {
        disableActionbarHomeUp();
        super.onDestroy();
    }

    // Get defaul intent to for ShareActionProvider
    private Intent getDefaultShareIntent() {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        if(venueData != null) {
            if(FieldValidator.isStringValid(venueData.getName())) {
                intent.putExtra(Intent.EXTRA_SUBJECT, venueData.getName());
            }
            StringBuilder sb = new StringBuilder();
            if(FieldValidator.isStringValid(venueData.getName())) {
                sb.append(venueData.getName());
            }
            if(FieldValidator.isStringValid(venueData.getAddress())) {
                sb.append("\n" + venueData.getAddress());
            }
            if(FieldValidator.isStringValid(venueData.getDescription())) {
                sb.append("\n" + venueData.getDescription());
            }
            if(FieldValidator.isStringValid(venueData.getTicketLink())) {
                sb.append("\n" + venueData.getTicketLink());
            }
            if(FieldValidator.isStringValid(venueData.getPhone())) {
                sb.append("\n" + venueData.getPhone());
            }
            if(FieldValidator.isStringValid(venueData.getTollFreePhone())) {
                sb.append("\n" + venueData.getTollFreePhone());
            }
            intent.putExtra(Intent.EXTRA_TEXT, sb.toString());
        }
        return intent;
    }

    // disable/hide Actionbar home up button
    private void disableActionbarHomeUp() {
        ActionBar actionBar = ((BaseActivity) getActivity()).getSupportActionBar();
        actionBar.setTitle(getString(R.string.app_name));
        actionBar.setDisplayHomeAsUpEnabled(false);
    }

    // Populate Venue data values to views
    private void initViewData() {
        if(venueData != null) {
            LogMe.d(TAG, "initViewData: " + JsonParser.fromVenue(venueData));

            if(FieldValidator.isStringValid(venueData.getName()))
                mTvName.setText(venueData.getName());

            if(FieldValidator.isStringValid(venueData.getAddress()))
                mTvAddress.setText(venueData.getAddress());

            if(venueData.getSchedule() != null && !venueData.getSchedule().isEmpty()) {
                StringBuilder sb = new StringBuilder();
                for(Schedule sched : venueData.getSchedule()) {
                    LogMe.d(TAG, "startDate: " + sched.getStartDate() + "endDate: " + sched.getEndDate());
                    String schedDates = TimestampUtil.toDisplayDate(sched);
                    if(sb.toString().length() > 0) {
                        sb.append("\n");// insert new line for the next schedule date
                    }
                    sb.append(schedDates);
                }
                if(FieldValidator.isStringValid(sb.toString()))
                    mTvSchedule.setText(sb.toString());
            }

            if(FieldValidator.isStringValid(venueData.getImageUrl())) {
                ImageLoaderUtil.setImage(getActivity(), venueData.getImageUrl(),
                        mImageView, R.drawable.ic_launcher);
            }
        }
    }

}
