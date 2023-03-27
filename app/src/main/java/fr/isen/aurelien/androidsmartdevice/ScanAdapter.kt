package fr.isen.aurelien.androidsmartdevice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.isen.aurelien.androidsmartdevice.databinding.ScanCellBinding

class ScanAdapter(private val devices: ScanActivity.LeDeviceListAdapter, private val onItemClick: (String, String) -> Unit) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ScanCellBinding.inflate(inflater, parent, false)
        return ScanViewHolder(binding)
    }

    class DeviceViewHolder(binding: DeviceCellBinding): RecyclerView.ViewHolder(binding.root){
        val deviceName= binding.devicename
        val deviceName= binding.devicename
        val deviceName= binding.devicename
    }

    override fun getItemCount(): Int = devices.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //holder.nameElement.text = devices[position]
        holder.nameElement.text = devices.device_name[position]
        holder.macAddress.text = devices.MAC[position]
        holder.distanceNumber.text = devices.distance[position].toString()

        holder.itemView.setOnClickListener {
            onItemClick(devices.device_name[position], devices.MAC[position])
        }
    }

    class ScanViewHolder(binding: ScanCellBinding): RecyclerView.ViewHolder(binding.root){

    }

}