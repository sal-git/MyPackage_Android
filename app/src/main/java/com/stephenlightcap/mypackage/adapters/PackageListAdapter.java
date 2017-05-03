package com.stephenlightcap.mypackage.adapters;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.stephenlightcap.mypackage.model.Package;

import com.stephenlightcap.mypackage.R;

import java.util.List;

/**
 * Created by Germex on 3/19/2017.
 */

public class PackageListAdapter extends RecyclerView.Adapter<PackageListAdapter.PackageViewHolder> {

    Context mContext;
    //private static OnShowListenerYoga mListener;
    String description;

    public static class PackageViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView from;
        TextView to, date, tracking, primaryKey;
        TextView updateDate;


        PackageViewHolder(View itemView) {
            super(itemView);
            //cv = (CardView) itemView.findViewById(R.id.car);
            from = (TextView) itemView.findViewById(R.id.textView_package_list_from);
            to = (TextView) itemView.findViewById(R.id.textView_package_list_to);
            date = (TextView) itemView.findViewById(R.id.textView_package_list_date);
            tracking = (TextView) itemView.findViewById(R.id.textView_package_tracking_number);
            primaryKey = (TextView) itemView.findViewById(R.id.textview_package_list_primary_key);

//            yogaPic = (ImageView) itemView.findViewById(R.id.yoga_photo);
//            description = (TextView) itemView.findViewById(R.id.yoga_description);
//            picIDHolder = (TextView) itemView.findViewById(R.id.yoga_picid_holder);

            //listener for the card view
//            itemView.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    if (mListener != null)
//                        mListener.onShow(engName.getText().toString(), sanName.getText().toString(),
//                                picIDHolder.getText().toString(), description.getText().toString());
//                }
//            });

        }
    }

    List<Package> packageList;

//    public YogaAdapter(List<Package> yogaList, Context mContext, OnShowListenerYoga mListener) {
    public PackageListAdapter(List<Package> packageList, Context mContext) {
        this.packageList = packageList;
        this.mContext = mContext;
    }

    @Override
    public int getItemCount() {
        return packageList.size();
    }

    @Override
    public PackageViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.package_item, viewGroup, false);
        PackageViewHolder pvh = new PackageViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(PackageViewHolder packageViewHolder, int i) {
        packageViewHolder.from.setText(packageList.get(i).getFromAddress());
        packageViewHolder.to.setText(packageList.get(i).getToAddress());
        //packageViewHolder.date.setText(packageList.get(i));
        packageViewHolder.tracking.setText(packageList.get(i).getTracking());
        packageViewHolder.primaryKey.setText(Integer.toString(packageList.get(i).getPrimaryKey()));
//        yogaViewHolder.yogaPic.setImageResource(packageList.get(i).getPhotoID());
//        yogaViewHolder.description.setText(packageList.get(i).getDescription());
//        yogaViewHolder.picIDHolder.setText(Integer.toString(i));


    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

}

