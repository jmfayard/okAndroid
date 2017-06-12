package com.github.jmfayard.okandroid.screens.p2p

import android.annotation.SuppressLint
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import com.github.jmfayard.okandroid.BuildConfig
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.screens.TagsScreen
import com.github.jmfayard.okandroid.toast
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.Screen
import io.palaima.smoothbluetooth.Device
import io.palaima.smoothbluetooth.SmoothBluetooth
import timber.log.Timber
import android.support.v4.app.ActivityCompat.startActivityForResult
import android.content.Intent
import android.os.Bundle
import android.support.v4.content.ContextCompat.startActivity
import com.github.jmfayard.okandroid.MainActivity

val text = """
Bluetooth: show #infod #enableBT

NFC: #enableNFC, send #text ; #url ; #aar #textAar

SmoothBluetooth: #configure #stop #tryConnect #doDiscovery #sendData
"""


@See(layout = R.layout.tags_screen, java = TagsScreen::class)
class P2PScreen : Screen<P2PView>() {

    override fun createView(context: Context) = P2PView(context)

    override fun getTitle(context: Context): String = "P2P"

    override fun onResume(context: Context?) {
        view.htmlContent = text
        view.setupTags()
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
            "#infos" -> showBluetoothInfos()
            in nfcPushes().keys -> handleNfc(nfcPushes()[hashtag]!!)
            "#enableNFC" -> enableNFC()
            "#enableBT" -> enableBluetooth()
            "#configure" -> smoothBluetooth = configureBluetooth()
            "#stop" -> stopBluetooth()
            "#tryConnect" -> smoothBluetooth?.tryConnection() ?: toast("Configure bluetooth first")
            "#doDiscovery" -> smoothBluetooth?.doDiscovery() ?: toast("Configure bluetooth first")
            "#sendData" -> smoothBluetooth?.send("Hello World".toByteArray(), true) ?: toast("Configure bluetooth first")
            else -> toast("Hashtag $hashtag not handled")
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

    var smoothBluetooth : SmoothBluetooth?  = null


}





