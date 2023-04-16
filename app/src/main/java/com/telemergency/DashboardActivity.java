package com.telemergency;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;

import android.text.Html;
import android.util.Log;
import android.view.Gravity;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.res.ResourcesCompat;
import androidx.core.view.GravityCompat;
import androidx.appcompat.app.ActionBarDrawerToggle;
import android.view.MenuItem;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.telemergency.ui.login.LoginActivity;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import android.view.Menu;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DashboardActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        HomeFragment.OnFragmentInteractionListener,
        ProfileUpdateFragment.OnFragmentInteractionListener {

    HomeFragment homeFragment = new HomeFragment();
    ProfileUpdateFragment profileUpdateFragment = new ProfileUpdateFragment();
    private String surname = "", first_name = "",  other_names = " ";
    private TextView user_name, user_email;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        final DrawerLayout root = drawer;
        NavigationView navigationView = findViewById(R.id.nav_view);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView.setNavigationItemSelectedListener(this);

        sharedPreferences = getPreferences(MODE_PRIVATE);
        editor = sharedPreferences.edit();

        user_name = navigationView.getHeaderView(0).findViewById(R.id.user_name);
        user_email = navigationView.getHeaderView(0).findViewById(R.id.user_email);

        mAuth = FirebaseAuth.getInstance();
        user_email.setText(mAuth.getCurrentUser().getEmail());

        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        DatabaseReference databaseReference = firebaseDatabase.getReference("users");

        databaseReference.child(mAuth.getCurrentUser().getUid()).child("surname").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                surname = dataSnapshot.getValue(String.class);

                editor.putString("surname", surname);
                editor.commit();

                user_name.setText(surname + " " + first_name + " " +other_names.substring(0,1));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) { }
        });

        databaseReference.child(mAuth.getCurrentUser().getUid()).child("first_name").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                first_name = dataSnapshot.getValue(String.class);

                editor.putString("first_name", first_name);
                editor.commit();

                user_name.setText(surname + " " + first_name + " " +other_names.substring(0,1));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        databaseReference.child(mAuth.getCurrentUser().getUid()).child("other_names").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                other_names = dataSnapshot.getValue(String.class);

                editor.putString("other_names", other_names);
                editor.commit();

                user_name.setText(surname + " " + first_name + " " +other_names.substring(0,1));
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });

        surname = sharedPreferences.getString("surname","");
        first_name = sharedPreferences.getString("first_name","");
        other_names = sharedPreferences.getString("other_names"," ");
        user_name.setText(surname + " " + first_name + " " + other_names.substring(0,1));

        registerReceiver(new BroadcastReceiver() {

            boolean notification_sent;

            @Override
            public void onReceive(Context context, Intent intent) {
                if(getResultCode() == Activity.RESULT_OK) {
                    if(!notification_sent) {
                       Snackbar snackbar = Snackbar.make(root,"Alert received, help is on the way", Snackbar.LENGTH_LONG);

                       snackbar.getView().setBackgroundColor(Color.WHITE);
                       TextView snackbarTextView =  snackbar.getView().findViewById(com.google.android.material.R.id.snackbar_text);
                       if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1)
                           snackbarTextView.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
                       else
                           snackbarTextView.setGravity(Gravity.CENTER_HORIZONTAL);

                       snackbarTextView.setTextColor(getResources().getColor(R.color.dodgerBlueLight));
                       snackbarTextView.setTypeface(ResourcesCompat.getFont(getBaseContext(),R.font.poppins));

                       snackbar.show();
                        notification_sent = true;
                        HomeFragment.setDelivered(true);
                    }
                }
            }
        }, new IntentFilter(HomeFragment.SMS_DELIVERED));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_layout, homeFragment);
        transaction.commit();

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_home) {
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_layout, homeFragment);
            transaction.commit();

            return true;
        }else if(id ==R.id.action_logout) {

            AlertDialog.Builder builder = new AlertDialog.Builder(DashboardActivity.this);
            builder.setMessage("Do you really want to log out?\n\nIt is highly recommended not to log out too " +
                    "often to be ready for emergency. And if you log out, please do log in soonest").setTitle(
                            Html.fromHtml("<font  color='#30D5F2'>Confirm Logout</font>")).setPositiveButton("YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            mAuth.signOut();
                            startActivity(new Intent(getBaseContext(), LoginActivity.class));
                            finish();
                        }
                    }).setNegativeButton("NO", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {

                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setBackgroundResource(0);
            alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.colorPrimary));
            alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setBackgroundResource(0);


            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        Fragment newFragment = null;
        int id = item.getItemId();

        if (id == R.id.nav_home) {
            newFragment = homeFragment;
        }else if(id == R.id.nav_edit){
           newFragment = profileUpdateFragment;
        }else if(id == R.id.nav_help){
            final Dialog aboutDialog = new Dialog(this);
            aboutDialog.setContentView(R.layout.dialog_about);
            aboutDialog.setCancelable(false);
            aboutDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            aboutDialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

            MaterialButton closeDialogButton = aboutDialog.findViewById(R.id.closeDialog);
            closeDialogButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    aboutDialog.dismiss();
                }
            });
            aboutDialog.show();
        }

        if(newFragment != null){
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            transaction.replace(R.id.fragment_layout, newFragment);
            transaction.commit();
        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
