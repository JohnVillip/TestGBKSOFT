package com.villip.testgbksoft.ui.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.google.firebase.auth.FirebaseAuth;
import com.villip.testgbksoft.R;
import com.villip.testgbksoft.ui.auth.AuthActivity;


public class ProfileFragment extends Fragment {
    private GoogleSignInClient googleSignInClient;

    private String personName;
    private Uri personPhoto;
    private GoogleSignInAccount account;

    public static ProfileFragment newInstance() {
        return new ProfileFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        account = GoogleSignIn.getLastSignedInAccount(getActivity());
        if (account != null) {
            personName = account.getDisplayName();
            personPhoto = account.getPhotoUrl();
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        googleSignInClient = GoogleSignIn.getClient(getActivity(), gso);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        ImageView avatarImageView = rootView.findViewById(R.id.avatarImageView);
        TextView nameTextView = rootView.findViewById(R.id.nameTextView);

        if (account != null) {
            nameTextView.setText(personName);

            Log.d("MyLog", "uri - " + personPhoto);

            Glide.with(this).load(String.valueOf(personPhoto)).into(avatarImageView);

            Button signOutButton = rootView.findViewById(R.id.signOutButton);
            signOutButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signOut();
                }
            });
        }
        return rootView;
    }

    private void signOut() {
        FirebaseAuth.getInstance().signOut();

        googleSignInClient.signOut()
                .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent intent = new Intent(getActivity(), AuthActivity.class);
                        startActivity(intent);
                    }
                });
    }

}
