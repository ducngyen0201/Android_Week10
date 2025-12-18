package com.example.myapplication

import android.app.Application
import com.example.myapplication.data.ItemRoomDatabase

/**
 * Application class that initializes the database.
 */
class InventoryApplication : Application() {
    
    // Using by lazy so the database is only created when needed
    // rather than when the application starts
    val database: ItemRoomDatabase by lazy { ItemRoomDatabase.getDatabase(this) }
}
