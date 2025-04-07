package com.example.todoapp

import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.LocalTime

class TaskListFragment : Fragment() {
    private lateinit var categoryRecyclerView: RecyclerView
    private lateinit var categoryRecyclerViewAdapter: CategoryRecyclerViewAdapter
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var taskRecyclerViewAdapter: TaskRecyclerViewAdapter

    private val categoryList = listOf(
        Category(1, "Starred", true),
        Category(2, "My Tasks", false),
        Category(3, "Work", false),
        Category(4, "School", false),
        Category(5, "Personal", false),
        Category(6, "Other", false),
    )

    @RequiresApi(Build.VERSION_CODES.O)
    private val taskList = listOf(
        Task(1, "Task 1", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
        Task(2, "Task 2", null, LocalDate.now(), LocalTime.now(), null, false, false, true, null),
        Task(3, "Task 3", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
        Task(4, "Task 4", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
        Task(5, "Task 5", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
        Task(6, "Task 6", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
        Task(7, "Task 7", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
        Task(8, "Task 8", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
        Task(9, "Task 9", null, LocalDate.now(), LocalTime.now(), null,false, false, false, null),
        Task(10, "Task 10", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
        Task(11, "Task 11", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
        Task(12, "Task 12", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
        Task(13, "Task 13", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
        Task(14, "Task 14", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
        Task(15, "Task 15", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
        Task(16, "Task 16", null, LocalDate.now(), LocalTime.now(), null, false, false, false, null),
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        categoryRecyclerView = view.findViewById(R.id.category_recycler_view)
        categoryRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerViewAdapter = CategoryRecyclerViewAdapter(categoryList)
        categoryRecyclerView.adapter = categoryRecyclerViewAdapter

        taskRecyclerView = view.findViewById(R.id.task_recycler_view)
        taskRecyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        taskRecyclerViewAdapter = TaskRecyclerViewAdapter(taskList)
        taskRecyclerView.adapter = taskRecyclerViewAdapter

        // Set padding to recycler view equal to header height
        val header = view.findViewById<View>(R.id.header)
        header.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(Build.VERSION_CODES.O)
            override fun onGlobalLayout() {
                header.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val headerHeight = header.height

                taskRecyclerView.setPadding(
                    taskRecyclerView.paddingLeft,
                    taskRecyclerView.paddingTop,
                    taskRecyclerView.paddingRight,
                    headerHeight
                )
            }
        })
    }
}