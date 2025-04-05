package com.example.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [TaskListFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
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