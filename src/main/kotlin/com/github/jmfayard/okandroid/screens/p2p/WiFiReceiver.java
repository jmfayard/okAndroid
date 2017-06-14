package com.github.jmfayard.okandroid.screens.p2p;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

public class WiFiReceiver extends BroadcastReceiver implements WifiP2pManager.ConnectionInfoListener, WifiP2pManager.PeerListListener {
    public static final String LOG_TAG = WiFiReceiver.class.getName();

    private final Context _context;

    private final WifiP2pManager _manager;
    private final WifiP2pManager.Channel _channel;
    private WifiP2pGroup p2pGroup;

    public WiFiReceiver(Context context, WifiP2pManager manager, WifiP2pManager.Channel channel) {
        super();

        _context = context;

        _manager = manager;
        _channel = channel;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.d(LOG_TAG, "onReceive:" + intent.getAction());

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                Log.d(LOG_TAG, "wifi state enabled");
                P2PScreen.Companion.setWifiEnabled(true);
            } else {
                Log.d(LOG_TAG, "wifi state disabled");
                P2PScreen.Companion.setWifiEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION.equals(action)) {
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
                Log.d(LOG_TAG, "wifi discovery start");
                P2PScreen.Companion.setWifiDiscovery(true);
            } else {
                Log.d(LOG_TAG, "wifi discovery stop");
                P2PScreen.Companion.setWifiDiscovery(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            _manager.requestPeers(_channel, this);
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            NetworkInfo netInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
            WifiP2pInfo p2pInfo = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_INFO);
            WifiP2pGroup p2pGroup = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_GROUP);
            this.p2pGroup = p2pGroup;

            Log.d(LOG_TAG, "netInfo:" + netInfo);
            Log.d(LOG_TAG, "p2pInfo:" + p2pInfo);
            Log.d(LOG_TAG, "p2pGroup:" + p2pGroup);

            if (netInfo.isConnected()) {
                Log.d(LOG_TAG, "connect noted");
                _manager.requestConnectionInfo(_channel, this);
            } else {
                Log.d(LOG_TAG, "disconnect noted");
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            Log.d(LOG_TAG, "update local device");
            Parcelable device = intent.getParcelableExtra(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE);
            P2PScreen.Companion.setLocalDevice(((WifiP2pDevice) device));
        }
    }

    // ConnectionInfoListener
    @Override
    public void onConnectionInfoAvailable(WifiP2pInfo p2pInfo) {
        Log.d(LOG_TAG, "connection noted:" + p2pInfo.toString());
        WiFiService.startAction(_context, WiFiService.PORT, p2pInfo, p2pGroup);
        if (p2pInfo.isGroupOwner) {
            Toast.makeText(_context, "Group Owner True", Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(_context, "Group Owner False", Toast.LENGTH_LONG).show();
        }
    }

    // PeerListListener
    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Log.d(LOG_TAG, "peers available");

        ArrayList<WifiP2pDevice> results = new ArrayList<>();

        Collection<WifiP2pDevice> deviceList = peerList.getDeviceList();
        Iterator<WifiP2pDevice> iterator = deviceList.iterator();
        while (iterator.hasNext()) {
            WifiP2pDevice device = iterator.next();
            Log.d(LOG_TAG, "fresh device:" + device.deviceName + ":" + device.deviceAddress);
            results.add(device);
        }

        P2PScreen.Companion.setDeviceList(results);
    }
}
