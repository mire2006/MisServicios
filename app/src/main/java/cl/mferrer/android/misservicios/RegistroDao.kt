package cl.mferrer.android.misservicios

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface RegistroDao {
    @Query("SELECT * FROM registros")
    suspend fun obtenerTodos(): List<RegistroEntity>

    @Insert
    suspend fun insertar(registro: RegistroEntity)
}
