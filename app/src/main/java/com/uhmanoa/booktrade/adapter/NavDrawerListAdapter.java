package com.uhmanoa.booktrade.adapter;


import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.uhmanoa.booktrade.R;
import com.uhmanoa.booktrade.fragment.model.NavDrawerItem;

import java.util.ArrayList;

public class NavDrawerListAdapter extends BaseAdapter {

    private final int TYPE_ME = 0;
    private final int TYPE_LIST = 1;
    private Context context;
    private ArrayList<NavDrawerItem> navDrawerItems;

    public NavDrawerListAdapter(Context context, ArrayList<NavDrawerItem> navDrawerItems) {
        this.context = context;
        this.navDrawerItems = navDrawerItems;
    }

    public int getCount() {
        return navDrawerItems.size();
    }


    public Object getItem(int position) {
        return navDrawerItems.get(position);
    }


    public long getItemId(int position) {
        return position;
    }



    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater)
                    context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
                    convertView = mInflater.inflate(R.layout.drawer_list_item, null);
                    ImageView imgIcon = (ImageView) convertView.findViewById(R.id.icon);
                    TextView txtTitle = (TextView) convertView.findViewById(R.id.title);
                    TextView txtCount = (TextView) convertView.findViewById(R.id.counter);

                    imgIcon.setImageResource(navDrawerItems.get(position).getIcon());
                    txtTitle.setText(navDrawerItems.get(position).getTitle());

                    // displaying count
                    // check whether it set visible or not
                    if (navDrawerItems.get(position).getCounterVisibility()) {
                        txtCount.setText(navDrawerItems.get(position).getCount());
                    }
                    else {
                        // hide the counter view
                        txtCount.setVisibility(View.GONE);
                    }
        }

        return convertView;
    }


}
