package org.unizd.rma.martinovic.ui

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import coil.load
import org.unizd.rma.martinovic.databinding.ActivityEditBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "id"
        private val METHODS = arrayOf("V60", "Aeropress", "French Press", "Espresso", "Moka")
        private const val AUTH = "org.unizd.rma.martinovic.fileprovider"
    }

    private lateinit var b: ActivityEditBinding
    private val vm: CoffeeViewModel by viewModels()

    private var currentId: Long? = null
    private var pickedDate: Date = Date()
    private var pickedUri: String = ""

    private val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


    private var cameraOutputUri: Uri? = null
    private val takePicture = registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            cameraOutputUri?.let { uri ->
                pickedUri = uri.toString()
                b.imgPreview.load(uri)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditBinding.inflate(layoutInflater)
        setContentView(b.root)


        b.spnMethod.adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            METHODS
        )


        currentId = intent.getLongExtra(EXTRA_ID, 0L).takeIf { it != 0L }


        b.btnPickDate.text = df.format(pickedDate)
        b.btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { time = pickedDate }
            DatePickerDialog(
                this,
                { _, y, m, d ->
                    cal.set(y, m, d, 0, 0, 0)
                    pickedDate = cal.time
                    b.btnPickDate.text = df.format(pickedDate)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        // Kamera (obavezna umjesto galerije)
        b.btnTakePhoto.setOnClickListener {
            cameraOutputUri = createImageUri()
            cameraOutputUri?.let { uri ->
                takePicture.launch(uri)
            } ?: toast("Cannot create image file")
        }


        b.btnSave.setOnClickListener {
            val name = b.edtName.text.toString().trim()
            val roaster = b.edtRoaster.text.toString().trim()
            val method = METHODS[b.spnMethod.selectedItemPosition]

            if (name.isEmpty() || roaster.isEmpty()) {
                toast("Name and Roaster are required")
                return@setOnClickListener
            }

            if (pickedUri.isBlank()) {
                toast("Please take a photo")
                return@setOnClickListener
            }

            vm.save(currentId, name, roaster, method, pickedDate, pickedUri)
            finish()
        }


        b.btnCancel.setOnClickListener {
            finish()
        }
        b.btnDelete.setOnClickListener {
            val id = currentId ?: return@setOnClickListener
            val item = vm.list.value.firstOrNull { it.id == id }
            if (item != null) {
                AlertDialog.Builder(this)
                    .setTitle("Delete")
                    .setMessage("Delete this coffee brew?")
                    .setPositiveButton("Delete") { _, _ ->
                        vm.delete(item)
                        finish()
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            }
        }


        currentId?.let { id ->
            val item = vm.list.value.firstOrNull { it.id == id }
            if (item != null) {
                b.edtName.setText(item.coffeeName)
                b.edtRoaster.setText(item.roaster)
                b.spnMethod.setSelection(METHODS.indexOf(item.brewMethod).coerceAtLeast(0))
                pickedDate = item.brewDate
                b.btnPickDate.text = df.format(pickedDate)
                pickedUri = item.photoUri
                if (pickedUri.isNotBlank()) b.imgPreview.load(Uri.parse(pickedUri))
                b.btnDelete.visibility = android.view.View.VISIBLE
            }
        }
    }


    private fun createImageUri(): Uri? = try {
        val time = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val fileName = "coffee_$time.jpg"
        val dir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES)
        if (dir?.exists() != true) dir?.mkdirs()
        val imageFile = File(dir, fileName)
        FileProvider.getUriForFile(this, AUTH, imageFile)
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }

    private fun toast(msg: String) =
        android.widget.Toast.makeText(this, msg, android.widget.Toast.LENGTH_SHORT).show()
}
