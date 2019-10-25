package de.applant.cordova.plugin.graphhopper;

public interface RoutingInterface {

     boolean loadMap();

     void calcPath( double fromLat, double fromLon, double toLat, double toLon, RoutingResultDelegate delegate );


}
