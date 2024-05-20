package cl.mferrer.android.misservicios

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RegistroEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun registroDao(): RegistroDao
}

