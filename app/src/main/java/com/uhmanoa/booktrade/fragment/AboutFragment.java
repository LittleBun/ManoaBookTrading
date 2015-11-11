package com.uhmanoa.booktrade.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.uhmanoa.booktrade.R;

/**
 *
 */
public class AboutFragment extends BaseFragment {

    private String TAG;
    private TextView about;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        TAG = getClass().getSimpleName();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
        about = (TextView) rootView.findViewById(R.id.about);
        return rootView;
    }
}
