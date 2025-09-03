package org.unizd.rma.martinovic.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import org.unizd.rma.martinovic.data.AppDatabase
import org.unizd.rma.martinovic.model.CoffeeBrew
import org.unizd.rma.martinovic.repository.CoffeeRepository
import java.util.Date

class CoffeeViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = CoffeeRepository(AppDatabase.get(app).coffeeDao())

    val list = repo.all().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun save(
        id: Long?,
        coffeeName: String,
        roaster: String,
        method: String,
        date: Date,
        photoUri: String
    ) = viewModelScope.launch {
        val item = CoffeeBrew(
            id = id ?: 0,
            coffeeName = coffeeName,
            roaster = roaster,
            brewMethod = method,
            brewDate = date,
            photoUri = photoUri
        )
        if (id == null || id == 0L) repo.add(item) else repo.update(item)
    }


    suspend fun findOnce(id: Long) = repo.find(id)


    fun delete(item: CoffeeBrew) = viewModelScope.launch {
        repo.delete(item)
    }
}
