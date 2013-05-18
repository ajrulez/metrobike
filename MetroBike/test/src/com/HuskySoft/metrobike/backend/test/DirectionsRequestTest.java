package com.HuskySoft.metrobike.backend.test;

import java.util.List;

import junit.framework.TestCase;

import junit.framework.Assert;

import com.HuskySoft.metrobike.backend.DirectionsRequest;
import com.HuskySoft.metrobike.backend.DirectionsStatus;
import com.HuskySoft.metrobike.backend.Route;
import com.HuskySoft.metrobike.backend.TravelMode;

/**
 * This class tests the DirectionsRequest class.
 * 
 * @author coreyh3
 * 
 */
public final class DirectionsRequestTest extends TestCase {

    /**
     * This holds a directionsRequest object for use by other testing methods.
     */
    private DirectionsRequest request = null;

    /**
     * This sets up the test class to use a new directions request object in
     * future tests.
     * 
     * @throws Exception
     */
    // @Before
    public void setUp() {
        request = new DirectionsRequest();

        String startAddress = "6504 Latona Ave NE,Seattle,WA";
        String endAddress = "3801 Brooklyn Ave NE,Seattle,WA";
        request.setStartAddress(startAddress);
        request.setEndAddress(endAddress);
        request.setArrivalTime(4000000);
        request.setTravelMode(TravelMode.TRANSIT);
        request.setMinDistanceToBikeInMeters(1000);
        request.setMaxDistanceToBikeInMeters(2000);
        request.setMinNumberBusTransfers(0);
        request.setMaxNumberBusTransfers(0);
    }

    /**
     * WhiteBox: This tests the toString method.
     */
    // @Test
    public void test_toStringTest() {
        setUp();
        String expected = "DirectionsRequest: RequestParameters:\nstartAddress: 6504 "
                + "Latona Ave NE,Seattle,WA\n" + "endAddress: 3801 Brooklyn Ave NE,Seattle,WA\n"
                + "arrivalTime: 4000000\n" + "departureTime: 0\n" + "travelMode: TRANSIT\n"
                + "minDistanceToBikeInMeters: 1000\n" + "maxDistanceToBikeInMeters: 2000\n"
                + "minNumberBusTransfers: 0\n" + "maxNumberBusTransfers: 0\n" + "solutions: null";

        String actual = request.toString();
        Assert.assertEquals("actual toString for the DirectionRequest object was: " + actual,
                expected, actual);
    }

