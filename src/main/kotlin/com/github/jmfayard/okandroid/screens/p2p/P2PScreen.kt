package com.github.jmfayard.okandroid.screens.p2p

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
import com.github.jmfayard.okandroid.screens.TagsView
import com.github.jmfayard.okandroid.toast
import com.github.jmfayard.okandroid.utils.See
import com.wealthfront.magellan.BaseScreenView
import com.wealthfront.magellan.Screen


@See(layout = R.layout.tags_screen, java = TagsScreen::class)
class P2PScreen : Screen<P2PView>() {

    val text = """
See #bluetooth info and #nfc

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
            "#bluetooth" -> handleBluetooth()
            "#nfc" -> handleNfc()
            else -> toast("Hashtag $hashtag not handled")
        }
    }

    fun handleNfc() {
        val context = activity ?: return
        val nfc = NfcAdapter.getDefaultAdapter(context)
        if (nfc != null) {
            log("NFC available")
            val macAddress = android.provider.Settings.Secure.getString(context.contentResolver, "bluetooth_address")
            val applicationRecord = NdefRecord.createApplicationRecord(BuildConfig.APPLICATION_ID)
            val handhoverRecord = NdefRecord.createTextRecord("EN", "Hello World! from $macAddress")
            val message = NdefMessage(applicationRecord, handhoverRecord)
            nfc.setNdefPushMessage(message, activity)
        } else {
            log("NFC is not available")
        }
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





