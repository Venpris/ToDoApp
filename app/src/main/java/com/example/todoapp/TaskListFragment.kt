package com.example.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class TaskListFragment : Fragment() {
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryRecyclerViewAdapter: CategoryRecyclerViewAdapter

    private val categoryList = listOf(
        Category(1, "Starred", null, true),
        Category(2, "My Tasks", null, false),
        Category(3, "Work", null, false),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryRecyclerView = view.findViewById(R.id.category_recycler_view)
        categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter(categoryList)
        categoryRecyclerView.adapter = categoryRecyclerViewAdapter
    }
}