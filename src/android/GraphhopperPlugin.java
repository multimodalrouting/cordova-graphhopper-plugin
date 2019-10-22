package de.applant.cordova.plugin.graphhopper;

import android.content.Context;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;

public class GraphhopperPlugin extends CordovaPlugin {

    RoutingInterface routing = null;

    @Override
    public void initialize(CordovaInterface cordova, CordovaWebView webView) {
        super.initialize(cordova, webView);
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
        if(routing == null)
            return false;

        if(action.equalsIgnoreCase("loadMap")) {
            routing.loadMap();
        } else {
            ret = false;
        }
        return ret;
    }

    @Override
    public void onStart() {
        super.onStart();
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
    private Context getContext () {
        return cordova.getActivity();
    }
}
