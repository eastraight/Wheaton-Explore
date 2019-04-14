package com.example.styledmap;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;

import android.location.Location;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;



import java.util.HashMap;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar mTopToolbar;


    private Button parkingToggle;
    private Button buildingToggle;
    private Button housingToggle;

    private HashMap<String, LocationSpaces> allLocations;
    private DrawerLayout drawerLayout;

    private Marker youAreHere;


    //private static final Context ContextCompat = checkPermission

    /* Object used to receive location updates */
    private FusedLocationProviderClient mFusedLocationClient;
    /* Object that defines important parameters regarding location request. */
    private LocationRequest locationRequest;

    @Override
    //create instance
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mTopToolbar =  findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        allLocations = new HashMap<>();
        drawerLayout = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        // set item as selected to persist highlight
                        //menuItem.setChecked(true);
                        int id = menuItem.getItemId();
                        drawerLayout.closeDrawers();
                        switch (id) {
                            case R.id.dining_button:
                                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wheaton.cafebonappetit.com/"));
                                startActivity(browserIntent);
                                return true;
                        }
                        return true;
                    }
                });
        allLocations = new HashMap<>();

        //Below code to add Toast to toggle buttons.
        parkingToggle = findViewById(R.id.parking_toggle);
        parkingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (LocationSpaces here : allLocations.values()) {
                    if(here.getType().equals("p")) {
                        if (here.getShape().isVisible()) {
                            here.getShape().setVisible(false);
                        } else {
                            here.getShape().setVisible(true);
                        }
                    }
                }
            }
        });

        buildingToggle = findViewById(R.id.building_toggle);
        buildingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (LocationSpaces here : allLocations.values()) {
                    if(here.getType().equals("b")) {
                        if (here.getShape().isVisible()) {
                            here.getShape().setVisible(false);
                        } else {
                            here.getShape().setVisible(true);
                        }
                    }
                }
            }
        });

        housingToggle = findViewById(R.id.housing_toggle);
        housingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for (LocationSpaces here : allLocations.values()) {
                    if(here.getType().equals("h")) {
                        if (here.getShape().isVisible()) {
                            here.getShape().setVisible(false);
                        } else {
                            here.getShape().setVisible(true);
                        }
                    }
                }
            }
        });
