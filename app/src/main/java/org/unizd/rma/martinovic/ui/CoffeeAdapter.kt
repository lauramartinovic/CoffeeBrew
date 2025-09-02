package org.unizd.rma.martinovic.ui

import android.net.Uri
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.unizd.rma.martinovic.databinding.ItemCoffeeBinding
import org.unizd.rma.martinovic.model.CoffeeBrew
import java.text.SimpleDateFormat
import java.util.Locale

class CoffeeAdapter(
    private val onClick: (CoffeeBrew) -> Unit
) : RecyclerView.Adapter<CoffeeAdapter.VH>() {

    private val items = mutableListOf<CoffeeBrew>()
    private val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun submit(list: List<CoffeeBrew>) {
        items.clear()
        items.addAll(list)
        notifyDataSetChanged()
    }

    inner class VH(val b: ItemCoffeeBinding) : RecyclerView.ViewHolder(b.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val b = ItemCoffeeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(b)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val it = items[position]
        holder.b.txtName.text = it.coffeeName
        holder.b.txtRoaster.text = it.roaster
        holder.b.txtMeta.text = "${it.brewMethod} — ${df.format(it.brewDate)}"
        if (it.photoUri.isNotBlank()) holder.b.imgPhoto.load(Uri.parse(it.photoUri))
        holder.b.root.setOnClickListener { onClick(items[position]) }
    }

    override fun getItemCount(): Int = items.size
}
