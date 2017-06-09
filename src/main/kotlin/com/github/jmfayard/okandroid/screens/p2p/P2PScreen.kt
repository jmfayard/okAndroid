package com.github.jmfayard.okandroid.screens.p2p

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.pm.PackageManager
import android.nfc.NdefMessage
import android.nfc.NdefRecord
import android.nfc.NfcAdapter
import android.nfc.NfcEvent
import com.github.jmfayard.okandroid.BuildConfig
import com.github.jmfayard.okandroid.R
import com.github.jmfayard.okandroid.screens.TagsScreen
import com.github.jmfayard.okandroid.toast
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.Screen


@See(layout = R.layout.tags_screen, java = TagsScreen::class)
class P2PScreen : Screen<P2PView>() {

    val text = """
Bluetooth: show #infos

NFC: send #text ; #url ; #aar #textAar

"""

    override fun createView(context: Context) = P2PView(context)

    override fun getTitle(context: Context): String = "P2P"

    override fun onResume(context: Context?) {
        view.htmlContent = text
        view.setupTags()

    }


    fun clickedOn(hashtag: String) {
        log("Clicked on $hashtag")
        when (hashtag) {
            "#infos" -> handleBluetooth()
            in nfcPushes().keys -> handleNfc(nfcPushes()[hashtag]!!)
            else -> toast("Hashtag $hashtag not handled")
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
            log("NFC is not available")
            return
        }

        log("Will sent NFC Push message: $message")
        nfc.setNdefPushMessage(message, activity)
        nfc.setOnNdefPushCompleteCallback({ event ->
            activity?.runOnUiThread {
                activity ?: return@runOnUiThread
                log("NFC Push callback $event")
            }
        }, activity, emptyArray())
    }

    fun handleBluetooth() {
        val context = view?.context ?: return
        // Use this check to determine whether BLE is supported on the device.  Then you can
        // selectively disable BLE-related features.
        if (!context.packageManager.hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)) {
            toast("Bluetooth LE not supported")
            return
        }

        // Initializes a Bluetooth adapter.  For API level 18 and above, get a reference to
        // BluetoothAdapter through BluetoothManager.
        val bluetoothManager: BluetoothManager = context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val adapter: BluetoothAdapter = bluetoothManager.adapter // OR BluetoothAdapter.getDefaultAdapter()
        val macAddress = android.provider.Settings.Secure.getString(context.contentResolver, "bluetooth_address")

        val bleInfo = "Bluetooth: Address=${adapter.address} Name=${adapter.name} macAddress=$macAddress"
        log(bleInfo)


    }

    private fun log(line: String) {
        view.history += "\n" + line
    }

}





