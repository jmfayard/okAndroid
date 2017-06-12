package com.github.jmfayard.okandroid.screens.p2p;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Looper;
import android.util.Log;

/**
 *
 */
public class WiFiHelper {
    public static final String LOG_TAG = WiFiHelper.class.getName();

    public static WiFiReceiver setupWiFiReceiver(Application application) {
        final WifiP2pManager manager = (WifiP2pManager) application.getSystemService(Context.WIFI_P2P_SERVICE);
        final WifiP2pManager.Channel channel = manager.initialize(application, Looper.getMainLooper(), null);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION);

        WiFiReceiver wiFiReceiver = new WiFiReceiver(application, manager, channel);
        application.registerReceiver(wiFiReceiver, intentFilter);

        Personality.wifiP2pManager = manager;
        Personality.wifiP2pChannel = channel;

        return wiFiReceiver;
    }


}
