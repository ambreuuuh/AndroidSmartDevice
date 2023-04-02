package fr.isen.aurelien.androidsmartdevice

import android.bluetooth.BluetoothDevice
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.isen.aurelien.androidsmartdevice.databinding.ScanCellBinding

class ScanAdapter(private val devices: ScanActivity.LeDeviceListAdapter, private val onItemClick: (String, String) -> Unit) :
    RecyclerView.Adapter<ScanAdapter.DeviceViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DeviceViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ScanCellBinding.inflate(inflater, parent, false)
        return DeviceViewHolder(binding)
    }

    class DeviceViewHolder(binding: ScanCellBinding): RecyclerView.ViewHolder(binding.root){
        val nameElement= binding.nameElement
        val address= binding.address
        val distance= binding.distance
    }

    override fun getItemCount(): Int = devices.size

    override fun onBindViewHolder(holder: DeviceViewHolder, position: Int) {
        //holder.nameElement.text = devices[position]
        holder.nameElement.text = devices.device_name[position]
        holder.address.text = devices.MAC[position]
        holder.distance.text = devices.distance[position].toString()

        holder.itemView.setOnClickListener {
            onItemClick(devices.device_name[position], devices.MAC[position])
        }
    }



}