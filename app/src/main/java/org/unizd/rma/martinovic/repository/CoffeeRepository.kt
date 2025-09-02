package org.unizd.rma.martinovic.repository

import org.unizd.rma.martinovic.data.CoffeeBrewDao
import org.unizd.rma.martinovic.model.CoffeeBrew

class CoffeeRepository(private val dao: CoffeeBrewDao) {
    fun all() = dao.getAll()
    suspend fun add(item: CoffeeBrew) = dao.insert(item)
    suspend fun update(item: CoffeeBrew) = dao.update(item)
    suspend fun delete(item: CoffeeBrew) = dao.delete(item)
    suspend fun find(id: Long) = dao.find(id)
}
