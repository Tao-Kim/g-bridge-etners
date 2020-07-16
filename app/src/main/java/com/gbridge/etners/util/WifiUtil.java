package com.gbridge.etners.util;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.DhcpInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;

import org.json.JSONArray;

import java.util.List;


/*
    필요 퍼미션
    ACCESS_WIFI_STATE, CHANGE_WIFI_STATE

    성공 : Sting ap
    실패 : null (연결된 wifi가 없거나, 퍼미션 없음)

    example
    String ap = WifiUtil.getAp(Activity.Context);
 */
public class WifiUtil {

    public static String getAp(@NonNull final Context context){
        if (ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            return null;
        }
        final WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        String bssid = wifiInfo.getBSSID();
        if(bssid != null){
            return bssid + wifiInfo.getIpAddress();
        } else {
            return  null;
        }
    }

}
