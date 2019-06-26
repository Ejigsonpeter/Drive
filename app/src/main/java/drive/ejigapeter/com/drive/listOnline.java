package drive.ejigapeter.com.drive;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
//import android.location.LocationListener;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
//import androidx.recyclerview.widget.RecyclerView.ViewHolder;

import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.firebase.ui.*;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.Api;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;


public class listOnline extends AppCompatActivity implements
GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener, LocationListener
{
    DatabaseReference onlineRef,currentUserRef,counterRef,locations;
    FirebaseRecyclerAdapter <Users,ListOnlineViewHolder> adapter;

    RecyclerView listonline;
    RecyclerView.LayoutManager layoutManager;

    //locstion
    private static  final int MY_PERMISSION_REQUEST_CODE = 7171;
    private static  final int PLAY_SERVICES_RES_REQUEST = 7172;
    private LocationRequest mLocationRequest;
    private GoogleApiClient mGoogleApiclient;
    private Location mLastLocation;
    private static int UPDATE_INTERVAL = 5000;
    private static int FASTEST_INTERVAL = 3000;
    private static int DISTANCE = 10;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_online);
//initialize the view
        listonline = (RecyclerView)findViewById(R.id.listonline);
        listonline.setHasFixedSize(true);


        layoutManager = new LinearLayoutManager(this);
        listonline.setLayoutManager(layoutManager);
//set toolbar and logout menu
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Equiping Tracking System");
        setSupportActionBar(toolbar);

        //online
        locations = FirebaseDatabase.getInstance().getReference("Locations");
        onlineRef = FirebaseDatabase.getInstance().getReference().child(".info/connected");
        counterRef = FirebaseDatabase.getInstance().getReference("lastOnline");
        currentUserRef = FirebaseDatabase.getInstance().getReference("lastOnline").child(FirebaseAuth.getInstance().getCurrentUser().getUid());
if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
)
{
    ActivityCompat.requestPermissions(this,new String[]{
    Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION

},MY_PERMISSION_REQUEST_CODE);
}
else{
    if(checkPlayServices()){
        buildGoogleApiClient();
        createLocationrequest();
        displayLocation();


    }

}



        setUpSystem();
        updateList();
    }

    private void displayLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {

                return;
        }
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiclient);
        if (mLastLocation != null){
            //update to firebase
            locations.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
            .setValue(new Tracking(FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                    FirebaseAuth.getInstance().getCurrentUser().getUid(),
                    String.valueOf(mLastLocation.getLatitude()),
                    String.valueOf(mLastLocation.getLongitude())));

        }
        else{

            //Toast.makeText(this, "couldnt get the location", Toast.LENGTH_SHORT).show();
            Log.d("TEST", "Loading .....please wait: ");
        }
    }

    private void createLocationrequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setSmallestDisplacement(DISTANCE);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    private void buildGoogleApiClient() {
        mGoogleApiclient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
        mGoogleApiclient.connect();



    }

    private boolean checkPlayServices() {
        int resultcode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if (resultcode != ConnectionResult.SUCCESS){
            if (GooglePlayServicesUtil.isUserRecoverableError(resultcode)){
                GooglePlayServicesUtil.getErrorDialog(resultcode,this,PLAY_SERVICES_RES_REQUEST).show();
            }
            else{
                Toast.makeText(this, "Device not Supported", Toast.LENGTH_SHORT).show();
                finish();
            }
            return false;

        }
        return true;

    }

    private void updateList() {
        adapter = new FirebaseRecyclerAdapter<Users, ListOnlineViewHolder>(
                Users.class,
                R.layout.user_online,
                ListOnlineViewHolder.class,
                counterRef
        ) {
            @Override
            protected void populateViewHolder(ListOnlineViewHolder viewHolder, final Users users, int i) {
                    viewHolder.txtEmail.setText(users.getEmail());

                    viewHolder.itemClickListener = new onItemClickListener() {
                        @Override
                        public void onClick(View view, int position) {

                            if(!users.getEmail().equals(FirebaseAuth.getInstance().getCurrentUser().getEmail())){
                                Intent intent = new Intent(listOnline.this,Welcome.class);
                                intent.putExtra("Email",users.getEmail());
                                intent.putExtra("lat",mLastLocation.getLatitude());
                                intent.putExtra("lng",mLastLocation.getLongitude());
                                startActivity(intent);

                            }

                        }
                    };

            }
        };
        adapter.notifyDataSetChanged();
        listonline.setAdapter(adapter);

    }


    private void setUpSystem() {
        onlineRef.addValueEventListener( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if(dataSnapshot.getValue(Boolean.class)){
                    currentUserRef.onDisconnect().removeValue();//remove old values
                    counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                            .setValue(new Users(FirebaseAuth.getInstance().getCurrentUser().getEmail(),"Online"));
                    adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        counterRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot:dataSnapshot.getChildren()){
                    Users users = postSnapshot.getValue(Users.class);
                    Log.d("LOG", "" + users.getEmail() + " is " + users.getStatus());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main,menu);

        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.join:
                counterRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(new Users(FirebaseAuth.getInstance().getCurrentUser().getEmail(),"Online"));
                break;
            case R.id.logout:
                currentUserRef.removeValue();//remove old values
                break;


        }
        return super.onOptionsItemSelected(item);


    }

    /**
     * Called when the location has changed.
     *
     * <p> There are no restrictions on the use of the supplied Location object.
     *
     * @param location The new location, as a Location object.
     */
    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        displayLocation();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mGoogleApiclient != null )
        {
            mGoogleApiclient.connect();
        }
    }

    @Override
    protected void onStop() {
        if (mGoogleApiclient != null){
            mGoogleApiclient.disconnect();
        }
        super.onStop();
    }

    @Override
    protected void onPostResume() {
        super.onPostResume();
        checkPlayServices();
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
       displayLocation();
       startLocationUpdates();
    }

    /**
     * Callback for the result from requesting permissions. This method
     * is invoked for every call on {@link #requestPermissions(String[], int)}.
     * <p>
     * <strong>Note:</strong> It is possible that the permissions request interaction
     * with the user is interrupted. In this case you will receive empty permissions
     * and results arrays which should be treated as a cancellation.
     * </p>
     *
     * @param requestCode  The request code passed in {@link #requestPermissions(String[], int)}.
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link PackageManager#PERMISSION_GRANTED}
     *                     or {@link PackageManager#PERMISSION_DENIED}. Never null.
     * @see #requestPermissions(String[], int)
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode){
            case MY_PERMISSION_REQUEST_CODE:
            {
                if(grantResults.length > 0  && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    if (checkPlayServices()){
                        buildGoogleApiClient();
                        createLocationrequest();
                        displayLocation();

                    }
                }

            }
            break;
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
        ) {

            return;
        }
         LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiclient,mLocationRequest,this);

    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }
}
