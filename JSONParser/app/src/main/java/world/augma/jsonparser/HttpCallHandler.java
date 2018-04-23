package world.augma.jsonparser;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class HttpCallHandler {

    private static final String TAG = HttpCallHandler.class.getSimpleName();

    public HttpCallHandler() {}

    public String makeServiceCall(String reqUrl) throws JSONException{
        String response = null;
        try {
            URL url = new URL(reqUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            JSONObject obj = new JSONObject();
            obj.put("username", "utku");
            obj.put("password", "malcagdas123");

            OutputStream os = connection.getOutputStream();
            os.write(obj.toString().getBytes("UTF-8"));
            os.close();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            response = convertToString(in);
        } catch (ProtocolException e) {
            Log.e(TAG, "ProtocolException: " + e.getMessage());
        } catch (MalformedURLException e) {
            Log.e(TAG, "MalformedURLException: " + e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, "IOException: " + e.getMessage());
        }
        return response;
    }

    private String convertToString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder str = new StringBuilder();

        String message = null;
        try {
            while((message = reader.readLine()) != null) {
                str.append(message).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str.toString();
    }
}
