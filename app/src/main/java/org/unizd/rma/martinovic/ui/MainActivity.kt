package org.unizd.rma.martinovic.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.unizd.rma.martinovic.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var b: ActivityMainBinding
    private val vm: CoffeeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        b = ActivityMainBinding.inflate(layoutInflater)
        setContentView(b.root)

        setSupportActionBar(b.toolbar)

        val adapter = CoffeeAdapter { item ->
            val i = Intent(this, EditActivity::class.java)
            i.putExtra(EditActivity.EXTRA_ID, item.id)
            startActivity(i)
        }

        b.recycler.layoutManager = LinearLayoutManager(this)
        b.recycler.adapter = adapter

        val swipe = object : ItemTouchHelper.SimpleCallback(
            0,
            ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                rv: RecyclerView,
                vh: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ) = false

            override fun onSwiped(vh: RecyclerView.ViewHolder, dir: Int) {
                val pos = vh.bindingAdapterPosition
                if (pos == RecyclerView.NO_POSITION) return
                val item = adapter.getItemAt(pos)

                AlertDialog.Builder(this@MainActivity)
                    .setTitle("Delete")
                    .setMessage("Delete \"${item.coffeeName}\"?")
                    .setPositiveButton("Delete") { _, _ ->
                        vm.delete(item)
                    }
                    .setNegativeButton("Cancel") { _, _ ->
                        adapter.notifyItemChanged(pos)
                    }
                    .setOnCancelListener {
                        adapter.notifyItemChanged(pos)
                    }
                    .show()
            }
        }
        ItemTouchHelper(swipe).attachToRecyclerView(b.recycler)

        lifecycleScope.launchWhenStarted {
            vm.list.collect { adapter.submit(it) }
        }

        b.fabAdd.setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }
    }
}
