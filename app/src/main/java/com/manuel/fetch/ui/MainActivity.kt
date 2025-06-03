package com.manuel.fetch.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.viewModels
import androidx.core.content.ContextCompat
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.manuel.fetch.R
import com.manuel.fetch.databinding.HiringsActivityBinding
import com.manuel.fetch.model.SortOrder
import com.manuel.fetch.ui.recyclerview.ExpandableAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

private const val TAG = "MainActivity"

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    private val viewModel by viewModels<HiringsViewModel>()

    private val binding: HiringsActivityBinding by lazy {
        HiringsActivityBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate called")

        setContentView(binding.root)

        binding.hiringsRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.groupSorting.setOnClickListener {
            Log.d(TAG, "Sort by Group Button clicked, processing intent")
            viewModel.processIntent(HiringsViewModel.Intent.SortGroups)
        }
        binding.nameSorting.setOnClickListener {
            Log.d(TAG, "Sort by Name Button clicked, processing intent")
            viewModel.processIntent(HiringsViewModel.Intent.SortHirings)
        }

        observeEvents()
    }

    private fun observeEvents() {
        lifecycleScope.launch {
            viewModel.state.flowWithLifecycle(lifecycle).collect { state ->
                when (state) {
                    is HiringsViewModel.MviState.Initial -> {
                        viewModel.processIntent(HiringsViewModel.Intent.LoadData)
                    }
                    is HiringsViewModel.MviState.Data -> {
                        binding.hiringsRecyclerView.adapter = ExpandableAdapter(state.hiringGroups)

                        binding.progressBar.visibility = View.GONE

                        state.hiringGroups.forEach { group ->
                            Log.d(TAG, "Group: ${group.id}, Group Order: ${state.groupOrder}, Hirings Order: ${state.hiringOrder}")
                        }

                        binding.groupSorting.setCompoundDrawablesWithIntrinsicBounds(state.groupOrder.toDrawable(), null, null, null)
                        binding.nameSorting.setCompoundDrawablesWithIntrinsicBounds(state.hiringOrder.toDrawable(), null, null, null)
                    }

                    is HiringsViewModel.MviState.Error -> {
                        binding.progressBar.visibility = View.GONE
                        Log.d(TAG, "Error: ${state.error.message}")
                        Toast.makeText(baseContext, "Error: ${state.error.message}", Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
    }

    private fun SortOrder.toDrawable() =
        ContextCompat.getDrawable(
            baseContext,
            if (this == SortOrder.ASC) {
                R.drawable.ic_sort_ascending
            } else {
                R.drawable.ic_sort_descending
            },
        )
}
