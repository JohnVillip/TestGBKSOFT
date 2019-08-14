package com.villip.testgbksoft.adapter;

import android.os.Parcelable;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.villip.testgbksoft.ui.main.MainActivity;
import com.villip.testgbksoft.R;
import com.villip.testgbksoft.model.PointWithKey;
import com.villip.testgbksoft.ui.main.PointListFragment;

import java.util.ArrayList;

public class PointRecyclerViewAdapter extends RecyclerView.Adapter<PointRecyclerViewAdapter.MyViewHolder> {
    private ArrayList<PointWithKey> pointWithKeyList;
    private PointListFragment pointListFragment;

    private LinearLayoutManager layoutManager;

    public PointRecyclerViewAdapter(
            PointListFragment pointListFragment,
            ArrayList<PointWithKey> pointWithKeyList,
            LinearLayoutManager layoutManager) {

        this.pointListFragment = pointListFragment;
        this.pointWithKeyList = pointWithKeyList;
        this.layoutManager = layoutManager;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView pointNameTextView;
        TextView latitudeTextView;
        TextView longitudeTextView;
        public MyViewHolder(View v) {
            super(v);
            cardView = v.findViewById(R.id.cardView);
            pointNameTextView = v.findViewById(R.id.pointNameTextView);
            latitudeTextView = v.findViewById(R.id.latitudeTextView);
            longitudeTextView = v.findViewById(R.id.longitudeTextView);
        }
    }

    @Override
    public PointRecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_point, parent, false);

        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder,final int position) {

        holder.pointNameTextView.setText(pointWithKeyList.get(position).getName());
        holder.latitudeTextView.setText(pointWithKeyList.get(position).getLatitude());
        holder.longitudeTextView.setText(pointWithKeyList.get(position).getLongitude());

        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity mainActivity = (MainActivity) pointListFragment.getActivity();
                mainActivity.goToMap(position);

            }
        });

        holder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Parcelable recylerViewState = layoutManager.onSaveInstanceState();

                pointListFragment.deletePoint(position, recylerViewState);

                return false;
            }
        });

    }

    @Override
    public int getItemCount() {
        return pointWithKeyList.size();
    }
}
