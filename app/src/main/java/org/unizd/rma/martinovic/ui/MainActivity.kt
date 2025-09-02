package org.unizd.rma.martinovic.ui

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
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

        lifecycleScope.launchWhenStarted {
            vm.list.collect { adapter.submit(it) }
        }


        b.fabAdd.setOnClickListener {
            startActivity(Intent(this, EditActivity::class.java))
        }
    }
}