//        diningButton = findViewById(R.id.dining_button);
//        diningButton.setMenuItemClickListener(new MenuItem.OnMenuItemClickListener())

        //location stuff:
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        locationRequest = LocationRequest.create();
        locationRequest.setInterval(5000); // 5 second delay between each request
        locationRequest.setFastestInterval(5000); // 5 seconds fastest time in between each request
        locationRequest.setSmallestDisplacement(10); // 10 meters minimum displacement for new location request
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); // enables GPS high accuracy location requests

        sendUpdatedLocationMessage();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.action_favorite) {
            drawerLayout.openDrawer(GravityCompat.END);
            //Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://wheaton.cafebonappetit.com/"));
            //startActivity(browserIntent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            this, R.raw.mapstyle));

            if (!success) {
                Log.e("MapsActivity", "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e("MapsActivity", "Can't find style. Error: ", e);
        }

        // Add a marker in the CS lab and move the camera
        //LatLng meysci = new LatLng(41.869559, -88.096015);
        //mMap.addMarker(new MarkerOptions().position(meysci).title("Meyer Science Center"));

        // Create a LatLngBounds that includes the Campus of Wheaton.
        LatLngBounds WHEATON = new LatLngBounds(
                new LatLng(41.864417, -88.103536), new LatLng(41.873451, -88.088258));
        // Constrain the camera target to the Wheaton.
        mMap.setLatLngBoundsForCameraTarget(WHEATON);

        // Set a preference for minimum and maximum zoom.
        mMap.setMinZoomPreference(15.5f);
        mMap.setMaxZoomPreference(18.5f);

        // Set center point for the map at startup
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(WHEATON.getCenter(), 15.5f));

        locationSetup(mMap);

        // location marker:
        youAreHere = mMap.addMarker(new MarkerOptions()
                .position(new LatLng(41.869559, -88.096015))
                .title("You are here")
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE))
                );
        youAreHere.setVisible(false);
    }

    private void locationSetup(GoogleMap mMap){
//        int campusOutLine = Color.argb(0, 255, 147, 38);
        int bHighlightOrange = Color.argb(200, 255, 147, 38);
        int pHighlightGrey = Color.argb(200, 64, 64, 64);
        int hHighlightBlue = Color.argb(255, 38, 53, 141);
        PolygonOptions polyOpt;
        Polygon poly;
        Building bInsert;
        Parking pInsert;
        Housing hInsert;

        int strokeWidth = 0;


        //Campus outline
//        //Meyer Science Center
//        polyOpt = new PolygonOptions().add(new LatLng(41.869850, -88.096759), new LatLng(41.869851, -88.095732), new LatLng(41.869282, -88.095713), new LatLng(41.869283, -88.096073), new LatLng(41.869634, -88.096077), new LatLng(41.869653, -88.096746),new LatLng(41.869850, -88.096759));
//        //Do not adjust the following 4 lines
//        polyOpt.strokeWidth(0);
//        polyOpt.fillColor(bHighlightOrange);
//        poly = mMap.addPolygon(polyOpt);
//        poly.setVisible(true);
//



        //--------------------------------------------------------------------------------------------------------------------------------------------------


        //--------------------------------------------------------------------------------------------------------------------------------------------------
        // Building Section

        //Meyer Science Center
        polyOpt = new PolygonOptions().add(new LatLng(41.869850, -88.096759), new LatLng(41.869851, -88.095732), new LatLng(41.869282, -88.095713), new LatLng(41.869283, -88.096073), new LatLng(41.869634, -88.096077), new LatLng(41.869653, -88.096746),new LatLng(41.869850, -88.096759));
        //Do not adjust the following 4 lines
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(bHighlightOrange);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        bInsert = new Building(poly, "Meyer Science Center");  //change name
        allLocations.put(bInsert.getName(),bInsert);

        //Student Services Building
        polyOpt = new PolygonOptions().add(new LatLng(41.869160, -88.097786), new LatLng(41.869158, -88.097972), new LatLng(41.869118, -88.097971), new LatLng(41.869121, -88.098089), new LatLng(41.868636, -88.098079), new LatLng(41.868639, -88.097766),new LatLng(41.869160, -88.097786));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(bHighlightOrange);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        bInsert = new Building(poly, "Student Services Building");
        allLocations.put(bInsert.getName(),bInsert);

        //Adams Hall
        polyOpt = new PolygonOptions().add(new LatLng(41.869286, -88.100006), new LatLng(41.869194, -88.100006), new LatLng(41.869192, -88.100045), new LatLng(41.869035, -88.100044), new LatLng(41.869035, -88.099936), new LatLng(41.868987, -88.099942),new LatLng(41.868991, -88.099775), new LatLng(41.869035,-88.099797), new LatLng(41.869037, -88.099692), new LatLng(41.869193, -88.099692), new LatLng(41.869195, -88.099730), new LatLng(41.869286, -88.099732), new LatLng(41.869288, -88.099799), new LatLng(41.869296, -88.099801), new LatLng(41.869294, -88.099936), new LatLng(41.869287, -88.099937));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(bHighlightOrange);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        bInsert = new Building(poly, "Adams Hall");
        allLocations.put(bInsert.getName(),bInsert);

        //Armeding/Conserve
        polyOpt = new PolygonOptions().add( new LatLng(41.870289, -88.098995), new LatLng(41.870289, -88.098779), new LatLng(41.870391, -88.098777), new LatLng(41.870393, -88.098736), new LatLng(41.870572, -88.098736), new LatLng(41.870579, -88.098591), new LatLng(41.870460, -88.098590), new LatLng(41.870465, -88.098462), new LatLng(41.870423, -88.098455), new LatLng(41.870423, -88.098305), new LatLng(41.870468, -88.098305), new LatLng(41.870465, -88.098166), new LatLng(41.870550, -88.098171), new LatLng(41.870552, -88.097931), new LatLng(41.870687, -88.097931), new LatLng(41.870687, -88.098171), new LatLng(41.870728, -88.098171), new LatLng(41.870727, -88.098586), new LatLng(41.870684, -88.098590), new LatLng(41.870681, -88.099040), new LatLng(41.870392, -88.099035), new LatLng(41.870390, -88.099004));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(bHighlightOrange);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        bInsert = new Building(poly, "Convservatory");
        allLocations.put(bInsert.getName(),bInsert);

        //Memorial Student Center
        polyOpt = new PolygonOptions().add( new LatLng(41.869025, -88.098800), new LatLng(41.869029, -88.098650), new LatLng(41.869065, -88.098651), new LatLng(41.869072, -88.098532), new LatLng(41.869215, -88.098540), new LatLng(41.869217, -88.098657), new LatLng(41.869258, -88.098657), new LatLng(41.869253, -88.098811), new LatLng(41.869212, -88.098812), new LatLng(41.869208, -88.098930), new LatLng(41.869067, -88.098923), new LatLng(41.869066, -88.098803));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(bHighlightOrange);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        bInsert = new Building(poly, "MemorialStudentCenter");
        allLocations.put(bInsert.getName(),bInsert);

        //--------------------------------------------------------------------------------------------------------------------------------------------------
        //Parking Section


        //Initializing Blanchard Parking Lot 1
        polyOpt = new PolygonOptions().add(new LatLng(41.868379, -88.098382), new LatLng(41.868326, -88.098956), new LatLng(41.868622, -88.098960), new LatLng(41.868610, -88.098467), new LatLng(41.868588, -88.097944), new LatLng(41.868435, -88.097897));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(pHighlightGrey);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        pInsert = new Parking(poly, "Blanchard Parking I");
        allLocations.put(pInsert.getName(),pInsert);

        //Initializing Blanchard Parking Lot 2
        polyOpt = new PolygonOptions().add(new LatLng(41.868563, -88.100200), new LatLng(41.868359, -88.100168), new LatLng(41.868369, -88.100878), new LatLng(41.868509, -88.100921));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(pHighlightGrey);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        pInsert = new Parking(poly, "Blanchard Parking II");
        allLocations.put(pInsert.getName(),pInsert);

        //North Washington Parking
        polyOpt = new PolygonOptions().add(new LatLng(41.868399, -88.101160), new LatLng(41.868399, -88.101086), new LatLng(41.867504, -88.101084), new LatLng(41.867492, -88.101141));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(pHighlightGrey);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        pInsert = new Parking(poly, "North Washington Parking");
        allLocations.put(pInsert.getName(),pInsert);


        //--------------------------------------------------------------------------------------------------------------------------------------------------
        // Housing Section

        // Williston Hall
        polyOpt = new PolygonOptions().add(new LatLng(41.869177, -88.098268), new LatLng(41.869175, -88.098102), new LatLng(41.868767, -88.098107), new LatLng(41.868766, -88.098259), new LatLng(41.868912, -88.098259), new LatLng(41.868926, -88.098323), new LatLng(41.868997, -88.098330), new LatLng(41.869019, -88.098270));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(hHighlightBlue);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        hInsert = new Housing(poly, "Williston Hall", "Upperclassmen");
        allLocations.put(hInsert.getName(),hInsert);

        // McManis-Evans Hall
        polyOpt = new PolygonOptions().add(new LatLng(41.870430, -88.097756), new LatLng(41.870303, -88.097757), new LatLng(41.870318, -88.097816), new LatLng(41.870089, -88.097813), new LatLng(41.870083, -88.097743), new LatLng(41.869952, -88.097743), new LatLng(41.869971, -88.097816),
                new LatLng(41.869744, -88.097810), new LatLng(41.869743, -88.097764), new LatLng(41.869625, -88.097767), new LatLng(41.869620, -88.098018), new LatLng(41.869741, -88.098017), new LatLng(41.869743, -88.097967), new LatLng(41.869967, -88.097968),
                new LatLng(41.869955, -88.098004), new LatLng(41.870080, -88.098003), new LatLng(41.870089, -88.097966), new LatLng(41.870316, -88.097963), new LatLng(41.870317, -88.098017), new LatLng(41.870432, -88.098016), new LatLng(41.870430, -88.097756));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(hHighlightBlue);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        hInsert = new Housing(poly, "McManis-Evans Hall", "Upperclassmen");
        allLocations.put(hInsert.getName(),hInsert);

        // Fischer Hall
        polyOpt = new PolygonOptions().add(new LatLng(41.873356, -88.096951), new LatLng(41.872813, -88.096946), new LatLng(41.872813, -88.096557), new LatLng(41.873372, -88.096571), new LatLng(41.873367, -88.096357), new LatLng(41.872650, -88.096363), new LatLng(41.872657, -88.097130), new LatLng(41.873372, -88.097123));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(hHighlightBlue);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        hInsert = new Housing(poly, "Fischer Hall", "Underclassmen");
        allLocations.put(hInsert.getName(),hInsert);

        // Smith-Traber Hall
        polyOpt = new PolygonOptions().add(new LatLng(41.870361, -88.094306), new LatLng(41.870321, -88.094452), new LatLng(41.870687, -88.094663), new LatLng(41.870660, -88.094756), new LatLng(41.870625, -88.094819), new LatLng(41.870667, -88.094875), new LatLng(41.870708, -88.094824), new LatLng(41.870754, -88.094853),
                new LatLng(41.870789, -88.094928), new LatLng(41.870780, -88.094963), new LatLng(41.870977, -88.095076), new LatLng(41.870992, -88.095040), new LatLng(41.871037, -88.095064), new LatLng(41.871094, -88.094885), new LatLng(41.871049, -88.094859), new LatLng(41.871067, -88.094813), new LatLng(41.870875, -88.094703),
                new LatLng(41.870905, -88.094613), new LatLng(41.870843, -88.094573), new LatLng(41.870826, -88.094616), new LatLng(41.870763, -88.094572), new LatLng(41.870930, -88.094061), new LatLng(41.870814, -88.093996), new LatLng(41.870666, -88.094485), new LatLng(41.870361, -88.094306));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(hHighlightBlue);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        hInsert = new Housing(poly, "Smith-Traber Hall", "Underclassmen");
        allLocations.put(hInsert.getName(),hInsert);

        //Evans
        polyOpt = new PolygonOptions().add(new LatLng(41.869627, -88.098021), new LatLng(41.869751, -88.098021), new LatLng(41.869751, -88.097972), new LatLng(41.869979, -88.097973), new LatLng(41.869979, -88.097993), new LatLng(41.870091, -88.097997), new LatLng(41.870092, -88.097973), new LatLng(41.870327, -88.097968), new LatLng(41.870320, -88.098023), new LatLng(41.870447, -88.098020), new LatLng(41.870447, -88.097769), new LatLng(41.870327, -88.097766), new LatLng(41.870325, -88.097819), new LatLng(41.870092, -88.097814), new LatLng(41.870091, -88.097774), new LatLng(41.869981, -88.097772), new LatLng(41.869882, -88.097816), new LatLng(41.869750, -88.097814), new LatLng(41.869748, -88.097762), new LatLng(41.869629, -88.097767));
        polyOpt.strokeWidth(strokeWidth);
        polyOpt.fillColor(hHighlightBlue);
        poly = mMap.addPolygon(polyOpt);
        poly.setVisible(false);
        hInsert = new Housing(poly, "Mac-Evans", "Underclassmen");
        allLocations.put(hInsert.getName(),hInsert);



    }




    /*
    (This method from github user kaushikravikumar, RealtimeTaxiAndroidDemo project)
    Needs to be modified for our code

    Checks user's location permission to see whether user has granted access to fine location and coarse location.
    If not it will request these permissions.

 */
    /*
    public void checkPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {//Can add more as per requirement

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                    123);
        }
    }
    */


    /*
    This method gets user's current location
 */
    private void sendUpdatedLocationMessage() {
        Log.d("SEND", "sendUpdatedLocationMessage() in process");

        mFusedLocationClient.requestLocationUpdates(locationRequest, new LocationCallback() {

            @Override
            public void onLocationResult(LocationResult locationResult) {
                Location location = locationResult.getLastLocation();

                Log.d("ONLOCATIONRESULT", "reached onLocationResult");
                // Add a marker on the user's current location
                LatLng whereYouAre = new LatLng(location.getLatitude(), location.getLongitude());
                youAreHere.setPosition(whereYouAre);
                youAreHere.setVisible(true);
            }
        }, Looper.myLooper());


    }





}