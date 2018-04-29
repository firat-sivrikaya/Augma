package world.augma.work;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import world.augma.asset.Circle;
import world.augma.asset.Note;
import world.augma.asset.User;

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
    private final String USER_ID = "userID";
    private final String JSON_BODY = "body";
    private final String CIRCLE_ID = "circleID";
    private final String CIRCLE_NAME = "circleName";
    private final String ITEM_ARRAY = "Items";
    private final String USERNAME = "username";
    private final String PASSWORD = "password";
    private final String EMAIL = "email";
    private final String STATUS_CODE = "statusCode";

    private final int VALID = 1;
    private final String TAG = "[".concat(AWS.class.getSimpleName()).concat("]");

    /*
     * -------- Fields below are service dependent! ----------
     */

    private String[] matchingCircleNames;
    private String serviceType;
    private String requestType;
    private String userID;
    private JSONObject userJSON;

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

            if(response != null) {

                switch (AWSService) {
                    case Service.LOGIN:
                        userJSON = jsonObject.getJSONObject(JSON_BODY);
                        userID = userJSON.getJSONArray(ITEM_ARRAY).getJSONObject(0).getString(USER_ID);
                        return jsonObject.getJSONObject(JSON_BODY).getInt(MATCH_COUNT) == VALID;

                    case Service.REGISTER:
                        userJSON = jsonObject.getJSONObject(JSON_BODY);
                        userID = userJSON.getJSONArray(ITEM_ARRAY).getJSONObject(0).getString(USER_ID);
                        return jsonObject.getString(STATUS_CODE).equals(STATUS_APPROVED);

                    case Service.CIRCLE_SEARCH:
                        matchingCircleNames = new String[jsonObject.getJSONObject(JSON_BODY).getInt(MATCH_COUNT)];
                        JSONArray circleArr = jsonObject.getJSONObject(JSON_BODY).getJSONArray(ITEM_ARRAY);

                        for(int i = 0; i < matchingCircleNames.length; i++) {
                            matchingCircleNames[i] = circleArr.getJSONObject(i).getString(CIRCLE_NAME);
                        }
                        return jsonObject.getJSONObject(JSON_BODY).getInt(MATCH_COUNT) >= VALID;

                    case Service.GET_USER:
                        userJSON = jsonObject.getJSONObject(JSON_BODY);
                        userID = userJSON.getJSONArray(ITEM_ARRAY).getJSONObject(0).getString(USER_ID);
                        return jsonObject.getString(STATUS_CODE).equals(STATUS_APPROVED);

                    default:
                        matchingCircleNames = null;
                        return false;
                }
            } else {
                Log.e(TAG, "ERROR: Response from AWS is null!");
            }
        } catch (JSONException e) {
            Log.e(TAG, "ERROR: JSONException has been thrown during JSON Object creation. \t-> "
                    + e.getClass().getSimpleName() + ": " + e.getMessage() + " -> Service: " + serviceType);
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
        Log.i(TAG, "Service execution started.");
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.i(TAG, "Service execution finished. ------->\n\t\t Service Executed: " + serviceType + "\n\t\t Request Type: " + requestType);
    }

    private String executeServiceCall(String AWSService, String... data) throws JSONException {
        JSONObject jsonObject = new JSONObject();
        String reqType = REQUEST_TYPE_POST;

        switch (AWSService) {
            case Service.LOGIN:
                if(data.length == 2) {
                    jsonObject.put(USERNAME, data[0]);
                    jsonObject.put(PASSWORD, data[1]);
                } else {
                    Log.e(TAG, "ERROR: For logging in, there must be 2 data fields, namely, username and password, respectively.");
                    return null;
                }
                break;

            case Service.REGISTER:
                if(data.length == 3) {
                    jsonObject.put(USERNAME, data[0]);
                    jsonObject.put(PASSWORD, data[1]);
                    jsonObject.put(EMAIL, data[2]);
                } else {
                    Log.e(TAG, "ERROR: For registering, there must be 3 data fields, namely, username, password and email, respectively.");
                    return null;
                }
                break;

            case Service.CIRCLE_SEARCH:
                if(data.length == 1) {
                    jsonObject.put(CIRCLE_NAME, data[0]);
                } else {
                    Log.e(TAG, "ERROR: Single circle name at a time is allowed for searching.");
                    return null;
                }
                break;

            case Service.GET_USER:
                if(data.length == 1) {
                    jsonObject.put(USER_ID, data[0]);
                } else {
                    Log.e(TAG, "ERROR: You must only send userID to retrieve user details.");
                    return null;
                }
                break;

            case Service.GET_USERDATA:
                if(data.length == 1) {
                    jsonObject.put("userID", data[0]);
                } else {
                    Log.e(TAG, "ERROR: You must only send userID to retrieve user details.");
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

    public String getUserID() {
        return userID;
    }

    public User fetchUser() {
        try {
            JSONArray memberships = userJSON.getJSONArray("circleMembershipList");
            List<Circle> circleMembershipList = new ArrayList<Circle>();
            for ( int i = 0 ; i < memberships.length() ; i++ )
            {
                String circleID = memberships.getString(0);
                String name = memberships.getString(1);

                Circle c = new Circle(circleID, name);

                circleMembershipList.add(c);
                // burasi boka sarabilir circleMembershipList icin ornek data olmadigi icin o arrayden tam ne donuyor bilmiyoruz
            }

            // TODO : UserData'ya request atilip OwnedNotes, Invitations ve OwnedCircle cekilecek
            List<Note> ownedNotes = new ArrayList<Note>();
            List<Circle> invitations = new ArrayList<Circle>();
            List<Circle> ownedCircles = new ArrayList<Circle>();

            return new User(
                    userJSON.getString("userID"),
                    userJSON.getString("username"),
                    userJSON.getString("bio"),
                    userJSON.getString("email"),
                    userJSON.getString("name"),
                    userJSON.getString("password"),
                    userJSON.getString("profilePic"),
                    userJSON.getString("birthdate"),
                    userJSON.getInt("type"),
                    circleMembershipList,
                    invitations,
                    ownedNotes,
                    ownedCircles
                    );

        } catch (JSONException e) {
            e.printStackTrace();
            Log.e("ERROR", "Failed retrieving User object from AWS");
            return null;
        }
    }

    public static final class Service {

        /* ------ Available Services ------ */

        public static final String LOGIN = "login";

        public static final String REGISTER = "user";

        public static final String CIRCLE = "circle";

        public static final String CIRCLE_SEARCH = "circle/search";

        public static final String GET_USER = "user/getuser";

        public static final String GET_USERDATA = "user/userdata";
    }
}