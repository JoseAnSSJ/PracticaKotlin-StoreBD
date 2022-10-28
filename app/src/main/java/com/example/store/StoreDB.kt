package com.example.store

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = arrayOf(StoreEntity::class), version = 2)
abstract class StoreDB : RoomDatabase() {

    abstract fun storeDao(): StoreDAO

}