package com.c14240126.myapplication

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.NumberFormat
import java.util.Locale

class AdapterTransaksi(
    private val listTransaksi: ArrayList<DcTransaksi>,
    private val listener: OnAdapterListener
) : RecyclerView.Adapter<AdapterTransaksi.ViewHolder>() {

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvKategori: TextView = view.findViewById(R.id.tvKategoriTransaksi)
        val tvJumlah: TextView = view.findViewById(R.id.tvJumlahTransaksi)
        val tvTanggal: TextView = view.findViewById(R.id.tvTanggalTransaksi)
        val tvKeterangan: TextView = view.findViewById(R.id.tvKeteranganTransaksi)
        val btnEdit: ImageButton = view.findViewById(R.id.btnEditTransaksi)
        val btnDelete: ImageButton = view.findViewById(R.id.btnDeleteTransaksi)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.row_transaksi, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listTransaksi[position]
        holder.tvKategori.text = item.kategori
        
        val localeID = Locale("in", "ID")
        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)
        holder.tvJumlah.text = formatRupiah.format(item.jumlah)

        holder.tvTanggal.text = item.tanggal
        holder.tvKeterangan.text = if (item.keterangan.isNotEmpty()) item.keterangan else "-"

        holder.btnEdit.setOnClickListener {
            listener.onEdit(position)
        }

        holder.btnDelete.setOnClickListener {
            listener.onDelete(position)
        }
    }

    override fun getItemCount(): Int = listTransaksi.size

    interface OnAdapterListener {
        fun onEdit(position: Int)
        fun onDelete(position: Int)
    }
}
