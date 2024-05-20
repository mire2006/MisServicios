package cl.mferrer.android.misservicios

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ListadoViewModel(private val database: AppDatabase) : ViewModel() {
    val listaRegistros = mutableStateListOf<RegistroEntity>()

    init {
        actualizarRegistrosDesdeBD()
    }

    fun agregarRegistro(registro: RegistroEntity) {
        viewModelScope.launch {
            database.registroDao().insertar(registro)
            actualizarRegistrosDesdeBD()
        }
    }

    private fun actualizarRegistrosDesdeBD() {
        viewModelScope.launch {
            listaRegistros.clear()
            listaRegistros.addAll(database.registroDao().obtenerTodos())
        }
    }
}

class ListadoViewModelFactory(private val database: AppDatabase) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ListadoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return ListadoViewModel(database) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
