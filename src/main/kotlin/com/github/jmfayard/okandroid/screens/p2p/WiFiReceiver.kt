package com.github.jmfayard.okandroid.screens.p2p

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.NetworkInfo
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pDeviceList
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.net.wifi.p2p.WifiP2pManager
import android.os.Parcelable
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import android.widget.Toast
import timber.log.Timber
import timber.log.Timber.d

import java.util.ArrayList

class WiFiReceiver(private val _context: Context, private val _manager: WifiP2pManager, private val _channel: WifiP2pManager.Channel) : BroadcastReceiver(), WifiP2pManager.ConnectionInfoListener, WifiP2pManager.PeerListListener {
    private var p2pGroup: WifiP2pGroup? = null
    
    val localBroadcastManager = LocalBroadcastManager.getInstance(_context)

    fun say(message: String) {
        Timber.d(message)
        localBroadcastManager.sendBroadcast(Intent(WIFI_RECEIVER).apply { putExtra("message", message) })
    }
    
    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        say("onReceive:" + intent.action)

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION == action) {
            val state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1)
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                d("wifi state enabled")
                P2PScreen.wifiEnabled = true
            } else {
                d("wifi state disabled")
                P2PScreen.wifiEnabled = false
            }
        } else if (WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION == action) {
            val state = intent.getIntExtra(WifiP2pManager.EXTRA_DISCOVERY_STATE, -1)
            if (state == WifiP2pManager.WIFI_P2P_DISCOVERY_STARTED) {
                d("wifi discovery start")
                P2PScreen.wifiDiscovery = true
            } else {
                d("wifi discovery stop")
                P2PScreen.wifiDiscovery = false
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION == action) {
            _manager.requestPeers(_channel, this)
        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION == action) {
            val netInfo = intent.getParcelableExtra<NetworkInfo>(WifiP2pManager.EXTRA_NETWORK_INFO)
            val p2pInfo = intent.getParcelableExtra<WifiP2pInfo>(WifiP2pManager.EXTRA_WIFI_P2P_INFO)
            val p2pGroup = intent.getParcelableExtra<WifiP2pGroup>(WifiP2pManager.EXTRA_WIFI_P2P_GROUP)
            this.p2pGroup = p2pGroup

            d("netInfo:" + netInfo)
            d("p2pInfo:" + p2pInfo)
            d("p2pGroup:" + p2pGroup)

            if (netInfo.isConnected) {
                d("connect noted")
                _manager.requestConnectionInfo(_channel, this)
            } else {
                d("disconnect noted")
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION == action) {
            d("update local device")
            val device = intent.getParcelableExtra<Parcelable>(WifiP2pManager.EXTRA_WIFI_P2P_DEVICE)
            P2PScreen.localDevice = device as WifiP2pDevice
        }
    }

    // ConnectionInfoListener
    override fun onConnectionInfoAvailable(p2pInfo: WifiP2pInfo) {
        d("Group Owner? : ${p2pInfo.isGroupOwner}")
        d("connection noted:" + p2pInfo.toString())
        WiFiService.startAction(_context, WiFiService.PORT, p2pInfo, p2pGroup!!)
    }

    // PeerListListener
    override fun onPeersAvailable(peerList: WifiP2pDeviceList) {

        val results = ArrayList<WifiP2pDevice>()

        val deviceList = peerList.deviceList
        val iterator = deviceList.iterator()
        while (iterator.hasNext()) {
            val device = iterator.next()
            results.add(device)
        }
        val message =  results
                .map { device -> "fresh device:${device.deviceName}:${device.deviceAddress}" }
                .joinToString(separator = "\n")
        say(message)

        P2PScreen.deviceList = results
    }

}
