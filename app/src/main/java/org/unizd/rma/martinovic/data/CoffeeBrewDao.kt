package org.unizd.rma.martinovic.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import org.unizd.rma.martinovic.model.CoffeeBrew

@Dao
interface CoffeeBrewDao {
    @Query("SELECT * FROM coffee_brew ORDER BY brewDate DESC")
    fun getAll(): Flow<List<CoffeeBrew>>

    @Insert
    suspend fun insert(item: CoffeeBrew): Long

    @Update
    suspend fun update(item: CoffeeBrew)

    @Delete
    suspend fun delete(item: CoffeeBrew)

    @Query("SELECT * FROM coffee_brew WHERE id = :id LIMIT 1")
    suspend fun find(id: Long): CoffeeBrew?
}
