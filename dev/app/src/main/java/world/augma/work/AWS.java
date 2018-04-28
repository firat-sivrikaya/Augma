package world.augma.work;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class AWS extends AsyncTask<String, Void, Boolean> {

    /*
     * ------- Amazon Web Service access URL. ----------
     */

    private final String URL = "https://kt0k4ju6dl.execute-api.eu-central-1.amazonaws.com/Deployment/";

    /*
     * ------- Amazon Web Service access URL. ----------
     */

    /*
     * ------ AWS Status Codes -------
     */

    private final String STATUS_APPROVED = "200";

    private final String STATUS_REJECTED = "400";

    /*
     * ------ AWS Status Codes -------
     */

    private final String REQUEST_TYPE_POST = "POST";
    private final String REQUEST_TYPE_GET = "GET";
    private final String MATCH_COUNT = "Count";
    private final String JSON_BODY = "body";
    private final String ITEM_ARRAY = "Items";
    private final String STATUS_CODE = "statusCode";

    private final int VALID = 1;
    private final String TAG = "[".concat(AWS.class.getSimpleName()).concat("]");

    /*
     * -------- Fields below are service dependent! ----------
     */

    private String[] matchingCircleNames;
    private String serviceType;
    private String requestType;
    private int numOfMatches;

    /*
     * -------- Fields above are service dependent! ----------
     */

    @Override
    protected Boolean doInBackground(String... params) {
        try {
            String AWSService = params[0];
            String[] data = new String[params.length - 1];

            for(int i = 0; i < data.length; i++) {
                data[i] = params[i + 1];
            }

            String response = executeServiceCall(AWSService, data);
            JSONObject jsonObject = new JSONObject(response);

            /* Set Log data to inform console */
            serviceType = AWSService;
            numOfMatches = jsonObject.getJSONObject(JSON_BODY).getInt(MATCH_COUNT);

            if(response != null) {

                switch (AWSService) {
                    case Service.LOGIN:
                        return jsonObject.getJSONObject(JSON_BODY).getInt(MATCH_COUNT) == VALID;
                    case Service.REGISTER:
                        return jsonObject.getString(STATUS_CODE).equals(STATUS_APPROVED);
                    case Service.CIRCLE_SEARCH:
                        //TODO Modify when lombok available!!
                        matchingCircleNames = new String[jsonObject.getJSONObject(JSON_BODY).getInt(MATCH_COUNT)];
                        JSONArray circleArr = jsonObject.getJSONObject(JSON_BODY).getJSONArray(ITEM_ARRAY);

                        for(int i = 0; i < matchingCircleNames.length; i++) {
                            matchingCircleNames[i] = circleArr.getJSONObject(i).getString("circleName");
                        }
                        return jsonObject.getJSONObject(JSON_BODY).getInt(MATCH_COUNT) >= VALID;
                    default:
                        matchingCircleNames = null;
                        return false;
                }
            } else {
                Log.e(TAG, "ERROR: Response from AWS is null!");
            }
        } catch (JSONException e) {
            Log.e(TAG, "ERROR: JSONException has been thrown during JSON Object creation.");
        }
        return false;
    }

    public String[] getMatchingCircleNames() {
        return matchingCircleNames;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        serviceType = "[NULL]";
        requestType = "[NULL]";
        numOfMatches = -1;
        Log.i(TAG, "Service execution started.");
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.i(TAG, "Service execution finished. ------->\n\t\t Service Executed: " + serviceType + "\n\t\t Request Type: " + requestType + "\n\t\t # of matches: " + numOfMatches);
    }

    private String executeServiceCall(String AWSService, String... data) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        String reqType = REQUEST_TYPE_POST;

        switch (AWSService) {
            case Service.LOGIN:
                if(data.length == 2) {
                    jsonObject.put("username", data[0]);
                    jsonObject.put("password", data[1]);
                } else {
                    Log.e(TAG, "ERROR: For logging in, there must be 2 data fields, namely, username and password, respectively.");
                    return null;
                }
                break;

            case Service.REGISTER:
                if(data.length == 3) {
                    jsonObject.put("username", data[0]);
                    jsonObject.put("password", data[1]);
                    jsonObject.put("email", data[2]);
                } else {
                    Log.e(TAG, "ERROR: For registering, there must be 3 data fields, namely, username, password and email, respectively.");
                    return null;
                }
                break;

            case Service.CIRCLE_SEARCH:
                if(data.length == 1) {
                    jsonObject.put("circleName", data[0]);
                } else {
                    Log.e(TAG, "ERROR: Single circle name at a time is allowed for searching.");
                    return null;
                }
                break;

            default:
                Log.e(TAG, "ERROR: No such service is provided.");
                return null;
        }
        requestType = reqType;

        return HttpCallHandler.makeCall(URL.concat(AWSService), jsonObject, reqType);
    }

   public static final class Service {

        /* ------ Available Services ------ */

        public static final String LOGIN = "login";

        public static final String REGISTER = "user";

        public static final String CIRCLE = "circle";

        public static final String CIRCLE_SEARCH = "circle/search";
    }
}
