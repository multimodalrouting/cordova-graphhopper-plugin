package de.applant.cordova.plugin.graphhopper;

import android.content.Context;
import android.graphics.Path;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.util.Constants;
import com.graphhopper.util.Helper;
import com.graphhopper.util.Parameters;
import com.graphhopper.util.ProgressListener;
import com.graphhopper.util.StopWatch;

import java.io.File;

public class GraphhopperSimpleRouting implements RoutingInterface {

    public GraphhopperSimpleRouting(Context _context) {
        context = _context;
        //log.info("Main activity created");
        mapsFolder = new File(Environment.getExternalStorageDirectory(), "/Download/graphhopper/maps/");

        if (!mapsFolder.exists())
            mapsFolder.mkdirs();
    }

    private Context context = null;
    private String downloadURL = null;
    private File mapsFolder;
    private String currentArea = "sachsen-latest";
    private GraphHopper hopper;


    private void log(String str) {
        Log.i("GH", str);
    }

    private void log(String str, Throwable t) {
        Log.i("GH", str, t);
    }

    private void logUser(String str) {
        log(str);
        Toast.makeText(context, str, Toast.LENGTH_LONG).show();
    }

    @Override
    public boolean loadMap() {
        logUser("loading map");
        loadGraphStorage();
        return true;
    }

    void loadGraphStorage() {
        logUser("loading graph (" + Constants.VERSION + ") ... ");
        new GHAsyncTask<Void, Void, Path>() {
            protected Path saveDoInBackground(Void... v) throws Exception {
                GraphHopper tmpHopp = new GraphHopper().forMobile();
                tmpHopp.load(new File(mapsFolder, currentArea).getAbsolutePath() + "-gh");
                log("found graph " + tmpHopp.getGraphHopperStorage().toString() + ", nodes:" + tmpHopp.getGraphHopperStorage().getNodes());
                hopper = tmpHopp;
                return null;
            }

            protected void onPostExecute(Path o) {
                if (hasError()) {
                    logUser("An error happened while creating graph:"
                            + getErrorMessage());
                    log("error", getError());
                } else {
                    logUser("Finished loading graph. Long press to define where to start and end the route.");
                }

                //finishPrepare();
            }
        }.execute();
    }


    public void calcPath(final double fromLat, final double fromLon,
                         final double toLat, final double toLon, RoutingResultDelegate delegate) {

        log("calculating path ...");
        new AsyncTask<Void, Void, GHResponse>() {
            float time;

            protected GHResponse doInBackground(Void... v) {
                StopWatch sw = new StopWatch().start();
                GHRequest req = new GHRequest(fromLat, fromLon, toLat, toLon).
                        setAlgorithm(Parameters.Algorithms.DIJKSTRA_BI);
                req.getHints().
                        put(Parameters.Routing.INSTRUCTIONS, "false");
                GHResponse resp = hopper.route(req);
                time = sw.stop().getSeconds();
                return resp;
            }

            protected void onPostExecute(GHResponse resp) {
                delegate.gotGHResponse(resp);
                /*if (!resp.hasErrors()) {
                    log("from:" + fromLat + "," + fromLon + " to:" + toLat + ","
                            + toLon + " found path with distance:" + resp.getDistance()
                            / 1000f + ", nodes:" + resp.getPoints().getSize() + ", time:"
                            + time + " " + resp.getDebugInfo());
                    logUser("the route is " + (int) (resp.getDistance() / 100) / 10f
                            + "km long, time:" + resp.getTime() / 60000f + "min, debug:" + time);

                    /*pathLayer = createPathLayer(resp);
                    mapView.map().layers().add(pathLayer);
                    mapView.map().updateMap(true);
                } else {
                    logUser("Error:" + resp.getErrors());
                }*/
                //shortestPathRunning = false;
            }
        }.execute();
    }


    void downloadingFiles() {
        final File areaFolder = new File(mapsFolder, currentArea + "-gh");
        if (downloadURL == null || areaFolder.exists()) {
            loadMap();
            return;
        }

        //final ProgressDialog dialog = new ProgressDialog(this);
        //dialog.setMessage("Downloading and uncompressing " + downloadURL);
        //dialog.setIndeterminate(false);
        //dialog.setMax(100);
        //dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        //dialog.show();

        new GHAsyncTask<Void, Integer, Object>() {
            protected Object saveDoInBackground(Void... _ignore)
                    throws Exception {
                String localFolder = Helper.pruneFileEnd(AndroidHelper.getFileName(downloadURL));
                localFolder = new File(mapsFolder, localFolder + "-gh").getAbsolutePath();
                log("downloading & unzipping " + downloadURL + " to " + localFolder);
                AndroidDownloader downloader = new AndroidDownloader();
                downloader.setTimeout(30000);
                downloader.downloadAndUnzip(downloadURL, localFolder,
                        new ProgressListener() {
                            @Override
                            public void update(long val) {
                                publishProgress((int) val);
                            }
                        });
                return null;
            }

            protected void onProgressUpdate(Integer... values) {
                super.onProgressUpdate(values);
                //dialog.setProgress(values[0]);
                //log(values[0].toString());
            }

            protected void onPostExecute(Object _ignore) {
                //dialog.dismiss();
                if (hasError()) {
                    String str = "An error happened while retrieving maps:" + getErrorMessage();
                    log(str, getError());
                    logUser(getErrorMessage());

                } else {
                    loadMap();
                }
            }
        }.execute();
    }
}
