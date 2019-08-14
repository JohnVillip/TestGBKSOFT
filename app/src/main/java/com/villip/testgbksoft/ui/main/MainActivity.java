package com.villip.testgbksoft.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;


import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.villip.testgbksoft.R;
import com.villip.testgbksoft.model.PointWithKey;
import com.villip.testgbksoft.ui.auth.AuthActivity;
import com.villip.testgbksoft.adapter.SectionsPagerAdapter;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    public static String clickedPointName = "";

    private TextView title;

    final int[] ICONS = new int[]{
            R.drawable.ic_list_white_24dp,
            R.drawable.ic_location_white_24dp,
            R.drawable.ic_profile_white_24dp
    };

    public ViewPager viewPager;
    private TabLayout tabLayout;

    public static ArrayList<PointWithKey> pointWithKeyList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);

        if(account == null) {
            Intent intent = new Intent(this, AuthActivity.class);
            startActivity(intent);

            finish();
        } else {
            title = findViewById(R.id.title);

            SectionsPagerAdapter sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
            viewPager = findViewById(R.id.view_pager);
            viewPager.setAdapter(sectionsPagerAdapter);
            tabLayout = findViewById(R.id.tabs);
            tabLayout.setupWithViewPager(viewPager);

            tabLayout.getTabAt(0).setIcon(ICONS[0]);
            tabLayout.getTabAt(1).setIcon(ICONS[1]);
            tabLayout.getTabAt(2).setIcon(ICONS[2]);

            tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
                @Override
                public void onTabSelected(TabLayout.Tab tab) {
                    switch(tab.getPosition()) {
                        case 0:
                            title.setText(R.string.tab_point_list);
                            break;
                        case 1:
                            title.setText(R.string.tab_map);
                            break;
                        case 2:
                            title.setText(R.string.tab_profile);
                            break;
                    }
                }

                @Override
                public void onTabUnselected(TabLayout.Tab tab) {

                }

                @Override
                public void onTabReselected(TabLayout.Tab tab) {

                }
            });
        }
    }


    @Override
    public void onBackPressed() {
        // заглушка
    }

    public void goToMap(int position) {
        clickedPointName = pointWithKeyList.get(position).getName();

        tabLayout.getTabAt(1).select();
    }
}