package com.github.jmfayard.okandroid.screens.p2p

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.net.wifi.WifiManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.Bundle
import android.os.Looper
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.ContextCompat.startActivity
import com.afollestad.materialdialogs.MaterialDialog
import com.github.jmfayard.okandroid.BuildConfig
import com.github.jmfayard.okandroid.MainActivity
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.screens.TagsScreen
import com.github.jmfayard.okandroid.toast
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.Screen
import io.palaima.smoothbluetooth.Device
import io.palaima.smoothbluetooth.SmoothBluetooth
import timber.log.Timber


@See(layout = R.layout.tags_screen, java = TagsScreen::class)
class P2PScreen(
        val nfc: NfcAdapter?,
        val bluetoothAdapter: BluetoothAdapter?,
        val wifiManger: WifiManager,
        val macAddress: String
) : Screen<P2PView>() {

    companion object {

        fun from(activity: Activity): P2PScreen = P2PScreen(
                nfc = NfcAdapter.getDefaultAdapter(activity.applicationContext),
                bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(),
                wifiManger = activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager,
                macAddress = android.provider.Settings.Secure.getString(activity.contentResolver, "bluetooth_address")
        )

        var wifiDiscovery: Boolean = false
        var wifiEnabled: Boolean = false

        var deviceList: MutableList<WifiP2pDevice> = mutableListOf()

        lateinit var wifiP2pManager: WifiP2pManager
        lateinit var wifiP2pChannel: WifiP2pManager.Channel
        var localDevice: WifiP2pDevice? = null

        /** Used in in Application.onCreate() **/
        fun setupWiFiReceiver(application: Application): Pair<WiFiReceiver, IntentFilter> {
            val manager = application.getSystemService(Context.WIFI_P2P_SERVICE) as WifiP2pManager
            val channel = manager.initialize(application, Looper.getMainLooper(), null)

            val intentFilter = IntentFilter()
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION)
            intentFilter.addAction(WifiP2pManager.WIFI_P2P_DISCOVERY_CHANGED_ACTION)

            val wiFiReceiver = WiFiReceiver(application, manager, channel)

            wifiP2pManager = manager
            wifiP2pChannel = channel

            return wiFiReceiver to intentFilter
        }


    }

    override fun createView(context: Context) = P2PView(context)

    override fun getTitle(context: Context): String = "P2P"


    fun updateContent() {
        val nfcText = when {
            nfc == null -> "NFC: no support"
            nfc.isEnabled -> "NFC enabled: send #text ; #url ; #aar #textAar"
            else -> "NFC disabled: #enableNFC"
        }

        val bluetoothText = when {
            bluetoothAdapter == null -> "Bluetooth: no support"
            !bluetoothAdapter.isEnabled -> "Bluetoth disabled: #enableBT"
            else -> "Bluetooth: #infos #configure #stop #tryConnect #doDiscovery #sendData"
        }

        val wifiText = when {
            !wifiManger.isWifiEnabled -> "Wifi not enabled: #enableWifi"
//            !wifiManger.isP2pSupported -> "Wifi P2P not supported"
            else -> "WifiP2P enabled: #discovery #connect #disconnect"
        }

        val text = """
$nfcText

$wifiText

$bluetoothText
"""
        view.htmlContent = text
        view.setupTags()
    }

    fun enableBluetooth() {
        if (bluetoothAdapter == null) {
            say("Device does not support bluetooth")
        } else if (!bluetoothAdapter.isEnabled) {
            say("Please enable bluetooth")
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(activity, enableBtIntent, MainActivity.REQUEST_ENABLE_BT, Bundle())
        } else {
            say("Bluetooth is available and enabled")
        }

    }


    override fun onShow(context: Context?) {
        updateContent()
        showBluetoothInfos()
    }

    override fun onHide(context: Context?) {
        stopBluetooth()
    }

    fun stopBluetooth() {
        smoothBluetooth?.stop()
        smoothBluetooth = null
    }

    fun clickedOn(hashtag: String) {
        say("Clicked on $hashtag", toast = false)
        when (hashtag) {
            in nfcPushes().keys -> handleNfc(nfcPushes()[hashtag]!!)
            "#connect" -> wifiConnect()
            "#discovery" -> discovery()
            "#disconnect" -> disconnect()
            "#infos" -> showBluetoothInfos()
            "#enableNFC" -> enableNFC()
            "#enableBT" -> enableBluetooth()
            "#enableWifi" -> enableWifi()
            "#configure" -> smoothBluetooth = configureBluetooth()
            "#stop" -> stopBluetooth()
            "#tryConnect" -> smoothBluetooth?.tryConnection() ?: toast("Configure bluetooth first")
            "#doDiscovery" -> smoothBluetooth?.doDiscovery() ?: toast("Configure bluetooth first")
            "#sendData" -> smoothBluetooth?.send("Hello World".toByteArray(), true) ?: toast("Configure bluetooth first")
            else -> toast("Hashtag $hashtag not handled")
        }
    }

    fun enableWifi() {
        say("Enabling Wifi")
        wifiManger.isWifiEnabled = true
        updateContent()
    }

    fun wifiConnect() {
        if (deviceList.isEmpty()) {
            say("wifiConnect: no devices")

        } else {
            val names = deviceList.map { device ->
                "${device.deviceName} [${device.deviceAddress}]"
            }
            MaterialDialog.Builder(activity)
                    .title("Connect via Wifi P2P to:")
                    .items(names)
                    .itemsCallback { _, _, which, text ->
                        say("Your choice: $text (option #$which)")
                        wifiConnect(deviceList[which])
                    }
                    .show()
        }
    }

    fun wifiConnect(device: WifiP2pDevice) {
        say("wifiConnect to [${device.deviceName}] with address: ${device.deviceAddress}")

        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        config.groupOwnerIntent = 0

        wifiP2pManager.connect(wifiP2pChannel, config, wifiListener("connect"))
    }

    fun disconnect() {
        say("disconnect start")
        wifiP2pManager.removeGroup(wifiP2pChannel, null)
        wifiP2pManager.cancelConnect(wifiP2pChannel, null)
    }

    fun discovery() {
        say("discovery start")
        wifiP2pManager.discoverPeers(wifiP2pChannel, wifiListener("discovery"))
    }

    fun wifiListener(message: String) = object : WifiP2pManager.ActionListener {
        override fun onSuccess() {
            say("WifiP2P[$message] : success", toast = false)
        }

        override fun onFailure(reason: Int) {
            say("WifiP2P[$message] : FAILURE with code $reason", toast = false)
        }

    }

    @SuppressLint("ObsoleteSdkInt")
    fun enableNFC() {
        if (nfc != null && nfc.isEnabled) {
            say("NFC already enabled")
        } else {
            say("Please enable NFC")
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                startActivity(activity, Intent(android.provider.Settings.ACTION_NFC_SETTINGS), Bundle.EMPTY)
            } else {
                startActivity(activity, Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS), Bundle.EMPTY)
            }
        }
    }


    fun nfcPushes(): Map<String, NdefMessage> {

        val handhoverRecord = NdefRecord.createTextRecord("EN", "Hello World! from $macAddress")
        val urlRecord = NdefRecord.createUri("http://www.google.com/pat/rick/ok")
        val applicationRecord = NdefRecord.createApplicationRecord(BuildConfig.APPLICATION_ID)

        return mapOf(
                "#url" to NdefMessage(urlRecord),
                "#aar" to NdefMessage(applicationRecord),
                "#textAar" to NdefMessage(handhoverRecord, applicationRecord),
                "#text" to NdefMessage(handhoverRecord)
        )
    }


    fun handleNfc(message: NdefMessage) {
        if (nfc == null) {
            say("NFC is not available")
            return
        }

        say("Will sent NFC Push message: $message")
        nfc.setNdefPushMessage(message, activity)
        nfc.setOnNdefPushCompleteCallback({ event ->
            activity?.runOnUiThread {
                activity ?: return@runOnUiThread
                say("NFC Push callback $event")
            }
        }, activity, emptyArray())
    }

    fun showBluetoothInfos() {
        val context = view?.context ?: return

        if (nfc == null) {
            say("NFC is not available")
        } else {
            val state = if (nfc.isEnabled) "enabled" else "disabled"
            say("NFC $state", toast = false)
        }


        if (bluetoothAdapter == null || !context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            say("Bluetooth LE not supported")
            return
        }
        val state = if (bluetoothAdapter.isEnabled) "enabled" else "disabled"
        say("Bluetooth $state", toast = false)
        val macAddress = android.provider.Settings.Secure.getString(context.contentResolver, "bluetooth_address")

        val bleInfo = "Bluetooth: Address=${bluetoothAdapter.address} Name=${bluetoothAdapter.name} macAddress=$macAddress"
        say(bleInfo)
    }

    fun say(message: String, toast: Boolean = true) {
        Timber.i("SAY: $message")
        if (view == null) return
        view.history += "\n" + message
        if (toast) toast(message)
    }

    fun configureBluetooth(): SmoothBluetooth {
        stopBluetooth()

        val listener = object : SmoothBluetooth.Listener {
            override fun onDevicesFound(devices: MutableList<Device>, callback: SmoothBluetooth.ConnectionCallback) {
                say("onDevicesFound")
                devices.forEach {
                    say("Found device ${it.name} isPaired:${(it.isPaired)} Adresss ${it.address}")
                }
                callback.connectTo(devices.first())
            }

            override fun onDiscoveryFinished() {
                say("onDiscoveryFinished")
            }

            override fun onConnecting(device: Device?) {
                say("onConnecting $device")
            }

            override fun onDataReceived(p0: Int) {
                say("onDataReceived($p0)")
            }

            override fun onBluetoothNotSupported() {
                say("onBluetoothNotSupported")
            }

            override fun onBluetoothNotEnabled() {
                say("onBluetoothNotEnabled")
            }

            override fun onConnected(p0: Device?) {
                say("onConnected")
            }

            override fun onDiscoveryStarted() {
                say("onDiscoveryStarted")
            }

            override fun onConnectionFailed(device: Device) {
                say("onConnectionFailed $device")
            }

            override fun onDisconnected() {
                say("onDisconnected")
            }

            override fun onNoDevicesFound() {
                say("onNoDevicesFound")
            }

        }
        return SmoothBluetooth(activity.baseContext, SmoothBluetooth.ConnectionTo.ANDROID_DEVICE, SmoothBluetooth.Connection.SECURE, listener)
    }

    var smoothBluetooth: SmoothBluetooth? = null


}








