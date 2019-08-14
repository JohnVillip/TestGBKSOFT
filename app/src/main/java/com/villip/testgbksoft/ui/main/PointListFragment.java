package com.villip.testgbksoft.ui.main;

import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.villip.testgbksoft.adapter.PointRecyclerViewAdapter;
import com.villip.testgbksoft.R;
import com.villip.testgbksoft.model.Point;
import com.villip.testgbksoft.model.PointWithKey;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class PointListFragment extends Fragment {

    private RecyclerView recyclerView;
    private LinearLayoutManager layoutManager;
    private Parcelable recyclerViewState = null;

    private DatabaseReference databaseReference;

    public PointListFragment() {
    }

    public static PointListFragment newInstance() {
        PointListFragment fragment = new PointListFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_point_list, container, false);

        recyclerView = rootView.findViewById(R.id.recyclerView);

        recyclerView.setHasFixedSize(true);

        layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        addEventFirebaseListener();
    }

    private void addEventFirebaseListener() {
        databaseReference = FirebaseDatabase.getInstance().getReference();

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());

        if (account != null) {
            databaseReference.child(account.getId()).child("point")
                    .addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            MainActivity.pointWithKeyList.clear();

                            if(dataSnapshot.getValue() == null) {
                                displayPointList();
                                Toast.makeText(getActivity(), "Пока нет ни одной точки", Toast.LENGTH_LONG).show();
                            } else {
                                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                                    Point point = postSnapshot.getValue(Point.class);

                                    PointWithKey pointWithKey = new PointWithKey(point.getName(), point.getLatitude(), point.getLongitude(), postSnapshot.getKey());
                                    MainActivity.pointWithKeyList.add(pointWithKey);
                                }

                                MainActivity.pointWithKeyList = sortPointList(MainActivity.pointWithKeyList);

                                displayPointList();
                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
        }
    }

    private ArrayList<PointWithKey> sortPointList(ArrayList<PointWithKey> pointWithKeyList) {
        Collections.sort(pointWithKeyList, new Comparator<PointWithKey>() {
            @Override
            public int compare(PointWithKey p1, PointWithKey p2) {
                return p1.name.compareTo(p2.name);
            }
        });

        return pointWithKeyList;
    }

    private void displayPointList() {
        PointRecyclerViewAdapter adapter = new PointRecyclerViewAdapter(this, MainActivity.pointWithKeyList, layoutManager);
        recyclerView.setAdapter(adapter);

        if(recyclerViewState != null) {
            layoutManager.onRestoreInstanceState(recyclerViewState);
        }
    }

    public void deletePoint(int position, Parcelable viewState) {
        recyclerViewState = viewState;

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());

        databaseReference.child(account.getId()).child("point").child(MainActivity.pointWithKeyList.get(position).getKey()).removeValue();
    }
}
