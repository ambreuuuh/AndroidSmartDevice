package fr.isen.aurelien.androidsmartdevice

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.le.BluetoothLeScanner
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import fr.isen.aurelien.androidsmartdevice.databinding.ActivityScanBinding
import android.Manifest.permission.*
import android.os.Build
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat

class ScanActivity : AppCompatActivity(){

    private lateinit var binding: ActivityScanBinding
    private var mScanning = false
    val bluetoothPermission = BLUETOOTH_CONNECT
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
            BLUETOOTH_CONNECT,
            BLUETOOTH_SCAN
        )
    } else {
        arrayOf(
            BLUETOOTH,
            BLUETOOTH_ADMIN
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityScanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val bluetoothLeScanner = bluetoothAdapter!!.bluetoothLeScanner
        checkSelfPermission()
        startScan(bluetoothLeScanner)


        binding.recyclerview.layoutManager = LinearLayoutManager(this)
        binding.recyclerview.adapter = ScanAdapter(arrayListOf("Device 1", "Device 2"))
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
                togglePlayPauseAction()
                //initList()
            }
        }
    }
    private fun togglePlayPauseAction(){
        mScanning= !mScanning
        if(mScanning){
            binding.textlancement.text= getString(R.string.ble_scan_title_pause)
            binding.play.setImageResource(R.drawable.baseline_pause_circle_outline_24  )
            binding.progressBar.isVisible=true
        } else {
            binding.textlancement.text= getString(R.string.ble_scan_title_play)
            binding.play.setImageResource(R.drawable.baseline_play_circle_outline_24)
            binding.progressBar.isVisible=false
        }

    }
    private fun checkSelfPermission()  {
        if (REQUIRED_PERMISSIONS.all {
                ContextCompat.checkSelfPermission(this, bluetoothPermission) != PackageManager.PERMISSION_GRANTED})
            // Demander l'autorisation Bluetooth
            ActivityCompat.requestPermissions(this, arrayOf(bluetoothPermission), REQUEST_BLUETOOTH_PERMISSION)

    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_BLUETOOTH_PERMISSION -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // L'autorisation Bluetooth a été accordée
                    // Faites ce que vous voulez ici
                } else {
                    // L'autorisation Bluetooth a été refusée
                    // Afficher un message à l'utilisateur pour l'informer que l'application a besoin de cette autorisation pour fonctionner correctement
                    Toast.makeText(this, "Vous avez besoin du bluetooth pour utiliser l'application", Toast.LENGTH_SHORT).show()
                }
                return
            }
        }
    }

}


