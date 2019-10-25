package de.applant.cordova.plugin.graphhopper;

import android.content.Context;
import android.util.Log;

import com.graphhopper.GHResponse;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.locationtech.jts.geom.LineString;

import java.sql.Array;
import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class GraphhopperPlugin extends CordovaPlugin {

    RoutingInterface routing = null;


    private void log(String str) {
        Log.i("GH", str);
    }

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
        log("graphhopper plugin initialized");

    }

    @Override
    protected void pluginInitialize() {
        log("Graphhopper initialized");
    }

    /**
     * Executes the request.
     * <p>
     * This method is called from the WebView thread. To do a non-trivial amount of work, use:
     * cordova.getThreadPool().execute(runnable);
     * <p>
     * To run on the UI thread, use:
     * cordova.getActivity().runOnUiThread(runnable);
     *
     * @param action          The action to execute.
     * @param args            The exec() arguments, wrapped with some Cordova helpers.
     * @param callbackContext The callback context used when calling back into JavaScript.
     * @return Whether the action was valid.
     */
    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {
        boolean ret = true;
        if (routing == null)
            return false;

        switch (action.toLowerCase()) {
            case "loadmap":
                routing.loadMap();
                break;
            case "route":
                double fromLat, fromLon, toLat, toLon;
                try {
                    JSONObject options = args.getJSONObject(0);
                    JSONObject startPoint = options.getJSONObject("start");
                    JSONObject endPoint = options.getJSONObject("end");
                    fromLat = startPoint.getDouble("lat");
                    fromLon = startPoint.getDouble("lng");
                    toLat = endPoint.getDouble("lat");
                    toLon = endPoint.getDouble("lng");

                } catch (Exception e) {
                    callbackContext.error("Incorrect options specified");
                    return false;
                }
                routing.calcPath(fromLat, fromLon, toLat, toLon, resp -> {
                    LineString lineString = resp.getBest().getPoints().toLineString(false);
                    Optional<String> coordinates = Arrays.stream(lineString.getCoordinates())
                            .map(coordinate -> "[" + coordinate.x + "," + coordinate.y + "]")
                            .reduce((coord, pre) -> pre + "," + coord);
                    if(!coordinates.isPresent()) {
                        callbackContext.error("cannot create coordinate array string");
                    } else {
                        try {
                            JSONArray result = new JSONArray(
                                    new JSONTokener( "{" +
                                            "   \"points\": {" +
                                            "       \"coordinates\": [" +
                                            coordinates.get() +
                                            "        ]," +
                                            "       \"type\": \"LineString\"" +
                                            "   }" +
                                            "}")
                            );
                            callbackContext.success(result);
                        } catch (JSONException e) {
                            e.printStackTrace();
                            callbackContext.error(e.getMessage());
                        }
                    }
                });
                break;
            default:
                ret = false;

        }

        return ret;
    }

    @Override
    public void onStart() {
        super.onStart();
        log("Graphhopper onStart");
        routing = new GraphhopperSimpleRouting(getContext());
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    /**
     * Returns the context of the activity.
     */
    private Context getContext() {
        return cordova.getActivity();
    }
}
