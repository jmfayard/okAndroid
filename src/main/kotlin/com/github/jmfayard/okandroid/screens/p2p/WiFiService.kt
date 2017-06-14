package com.github.jmfayard.okandroid.screens.p2p

import android.app.IntentService
import android.content.Context
import android.content.Intent
import android.net.wifi.p2p.WifiP2pGroup
import android.net.wifi.p2p.WifiP2pInfo
import android.support.v4.content.LocalBroadcastManager
import android.util.Log
import timber.log.Timber
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.util.*

class WiFiService : IntentService("WiFiService") {
    lateinit var localBroadcastManager: LocalBroadcastManager

    override fun onDestroy() {
        Timber.d("onDestroy")
    }

    override fun onHandleIntent(intent: Intent) {
        localBroadcastManager = LocalBroadcastManager.getInstance(applicationContext)
        val action = intent.action
        if (ACTION_START == action) {
            val port = intent.getIntExtra(EXTRA_PORT, -1)
            val p2pInfo = intent.getParcelableExtra<WifiP2pInfo>(EXTRA_P2P_INFO)
            val p2pGroup = intent.getParcelableExtra<WifiP2pGroup>(EXTRA_P2P_GROUP)

            Timber.d("port:" + port)
            Timber.d("p2pInfo:" + p2pInfo)
            Timber.d("p2pGroup:" + p2pGroup)

            if (p2pGroup.isGroupOwner) {
                setupServer(port, p2pInfo, p2pGroup)
            } else {
                setupClient(port, p2pInfo, p2pGroup)
            }
        }
    }


    private fun setupClient(port: Int, p2pInfo: WifiP2pInfo, p2pGroup: WifiP2pGroup) {
        Timber.d("setup client")

        val container = WiFiContainer()
        container.put(WiFiContainer.TIME_STAMP, Date())
        container.put(WiFiContainer.ORIGIN_NAME, P2PScreen.localDevice!!.deviceName)

        val host = p2pInfo.groupOwnerAddress.hostAddress

        for (i in 0..2) {
            val success = tryConnectToServer(port, container, host)
            if (success) break
        }

    }

    private fun tryConnectToServer(port: Int, container: WiFiContainer, host: String): Boolean {

        try {
            Socket().use { socket ->
                Timber.w("CLIENT: connecting to $host:$port")
                socket.bind(null)
                socket.connect(InetSocketAddress(host, port), SOCKET_TIMEOUT)
                val oss = ObjectOutputStream(socket.getOutputStream())
                oss.writeObject(container)
                var msg = "sending to server:" + container[WiFiContainer.ORIGIN_NAME]
                Timber.w(msg)
                localBroadcastManager.sendBroadcast(dataExchange(msg))

                val ois = ObjectInputStream(socket.getInputStream())
                val wfc = ois.readObject() as WiFiContainer
                msg = "server responded:" + wfc[WiFiContainer.ORIGIN_NAME]
                localBroadcastManager.sendBroadcast(dataExchange(msg))

            }
            return true
        } catch(e: Exception) {
            e.printStackTrace()
            localBroadcastManager.sendBroadcast(dataExchange("Client error $e"))
            return false
        }
    }

    private fun setupServer(port: Int, p2pInfo: WifiP2pInfo, p2pGroup: WifiP2pGroup) {
        Timber.d("server start")
        val device = P2PScreen.localDevice

        val container = WiFiContainer().apply {
            put(WiFiContainer.TIME_STAMP, Date())
            put(WiFiContainer.ORIGIN_NAME, device?.deviceName ?: "")
        }

        try {
            ServerSocket(port).use { serverSocket ->
                serverSocket.accept().use { client ->
                    Timber.w("client socket:" + client!!.inetAddress)

                    val ois = ObjectInputStream(client.getInputStream())
                    val wfc = ois.readObject() as WiFiContainer
                    var msg = "server read:" + wfc[WiFiContainer.ORIGIN_NAME]
                    Timber.w(msg)
                    localBroadcastManager.sendBroadcast(dataExchange(msg))

                    val oos = ObjectOutputStream(client.getOutputStream())
                    oos.writeObject(container)
                    msg = "server write:" + container[WiFiContainer.ORIGIN_NAME]
                    Timber.w(msg)
                    localBroadcastManager.sendBroadcast(dataExchange(msg))

                    serverSocket.close()
                    Timber.w("server close")
                }
            }
        } catch(e: Exception) {
            localBroadcastManager.sendBroadcast(dataExchange("Server error $e"))
            e.printStackTrace()
        }
    }

    companion object {

        val PORT = 8765 // FIXME: we should use Socket(0) instead to find an available port
        val SOCKET_TIMEOUT = 5000

        private val ACTION_START = "net.braingang.service.action.start"

        private val EXTRA_P2P_GROUP = "net.braingang.service.extra.group"
        private val EXTRA_P2P_INFO = "net.braingang.service.extra.info"
        private val EXTRA_PORT = "net.braingang.service.extra.port"

        fun startAction(context: Context, port: Int, p2pInfo: WifiP2pInfo, p2pGroup: WifiP2pGroup) {
            val intent = Intent(context, WiFiService::class.java)
            intent.action = ACTION_START
            intent.putExtra(EXTRA_PORT, port)
            intent.putExtra(EXTRA_P2P_GROUP, p2pGroup)
            intent.putExtra(EXTRA_P2P_INFO, p2pInfo)
            context.startService(intent)
        }
    }
}