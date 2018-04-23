package world.augma.jsonparser;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListView;
import android.widget.SimpleAdapter;
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

    private static String url = "https://kt0k4ju6dl.execute-api.eu-central-1.amazonaws.com/Deployment/login";

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
        l = (ListView) findViewById(R.id.msgList);

        new AugmaJSONParser().execute();
    }

    private class AugmaJSONParser extends AsyncTask<Void, Void, Void> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            Toast.makeText(MainActivity.this, "Initiated.", Toast.LENGTH_SHORT).show();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            HttpCallHandler sh = new HttpCallHandler();
            String jsonResponse = "";
            try
            {
                jsonResponse = sh.makeServiceCall(url);
            }
            catch (final JSONException e) {
                Log.e(TAG, "JSON PARSING ERROR: " + e.getMessage());

                //Main thread donarsa diye..
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getApplicationContext(), "JSON Parsing Error: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }
            Log.e(TAG, "Gelen response: " + jsonResponse);

            //TODO Buradan sonra gönderilen JSON objesinin içeriğine göre data pointlere ayırırsınız

            if(jsonResponse != null) {
                try {
                    JSONObject jsonObject = new JSONObject(jsonResponse);

                    //Aşağıda array ile başlıyorsa array ismini girin yoksa direk json objesi alıcak şekilde düzenlersiniz.
                    JSONArray jsonArray = jsonObject.getJSONArray(/* TODO JSON Array adı */"Login");

                    for(int i = 0; i < jsonArray.length(); i++) {
                        JSONObject obj = jsonArray.getJSONObject(i);

                        String str3 = obj.getString("userID");
                        String str1 = obj.getString("userName");
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

            l.setAdapter(new SimpleAdapter(MainActivity.this, list, R.layout.list_item, new String[]{
                    "str1",
                    "str2",
                    "str3",
                    //HashMap'deki keylerin tam adı gelmeli, buradaki keylerin itemleri, list item olarak sıralanacak

            }, new int[]{
                    R.id.msg1,
                    R.id.msg2,
                    R.id.msg3,
                    //Kaç entry okunacaksa hepsinin idleri.. (Bunlar list_item.xml'in içindeki idler)
            }));
        }
    }
}
