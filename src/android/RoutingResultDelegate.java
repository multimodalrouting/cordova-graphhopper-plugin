package de.applant.cordova.plugin.graphhopper;

import com.graphhopper.GHResponse;

public interface RoutingResultDelegate {

    void gotGHResponse(GHResponse resp);


}
