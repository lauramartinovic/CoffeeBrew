package org.unizd.rma.martinovic.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "coffee_brew")
data class CoffeeBrew(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val coffeeName: String,
    val roaster: String,
    val brewMethod: String, // iz fiksnog popisa
    val brewDate: Date,
    val photoUri: String // spremamo kao String (URI.toString())
)
