package com.villip.testgbksoft.ui.main;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.google.maps.android.ui.IconGenerator;
import com.villip.testgbksoft.model.MyItem;
import com.villip.testgbksoft.R;
import com.villip.testgbksoft.model.Point;
import com.villip.testgbksoft.model.PointWithKey;

public class MapFragment extends Fragment implements OnMapReadyCallback{
    private GoogleMap googleMap;

    private ClusterManager<MyItem> clusterManager;

    private String shownInfoWindowMarkerId = "";
    boolean markerShown = false;

    private MyClusterRenderer render;

    public MapFragment() {
    }

    public static MapFragment newInstance() {
        MapFragment fragment = new MapFragment();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();

        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(getActivity(), new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                LatLng ll = new LatLng(location.getLatitude(), location.getLongitude());
                                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(ll, 16));

                                if (ContextCompat.checkSelfPermission(getActivity(), android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    googleMap.setMyLocationEnabled(true);
                                }
                            }
                        }
                    });
        }
    }

    @Override
    public void onMapReady(GoogleMap map) {
        googleMap = map;
        googleMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                startDialog(latLng);
            }
        });

        clusterManager = new ClusterManager<>(getActivity(), googleMap);

        googleMap.setOnCameraIdleListener(clusterManager);
        googleMap.setOnMarkerClickListener(clusterManager);

        render = new MyClusterRenderer(getActivity(), googleMap, clusterManager);
        clusterManager.setRenderer(render);

        clusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<MyItem>() {
            @Override
            public boolean onClusterItemClick(MyItem myItem) {
                Marker marker = render.getMarker(myItem);

                hideOrShowMarkerInfoWindow(marker);

                return true;
            }
        });

    }

    private void hideOrShowMarkerInfoWindow(Marker marker) {
        if (!shownInfoWindowMarkerId.equals(marker.getId())) {
            marker.showInfoWindow();
            markerShown = true;
        } else if(markerShown) {
            marker.hideInfoWindow();
            markerShown = false;
        } else {
            marker.showInfoWindow();
            markerShown = true;
        }
        shownInfoWindowMarkerId = marker.getId();
    }

    private void startDialog(final LatLng latLng) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(), R.style.MyDialogTheme);
        final EditText nameEditText = new EditText(getActivity());
        nameEditText.setTextColor(getResources().getColor(android.R.color.white));

        builder.setTitle("Введите название")
                .setIcon(R.drawable.ic_location_white_24dp)
                .setView(nameEditText)
                .setPositiveButton("Сохранить", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String name = nameEditText.getText().toString();

                        String latitude = "" + latLng.latitude;
                        String longitude = "" + latLng.longitude;

                        saveData(name, latitude, longitude);
                        addPoint(latLng, name);
                    }
                });
        AlertDialog alertDialog = builder.create();

        alertDialog.show();


    }

    private void saveData(String name, String latitude, String longitude) {
        Point point = new Point(name, latitude, longitude);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getActivity());

        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference();
        databaseReference.child(account.getId()).child("point").push().setValue(point, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(@Nullable DatabaseError databaseError, @NonNull DatabaseReference databaseReference) {
            }
        });

    }

    private void addPoint(LatLng latLng, String name) {
        MyItem myItem = new MyItem(latLng.latitude, latLng.longitude, name, null);
        clusterManager.addItem(myItem);
        clusterManager.cluster();
    }

    private BitmapDescriptor BitmapDescriptorFromVector(int icon) {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), icon);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            googleMap.clear();
            clusterManager.clearItems();
            setAllMarkers();
        }
    }

    private void setAllMarkers() {
        for (PointWithKey point : MainActivity.pointWithKeyList) {
            LatLng latLng = new LatLng(Double.valueOf(point.getLatitude()), Double.valueOf(point.getLongitude()));

            addPoint(latLng, point.getName());
        }
    }


    public class MyClusterRenderer extends DefaultClusterRenderer<MyItem> {
        private final IconGenerator iconGenerator = new IconGenerator(getContext());
        final Drawable clusterIcon = getResources().getDrawable(R.drawable.ic_cloud_green_48dp);

        public MyClusterRenderer(Context context, GoogleMap map, ClusterManager<MyItem> clusterManager) {
            super(context, map, clusterManager);
            setupIconGen(iconGenerator, clusterIcon, context);
        }

        private void setupIconGen(IconGenerator generator, Drawable drawable, Context context) {
            TextView textView = new TextView(context);
            textView.setId(com.google.maps.android.R.id.amu_text);
            textView.setGravity(android.view.Gravity.CENTER);
            textView.setTextColor(getResources().getColor(android.R.color.white));
            textView.setLayoutParams(new FrameLayout.LayoutParams(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight()));
            generator.setContentView(textView);
            generator.setBackground(drawable);
        }


        @Override
        protected void onBeforeClusterRendered(Cluster<MyItem> cluster, MarkerOptions markerOptions) {
            Bitmap icon = iconGenerator.makeIcon(String.valueOf(cluster.getSize()));
            markerOptions.icon(BitmapDescriptorFactory.fromBitmap(icon));
        }

        @Override
        protected boolean shouldRenderAsCluster(Cluster<MyItem> cluster) {
            return cluster.getSize() >= 3;
        }

        @Override
        protected void onBeforeClusterItemRendered(MyItem item, MarkerOptions markerOptions) {
            markerOptions.title(item.getTitle());
            markerOptions.icon(BitmapDescriptorFromVector(R.drawable.ic_smile_36dp));

            super.onBeforeClusterItemRendered(item, markerOptions);
        }

        @Override
        protected void onClusterItemRendered(MyItem clusterItem, Marker marker) {
            if(MainActivity.clickedPointName.equals(marker.getTitle())) {
                marker.showInfoWindow();

                MainActivity.clickedPointName = "";
            }

            super.onClusterItemRendered(clusterItem, marker);
        }
    }
}


