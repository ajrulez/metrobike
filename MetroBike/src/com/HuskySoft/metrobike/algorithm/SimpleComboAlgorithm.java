package com.HuskySoft.metrobike.algorithm;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import com.HuskySoft.metrobike.backend.DirectionsStatus;
import com.HuskySoft.metrobike.backend.DirectionsRequest.RequestParameters;
import com.HuskySoft.metrobike.backend.Leg;
import com.HuskySoft.metrobike.backend.Location;
import com.HuskySoft.metrobike.backend.Route;
import com.HuskySoft.metrobike.backend.Step;
import com.HuskySoft.metrobike.backend.TravelMode;
import com.HuskySoft.metrobike.backend.Utility;
import com.HuskySoft.metrobike.backend.Utility.TransitTimeMode;

/**
 * The simplest "combo" algorithm, this class takes a transit Route and replaces
 * the walking steps with bicycling directions.
 *
 * @author Adrian Laurenzi
 */
public final class SimpleComboAlgorithm extends AlgorithmWorker {

    /**
     * {@inheritDoc}
     */
    @Override
    public DirectionsStatus findRoutes(final RequestParameters toProcess) {
        clearErrors();
        clearResults();

        // NOTE: this method assumes:
        // toProcess.getTravelMode() == TravelMode.MIXED

        // NOTE: This assumes a valid toProcess object (arrival time is set XOR
        // departure time is set). If both the arrival and
        // departure times are set, we'll use the arrival time.
        long routeTime;
        Utility.TransitTimeMode timeMode;
        if (toProcess.getArrivalTime() != 0) {
            timeMode = TransitTimeMode.ARRIVAL_TIME;
            routeTime = toProcess.getArrivalTime();

            // TODO: currently unsupported
            //System.err.println("ERROR: arrival time not yet supported");
            //return addError(DirectionsStatus.INVALID_REQUEST_PARAMS);
        } else {
            timeMode = TransitTimeMode.DEPARTURE_TIME;
            routeTime = toProcess.getDepartureTime();
        }

        List<Route> unsortedTransitRoutes;
        try {
        	unsortedTransitRoutes = getTransitResults(toProcess.getStartAddress(),
                    toProcess.getEndAddress(), routeTime, timeMode);
        } catch (UnsupportedEncodingException e) {
            return addError(DirectionsStatus.UNSUPPORTED_CHARSET);
        }

        // TODO uncomment for comparing combo to normal transit
        // addResults(transitRoutes);
        
        if(unsortedTransitRoutes != null) {
        	List<Route> transitRoutes =
        			Utility.sortRoutesByTransitDuration(unsortedTransitRoutes);
        	
            List<Route> comboResults = new ArrayList<Route>();
            // TODO only use 1 or 2 of these routes...
            for (Route curRoute : transitRoutes) {
                
                Route comboRoute = null;
                if(timeMode.equals(TransitTimeMode.DEPARTURE_TIME)) {
                    comboRoute = replaceWalkingWithBicyclingDeparture(
                        curRoute, routeTime);
                } else if(timeMode.equals(TransitTimeMode.ARRIVAL_TIME)) {
                    comboRoute = replaceWalkingWithBicyclingArrival(
                        curRoute, routeTime);
                }
    
                if (comboRoute != null && comboRoute.getLegList().size() > 0) {
                    comboResults.add(comboRoute);
                }
                
                // For preventing exceeding query request limit
                try {
                    Thread.sleep(TRANSIT_QUERY_DELAY_MS);
                } catch (InterruptedException e) {
                    System.err.println("Error delaying transit query request.");
                }
            }
            addResults(comboResults);
        }

        // If we got no results, return the appropriate status code
        if (getResults() == null || getResults().size() == 0) {
            if (!hasErrors()) {
                // If we didn't notice not getting results somehow, add this
                // error manually.
                addError(DirectionsStatus.NO_RESULTS_FOUND);
            }
            return getMostRecentStatus();
        }

        return markSuccessful();
    }

}
