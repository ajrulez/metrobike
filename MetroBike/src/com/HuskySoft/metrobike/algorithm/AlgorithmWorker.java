package com.HuskySoft.metrobike.algorithm;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.HuskySoft.metrobike.backend.DirectionsStatus;
import com.HuskySoft.metrobike.backend.DirectionsRequest;
import com.HuskySoft.metrobike.backend.GoogleMapsResponseStatusCodes;
import com.HuskySoft.metrobike.backend.Route;
import com.HuskySoft.metrobike.backend.Utility;
import com.HuskySoft.metrobike.backend.WebRequestJSONKeys;

/**
 * An abstract class for the implementation of a metrobike route-finding
 * algorithm. This abstract class provides error message and result managing
 * facilities and defines the interface for the various algorithm
 * implementations.
 * 
 * @author dutchscout
 */
public abstract class AlgorithmWorker {

    /**
     * The TAG to use in this file for Android Log messages.
     */
    private static final String TAG = "AlgorithmWorker(Abstract)";

    /**
     * The maximum number of consecutive attempts to contact the Google server.
     */
    private static final int MAX_CONNECTION_ATTEMPTS = 5;

    /**
     * The time in milliseconds to wait between retries.
     */
    private static final long CONNECTION_RETRY_DELAY_MS = 500;

    /**
     * Holds any error message(s) generated during algorithm execution.
     */
    private String errorMessages = null;

    /**
     * The most recent status of this request; multiple errors are ignored, only
     * the most recent is stored here.
     */
    private DirectionsStatus mostRecentStatus = DirectionsStatus.NOT_YET_COMPLETE;

    /**
     * Holds the routes found/built by the worker.
     */
    private List<Route> results = null;

    /**
     * Runs the algorithm on the RequestParameters.
     * 
     * @param toProcess
     *            the RequestParameters object describing the search to make
     * @return the final status of the findRoutes process
     */
    public abstract DirectionsStatus findRoutes(DirectionsRequest.RequestParameters toProcess);

    /**
     * Returns true if there was an error, false otherwise.
     * 
     * @return true if there was an error, false otherwise
     */
    public final boolean hasErrors() {
        return mostRecentStatus.isError();
    }

    /**
     * Adds a message to the error message log.
     * 
     * @param theError
     *            the message to add
     * 
     * @return the error that was passed in
     */
    protected final DirectionsStatus addError(final DirectionsStatus theError) {
        return addError(theError, null);
    }

    /**
     * Adds a message to the error message log with extra details.
     * 
     * @param theError
     *            the message to add
     * @param extraDetails
     *            any extra information
     * @return the error that was passed in
     */
    protected final DirectionsStatus addError(final DirectionsStatus theError,
            final String extraDetails) {
        String toAdd = theError.getMessage();
        if (extraDetails != null) {
            toAdd += extraDetails;
        }
        if (errorMessages == null) {
            errorMessages = toAdd;
        } else {
            errorMessages = errorMessages + "\n" + toAdd;
        }

        mostRecentStatus = theError;

        return theError;
    }

    /**
     * Returns a String containing a human-readable error message.
     * 
     * @return A string if there is an error. Null otherwise.
     */
    public final String getErrors() {
        return errorMessages;
    }

    /**
     * Clears all error messages generated during algorithm execution.
     */
    protected final void clearErrors() {
        errorMessages = null;
        mostRecentStatus = DirectionsStatus.NOT_YET_COMPLETE;
    }

    /**
     * The algorithm calls this to mark the findRoutes() process successful.
     * 
     * @return DirectionsStatus.REQUEST_SUCCESSFUL
     */
    protected final DirectionsStatus markSuccessful() {
        mostRecentStatus = DirectionsStatus.REQUEST_SUCCESSFUL;
        errorMessages = null;

        return DirectionsStatus.REQUEST_SUCCESSFUL;
    }

    /**
     * Add results found by the algorithm to the results list.
     * 
     * @param theResults
     *            the results to add
     */
    protected final void addResults(final List<Route> theResults) {
        if (results == null) {
            results = new ArrayList<Route>(theResults);
        } else {
            results.addAll(theResults);
        }
    }

    /**
     * Clear the stored results.
     */
    protected final void clearResults() {
        results = null;
    }

    /**
     * This method returns the list of routes the resulted from the last call to
     * findRoutes().
     * 
     * @return A list of routes that this algorithm considers to be
     *         near-optimal. Returns null if no routes were found, if
     *         findRoutes() has not yet been run, or if there is an error.
     */
    public final List<Route> getResults() {
        return results;
    }

    /**
     * Returns the mostRecentStatus. Meant for use by individual algorithm
     * implementations.
     * 
     * @return the mostRecentStatus
     */
    protected final DirectionsStatus getMostRecentStatus() {
        return mostRecentStatus;
    }

    /**
     * Completes the URL query, handling any likely exceptions.
     * 
     * @param queryURL
     *            the URL to fetch
     * @return the response from the server, or null if there is an error
     */
    protected final String doQueryWithHandling(final String queryURL) {
        String response = null;

        int tryNum = 0;

        while (response == null && tryNum < MAX_CONNECTION_ATTEMPTS) {
            try {
                response = Utility.doQuery(queryURL);
            } catch (IOException e) {
                tryNum++;
                System.err.println(TAG + "Bad connection... retrying " 
                        + (MAX_CONNECTION_ATTEMPTS - tryNum)
                        + " more times.");
                try {
                    Thread.sleep(CONNECTION_RETRY_DELAY_MS);
                } catch (InterruptedException e1) {
                    System.err.println(TAG + "Connection retry interrupted (not a problem)");
                }
            }
        }

        if (response == null) {
            addError(DirectionsStatus.CONNECTION_ERROR);
            return null;
        }
        return response;
    }

    /**
     * A helper method to build a list of routes from a String holding Google's
     * JSON response.
     * 
     * @param srcJSON
     *            the String holding the JSON to parse
     * @return a list of Routes parsed from the JSON
     */
    protected final List<Route> buildRouteListFromJSONString(final String srcJSON) {

        List<Route> routesList = new ArrayList<Route>();

        JSONObject myJSON;

        try {
            myJSON = new JSONObject(srcJSON);
            String statusString = myJSON.getString(WebRequestJSONKeys.STATUS.getLowerCase());
            if (!statusString.equalsIgnoreCase(GoogleMapsResponseStatusCodes.OK.toString())) {
                // TODO: Find a way to let the calling algorithm handle this
                // error
                // Could even be as simple as checking for this error
                addError(DirectionsStatus.NO_RESULTS_FOUND);
                return null;
            } else if (statusString.equalsIgnoreCase(
                    GoogleMapsResponseStatusCodes.OVER_QUERY_LIMIT.toString())) {
                addError(DirectionsStatus.OVER_QUERY_LIMIT);
                return null;
            }
        } catch (JSONException e) {
            addError(DirectionsStatus.PARSING_ERROR);
            return null;
        }

        JSONArray routesArray;
        try {
            routesArray = myJSON.getJSONArray(WebRequestJSONKeys.ROUTES.getLowerCase());
            for (int i = 0; i < routesArray.length(); i++) {
                Route currentRoute = Route.buildRouteFromJSON(routesArray.getJSONObject(i));
                routesList.add(currentRoute);
            }
            System.err.println("JSON_TEST" + "Processed " + routesArray.length() + " routes!");
        } catch (JSONException e1) {
            addError(DirectionsStatus.PARSING_ERROR);
            return null;
        }

        return routesList;
    }
}
