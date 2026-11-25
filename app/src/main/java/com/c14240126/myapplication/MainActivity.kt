package com.c14240126.myapplication

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.EditText
import android.widget.RadioButton
import android.widget.RadioGroup
import android.widget.TextView
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

class MainActivity : AppCompatActivity(), AdapterKategori.OnAdapterListener {

    private val arKategori = ArrayList<DcKategori>()
    private lateinit var rvKategori: RecyclerView
    private lateinit var tvEmpty: TextView
    private lateinit var fabTambah: FloatingActionButton
    private lateinit var adapter: AdapterKategori
    private lateinit var sp: SharedPreferences

    private val gson = Gson()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        sp = getSharedPreferences("pref_keuangan", MODE_PRIVATE)

        rvKategori = findViewById(R.id.rvKategori)
        tvEmpty = findViewById(R.id.tvEmpty)
        fabTambah = findViewById(R.id.fabTambahKategori)

        val btnGoIncome = findViewById<Button>(R.id.btnGoIncome)
        val btnGoExpense = findViewById<Button>(R.id.btnGoExpense)

        btnGoIncome.setOnClickListener {
            startActivity(Intent(this, IncomeActivity::class.java))
        }

        btnGoExpense.setOnClickListener {
            startActivity(Intent(this, ExpenseActivity::class.java))
        }

        adapter = AdapterKategori(arKategori, this)
        rvKategori.layoutManager = LinearLayoutManager(this)
        rvKategori.adapter = adapter

        muatDataDariSP()

        fabTambah.setOnClickListener {
            tampilkanDialogForm(null, -1)
        }
    }

    private fun muatDataDariSP() {
        arKategori.clear()
        val json = sp.getString("spKategori", null)
        if (json != null) {
            val type = object : TypeToken<ArrayList<DcKategori>>() {}.type
            val data: ArrayList<DcKategori> = gson.fromJson(json, type)
            arKategori.addAll(data)
        }
        updateUI()
    }

    private fun simpanKeSP() {
        val json = gson.toJson(arKategori)
        sp.edit().putString("spKategori", json).apply()
        updateUI()
    }

    private fun updateUI() {
        adapter.notifyDataSetChanged()
        tvEmpty.isVisible = arKategori.isEmpty()
    }

    private fun tampilkanDialogForm(kategoriToEdit: DcKategori?, position: Int) {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_kategori, null)
        val etNama = dialogView.findViewById<EditText>(R.id.etNamaKategori)
        val rbPemasukan = dialogView.findViewById<RadioButton>(R.id.rbPemasukan)
        val rbPengeluaran = dialogView.findViewById<RadioButton>(R.id.rbPengeluaran)

        if (kategoriToEdit != null) {
            etNama.setText(kategoriToEdit.nama)
            if (kategoriToEdit.jenis == "Pemasukan") {
                rbPemasukan.isChecked = true
            } else {
                rbPengeluaran.isChecked = true
            }
        }

        val title = if (kategoriToEdit == null) "Tambah Kategori" else "Ubah Kategori"

        AlertDialog.Builder(this)
            .setTitle(title)
            .setView(dialogView)
            .setPositiveButton("Simpan") { _, _ ->
                val nama = etNama.text.toString().trim()
                val jenis = if (rbPemasukan.isChecked) "Pemasukan" else "Pengeluaran"

                if (nama.isNotEmpty()) {
                    if (kategoriToEdit == null) {
                        arKategori.add(DcKategori(nama, jenis))
                    } else {
                        arKategori[position].nama = nama
                        arKategori[position].jenis = jenis
                    }
                    simpanKeSP()
                }
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    override fun onEdit(position: Int) {
        tampilkanDialogForm(arKategori[position], position)
    }

    override fun onDelete(position: Int) {
        AlertDialog.Builder(this)
            .setTitle("Hapus Kategori")
            .setMessage("Apakah Anda yakin ingin menghapus ${arKategori[position].nama}?")
            .setPositiveButton("Ya") { _, _ ->
                arKategori.removeAt(position)
                simpanKeSP()
            }
            .setNegativeButton("Tidak", null)
            .show()
    }
}
