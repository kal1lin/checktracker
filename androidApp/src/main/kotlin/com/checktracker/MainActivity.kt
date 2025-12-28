package com.checktracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.checktracker.data.repository.InMemoryExpenseRepository
import com.checktracker.presentation.ui.DashboardScreen
import com.checktracker.presentation.viewmodel.DashboardViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        setContent {
            MaterialTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val repository = InMemoryExpenseRepository()
                    val viewModel: DashboardViewModel = viewModel(
                        factory = object : androidx.lifecycle.ViewModelProvider.Factory {
                            @Suppress("UNCHECKED_CAST")
                            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                                return DashboardViewModel(repository) as T
                            }
                        }
                    )
                    
                    val state = viewModel.state.collectAsState().value
                    
                    DashboardScreen(
                        state = state,
                        onScanClick = { /* Navigate to camera */ },
                        onDateClick = { viewModel.selectDate(it) }
                    )
                }
            }
        }
    }
}
