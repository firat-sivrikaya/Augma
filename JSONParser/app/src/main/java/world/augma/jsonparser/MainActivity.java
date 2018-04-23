package world.augma.jsonparser;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    /**
     * -- Aşağıdaki url stringinin içine çekeceği adresi girin --
     */

    private static String url = "https://kt0k4ju6dl.execute-api.eu-central-1.amazonaws.com/Deployment/";

    /**
     * --------------------------
     */

    private String TAG = MainActivity.class.getSimpleName();
    private ListView l;
    private ArrayList<HashMap<String, String>> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        list = new ArrayList<>();
        //l = (ListView) findViewById(R.id.msgList);

        new AugmaJSONParser().execute();
    }

    private class AugmaJSONParser extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Initiated.", Toast.LENGTH_SHORT).show();
        }

        public  String loginUser(String username,String password){
            HttpCallHandler sh = new HttpCallHandler();
            JSONObject obj = new JSONObject();

            try {
                obj.put("username", username);
                obj.put("password", password);

                 String callURL = url+"login";

                 return sh.makeServiceCall(callURL, obj, "POST");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }
        @Override
        protected Void doInBackground(Void... voids) {
            String jsonResponse = "";

            jsonResponse = loginUser("utku","malcagdas123");
            Log.e(TAG, "Gelen response: " + jsonResponse);

            //TODO Buradan sonra gönderilen JSON objesinin içeriğine göre data pointlere ayırırsınız

            if(jsonResponse != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    //Aşağıda array ile başlıyorsa array ismini girin yoksa direk json objesi alıcak şekilde düzenlersiniz.
                    JSONArray jsonArray = jsonObject.getJSONObject("body").getJSONArray("Items");

                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String str3 = obj.getString("userID");
                        String str1 = obj.getString("username");
                        String str2 = obj.getString("password");
                        //...


                        /*
                            Eğer bir index içinde multiple json objesi varsa tag ismiyle de alabilirsiniz

                            JSONObject obj2 = obj.getJSONObject("<tag-name>");

                            String tag1 = obj2.getString("tag1");
                            String tag2 = obj2.getString("tag2");

                            gibi gibi...
                         */

                        HashMap<String, String> objMap = new HashMap<>();

                        objMap.put("str1", str1);
                        objMap.put("str2", str2);
                        objMap.put("str3", str3);
                        //...

                        list.add(objMap);
                    }

                } catch (final JSONException e) {
                    Log.e(TAG, "JSON PARSING ERROR: " + e.getMessage());

                    //Main thread donarsa diye..
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(getApplicationContext(), "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            } else {
                Log.e(TAG, "Serverdan JSON alınamadı.");

                //Main thread donarsa diye..
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "Serverdan JSON alınamadı.", Toast.LENGTH_LONG).show();
                    }
                });
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            //TODO ------>>

        }
    }
}
