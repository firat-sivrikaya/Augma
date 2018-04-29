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

    /*
     * ------- AWS Fields --------
     */

    private final String REQUEST_TYPE_POST  = "POST";
    private final String MATCH_COUNT        = "Count";
    private final String USER_ID            = "userID";
    private final String JSON_BODY          = "body";
    private final String CIRCLE_ID          = "circleID";
    private final String OWNED_CIRCLE       = "ownedCircle";
    private final String OWNED_NOTES        = "note";
    private final String INVITATION         = "invitation";
    private final String NOTE               = "note";
    private final String NOTE_ID            = "noteID";
    private final String NOTE_RATING        = "rating";
    private final String NOTE_SUPER_RATING  = "beacons";
    private final String CIRCLE_NAME        = "circleName";
    private final String CIRCLE_LIST        = "circleList";
    private final String ITEM_ARRAY         = "Items";
    private final String ITEM               = "Item";
    private final String IMAGE            = "image";
    private final String USERNAME           = "username";
    private final String PASSWORD           = "password";
    private final String EMAIL              = "email";
    private final String STATUS_CODE        = "statusCode";

    /*
     * ------- AWS Fields --------
     */

    private final int VALID = 1;
    private final String TAG = "[".concat(AWS.class.getSimpleName()).concat("]");

    /*
     * -------- Fields below are service dependent! ----------
     */

    private String[] matchingCircleNames;
    private String serviceType;
    private String userID;
    private Note[] matchedNotes;
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
                        userID = userJSON.getJSONObject(ITEM).getString(USER_ID);
                        return jsonObject.getString(STATUS_CODE).equals(STATUS_APPROVED);

                    case Service.GET_NOTE_WITH_FILTER:
                        userJSON = jsonObject.getJSONObject(JSON_BODY);
                        generateNotes(userJSON);
                        return jsonObject.getJSONObject(JSON_BODY).getInt(MATCH_COUNT) >= VALID;

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

    private void generateNotes(JSONObject body) throws JSONException {
        JSONArray itemsArray = body.getJSONArray(ITEM_ARRAY);
        matchedNotes = new Note[body.getInt(MATCH_COUNT)];
        JSONArray circleList;
        List<Circle> cList = new ArrayList<>();
        JSONObject jObj;
        JSONObject iObj;

        for(int i = 0; i < matchedNotes.length; i++) {
            iObj = ((JSONObject) itemsArray.get(i));
            circleList = iObj.getJSONArray(CIRCLE_LIST);

            for(int j = 0; j < circleList.length(); j++) {
                jObj = ((JSONObject) circleList.get(i));
                cList.add(new Circle(jObj.getString(CIRCLE_NAME), jObj.getString(CIRCLE_ID)));
            }

//            matchedNotes[i] = new Note(iObj.getString(NOTE_ID), circleList)
//                    ..
        }
    }

    public String[] getMatchingCircleNames() {
        return matchingCircleNames;
    }

    public Note[] getMatchedNotes() {
        return matchedNotes;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        serviceType = "[NULL]";
        Log.i(TAG, "Service execution started.");
    }

    @Override
    protected void onPostExecute(Boolean aBoolean) {
        super.onPostExecute(aBoolean);
        Log.i(TAG, "Service execution finished. ------->\n\t\t Service Executed: " + serviceType);
    }

    private String executeServiceCall(String AWSService, String... data) throws JSONException {
        JSONObject jsonObject = new JSONObject();

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
            case Service.GET_NOTE_WITH_FILTER:
                if(data.length == 2) {
                    jsonObject.put("lat", Float.parseFloat(data[0]));
                    jsonObject.put("lon", Float.parseFloat(data[1]));
                } else {
                    Log.e(TAG, "ERROR: You must enter latitude and longitute only");
                    return null;
                }
                break;
            case Service.UPLOAD_IMAGE:
                if(data.length == 3) {
                    jsonObject.put(USER_ID, data[0]);
                    jsonObject.put(NOTE_ID, data[1]);
                    jsonObject.put(IMAGE, data[2]);
                } else {
                    Log.e(TAG, "ERROR: You must enter latitude and longitute only");
                    return null;
                }
                break;

            default:
                Log.e(TAG, "ERROR: No such service is provided.");
                return null;
        }

        return HttpCallHandler.makeCall(URL.concat(AWSService), jsonObject, REQUEST_TYPE_POST);
    }

    public String getUserID() {
        return userID;
    }

    public User fetchUser() {
        try {
            JSONArray memberships = userJSON.getJSONArray("circleMembershipList");
            List<Circle> circleMembershipList = new ArrayList<>();

            for (int i = 0; i < memberships.length(); i++) {
                String circleID = ((JSONObject) memberships.get(i)).getString(CIRCLE_ID);
                String name = ((JSONObject) memberships.get(i)).getString(CIRCLE_NAME);
                Circle c = new Circle(circleID, name);
                circleMembershipList.add(c);
            }

            String response = executeServiceCall(Service.GET_USERDATA, userID);
            JSONObject userData = new JSONObject(response);
            JSONObject obj = userData.getJSONObject(JSON_BODY).getJSONObject(ITEM);
            List<Note> ownedNotes = new ArrayList<>();
            List<Circle> invitations = new ArrayList<>();
            List<Circle> ownedCircles = new ArrayList<>();



            //getting ownedCircles
            JSONArray ownedCircle = obj.getJSONArray(OWNED_CIRCLE);
            for(int i = 0; i < ownedCircle.length(); i++) {
                String circleID = ((JSONObject) ownedCircle.get(i)).getString(CIRCLE_ID);
                String name = ((JSONObject) ownedCircle.get(i)).getString(CIRCLE_NAME);
                Circle c = new Circle(circleID, name);
                ownedCircles.add(c);
            }

            //getting ownedNotes
            JSONArray ownedNote = obj.getJSONArray(OWNED_NOTES);
            for(int i = 0; i < ownedNote.length(); i++) {
                String noteID = ((JSONObject) ownedNote.get(i)).getString(NOTE_ID);
                int rating = ((JSONObject) ownedNote.get(i)).getInt(NOTE_RATING);
                int superRating = ((JSONObject) ownedNote.get(i)).getInt(NOTE_SUPER_RATING);
                Note n = new Note(noteID,rating,superRating);
                ownedNotes.add(n);
            }

            //getting invitations
           /*
           //TODO bu part invitation objesi olusturulduktan sonra biticek
           JSONArray invitation = obj.getJSONArray(INVITATION);
            for(int i = 0; i < invitation.length(); i++) {
                String circleID = ((JSONObject) invitation.get(i)).getString(CIRCLE_ID);
                String circleName = ((JSONObject) invitation.get(i)).getString(CIRCLE_NAME);
                String userID = ((JSONObject) invitation.get(i)).getString(USER_ID);
                String userName = ((JSONObject) invitation.get(i)).getString(USERNAME);

            }*/



            return new User(
                    userJSON.getString(USER_ID),
                    userJSON.getString(USERNAME),
                    userJSON.getString("bio"),
                    userJSON.getString(EMAIL),
                    userJSON.getString("name"),
                    userJSON.getString(PASSWORD),
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

        public static final String GET_NOTE_WITH_FILTER = "note/getwithfilter";

        public static final String UPLOAD_IMAGE = "note/uploadimage";
    }
}
