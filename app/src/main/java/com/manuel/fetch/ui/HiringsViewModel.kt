package com.manuel.fetch.ui

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.manuel.fetch.model.HiringGroup
import com.manuel.fetch.model.SortOrder
import com.manuel.fetch.model.toggle
import com.manuel.fetch.repo.HiringRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "HiringsViewModel"

@HiltViewModel
class HiringsViewModel @Inject constructor(private val hiringRepository: HiringRepository) : ViewModel() {

    sealed class MviState {
        object Initial : MviState()
        data class Data(
            val hiringGroups: List<HiringGroup>,
            val groupOrder: SortOrder,
            val hiringOrder: SortOrder
        ) : MviState()
        class Error(val error: Throwable) : MviState()
    }

    sealed class Intent {
        object LoadData : Intent()
        object SortGroups : Intent()
        object SortHirings : Intent()
    }

    private val _state: MutableStateFlow<MviState> = MutableStateFlow(MviState.Initial)
    val state: StateFlow<MviState> = _state.asStateFlow()

    fun processIntent(intent: Intent) {
        viewModelScope.launch {
            handleIntent(intent).fold(
                onSuccess = { data -> _state.update { data } },
                onFailure = { exception -> _state.update { MviState.Error(exception)  } }
            )
        }
    }

    private suspend fun handleIntent(intent: Intent): Result<MviState.Data> {
        return when (intent) {
            is Intent.LoadData -> runCatching {
                MviState.Data(hiringRepository.fetchHirings(), SortOrder.ASC, SortOrder.ASC)
            }.onFailure { Log.d(TAG, "Something went wrong with fetching hirings", it) }

            is Intent.SortGroups -> runCatching {
                sortGroups()
            }.onFailure { Log.d(TAG, "Something went wrong with sorting Groups", it) }

            is Intent.SortHirings -> runCatching {
                sortHirings()
            }.onFailure { Log.d(TAG, "Something went wrong with sorting Hirings", it) }
        }
    }

    private fun sortGroups() : MviState.Data {
        val vmData: MviState.Data = (_state.value as? MviState.Data)!!
        val newSortOrder = vmData.groupOrder.toggle()

        // Sort the hiring groups based on the new sort order
        return vmData.copy(
            hiringGroups = if (newSortOrder == SortOrder.ASC) {
                vmData.hiringGroups.sortedBy { it.id }
            } else {
                vmData.hiringGroups.sortedByDescending { it.id }
            },
            groupOrder = newSortOrder
        )
    }

    private fun sortHirings() : MviState.Data {
        val vmData: MviState.Data = (_state.value as? MviState.Data)!!
        val newSortOrder = vmData.hiringOrder.toggle()

        val newGroups = vmData.hiringGroups.map {
            // Sort hirings within each group based on the new sort order
            it.copy(hirings = if (newSortOrder == SortOrder.ASC) {
                it.hirings.sortedBy { hiring -> hiring.name }
            } else {
                it.hirings.sortedByDescending { hiring -> hiring.name }
            })
        }

        return vmData.copy(
            hiringGroups = newGroups,
            hiringOrder = newSortOrder
        )
    }
}
