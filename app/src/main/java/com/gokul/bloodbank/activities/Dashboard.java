package com.gokul.bloodbank.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.gokul.bloodbank.R;
import com.gokul.bloodbank.fragments.AboutUs;
import com.gokul.bloodbank.fragments.AchievmentsView;
import com.gokul.bloodbank.fragments.BloodInfo;
import com.gokul.bloodbank.fragments.HomeView;
import com.gokul.bloodbank.fragments.NearByHospitalActivity;
import com.gokul.bloodbank.fragments.SearchDonorFragment;
import com.gokul.bloodbank.viewmodels.UserData;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    private FirebaseAuth mAuth;
    private TextView getUserName;
    private TextView getUserEmail;
    private FirebaseDatabase user_db;
    private FirebaseUser cur_user;
    private DatabaseReference userdb_ref;

    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        progressDialog = new ProgressDialog(Dashboard.this,R.style.AppCompatAlertDialogStyle);
        progressDialog.setMessage("Loading...");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        user_db = FirebaseDatabase.getInstance();
        cur_user = mAuth.getCurrentUser();
        userdb_ref = user_db.getReference("users");

        getUserEmail = findViewById(R.id.UserEmailView);
        getUserName = findViewById(R.id.UserNameView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);


        FloatingActionButton fab = findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Dashboard.this,Postctivity.class));
                overridePendingTransition(R.anim.entry,R.anim.exit);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        getUserEmail = (TextView) header.findViewById(R.id.UserEmailView);
        getUserName = (TextView) header.findViewById(R.id.UserNameView);

        Query singleuser = userdb_ref.child(cur_user.getUid());

        progressDialog.show();

        singleuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                String name = dataSnapshot.getValue(UserData.class).getName();

                getUserName.setText(name);
                getUserEmail.setText(cur_user.getEmail());

                progressDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Log.d("User", databaseError.getMessage());
            }
        });

        if (savedInstanceState == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer,new HomeView()).commit();
            navigationView.getMenu().getItem(0).setChecked(true);
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.dashboard, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.donateinfo) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new BloodInfo()).commit();
        }
        if (id == R.id.devinfo) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new AboutUs()).commit();
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.home) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new HomeView()).commit();
            overridePendingTransition(R.anim.entry,R.anim.exit);

        } else if (id == R.id.userprofile) {
            startActivity(new Intent(getApplicationContext(), ProfileActivity.class));
            overridePendingTransition(R.anim.entry,R.anim.exit);

        }
        else if (id == R.id.user_achiev) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new AchievmentsView()).commit();
            overridePendingTransition(R.anim.entry,R.anim.exit);

        }
        else if (id == R.id.logout) {
            mAuth.signOut();
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            overridePendingTransition(R.anim.entry,R.anim.exit);
            finish();
        }
        else if (id == R.id.blood_storage){

            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new SearchDonorFragment()).commit();
            overridePendingTransition(R.anim.entry,R.anim.exit);

        } else if (id == R.id.nearby_hospital) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fragmentcontainer, new NearByHospitalActivity()).commit();
            overridePendingTransition(R.anim.entry,R.anim.exit);

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.entry,R.anim.exit);
            finish();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser == null)
        {
            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.entry,R.anim.exit);
            finish();
        }
    }
}
