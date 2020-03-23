package com.example.traceme;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SyncResult;
import android.database.Cursor;
import android.net.SSLCertificateSocketFactory;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import org.apache.http.conn.ssl.AllowAllHostnameVerifier;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import javax.net.ssl.HttpsURLConnection;

public class SyncAdapter extends AbstractThreadedSyncAdapter {
//    private static final String serverURL = "http://35.207.134.96:8000/upload";
//    private static final String serverURL = "http://10.0.2.2:8000/upload";
      private static final String serverURL = "http://192.168.43.95:8000/upload";
private Context context;
    ContentResolver contentResolver;

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        contentResolver = context.getContentResolver();
        context = this.context;
    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        contentResolver = context.getContentResolver();
    }

    public void syncNow(){

    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority, ContentProviderClient provider, SyncResult syncResult) {

        JSONArray resultSet = new JSONArray();
        Long lastInsertRowID = 0L;

        DatabaseHelper dbHelper = new DatabaseHelper(getContext());
        //        Location
        Cursor location = dbHelper.getNewLocation();
        if (location != null && location.getCount() > 0) {
            try {
                while (location.moveToNext()) {
                    JSONObject rowObject = new JSONObject();
                    rowObject.put("latitude", location.getString(1));
                    rowObject.put("longitude", location.getString(2));
                    rowObject.put("timestamp", location.getString(3));
                    Log.i("decimal data", location.getString(1));
                    resultSet.put(rowObject);
                }
                Log.i("resultset",resultSet.toString());
            } catch (JSONException e) {
                Log.e("Error", e.toString());
            } finally {
                if (location != null && !location.isClosed()) {
                    location.close();
                }
            }

        }

        if (HttpPost(resultSet)) {
            dbHelper.locationUpdated();
            dbHelper.deleteLoc();
        }
        dbHelper.close();
    }

    private boolean HttpPost(JSONArray resultSet) {
        String result = "";
        JSONObject JsonResult;

        try {

            String auth_token = Settings.Secure.getString(getContext().getContentResolver(), Settings.Secure.ANDROID_ID);
            URL url = new URL(serverURL);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if (conn instanceof HttpsURLConnection) {
                HttpsURLConnection httpsConn = (HttpsURLConnection) conn;
                httpsConn.setSSLSocketFactory(SSLCertificateSocketFactory.getInsecure(0, null));
                httpsConn.setHostnameVerifier(new AllowAllHostnameVerifier());
            }
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("auth_token", auth_token);
            Log.v("auth_token",auth_token);
            setPostRequestContent(conn, resultSet);
            conn.connect();
            result = conn.getResponseMessage();
            Log.i("Data responsesss", result+conn.getResponseCode());

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                Log.i("Sach hai", "Working");
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                StringBuilder sb = new StringBuilder();
                String inputLine = "";
                while ((inputLine = br.readLine()) != null) {
                    sb.append(inputLine);
                }
                br.close();
                JsonResult = new JSONObject(sb.toString());
                Log.i("Data", sb.toString());
                Log.i("JsonExc", JsonResult.toString());
                if (JsonResult.getString("status").equals("success")) {
                    return true;
                }
            }
        } catch (MalformedURLException e) {
            Log.e("Student keyloggerMalf", e.toString());
        } catch (IOException e) {
            Log.e("Student keyloggerIOE", e.toString());
        } catch (JSONException e) {
            Log.e("Error", e.toString());
        }
        return false;
    }
    private void setPostRequestContent(HttpURLConnection conn, JSONArray resultSet) throws IOException {
        OutputStream os = conn.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
        writer.write(resultSet.toString());
        Log.i(MainActivity.class.toString(), resultSet.toString());
        writer.flush();
        writer.close();
        os.close();
    }

}
