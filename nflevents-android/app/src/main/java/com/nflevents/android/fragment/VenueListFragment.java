package com.nflevents.android.fragment;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.nflevents.android.R;
import com.nflevents.android.activity.HomeActivity;
import com.nflevents.android.base.BaseFragment;
import com.nflevents.android.core.entity.venue.Venue;
import com.nflevents.android.core.http.ApiServiceType;
import com.nflevents.android.core.http.params.HttpParams;
import com.nflevents.android.core.http.rest.RestCallback;
import com.nflevents.android.core.http.rest.RestResponse;
import com.nflevents.android.core.json.JsonParser;
import com.nflevents.android.core.utils.FieldValidator;

import java.util.ArrayList;

public class VenueListFragment extends BaseFragment {

    private static VenueListFragment venueListFragment;

    private View mRootView;
    private ListView lv;

    private VenueListAdapter lvAdapter;

    private OnVenueListItemClick mOnListItemClick;

    private ArrayList<Venue> arrVenues;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public VenueListFragment() {
    }

    public static VenueListFragment getInstance() {
        if(venueListFragment == null) {
            venueListFragment = new VenueListFragment();
        }
        return venueListFragment;
    }

    /** Set callback when Venue list item has been clicked. */
    public void setOnListItemClick(OnVenueListItemClick onListItemClick) {
        this.mOnListItemClick = onListItemClick;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        mRootView = inflater.inflate(R.layout.fragment_venue_list, null);
        return mRootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        // Initialize child views here after mRootView has been created.
        lv = (ListView) mRootView.findViewById(R.id.frag_venue_list_lv);
        setHasOptionsMenu(true);
        if(arrVenues != null) {
            if(lvAdapter != null) {
                lv.setAdapter(lvAdapter);
                lvAdapter.notifyDataSetChanged();
            } else {
                lvAdapter = new VenueListAdapter(getActivity(), arrVenues);
                lv.setAdapter(lvAdapter);
            }
        } else {
            getVenueList();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    // perform http request getting list of Venue
    private void getVenueList() {
        RestCallback restCallback = new RestCallback() {
            @Override
            public void onRequestStart() {
                super.onRequestStart();
                setRefreshing(true);
            }

            @Override
            public void onRequestResponse(RestResponse restResponse) {
                super.onRequestResponse(restResponse);
                setRefreshing(false);

                if(restResponse.isReqSuccess()) {
                    arrVenues = JsonParser.toVenueList(restResponse.getResponseBody());
                    lvAdapter = new VenueListAdapter(getActivity(), arrVenues);
                    lv.setAdapter(lvAdapter);
                }
            }

            @Override
            public void onRequestFailure(RestResponse restResponse) {
                super.onRequestFailure(restResponse);
                setRefreshing(false);
            }
        };
        // Temporary instance of HttpParams created until parameters for retrieving Venue List are finalized
        getRestManager().performRequest(
                getRestManager().createRequest(
                    ApiServiceType.GET_VENUE_LIST,
                    restCallback,
                    new HttpParams()));
    }

    // update action menu refresh state and toolbar progressbar
    private void setRefreshing(boolean isRefreshing) {
        if(getActivity() != null) {
            ((HomeActivity) getActivity()).showPgBarVisibility(isRefreshing);
        }
    }

    /** Callback when an item in Venue list is clicked. */
    public static interface OnVenueListItemClick {

        /** Called the list item has been clicked.  */
        public void onClick(Venue venueData);
    }

    // Adapter of ListView for Venue
    private class VenueListAdapter extends BaseAdapter {

        private Context mContext;
        private ArrayList<Venue> arrAdapterVenues;

        public VenueListAdapter(Context context, ArrayList<Venue> arrVenues) {
            mContext = context;
            this.arrAdapterVenues = arrVenues;
        }

        private class ViewHolder {
            LinearLayout lnVenueItemContainer;
            TextView tvVenueName, tvVenueAddress;
        }

        @Override
        public int getCount() {
            if(arrAdapterVenues != null) {
                return arrAdapterVenues.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup parent) {
            if (arrAdapterVenues != null) {
                final Venue venueData = arrAdapterVenues.get(position);
                ViewHolder vh;
                if (view == null) {
                    vh = new ViewHolder();
                    view = LayoutInflater.from(mContext)
                            .inflate(R.layout.fragment_venue_list_item, parent, false);
                    vh.lnVenueItemContainer = (LinearLayout) view.findViewById(
                            R.id.frag_menu_list_item_container);
                    vh.tvVenueName = (TextView) view.findViewById(R.id.frag_venue_list_item_tv_name);
                    vh.tvVenueAddress = (TextView) view.findViewById(R.id.frag_venue_list_item_tv_address);
                    view.setTag(vh);
                } else {
                    vh = (ViewHolder) view.getTag();
                }

                if(FieldValidator.isStringValid(venueData.getName()))
                    vh.tvVenueName.setText(venueData.getName());

                if(FieldValidator.isStringValid(venueData.getAddress()))
                    vh.tvVenueAddress.setText(venueData.getAddress());

                vh.lnVenueItemContainer.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(mOnListItemClick != null) {
                            mOnListItemClick.onClick(venueData);// pass Venue details of clicked item
                        }
                    }
                });
            }
            return view;
        }
    }

}
