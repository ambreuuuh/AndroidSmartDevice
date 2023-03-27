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
import android.bluetooth.le.ScanCallback
import android.bluetooth.le.ScanResult
import android.os.Build
import android.os.Handler
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ScanActivity : AppCompatActivity(){

    private lateinit var binding: ActivityScanBinding
    private var mScanning = false
    companion object {
        private const val REQUEST_BLUETOOTH_PERMISSION=1
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

    private val handler = Handler()

    // Stops scanning after 10 seconds.
    private val SCAN_PERIOD: Long = 10000


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)

        checkSelfPermission()

    }

    private fun initList(){
        binding.recyclerview.adapter = LinearLayoutManager(this)
            //val devices: ArrayList<String> = ArrayList()

        binding.recyclerview.layoutManager = ScanAdapter(leDeviceListAdapter) { deviceName, MAC ->
            val intent = Intent(this@ScanActivity, DetailDeviceActivity::class.java)
            intent.putExtra("DEVICE_NAME",deviceName)
            intent.putExtra("DEVICE_MAC", MAC)
            startActivity(intent)
        }
    }

    private fun startScan() {
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
                togglePlayPauseAction()
                initList()
            }
        }
    }

    private fun togglePlayPauseAction(){
        mScanning= !mScanning
        if(mScanning){
            binding.textlancement.text= getString(R.string.ble_scan_title_pause)
            binding.play.setImageResource(R.drawable.baseline_pause_circle_outline_24  )
            binding.progressBar.isIndeterminate=true

        } else {
            binding.textlancement.text= getString(R.string.ble_scan_title_play)
            binding.play.setImageResource(R.drawable.baseline_play_circle_outline_24)
            binding.progressBar.isIndeterminate=false

        }
    }

    private fun checkSelfPermission()  {
        if (REQUIRED_PERMISSIONS.any {
                ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED})
            // Demander l'autorisation Bluetooth
            ActivityCompat.requestPermissions(this, REQUIRED_PERMISSIONS, REQUEST_BLUETOOTH_PERMISSION)
        else{
            startScan()
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults.all { it  == PackageManager.PERMISSION_GRANTED }) {
                    // L'autorisation Bluetooth a été accordée
                    // Faites ce que vous voulez ici
                    startScan()
                } else {
                    // L'autorisation Bluetooth a été refusée
                    // Afficher un message à l'utilisateur pour l'informer que l'application a besoin de cette autorisation pour fonctionner correctement
                    Toast.makeText(this, "Vous avez besoin du bluetooth pour utiliser l'application", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }


    @SuppressLint("MissingPermission")
    private fun scanLeDevice() {
        if (!mScanning) { // Stops scanning after a pre-defined scan period.
            handler.postDelayed({
                mScanning = false
                bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
            }, SCAN_PERIOD)
            mScanning = true
            bluetoothAdapter?.bluetoothLeScanner?.startScan(leScanCallback)
        } else {
            mScanning = false
            bluetoothAdapter?.bluetoothLeScanner?.stopScan(leScanCallback)
        }
    }

    private val leDeviceListAdapter = LeDeviceListAdapter()
    private val leScanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            super.onScanResult(callbackType, result)
            leDeviceListAdapter.addDevice(result.device, result.rssi)
            //leDeviceListAdapter.notifyDataSetChanged()
        }
    }

    class LeDeviceListAdapter {
        var device_name: ArrayList<String> = ArrayList()
        var MAC: ArrayList<String> = ArrayList()
        var distance: ArrayList<Int> = ArrayList()
        var size: Int = 0

        @SuppressLint("MissingPermission")
        fun addDevice(device:  BluetoothDevice, rssi :Int) {
            if (!device.name.isNullOrBlank()) {
                if(!MAC.contains(device.address)) {
                    device_name.add(device.name)
                    MAC.add(device.address)
                    distance.add(rssi)
                    size ++
                }
            }
        }

    }




}


