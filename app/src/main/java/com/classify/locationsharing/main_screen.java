package com.classify.locationsharing;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.ncapdevi.fragnav.FragNavController;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabReselectListener;
import com.roughike.bottombar.OnTabSelectListener;
import com.squareup.picasso.Picasso;


public class main_screen extends AppCompatActivity implements FragNavController.RootFragmentListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,LocationListener {

    String userEmail,uidd;


    private BottomBar mBottomBar;
    private GoogleApiClient mgoogleapiclient;
    private FragNavController mNavController;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private LocationRequest mLocationrequest;
    protected FirebaseAuth.AuthStateListener mAuthListener;
    private final int TAB_FIRST = FragNavController.TAB1;
   private final int TAB_SECOND = FragNavController.TAB2;
//    private final int TAB_THREE = FragNavController.TAB3;
    private final int TAB_FOUR = FragNavController.TAB4;
    private final int TAB_FIVE = FragNavController.TAB5;
    DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
    private String Userno;
    public ImageButton menubtn;
    DatabaseHandler db;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_screen);
        db = new DatabaseHandler(this);


        FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        Log.e("LogIt", "Running it      ");
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }


        final ImageView userpic = (ImageView)findViewById(R.id.userpic);
        final TextView user = (TextView)findViewById(R.id.username);
        menubtn = (ImageButton)findViewById(R.id.menu_main);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()==null)
                {
                    startActivity(new Intent(main_screen.this,MainActivity.class));
                    finishAffinity();
                    Log.d("hey11","1234");
                }
                else
                {
                    Intent i =new Intent(getBaseContext(),ServiceTest.class);
                    i.putExtra("uid",firebaseAuth.getCurrentUser().getUid()+"");
                    getBaseContext().startService(i);

                    String grant_permission = db.getGrant();
                    if(grant_permission.equals("true"))
                    {
                        db.updateGrant("false");
                    }
                    else
                    {
                        db.updateGrant("true");
                        Toast.makeText(main_screen.this,"Please enable the autostart for this application",Toast.LENGTH_LONG).show();
                        String manufacture = "xiaomi";
                        if(manufacture.equalsIgnoreCase(Build.MANUFACTURER))
                        {
                            Intent i1 = new Intent();
                            i1.setComponent(new ComponentName("com.miui.securitycenter","com.miui.permcenter.autostart.AutoStartManagementActivity"));
                            startActivity(i1);
                        }
                    }



                    String username = firebaseAuth.getCurrentUser().getDisplayName();
                    userEmail = firebaseAuth.getCurrentUser().getEmail();
                    Log.d("hey11",userEmail+"123");
                    Globalshare.uid = firebaseAuth.getCurrentUser().getUid();
                    Picasso.with(main_screen.this).load(firebaseAuth.getCurrentUser().getPhotoUrl()).resize(50,50).centerCrop().into(userpic);
                    user.setText(username);
                }
            }
        };

        try{
            firebaseDatabase.setPersistenceEnabled(true);
        }catch (Exception e){}

        mgoogleapiclient = new GoogleApiClient.Builder(this).addApi(LocationServices.API).addConnectionCallbacks(this).addOnConnectionFailedListener(main_screen.this).build();
        mgoogleapiclient.connect();


        toolbar = (Toolbar)findViewById(R.id.toolbar);
        mBottomBar = (BottomBar)findViewById(R.id.bottomBar);
        mBottomBar.selectTabAtPosition(TAB_FIRST);

        /*Controller to control movement of tabs*/
        mNavController = new FragNavController(savedInstanceState,getSupportFragmentManager(),R.id.container,this,5,TAB_SECOND);

        menubtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if(mBottomBar.getCurrentTabPosition()==TAB_FIRST)
                {
                    Intent i = new Intent(main_screen.this,ContactToRequest.class);
                    startActivity(i);

                    //Toast.makeText(MainActivity.this,"11",Toast.LENGTH_SHORT).show();

                }
                else  if(mBottomBar.getCurrentTabPosition()==TAB_SECOND)
                {
                    startActivity(new Intent(main_screen.this,MultipleLocation.class));
                }
            //    else  if(mBottomBar.getCurrentTabPosition()==TAB_THREE)
//                {
//                    //Toast.makeText(MainActivity.this,"hi",Toast.LENGTH_SHORT).show();
//
//                }
//                else
//                {
//                    Toast.makeText(MainActivity.this,"12",Toast.LENGTH_SHORT).show();
//
//                }
            }
        });



        /*select tab to move from one fragment to another*/
        mBottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId)
                {
                    case R.id.tab_chats: {
                        mNavController.switchTab(TAB_FIRST);
                        menubtn.setVisibility(View.VISIBLE);
                        menubtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_add_white_24dp));
                        toolbar.setBackgroundDrawable(new ColorDrawable(getResources()
                                .getColor(R.color.blue_new)));
                        break;
                    }

//                    case R.id.tab_groups:
//                        mNavController.switchTab(TAB_SECOND);
//                        menubtn.setVisibility(View.VISIBLE);
//                        menubtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_group_add_white_24dp));
//                        toolbar.setBackgroundDrawable(new ColorDrawable(getResources()
//                                .getColor(R.color.blue_new)));
//                        break;

                    case R.id.tab_People:
                        mNavController.switchTab(TAB_SECOND);
                        menubtn.setVisibility(View.VISIBLE);
                        menubtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_white_24dp));
                        toolbar.setBackgroundDrawable(new ColorDrawable(getResources()
                                .getColor(R.color.blue_new)));
                        break;

//                    case R.id.tab_map:
//                        fragment_emergency.flag_emergency=1;
//                        fragment_emergency.f=10;
//                        mNavController.switchTab(TAB_FOUR);
//                        menubtn.setVisibility(View.INVISIBLE);
//                        menubtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_white_24dp));
//                        toolbar.setBackgroundDrawable(new ColorDrawable(getResources()
//                                .getColor(R.color.red_600)));
//                        break;
//
//                    case R.id.tab_settings:
//                        fragment_emergency.flag_emergency=1;
//                        fragment_emergency.f=10;
//                        mNavController.switchTab(TAB_FIVE);
//                        menubtn.setVisibility(View.INVISIBLE);
//                        menubtn.setImageDrawable(getResources().getDrawable(R.drawable.ic_search_white_24dp));
//                        toolbar.setBackgroundDrawable(new ColorDrawable(getResources()
//                                .getColor(R.color.blue_new)));
//                        break;
                }

            }
        });

        mBottomBar.setOnTabReselectListener(new OnTabReselectListener() {
            @Override
            public void onTabReSelected(@IdRes int tabId) {
                mNavController.clearStack();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);

    }



    @Override
    public Fragment getRootFragment(int i) {
        switch (i)
        {
            case TAB_FIRST:
                return fragment_chats.newInstance(0);
            case TAB_SECOND:
                return fragment_people.newInstance(0);

            default:
                return null;
        }

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        mLocationrequest = LocationRequest.create();
        mLocationrequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        mLocationrequest.setInterval(500);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mgoogleapiclient, mLocationrequest, this);

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        if(location != null )
        {
            Double Lat = location.getLatitude();
            Double Log = location.getLongitude();
            if(Globalshare.uid!=null)
            {
                mRef.child("LocationUser").child(Globalshare.uid).child("latitude").setValue(Lat+"");
                mRef.child("LocationUser").child(Globalshare.uid).child("longitude").setValue(Log+"");
            }


            //android.util.Log.d("latndlon1", Lat+" "+Log);

        }

    }


}
