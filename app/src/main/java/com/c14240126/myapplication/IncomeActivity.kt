package com.c14240126.myapplication

import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class IncomeActivity : AppCompatActivity(), AdapterTransaksi.OnAdapterListener {

    private val arIncome = ArrayList<DcTransaksi>()
    private val arKategoriIncome = ArrayList<String>()
    private lateinit var rvIncome: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var fabTambah: FloatingActionButton
    private lateinit var adapter: AdapterTransaksi
    private lateinit var sp: SharedPreferences

    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_income)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.tvTitleIncome)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sp = getSharedPreferences("pref_keuangan", MODE_PRIVATE)

        rvIncome = findViewById(R.id.rvIncome)
        tvEmpty = findViewById(R.id.tvEmptyIncome)
        fabTambah = findViewById(R.id.fabTambahIncome)

        adapter = AdapterTransaksi(arIncome, this)
        rvIncome.layoutManager = LinearLayoutManager(this)
        rvIncome.adapter = adapter

        muatKategori()
        muatDataDariSP()

        fabTambah.setOnClickListener {
            if (arKategoriIncome.isEmpty()) {
                Toast.makeText(this, "Silakan buat Master Kategori Pemasukan terlebih dahulu!", Toast.LENGTH_SHORT).show()
            } else {
                tampilkanDialogForm(null, -1)
            }
        }
    }

    private fun muatKategori() {
        arKategoriIncome.clear()
        val json = sp.getString("spKategori", null)
        if (json != null) {
            val type = object : TypeToken<ArrayList<DcKategori>>() {}.type
            val listKategori: ArrayList<DcKategori> = gson.fromJson(json, type)
            for (k in listKategori) {
                if (k.jenis == "Pemasukan") {
                    arKategoriIncome.add(k.nama)
                }
            }
        }
    }

    private fun muatDataDariSP() {
        arIncome.clear()
        val json = sp.getString("spPemasukan", null)
        if (json != null) {
            val type = object : TypeToken<ArrayList<DcTransaksi>>() {}.type
            val data: ArrayList<DcTransaksi> = gson.fromJson(json, type)
            arIncome.addAll(data)
        }
        updateUI()
    }

    private fun simpanKeSP() {
        val json = gson.toJson(arIncome)
        sp.edit().putString("spPemasukan", json).apply()
        updateUI()
    }

    private fun updateUI() {
        adapter.notifyDataSetChanged()
        tvEmpty.isVisible = arIncome.isEmpty()
    }

    private fun tampilkanDialogForm(itemToEdit: DcTransaksi?, position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_transaksi, null)
        val etJumlah = dialogView.findViewById<EditText>(R.id.etJumlahTransaksi)
        val spinnerKategori = dialogView.findViewById<Spinner>(R.id.spinnerKategori)
        val etTanggal = dialogView.findViewById<EditText>(R.id.etTanggalTransaksi)
        val etKeterangan = dialogView.findViewById<EditText>(R.id.etKeteranganTransaksi)

        val spinnerAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, arKategoriIncome)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerKategori.adapter = spinnerAdapter

        if (itemToEdit != null) {
            etJumlah.setText(itemToEdit.jumlah.toString())
            etTanggal.setText(itemToEdit.tanggal)
            etKeterangan.setText(itemToEdit.keterangan)
            val spinnerPosition = spinnerAdapter.getPosition(itemToEdit.kategori)
            if (spinnerPosition >= 0) {
                spinnerKategori.setSelection(spinnerPosition)
            }
        }

        val title = if (itemToEdit == null) "Tambah Pemasukan" else "Ubah Pemasukan"

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val strJumlah = etJumlah.text.toString().trim()
                val kategori = spinnerKategori.selectedItem?.toString() ?: ""
                val tanggal = etTanggal.text.toString().trim()
                val keterangan = etKeterangan.text.toString().trim()

                if (strJumlah.isNotEmpty() && tanggal.isNotEmpty() && kategori.isNotEmpty()) {
                    val jumlah = strJumlah.toDoubleOrNull() ?: 0.0
                    if (itemToEdit == null) {
                        arIncome.add(DcTransaksi(jumlah, kategori, tanggal, keterangan, "Pemasukan"))
                    } else {
                        arIncome[position].jumlah = jumlah
                        arIncome[position].kategori = kategori
                        arIncome[position].tanggal = tanggal
                        arIncome[position].keterangan = keterangan
                    }
                    simpanKeSP()
                } else {
                    Toast.makeText(this, "Mohon lengkapi data!", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onEdit(position: Int) {
        tampilkanDialogForm(arIncome[position], position)
    }

    override fun onDelete(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Pemasukan")
            .setMessage("Apakah Anda yakin ingin menghapus pemasukan ini?")
            .setPositiveButton("Ya") { _, _ ->
                arIncome.removeAt(position)
                simpanKeSP()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}
