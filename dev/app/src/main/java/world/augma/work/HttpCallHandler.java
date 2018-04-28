package world.augma.work;

import android.util.Log;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpCallHandler {

    private static final String TAG = "[".concat(HttpCallHandler.class.getSimpleName()).concat("]:");

    private HttpCallHandler() {}

    public static String makeCall(String reqURL, JSONObject body, String reqType) {
        String response = null;

        try {
            URL url = new URL(reqURL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(reqType);
            connection.setRequestProperty("Content-type", "application/json");

            OutputStream os = connection.getOutputStream();
            os.write(body.toString().getBytes("UTF-8"));
            os.close();

            InputStream in = new BufferedInputStream(connection.getInputStream());
            response = convertToString(in);
        } catch (Exception e) {
            Log.e(TAG, "[".concat(e.getClass().getSimpleName().concat("]:")).concat(e.getMessage()));
        }
        return response;
    }

    private static String convertToString(InputStream in) {
        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
        StringBuilder str = new StringBuilder();

        try {
            String message = null;
            while((message = reader.readLine()) != null) {
                str.append(message).append('\n');
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return str.toString();
    }
}
