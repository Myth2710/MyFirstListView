package com.example.myfirstlistview

import android.app.AlertDialog
import android.os.Bundle
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    private val data = mutableListOf<String>()
    private lateinit var lvAdapter: ArrayAdapter<String>
    private lateinit var lv1: ListView
    private lateinit var btnTambah: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        lv1 = findViewById(R.id.lv1)
        btnTambah = findViewById(R.id.btnTambah)

        data.addAll(listOf("1", "2", "3", "4", "5"))
        lvAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, data)
        lv1.adapter = lvAdapter

        btnTambah.setOnClickListener {
            val next = (data.mapNotNull { it.toIntOrNull() }.maxOrNull() ?: 0) + 1
            data.add(next.toString())
            lvAdapter.notifyDataSetChanged()
            lv1.smoothScrollToPosition(data.lastIndex)
        }

        lv1.setOnItemClickListener { _, _, position, _ ->
            Toast.makeText(this, data[position], Toast.LENGTH_SHORT).show()
        }

        val gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onDown(e: MotionEvent): Boolean = true

            override fun onDoubleTap(e: MotionEvent): Boolean {
                val position = lv1.pointToPosition(e.x.toInt(), e.y.toInt())
                if (position != ListView.INVALID_POSITION) {
                    showActionDialog(position, data[position])
                }
                return true
            }
        })

        lv1.setOnTouchListener { _, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    private fun showActionDialog(
        position: Int,
        selectedItem: String
    ) {
        AlertDialog.Builder(this)
            .setTitle("Item $selectedItem")
            .setMessage("Pilih tindakan yang ingin dilakukan:")
            .setPositiveButton("Update") { _, _ ->
                showUpdateDialog(position, selectedItem)
            }
            .setNegativeButton("Hapus") { _, _ ->
                data.removeAt(position)
                lvAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Item $selectedItem dihapus", Toast.LENGTH_SHORT).show()
            }
            .setNeutralButton("Batal", null)
            .show()
    }

    private fun showUpdateDialog(position: Int, oldValue: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Update Data")

        val layout = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(50, 40, 50, 10)
        }

        val tvOld = TextView(this).apply {
            text = "Data lama: $oldValue"
            textSize = 16f
        }

        val etNew = EditText(this).apply {
            hint = "Masukkan data baru"
            setText(oldValue)
            setSelection(text.length)
        }

        layout.addView(tvOld)
        layout.addView(etNew)
        builder.setView(layout)

        builder.setPositiveButton("Simpan") { dialog, _ ->
            val newValue = etNew.text.toString().trim()
            if (newValue.isNotEmpty()) {
                data[position] = newValue
                lvAdapter.notifyDataSetChanged()
                Toast.makeText(this, "Data diupdate jadi: $newValue", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Data baru tidak boleh kosong!", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
        builder.setNegativeButton("Batal") { dialog, _ -> dialog.dismiss() }
        builder.create().show()
    }
}
