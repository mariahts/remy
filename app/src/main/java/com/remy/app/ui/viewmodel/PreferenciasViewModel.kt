package com.remy.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.remy.app.data.model.FiltrosSugerencias
import com.remy.app.data.repository.PreferenciasRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PreferenciasViewModel(private val repositorio: PreferenciasRepository) : ViewModel() {

    val filtros: StateFlow<FiltrosSugerencias> = repositorio.filtros
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), FiltrosSugerencias())

    fun alternarTipoDePlato(tipo: String) {
        viewModelScope.launch {
            val actuales = repositorio.filtros.first().tiposDePlato
            repositorio.guardarTiposDePlato(if (tipo in actuales) actuales - tipo else actuales + tipo)
        }
    }

    fun cambiarMaximoFaltantes(valor: Int) {
        viewModelScope.launch { repositorio.guardarMaximoFaltantes(valor) }
    }

    fun alternarFaltantePermitido(id: Long) {
        viewModelScope.launch {
            val actuales = repositorio.filtros.first().faltantesPermitidos
            repositorio.guardarFaltantesPermitidos(
                if (id in actuales) actuales - id else actuales + id
            )
        }
    }

    fun limpiarFaltantesPermitidos() {
        viewModelScope.launch { repositorio.guardarFaltantesPermitidos(emptySet()) }
    }

    fun cambiarTiempoMaximo(minutos: Int) {
        viewModelScope.launch { repositorio.guardarTiempoMaximo(minutos) }
    }

    fun cambiarKcalMaximas(kcal: Int) {
        viewModelScope.launch { repositorio.guardarKcalMaximas(kcal) }
    }

    fun cambiarProteinasMinimas(gramos: Int) {
        viewModelScope.launch { repositorio.guardarProteinasMinimas(gramos) }
    }

    fun cambiarRegion(region: String) {
        viewModelScope.launch { repositorio.guardarRegion(region) }
    }

    fun alternarAltaProteina() {
        viewModelScope.launch {
            val actual = repositorio.filtros.first().altaProteina
            repositorio.guardarAltaProteina(!actual)
        }
    }

    fun alternarBajaHidratos() {
        viewModelScope.launch {
            val actual = repositorio.filtros.first().bajaHidratos
            repositorio.guardarBajaHidratos(!actual)
        }
    }

    fun alternarBajaGrasa() {
        viewModelScope.launch {
            val actual = repositorio.filtros.first().bajaGrasa
            repositorio.guardarBajaGrasa(!actual)
        }
    }

    fun limpiarFiltros() {
        viewModelScope.launch { repositorio.limpiarFiltros() }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = viewModelFactory {
            initializer {
                val app = this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY]!! as Application
                PreferenciasViewModel(PreferenciasRepository.delContexto(app))
            }
        }
    }
}
