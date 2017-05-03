package com.stephenlightcap.mypackage.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.stephenlightcap.mypackage.R;
import com.stephenlightcap.mypackage.model.Package;
import com.stephenlightcap.mypackage.model.TrackingList;

import java.util.List;

/**
 * Created by Germex on 4/20/2017.
 */

public class TrackingListAdapter extends RecyclerView.Adapter<TrackingListAdapter.TrackingViewAdapter> {

    Context mContext;
    //private static OnShowListenerYoga mListener;
    String description;

    public static class TrackingViewAdapter extends RecyclerView.ViewHolder {
        CardView cv;
        TextView message;

        TrackingViewAdapter(View itemView) {
            super(itemView);
            message = (TextView) itemView.findViewById(R.id.textview_tracking_item_message);

        }
    }

    List<TrackingList> trackingList;

    public TrackingListAdapter(List<TrackingList> trackingList, Context mContext) {
        this.trackingList = trackingList;
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        return trackingList.size();
    }

    @Override
    public TrackingListAdapter.TrackingViewAdapter onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.tracking_item, viewGroup, false);
        TrackingListAdapter.TrackingViewAdapter pvh = new TrackingListAdapter.TrackingViewAdapter(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(TrackingListAdapter.TrackingViewAdapter packageViewHolder, int i) {
        packageViewHolder.message.setText(trackingList.get(i).getMessage());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

