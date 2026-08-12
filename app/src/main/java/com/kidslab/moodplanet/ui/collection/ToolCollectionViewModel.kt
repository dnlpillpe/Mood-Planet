package com.kidslab.moodplanet.ui.collection

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.kidslab.moodplanet.data.local.entity.CopingTool
import com.kidslab.moodplanet.data.repository.ToolRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch

private const val ALL_TIME_DAYS = 36_500 // ~100 años: suficiente para representar "todo el historial"

data class CollectedTool(val tool: CopingTool, val timesUsed: Int)

class ToolCollectionViewModel(private val toolRepository: ToolRepository) : ViewModel() {

    private val _collectedTools = MutableStateFlow<List<CollectedTool>>(emptyList())
    val collectedTools: StateFlow<List<CollectedTool>> = _collectedTools.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                toolRepository.observeTools(),
                toolRepository.observeUsage(ALL_TIME_DAYS)
            ) { tools, usage ->
                val usageMap = usage.associateBy { it.copingToolId }
                tools.map { tool -> CollectedTool(tool, usageMap[tool.id]?.count ?: 0) }
            }.collect { _collectedTools.value = it }
        }
    }
}
