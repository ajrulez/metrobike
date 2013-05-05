package com.HuskySoft.metrobike.algorithm;

import java.io.UnsupportedEncodingException;
import java.util.List;

import com.HuskySoft.metrobike.backend.DirectionsRequest.RequestParameters;
import com.HuskySoft.metrobike.backend.Route;
import com.HuskySoft.metrobike.backend.Utility;
import com.HuskySoft.metrobike.backend.Utility.TransitTimeMode;

/**
 * The simplest algorithm, this class simply asks Google for multiple Routes and
 * returns all of them.
 * 
 * @author dutchscout
 */
public final class SimpleAlgorithm extends AlgorithmWorker {

    /**
     * Runs the algorithm on the RequestParameters.
     * 
     * @param toProcess
     *            the RequestParameters object describing the search to make
     */
    @Override
    public void findRoutes(final RequestParameters toProcess) {
        clearErrors();
        clearResults();

        try {
            switch (toProcess.getTravelMode()) {
            case BICYCLING:
                addBicycleResults(toProcess);
                break;
            case TRANSIT:
                addTransitResults(toProcess);
                break;
            case MIXED:
                addBicycleResults(toProcess);
                addTransitResults(toProcess);
                break;
            default:
                addError(AlgorithmError.UNSUPPORTED_TRAVEL_MODE_ERROR, ": "
                        + toProcess.getTravelMode().toString());
                break;
            }
        } catch (UnsupportedEncodingException e) {
            addError(AlgorithmError.UNSUPPORTED_CHARSET);
            return;
        }
    }

    /**
     * Retrieves bicycle directions and adds these to the overall algorithm
     * results.
     * 
     * @param toProcess
     *            the RequestParameters object describing the search to make
     * @throws UnsupportedEncodingException
     *             if there is a problem with the default charset
     */
    private void addBicycleResults(final RequestParameters toProcess)
            throws UnsupportedEncodingException {
        // Build the query string
        String queryString;
        queryString = Utility.buildBicycleQueryString(
                toProcess.getStartAddress(), toProcess.getEndAddress(), true);

        // Fetch the query results
        String jsonResult = doQueryWithHandling(queryString);

        if (jsonResult != null) {
            // Parse the results
            List<Route> result = buildRouteListFromJSONString(jsonResult);
            if (result != null) {
                // Add the results
                addResults(result);
            }
        }
    }

    /**
     * Retrieves transit directions and adds these to the overall algorithm
     * results.
     * 
     * @param toProcess
     *            the RequestParameters object describing the search to make
     * @throws UnsupportedEncodingException
     *             if there is a problem with the default charset
     */
    private void addTransitResults(final RequestParameters toProcess)
            throws UnsupportedEncodingException {
        long routeTime;
        Utility.TransitTimeMode timeMode;

        // NOTE: This assumes a valid toProcess object (arrival time is set XOR
        // departure time is set). If both the arrival and
        // departure times are set, we'll use the arrival time.
        if (toProcess.getArrivalTime() != 0) {
            timeMode = TransitTimeMode.ARRIVAL_TIME;
            routeTime = toProcess.getArrivalTime();
        } else {
            timeMode = TransitTimeMode.DEPARTURE_TIME;
            routeTime = toProcess.getDepartureTime();
        }

        String queryString = Utility.buildTransitQueryString(
                toProcess.getStartAddress(), toProcess.getEndAddress(),
                routeTime, timeMode, true);

        // Fetch the query results
        String jsonResult = doQueryWithHandling(queryString);

        if (jsonResult != null) {
            // Parse the results
            List<Route> result = buildRouteListFromJSONString(jsonResult);
            if (result != null) {
                // Add the results
                addResults(result);
            }
        }
    }
}