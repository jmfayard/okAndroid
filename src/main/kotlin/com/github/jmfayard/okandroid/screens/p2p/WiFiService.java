package com.github.jmfayard.okandroid.screens.p2p;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pGroup;
import android.net.wifi.p2p.WifiP2pInfo;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.github.jmfayard.okandroid.ExtensionsKt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Date;

public class WiFiService extends IntentService {
    public static final String LOG_TAG = WiFiService.class.getName();

    public static final int PORT = 8765;
    public static final int SOCKET_TIMEOUT = 5000;

    private static final String ACTION_START = "net.braingang.service.action.start";

    private static final String EXTRA_P2P_GROUP = "net.braingang.service.extra.group";
    private static final String EXTRA_P2P_INFO = "net.braingang.service.extra.info";
    private static final String EXTRA_PORT = "net.braingang.service.extra.port";
    private LocalBroadcastManager localBroadcastManager;

    public static void startAction(Context context, int port, WifiP2pInfo p2pInfo, WifiP2pGroup p2pGroup) {
        Intent intent = new Intent(context, WiFiService.class);
        intent.setAction(ACTION_START);
        intent.putExtra(EXTRA_PORT, port);
        intent.putExtra(EXTRA_P2P_GROUP, p2pGroup);
        intent.putExtra(EXTRA_P2P_INFO, p2pInfo);
        context.startService(intent);
    }

    public WiFiService() {
        super("WiFiService");
    }

    @Override
    public void onDestroy() {
        Log.d(LOG_TAG, "onDestroy");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        localBroadcastManager = LocalBroadcastManager.getInstance(getApplicationContext());
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_START.equals(action)) {
                final Integer port = intent.getIntExtra(EXTRA_PORT, -1);
                final WifiP2pInfo p2pInfo = intent.getParcelableExtra(EXTRA_P2P_INFO);
                final WifiP2pGroup p2pGroup = intent.getParcelableExtra(EXTRA_P2P_GROUP);

                Log.d(LOG_TAG, "port:" + port);
                Log.d(LOG_TAG, "p2pInfo:" + p2pInfo);
                Log.d(LOG_TAG, "p2pGroup:" + p2pGroup);

                if (p2pGroup.isGroupOwner()) {
                    setupServer(port, p2pInfo, p2pGroup);
                } else {
                    setupClient(port, p2pInfo, p2pGroup);
                }
            }
        }
    }



    private void setupClient(int port, WifiP2pInfo p2pInfo, WifiP2pGroup p2pGroup) {
        Log.d(LOG_TAG, "setup client");

        WiFiContainer container = new WiFiContainer();
        container.put(WiFiContainer.TIME_STAMP, new Date());
        container.put(WiFiContainer.ORIGIN_NAME, P2PScreen.Companion.getLocalDevice().deviceName );

        String host = p2pInfo.groupOwnerAddress.getHostAddress();

        for(int i = 0 ; i < 3 ; i++) {
            boolean success = tryConnectToServer(port, container, host);
            if (success) break;
        }

    }

    private boolean tryConnectToServer(int port, WiFiContainer container, String host) {
        Socket socket = new Socket();
        try {
            Log.w(LOG_TAG, "CLIENT: connecting to " + host + ":" + port);
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), SOCKET_TIMEOUT);
            ObjectOutputStream oss = new ObjectOutputStream(socket.getOutputStream());
            oss.writeObject(container);
            String msg = "sending to server:" + container.get(WiFiContainer.ORIGIN_NAME);
            Log.w(LOG_TAG, msg);
            localBroadcastManager.sendBroadcast(ExtensionsKt.dataExchange(msg));

            ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
            WiFiContainer wfc = (WiFiContainer) ois.readObject();
            msg = "server responded:" + wfc.get(WiFiContainer.ORIGIN_NAME);
            localBroadcastManager.sendBroadcast(ExtensionsKt.dataExchange(msg));

            return true;
        } catch(Exception exception) {
            exception.printStackTrace();
            return false;
        } finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                }
            }
        }
    }

    private void setupServer(int port, WifiP2pInfo p2pInfo, WifiP2pGroup p2pGroup) {
        Log.d(LOG_TAG, "server start");
        WifiP2pDevice device = P2PScreen.Companion.getLocalDevice();

        WiFiContainer container = new WiFiContainer();
        container.put(WiFiContainer.TIME_STAMP, new Date());
        container.put(WiFiContainer.ORIGIN_NAME, device != null ? device.deviceName : "");

        ServerSocket serverSocket = null;
        Socket client = null;

        try {
            serverSocket = new ServerSocket(port);
            client = serverSocket.accept();
            Log.w(LOG_TAG, "client socket:" + client.getInetAddress());

            ObjectInputStream ois = new ObjectInputStream(client.getInputStream());
            WiFiContainer wfc = (WiFiContainer) ois.readObject();
            String msg = "server read:" + wfc.get(WiFiContainer.ORIGIN_NAME);
            Log.w(LOG_TAG, msg);
            localBroadcastManager.sendBroadcast(ExtensionsKt.dataExchange(msg));

            ObjectOutputStream oos = new ObjectOutputStream(client.getOutputStream());
            oos.writeObject(container);
            msg = "server write:" + container.get(WiFiContainer.ORIGIN_NAME);
            Log.w(LOG_TAG, msg);
            localBroadcastManager.sendBroadcast(ExtensionsKt.dataExchange(msg));

            serverSocket.close();
            Log.w(LOG_TAG, "server close");

            /*
            Personality.wifiP2pManager.removeGroup(Personality.wifiP2pChannel, null);
            WiFiReceiver.restartReceiver(getBaseContext());
            */
        } catch (Exception exception) {
            exception.printStackTrace();
        } finally {

            if (client != null) try {
                client.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (serverSocket != null) try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }
}