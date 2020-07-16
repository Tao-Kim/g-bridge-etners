package com.gbridge.etners.ui.main;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.databinding.DataBindingUtil;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.gbridge.etners.R;
import com.gbridge.etners.databinding.ActivityTempBinding;
import com.gbridge.etners.util.GpsReceiver;
import com.gbridge.etners.util.WifiReceiver;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class TempActivity extends AppCompatActivity {

    String token;
    double _longitude;
    double _latitude;
    private final String TAG = "test";
    ActivityTempBinding binding;
    JSONArray aps = new JSONArray();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_temp);
        binding.setActivity(this);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_WIFI_STATE) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.CHANGE_WIFI_STATE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_WIFI_STATE, Manifest.permission.CHANGE_WIFI_STATE}, 0);
        }
    }

    public void onGetToken(View v) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    URL url = new URL("http://34.82.68.95:3001/auth/login");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("POST");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestProperty("Content-Type", "application/json");

                    JSONObject json = new JSONObject();
                    json.put("employeeNumber", "200000000");
                    json.put("password", "000000");

                    OutputStream os = con.getOutputStream();
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    os.close();

                    int responseCode = con.getResponseCode();
                    Log.d(TAG, "responseCode :" + Integer.toString(responseCode));
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream is = con.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int length = 0;
                        while ((length = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, length);
                        }
                        byteData = baos.toByteArray();

                        String response = new String(byteData);

                        JSONObject responseJSON = new JSONObject(response);
                        token = responseJSON.getString("token");
                        binding.tvResult.setText(responseJSON.toString());

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onCheckToken(View v) {
        binding.tvResult.setText(token);
    }

    public void onCheckGps(View v) {

        new GpsReceiver(TempActivity.this) {
            @NonNull
            @Override
            protected void onReceive(Double latitude, Double longitude) {
                if(latitude != null && longitude != null){
                    binding.tvResult.setText("longitude : " + Double.toString(longitude) + "\nlatitude" + Double.toString(latitude));
                    _latitude = latitude;
                    _longitude = longitude;
                } else {

                }

            }
        };
    }

    public void onClockInGps(View v) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
                    Date date = new Date();
                    TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                    simpleDateFormat.setTimeZone(timeZone);
                    String dateString = simpleDateFormat.format(date);

                    URL url = new URL("http://34.82.68.95:3001/commute/check");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("PATCH");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("x-access-token", token);

                    JSONObject json = new JSONObject();
                    json.put("dateString", dateString);
                    json.put("method", "gps");
                    json.put("type", "in");
                    json.put("latitude", _latitude);
                    json.put("longitude", _longitude);

                    OutputStream os = con.getOutputStream();
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    os.close();

                    int responseCode = con.getResponseCode();
                    Log.d(TAG, "responseCode :" + Integer.toString(responseCode));
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream is = con.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int length = 0;
                        while ((length = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, length);
                        }
                        byteData = baos.toByteArray();

                        String response = new String(byteData);

                        JSONObject responseJSON = new JSONObject(response);
                        binding.tvResult.setText(responseJSON.toString());

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onClockOutGps(View v) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
                    Date date = new Date();
                    TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                    simpleDateFormat.setTimeZone(timeZone);
                    String dateString = simpleDateFormat.format(date);

                    URL url = new URL("http://34.82.68.95:3001/commute/check");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("PATCH");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("x-access-token", token);

                    JSONObject json = new JSONObject();
                    json.put("dateString", dateString);
                    json.put("method", "gps");
                    json.put("type", "out");
                    json.put("latitude", _latitude);
                    json.put("longitude", _longitude);

                    OutputStream os = con.getOutputStream();
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    os.close();

                    int responseCode = con.getResponseCode();
                    Log.d(TAG, "responseCode :" + Integer.toString(responseCode));
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream is = con.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int length = 0;
                        while ((length = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, length);
                        }
                        byteData = baos.toByteArray();

                        String response = new String(byteData);

                        JSONObject responseJSON = new JSONObject(response);
                        binding.tvResult.setText(responseJSON.toString());

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onCheckWifi(View v) {

        new WifiReceiver(TempActivity.this) {
            @NonNull
            @Override
            protected void onReceive(JSONArray aps1) {
                binding.tvResult.setText(aps1.toString());
                aps=aps1;
            }
        };
    }

    public void onClockInWifi(View v) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
                    Date date = new Date();
                    TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                    simpleDateFormat.setTimeZone(timeZone);
                    String dateString = simpleDateFormat.format(date);

                    URL url = new URL("http://34.82.68.95:3001/commute/check");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("PATCH");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("x-access-token", token);

                    JSONObject json = new JSONObject();
                    json.put("dateString", dateString);
                    json.put("method", "wifi");
                    json.put("type", "in");
                    json.put("aps", aps);

                    OutputStream os = con.getOutputStream();
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    os.close();

                    int responseCode = con.getResponseCode();
                    Log.d(TAG, "responseCode :" + Integer.toString(responseCode));
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream is = con.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int length = 0;
                        while ((length = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, length);
                        }
                        byteData = baos.toByteArray();

                        String response = new String(byteData);

                        JSONObject responseJSON = new JSONObject(response);
                        binding.tvResult.setText(responseJSON.toString());

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public void onClockOutWifi(View v) {
        AsyncTask.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy/MM/dd/HH:mm:ss");
                    Date date = new Date();
                    TimeZone timeZone = TimeZone.getTimeZone("Asia/Seoul");
                    simpleDateFormat.setTimeZone(timeZone);
                    String dateString = simpleDateFormat.format(date);

                    URL url = new URL("http://34.82.68.95:3001/commute/check");
                    HttpURLConnection con = (HttpURLConnection) url.openConnection();
                    con.setRequestMethod("PATCH");
                    con.setDoInput(true);
                    con.setDoOutput(true);
                    con.setRequestProperty("Accept", "application/json");
                    con.setRequestProperty("Content-Type", "application/json");
                    con.setRequestProperty("x-access-token", token);

                    JSONObject json = new JSONObject();
                    json.put("dateString", dateString);
                    json.put("method", "wifi");
                    json.put("type", "out");
                    json.put("aps", aps);

                    OutputStream os = con.getOutputStream();
                    os.write(json.toString().getBytes(StandardCharsets.UTF_8));
                    os.flush();
                    os.close();

                    int responseCode = con.getResponseCode();
                    Log.d(TAG, "responseCode :" + Integer.toString(responseCode));
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        InputStream is = con.getInputStream();
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        byte[] byteBuffer = new byte[1024];
                        byte[] byteData = null;
                        int length = 0;
                        while ((length = is.read(byteBuffer, 0, byteBuffer.length)) != -1) {
                            baos.write(byteBuffer, 0, length);
                        }
                        byteData = baos.toByteArray();

                        String response = new String(byteData);

                        JSONObject responseJSON = new JSONObject(response);
                        binding.tvResult.setText(responseJSON.toString());

                    }
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
