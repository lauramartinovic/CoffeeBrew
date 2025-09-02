package org.unizd.rma.martinovic.ui

import android.app.DatePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.OpenableColumns
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import coil.load
import org.unizd.rma.martinovic.databinding.ActivityEditBinding
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class EditActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ID = "id"
        private val METHODS = arrayOf("V60", "Aeropress", "French Press", "Espresso", "Moka")
    }

    private lateinit var b: ActivityEditBinding
    private val vm: CoffeeViewModel by viewModels()
    private var currentId: Long? = null
    private var pickedDate: Date = Date()
    private var pickedUri: String = ""

    private val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val pickImage = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
        uri?.let {
            contentResolver.takePersistableUriPermission(
                it, Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            pickedUri = it.toString()
            b.imgPreview.load(it)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        b = ActivityEditBinding.inflate(layoutInflater)
        setContentView(b.root)

        // Spinner – jednostavno postavljanje
        b.spnMethod.adapter = android.widget.ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            METHODS
        )

        // Ako je edit
        currentId = intent.getLongExtra(EXTRA_ID, 0L).takeIf { it != 0L }

        // Datum
        b.btnPickDate.text = df.format(pickedDate)
        b.btnPickDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { time = pickedDate }
            DatePickerDialog(this,
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

        b.btnPickImage.setOnClickListener {
            pickImage.launch(arrayOf("image/*"))
        }

        b.btnSave.setOnClickListener {
            val name = b.edtName.text.toString().trim()
            val roaster = b.edtRoaster.text.toString().trim()
            val method = METHODS[b.spnMethod.selectedItemPosition]

            if (name.isEmpty() || roaster.isEmpty()) {
                android.widget.Toast.makeText(this, "Name and Roaster are required", android.widget.Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            vm.save(currentId, name, roaster, method, pickedDate, pickedUri)
            finish()
        }

        b.btnDelete.setOnClickListener {
            val id = currentId ?: return@setOnClickListener
            // jednostavno brisanje: dohvat iz liste i delete
            val item = vm.list.value.firstOrNull { it.id == id }
            if (item != null) {
                vm.delete(item)
                finish()
            }
        }

        // Ako je edit, popuni polja (jednostavno iz current liste)
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
}
