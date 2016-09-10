package com.nflevents.android.base;

import android.os.Bundle;
import android.support.v4.app.Fragment;

import com.nflevents.android.core.http.rest.RestManager;

public class BaseFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /** Get Application Context from the Activity of this Fragment instance. */
    public BaseApp getApp() {
        return ((BaseActivity) getActivity()).getApp();
    }

    /** Get the RestManager for performing API or http calls. */
    public RestManager getRestManager() {
        return ((BaseActivity) getActivity()).getRestManager();
    }

}
