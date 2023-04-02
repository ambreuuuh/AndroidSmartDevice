package fr.isen.aurelien.androidsmartdevice

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.aurelien.androidsmartdevice.databinding.ActivityScanBinding
import android.Manifest.permission.*
import android.annotation.SuppressLint
import android.bluetooth.BluetoothDevice
import android.bluetooth.le.BluetoothLeScanner
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ScanActivity : AppCompatActivity(){

    private lateinit var binding: ActivityScanBinding
    private var mScanning = false
    private val handler = Handler()

    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION=1
        // Stops scanning after 10 seconds.
        private val SCAN_PERIOD: Long = 10000
    }

    private val bluetoothAdapter: BluetoothAdapter? by
    lazy(LazyThreadSafetyMode.NONE) {
        val bluetoothManager =
            getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        bluetoothManager.adapter
    }


    private val REQUIRED_PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION,
            BLUETOOTH_CONNECT,
            BLUETOOTH_SCAN
        )
    } else {
        arrayOf(
            ACCESS_FINE_LOCATION,
            ACCESS_COARSE_LOCATION

        )
    }

    /*
    override fun onStop() {
        super.onStop()
        if(bluetoothAdapter?.isEnabled == true && checkSelfPermission()) {
            mScanning = false
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }

*/

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bluetoothLeScanner=bluetoothAdapter!!.bluetoothLeScanner
        checkSelfPermission()
        startScan(bluetoothLeScanner)
    }

    private fun initList(){
        binding.recyclerview.layoutManager= LinearLayoutManager(this)
        //binding.recyclerview.adapter = adapter


        binding.recyclerview.adapter = ScanAdapter(leDeviceListAdapter) { deviceName, MAC ->
            val intent = Intent(this@ScanActivity, DeviceActivity::class.java)
            intent.putExtra("DEVICE_NAME",deviceName)
            intent.putExtra("DEVICE_MAC", MAC)
            startActivity(intent)
        }
    }

    private fun startScan(bluetoothLeScanner: BluetoothLeScanner) {
        if (bluetoothAdapter == null) {
            Toast.makeText(this, "Bluetooth non disponible", Toast.LENGTH_SHORT).show()
            return
        }
        if (bluetoothAdapter?.isEnabled == false) {
            Toast.makeText(this, "Bluetooth non activé", Toast.LENGTH_SHORT).show()
            return
        }
        if (bluetoothAdapter?.isEnabled == true) {
            binding.play.setOnClickListener() {
                togglePlayPauseAction(bluetoothLeScanner)
                initList()
            }
        }
    }

    private fun togglePlayPauseAction(bluetoothLeScanner: BluetoothLeScanner){
        if(!mScanning){
            binding.textlancement.text= getString(R.string.ble_scan_title_pause)
            binding.play.setImageResource(R.drawable.baseline_pause_circle_outline_24  )
            binding.progressBar.isIndeterminate=true
            scanLeDevice(bluetoothLeScanner)
            mScanning = true

        } else {
            binding.textlancement.text= getString(R.string.ble_scan_title_play)
            binding.play.setImageResource(R.drawable.baseline_play_circle_outline_24)
            binding.progressBar.isIndeterminate=false
            scanLeDevice(bluetoothLeScanner)
            mScanning = false

        }
    }

    private fun checkSelfPermission() {
        if (REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED})
            // Demander l'autorisation Bluetooth
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_BLUETOOTH_PERMISSION)
        //else{
            //startScan()
        //}
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it  == PackageManager.PERMISSION_GRANTED }) {
                   // startScan()
                } else {
                    Toast.makeText(this, "Vous avez besoin du bluetooth pour utiliser l'application", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun scanLeDevice(bluetoothLeScanner: BluetoothLeScanner) {
        if (!mScanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                mScanning = false
                bluetoothLeScanner.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            mScanning = true
            bluetoothLeScanner.startScan(leScanCallback)
        } else {
            mScanning = false
            bluetoothLeScanner.stopScan(leScanCallback)
        }
    }

    private val leDeviceListAdapter = LeDeviceListAdapter()
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            leDeviceListAdapter.addDevice(result.device, result.rssi)

            //leDeviceListAdapter.notifyDataSetChanged()
            binding.recyclerview.adapter = ScanAdapter(leDeviceListAdapter) { deviceName, MAC ->
                val intent = Intent(this@ScanActivity, DeviceActivity::class.java)
                intent.putExtra("DEVICE_NAME",deviceName)
                intent.putExtra("DEVICE_MAC", MAC)
                startActivity(intent)
            }
        }
    }




    class LeDeviceListAdapter {
        var device_name: ArrayList<String> = ArrayList()
        var MAC: ArrayList<String> = ArrayList()
        var distance: ArrayList<Int> = ArrayList()
        var size: Int = 0

        @SuppressLint("MissingPermission")
        fun addDevice(device: BluetoothDevice, rssi: Int) {
            if (!device.name.isNullOrBlank()) {
                if (!MAC.contains(device.address)) {
                    device_name.add(device.name)
                    MAC.add(device.address)
                    distance.add(rssi)
                    size++
                    Log.d("Device", "${device.name} + $MAC")
                }
            }
        }





    }

}


