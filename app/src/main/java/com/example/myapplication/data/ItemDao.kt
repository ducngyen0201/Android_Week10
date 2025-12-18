package com.example.myapplication.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

/**
 * Database access object to access the Inventory database
 */
@Dao
interface ItemDao {
    
    /**
     * Insert an item into the database
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(item: Item)
    
    /**
     * Update an item in the database
     */
    @Update
    suspend fun update(item: Item)
    
    /**
     * Delete an item from the database
     */
    @Delete
    suspend fun delete(item: Item)
    
    /**
     * Get a specific item by ID
     */
    @Query("SELECT * from item WHERE id = :id")
    fun getItem(id: Int): Flow<Item>
    
    /**
     * Get all items from the database, ordered by name ascending
     */
    @Query("SELECT * from item ORDER BY name ASC")
    fun getItems(): Flow<List<Item>>
}
