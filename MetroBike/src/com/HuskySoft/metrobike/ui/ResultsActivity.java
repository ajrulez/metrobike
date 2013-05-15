package com.HuskySoft.metrobike.ui;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.HuskySoft.metrobike.R;
import com.HuskySoft.metrobike.backend.Leg;
import com.HuskySoft.metrobike.backend.Location;
import com.HuskySoft.metrobike.backend.Route;
import com.HuskySoft.metrobike.backend.Step;
import com.HuskySoft.metrobike.backend.TravelMode;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;

/**
 * 
 * @author mengwan
 * 
 */

public class ResultsActivity extends Activity {

    /**
     * duration of the animated camera in the map.
     */
    private static final int ANIMATED_CAMERA_DURATION_IN_MILLISECOND = 3000;

    /**
     * GoogleMap object stored here to be modified.
     */
    private GoogleMap mMap;

    /**
     * Results from the search.
     */
    private ArrayList<Route> routes;

    /**
     * Current route that should be displayed on the map.
     */
    private int currRoute = -1;

    /**
     * {@inheritDoc}
     * 
     * @see android.app.Activity#onCreate(android.os.Bundle)
     */
    @Override
    protected final void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // get the solution from the search activity
        @SuppressWarnings("unchecked")
        List<Route> recievedRoutes = (ArrayList<Route>) getIntent().getSerializableExtra(
                "List of Routes");

        // set the default route to be the first route of the solution

        setContentView(R.layout.activity_results);
        if (recievedRoutes != null) {
            routes = (ArrayList<Route>) recievedRoutes;
            currRoute = (Integer) getIntent().getSerializableExtra("Current Route Index");
            addRouteButtons();
            mMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.map)).getMap();
            drawRoute();
        }
    }

    /**
     * Show the menu bar when the setting button is clicked.
     * 
     * @param menu
     *            The options menu in which you place your items.
     * @return true if the menu to be displayed.
     * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
     */
    @Override
    public final boolean onCreateOptionsMenu(final Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_menu, menu);
        return true;
    }

    /**
     * this method will be called when user click buttons in the setting menu.
     * 
     * @param item
     *            the menu item that user will click
     * @return true if user select an item
     */
    @Override
    public final boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
        case R.id.action_settings:
            // user click the setting button, start the settings activity
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        default:
            return super.onOptionsItemSelected(item);
        }
    }

    /**
     * Go to the Navigate Page.
     * 
     * @param view
     *            : the view of the button onClick function of the go to
     *            Navigate button
     */
    public final void goToNavigate(final View view) {
        // Do something in response to button
        Intent intent = new Intent(this, NavigateActivity.class);
        intent.putExtra("List of Routes", (Serializable) routes);
        intent.putExtra("Current Route Index", currRoute);
        startActivity(intent);
    }

    /**
     * Direct to search page.
     * 
     * @param view
     *            the view of the button onClick function of the return to
     *            search page button
     */
    public final void goToSearchPage(final View view) {
        // Do something in response to button
        Intent intent = new Intent(this, SearchActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
    }

    /**
     * Direct to detail page.
     * 
     * @param view
     *            : the view of the button onClick function of the go to details
     *            button
     */
    public final void goToDetail(final View view) {
        // Do something in response to button
        Intent intent = new Intent(this, DetailsActivity.class);
        intent.putExtra("List of Routes", (Serializable) routes);
        intent.putExtra("Current Route Index", currRoute);
        startActivity(intent);
    }

    /**
     * add the selecting route buttons to the scroll bar on left.
     */
    private void addRouteButtons() {
        LinearLayout main = (LinearLayout) findViewById(R.id.linearLayoutForRouteSelection);
        for (int i = 0; i < routes.size(); i++) {
            Button selectRouteBtn = new Button(this);
            selectRouteBtn.setText("Route" + (i + 1));
            selectRouteBtn.setPadding(0, 0, 0, 0);
            selectRouteBtn.setOnClickListener(new MyOnClickListener(i));
            main.addView(selectRouteBtn);
        }
    }

    /**
     * Implemented onClickListener in order to passed in parameter.
     * 
     * @author mengwan
     * 
     */
    private class MyOnClickListener implements OnClickListener {
        /**
         * stores the routeNumber gets passed in.
         */
        private int routeNumber;

        /**
         * Create a MyOnClickListener object.
         * 
         * @param routeSelectionNumber
         *            Route number that is passed in.
         */
        public MyOnClickListener(final int routeSelectionNumber) {
            this.routeNumber = routeSelectionNumber;
        }

        @Override
        public void onClick(final View v) {
            currRoute = routeNumber;
            drawRoute();
        }
    };

    /**
     * draw the current route on the map.
     */
    private void drawRoute() {
        Toast.makeText(getApplicationContext(), "" + com.HuskySoft.metrobike.ui.utility.Utility.getCameraZoomLevel(routes.get(currRoute)), Toast.LENGTH_SHORT).show();
        if (currRoute >= 0 && currRoute < routes.size()) {
            //clear the map drawing first
            mMap.clear();
            
            //get the source and destination
            List<Leg> legs = routes.get(currRoute).getLegList();
            Location start = legs.get(0).getStartLocation();
            Location end = legs.get(legs.size() - 1).getStepList()
                    .get(legs.get(legs.size() - 1).getStepList().size() - 1).getEndLocation();

            //draw Markers for starting and ending points
            mMap.addMarker(new MarkerOptions()
                    .position(com.HuskySoft.metrobike.ui.utility.Utility.convertLocation(start))
                    .title("Start Here!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.starting)));

            mMap.addMarker(new MarkerOptions()
                    .position(com.HuskySoft.metrobike.ui.utility.Utility.convertLocation(end)).title("End Here!")
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ending)));
            
            mMap.addCircle(new CircleOptions()
            .center(com.HuskySoft.metrobike.ui.utility.Utility.convertLocation(start))
            .radius(4)
            .strokeColor(Color.BLACK).strokeWidth(3)
            .fillColor(Color.WHITE).zIndex(2));
            
            
            //draw Poly-line on the map
            
            for (Leg l : legs) {
                for (Step s : l.getStepList()) {
                    PolylineOptions polylineOptions = new PolylineOptions();
                    for (LatLng ll : com.HuskySoft.metrobike.ui.utility.Utility.convertLocationList(s.getPolyLinePoints())) {
                        polylineOptions = polylineOptions.add(ll);
                    }
                    if (s.getTravelMode() == TravelMode.TRANSIT) {
                        mMap.addPolyline(polylineOptions.color(Color.argb(200, 255, 0, 0)).width(12f));
                    } else {
                        mMap.addPolyline(polylineOptions.color(Color.argb(200, 0, 0, 255)).width(8f).zIndex(1));
                    }
                    mMap.addCircle(new CircleOptions()
                    .center(com.HuskySoft.metrobike.ui.utility.Utility.convertLocation(s.getEndLocation()))
                    .radius(4)
                    .strokeColor(Color.BLACK).strokeWidth(3)
                    .fillColor(Color.WHITE).zIndex(2));
                }
            }
            

            //set the camera to focus on the route
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    com.HuskySoft.metrobike.ui.utility.Utility.getCameraCenter(routes.get(currRoute)), 
                    com.HuskySoft.metrobike.ui.utility.Utility.getCameraZoomLevel(routes.get(currRoute))),
                    ANIMATED_CAMERA_DURATION_IN_MILLISECOND, null);
        }
    }
}