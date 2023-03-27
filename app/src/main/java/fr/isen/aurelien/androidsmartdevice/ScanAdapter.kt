package fr.isen.aurelien.androidsmartdevice

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import fr.isen.aurelien.androidsmartdevice.databinding.ScanCellBinding

class ScanAdapter (var devices: ArrayList<String>): RecyclerView.Adapter<RecyclerView.ViewHolder>()
{
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ScanCellBinding.inflate(inflater, parent, false)
        return ScanViewHolder(binding)
    }

    override fun getItemCount(): Int = devices.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        //holder.nameElement.text = devices[position]
    }

    class ScanViewHolder(binding: ScanCellBinding): RecyclerView.ViewHolder(binding.root){

    }
}