package com.example.styledmap;

import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;


public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Toolbar mTopToolbar;


    private Button parkingToggle;
    private Button buildingToggle;
    private Button housingToggle;

    private Polygon[] buildingPolygons;

    @Override
    //create instance
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mTopToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mTopToolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //adjust size later
        buildingPolygons = new Polygon[11];

        //Below code to add Toast to toggle buttons.
        parkingToggle = findViewById(R.id.parking_toggle);
        parkingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Parking is filtered", Toast.LENGTH_LONG).show();
            }
        });

        buildingToggle = findViewById(R.id.building_toggle);
        buildingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //The following if block toggles all the polygons of type "building" into visibility and out.
                if(buildingPolygons[0].isVisible()){
                    for(Polygon g: buildingPolygons)
                        if(g == null){
                            break;
                        } else {
                            g.setVisible(false);
                        }
                } else {
                    for(Polygon g: buildingPolygons)
                        if(g == null){
                            break;
                        } else {
                            g.setVisible(true);
                        }
                }
            }
        });

        housingToggle = findViewById(R.id.housing_toggle);
        housingToggle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Housing is filtered", Toast.LENGTH_LONG).show();
            }
        });
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
            Toast.makeText(MapsActivity.this, "Insert drop-down menu here", Toast.LENGTH_LONG).show();
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
        LatLng meysci = new LatLng(41.869559, -88.096015);
        mMap.addMarker(new MarkerOptions().position(meysci).title("Meyer Science Center"));

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

        buildingSetup(mMap);
    }

    private void buildingSetup(GoogleMap mMap){


        /*
        *the first polygon for testing and demo (MeySci)
        * replace "examplePoly" with new name for building
        */

        //Defining coordinates of the polygon
        PolygonOptions examplePoly = new PolygonOptions().add(new LatLng(41.869850, -88.096759), new LatLng(41.869851, -88.095732), new LatLng(41.869282, -88.095713), new LatLng(41.869283, -88.096073), new LatLng(41.869634, -88.096077), new LatLng(41.869653, -88.096746),new LatLng(41.869850, -88.096759));
        //Only replace the name "example poly" for the next two lines
        examplePoly.strokeWidth(0);
        examplePoly.fillColor(Color.BLUE);
        //for a new building, do not say "Polygon test" just say "test".  Otherwise copy exactly
        Polygon test = mMap.addPolygon(examplePoly);
        test.setVisible(false);
        buildingPolygons[0] = test;

        //insert more buildings here
    }

}