    /**
     * BlackBox: This tests the doDummyRequest method in the success case.
     */
    // @Test
    public void test_doDummyRequest1Test() {

        request = new DirectionsRequest();

        String startAddress = "University of Washington";
        String endAddress = "3801 Brooklyn Ave NE,Seattle,WA";
        request.setStartAddress(startAddress);
        request.setEndAddress(endAddress);
        request.setDepartureTime(4000000);
        request.setTravelMode(TravelMode.MIXED);
        request.setMinDistanceToBikeInMeters(1000);
        request.setMaxDistanceToBikeInMeters(2000);

        DirectionsStatus expected = DirectionsStatus.REQUEST_SUCCESSFUL;

        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * BlackBox: This tests the doDummyRequest method in the success case.
     */
    // @Test
    public void test_doDummyRequest2Test() {
        request = new DirectionsRequest();

        String startAddress = "The Space Needle";
        String endAddress = "University of Washington, Seattle";
        request.setStartAddress(startAddress);
        request.setEndAddress(endAddress);
        request.setDepartureTime(4000000);
        request.setTravelMode(TravelMode.MIXED);
        request.setMinNumberBusTransfers(0);
        request.setMaxNumberBusTransfers(5);

        DirectionsStatus expected = DirectionsStatus.REQUEST_SUCCESSFUL;

        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * BlackBox: This tests the doDummyRequest method in the success case.
     */
    // @Test
    public void test_doDummyRequest3Test() {

        request = new DirectionsRequest();

        String startAddress = "Jaylens place";
        String endAddress = "Corey's place";
        request.setStartAddress(startAddress);
        request.setEndAddress(endAddress);
        request.setArrivalTime(4000000);
        request.setTravelMode(TravelMode.MIXED);
        request.setMinDistanceToBikeInMeters(1000);
        request.setMaxDistanceToBikeInMeters(2000);
        request.setMinNumberBusTransfers(0);
        request.setMaxNumberBusTransfers(0);
        DirectionsStatus expected = DirectionsStatus.REQUEST_SUCCESSFUL;

        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * BlackBox: This tests the doRequest method in the success case.
     * 
     * @Warning I don't believe we can have more than one test in the test suite
     *          that makes requests to the Google Maps API or else they reject
     *          us causing the test to fail.
     */
    // @Test
    public void test_doRequestTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.REQUEST_SUCCESSFUL;

        request.setArrivalTime(0);
        request.setDepartureTime(4000000);

        DirectionsStatus actual = request.doRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the doRequest method with invalid params.
     */
    // @Test
    public void test_doRequestInvalidParamsTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.INVALID_REQUEST_PARAMS;
        request.setStartAddress(null);

        DirectionsStatus actual = request.doRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the doDummyRequest with the arrival value set.
     */
    // @Test
    public void test_doDummyRequest_ArrivalSetTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.REQUEST_SUCCESSFUL;

        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the doDummyRequest with the departure value set.
     */
    // @Test
    public void test_doDummyRequestDepartureSetTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.REQUEST_SUCCESSFUL;
        request.setArrivalTime(0);
        request.setDepartureTime(4000000);

        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the doDummyRequest with the start address set to
     * null.
     */
    // @Test
    public void test_doDummyRequestStartAddressNullTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.INVALID_REQUEST_PARAMS;
        request.setStartAddress(null);
        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the doDummyRequest with the start address empty
     * ("").
     */
    // @Test
    public void test_doDummyRequest_StartAddressEmptyTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.INVALID_REQUEST_PARAMS;
        request.setStartAddress("");
        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the doDummyRequest with the endAddress null.
     */
    // @Test
    public void test_doDummyRequestEndAddressNullTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.INVALID_REQUEST_PARAMS;
        request.setEndAddress(null);
        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the doDummyRequest with an empty end address.
     */
    // @Test
    public void test_doDummyRequestEndAddressEmptyTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.INVALID_REQUEST_PARAMS;
        request.setEndAddress("");
        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the doDummyRequest without setting the departure or
     * arrival times for a transit call.
     */
    // @Test
    public void test_doDummyRequestFailToSetDepartureAndArrivalTimeforTransitOrMixedTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.INVALID_REQUEST_PARAMS;
        request.setArrivalTime(0);
        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This test should ignore that fact that the departure and
     * arrival times equal zero when bicycling.
     */
    // @Test
    public void test_doDummyRequestBreakOnBicylingModeTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.REQUEST_SUCCESSFUL;

        request.setTravelMode(TravelMode.BICYCLING);
        request.setArrivalTime(0);
        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the do dummy Request with the invalid mode of
     * walking.
     */
    // @Test
    public void test_doDummyRequestInvalidTravelModeWalkingTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.INVALID_REQUEST_PARAMS;

        request.setTravelMode(TravelMode.WALKING);
        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the doDummyRequest method and sets the min greater
     * than the max.
     */
    // @Test
    public void test_doDummyRequestMinDistanceToBikeGreaterThanMaxDistanceTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.INVALID_REQUEST_PARAMS;

        request.setMinDistanceToBikeInMeters(500);
        request.setMaxDistanceToBikeInMeters(400);
        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the doDummyRequest method and sets the min greater
     * than the max.
     */
    // @Test
    public void test_doDummyRequestMinNumberOfTransfersGreaterThanMaxNumberTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.INVALID_REQUEST_PARAMS;

        request.setMinNumberBusTransfers(3);
        request.setMaxNumberBusTransfers(2);
        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the doDummyRequest and sets the value to less than
     * 0.
     */
    // @Test
    public void test_doDummyRequestMinNumberOfTransfersLessThanZeroTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.INVALID_REQUEST_PARAMS;

        request.setMinNumberBusTransfers(-1);
        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the doDummyRequest and sets the value to less than
     * zero.
     * 
     * @Bug https://github.com/alaurenz/metrobike/issues/93
     */
    // @Test
    public void test_doDummyRequestMaxNumberOfTransfersLessThanZeroTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.INVALID_REQUEST_PARAMS;

        request.setMaxNumberBusTransfers(-1);
        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the doDummyRequest and sets the value to less than
     * 0.
     */
    // @Test
    public void test_doDummyRequestMinDistanceToBikeLessThanZeroTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.INVALID_REQUEST_PARAMS;

        request.setMinDistanceToBikeInMeters(-1);
        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests setting the departure time and then the arrival
     * time. It should throw an IllegalArgumentException that is caught.
     */
    // @Test
    public void test_setDepartureThenArrivalTimeTest() {
        setUp();
        request.setArrivalTime(0);
        request.setDepartureTime(100);
        try {
            request.setArrivalTime(100);
        } catch (IllegalArgumentException iae) {
            // It should throw an exception here
            return;
        }

        Assert.fail("An IllegalArgumentException should have been thrown.");
    }

    /**
     * WhiteBox: This tests setting the arrival time and then the departure
     * time. It should throw an IllegalArgumentException that is caught.
     */
    // @Test
    public void test_setArrivalThenDepartureTimeTest() {
        setUp();
        try {
            request.setDepartureTime(100);
        } catch (IllegalArgumentException iae) {
            // It should throw an exception here.
            return;
        }

        Assert.fail("An IllegalArgumentException should have been thrown.");
    }

    /**
     * WhiteBox: This tests the doDUmmyRequest and sets the value to less than
     * 0.
     */
    // @Test
    public void test_doDummyRequest_maxDistanceToBikeLessThanZeroTest() {
        setUp();
        DirectionsStatus expected = DirectionsStatus.INVALID_REQUEST_PARAMS;

        request.setMaxDistanceToBikeInMeters(-1);
        DirectionsStatus actual = request.doDummyRequest();
        Assert.assertEquals(
                "Actual status for request.doRequest() call was: " + actual.getMessage(), expected,
                actual);
    }

    /**
     * WhiteBox: This tests the getErrorMessages method.
     */
    // @Test
    public void test_getErrorMessagesTest() {
        setUp();
        String expected = null;
        request.doDummyRequest();
        String actual = request.getVerboseErrorMessages();
        Assert.assertEquals("Actual error messages from request.getErrorMessages() is: " + actual,
                expected, actual);
    }

    /**
     * WhiteBox: This tests the getSolutions method and checks that the returned
     * list is not null.
     */
    // @Test
    public void test_getSolutionsTest() {
        setUp();
        request.doDummyRequest();
        List<Route> actual = request.getSolutions();
        Assert.assertFalse("Actual value for actual.equals(null) should have been false.",
                actual.equals(null));
    }
}
