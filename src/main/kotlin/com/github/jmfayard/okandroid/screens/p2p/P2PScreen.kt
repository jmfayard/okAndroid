package com.github.jmfayard.okandroid.screens.p2p

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.wifi.p2p.WifiP2pConfig
import android.net.wifi.p2p.WifiP2pDevice
import android.net.wifi.p2p.WifiP2pManager
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.os.Bundle
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.support.v4.content.ContextCompat.startActivity
import android.util.Log
import com.afollestad.materialdialogs.MaterialDialog
import com.github.jmfayard.okandroid.*
import com.github.jmfayard.okandroid.screens.TagsScreen
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.Screen
import io.palaima.smoothbluetooth.Device
import io.palaima.smoothbluetooth.SmoothBluetooth
import timber.log.Timber
import android.net.wifi.WifiManager






@See(layout = R.layout.tags_screen, java = TagsScreen::class)
class P2PScreen : Screen<P2PView>() {

    override fun createView(context: Context) = P2PView(context)

    override fun getTitle(context: Context): String = "P2P"


    fun updateContent() {
        val nfc = NfcAdapter.getDefaultAdapter(activity)
        val nfcText = when {
            nfc == null -> "NFC: no support"
            nfc.isEnabled -> "NFC enabled: send #text ; #url ; #aar #textAar"
            else -> "NFC disabled: #enableNFC"
        }
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
        val bluetoothText = when {
            bluetoothAdapter == null -> "Bluetooth: no support"
            !bluetoothAdapter.isEnabled -> "Bluetoth disabled: #enableBT"
            else -> "Bluetooth: #infos #configure #stop #tryConnect #doDiscovery #sendData"
        }

        val wifiManger = activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        val wifiText = when {
            !wifiManger.isWifiEnabled -> "Wifi not enabled: #enableWifi"
            !wifiManger.isP2pSupported -> "Wifi P2P not supported"
            else -> "WifiP2P enabled: #setup #discovery #connect #disconnect"
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
        val bluetoothAdapter = BluetoothAdapter.getDefaultAdapter()
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
//    override fun onResume(context: Context?) {
//        updateContent()
//        showBluetoothInfos()
//    }

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
            "#setup" -> WiFiHelper.setupWiFiReceiver(App.ctx)
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
        val wifiManger = activity.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
        wifiManger.isWifiEnabled = true
        updateContent()
    }

    fun wifiConnect() {
        if (Personality.deviceList == null || Personality.deviceList.isEmpty()) {
            say("wifiConnect: no devices")

        } else {
            val names = Personality.deviceList.map { device ->
                "${device.deviceName} [${device.deviceAddress}]"
            }
            MaterialDialog.Builder(activity)
                    .title("Connect via Wifi P2P to:")
                    .items(names)
                    .itemsCallback { _, _, which, text ->
                        say("Your choice: $text (option #$which)")
                        wifiConnect(Personality.deviceList[which])
                    }
                    .show()
        }
    }

    fun wifiConnect(device: WifiP2pDevice) {
        say("wifiConnect to [${device.deviceName}] with address: ${device.deviceAddress}")

        val config = WifiP2pConfig()
        config.deviceAddress = device.deviceAddress
        config.groupOwnerIntent = 0

        Personality.wifiP2pManager.connect(Personality.wifiP2pChannel, config, wifiListener("connect"))
    }

    fun disconnect() {
        say("disconnect start")
        Personality.wifiP2pManager.removeGroup(Personality.wifiP2pChannel, null)
        Personality.wifiP2pManager.cancelConnect(Personality.wifiP2pChannel, null)
    }

    fun discovery() {
        say("discovery start")
        Personality.wifiP2pManager.discoverPeers(Personality.wifiP2pChannel, wifiListener("discovery"))
    }

    fun wifiListener(message: String) = object: WifiP2pManager.ActionListener {
        override fun onSuccess() {
            say("WifiP2P[$message] : success", toast = false)
        }

        override fun onFailure(reason: Int) {
            say("WifiP2P[$message] : FAILURE with code $reason", toast = false)
        }

    }

    @SuppressLint("ObsoleteSdkInt")
    fun enableNFC() {
        val nfc = NfcAdapter.getDefaultAdapter(activity) ?: return
        if (nfc.isEnabled) {
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
        val macAddress = android.provider.Settings.Secure.getString(activity.contentResolver, "bluetooth_address")
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
        val context = activity ?: return
        val nfc = NfcAdapter.getDefaultAdapter(context)
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

        val nfc = NfcAdapter.getDefaultAdapter(context)
        if (nfc == null) {
            say("NFC is not available")
        } else {
            val state = if (nfc.isEnabled) "enabled" else "disabled"
            say("NFC $state", toast = false)
        }


        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter: BluetoothAdapter? = bluetoothManager.adapter // OR BluetoothAdapter.getDefaultAdapter()
        if (adapter == null || !context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            say("Bluetooth LE not supported")
            return
        }
        val state = if (adapter.isEnabled) "enabled" else "disabled"
        say("Bluetooth $state", toast = false)
        val macAddress = android.provider.Settings.Secure.getString(context.contentResolver, "bluetooth_address")

        val bleInfo = "Bluetooth: Address=${adapter.address} Name=${adapter.name} macAddress=$macAddress"
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





