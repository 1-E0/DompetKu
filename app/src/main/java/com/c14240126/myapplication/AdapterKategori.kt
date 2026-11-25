package com.c14240126.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class AdapterKategori(
    private val listKategori: ArrayList<DcKategori>,
    private val listener: OnAdapterListener
) : RecyclerView.Adapter<AdapterKategori.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvNama: TextView = view.findViewById(R.id.tvNamaKategori)
        val tvJenis: TextView = view.findViewById(R.id.tvJenisKategori)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEdit)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_kategori, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listKategori[position]
        holder.tvNama.text = item.nama
        holder.tvJenis.text = item.jenis

        holder.btnEdit.setOnClickListener {
            listener.onEdit(position)
        }

        holder.btnDelete.setOnClickListener {
            listener.onDelete(position)
        }
    }

    override fun getItemCount(): Int = listKategori.size

    interface OnAdapterListener {
        fun onEdit(position: Int)
        fun onDelete(position: Int)
    }
}
