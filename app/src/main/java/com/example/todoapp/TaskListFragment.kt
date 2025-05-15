package com.example.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Button
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class TaskListFragment : Fragment() {
    private lateinit var taskRv: RecyclerView
    private lateinit var taskAdapter: TaskRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        val taskDao = db.taskDao()
        val categoryDao = db.categoryDao()
        val categories: TabLayout = view.findViewById(R.id.tab_layout)

        categoryDao.getAll().observe(viewLifecycleOwner) { list ->
            for (category in list) {
                if (category.isStarredCategory) {
                    categories.addTab(categories.newTab().setIcon(R.drawable.ic_star_filled))
                } else {
                    categories.addTab(categories.newTab().setText(category.name))
                }
            }
        }

        taskRv = view.findViewById(R.id.task_recycler_view)
        taskRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        taskAdapter = TaskRecyclerViewAdapter(emptyList())
        taskRv.adapter = taskAdapter

        taskDao.getAll().observe(viewLifecycleOwner) { list ->
            taskAdapter.updateData(list)
        }

        // Set padding to recycler view equal to header height
        val header = view.findViewById<View>(R.id.header)
        header.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                header.viewTreeObserver.removeOnGlobalLayoutListener(this)
                val headerHeight = header.height

                taskRv.setPadding(
                    taskRv.paddingLeft,
                    taskRv.paddingTop,
                    taskRv.paddingRight,
                    headerHeight
                )
            }
        })

        view.findViewById<FloatingActionButton>(R.id.btn_add).setOnClickListener {
            findNavController().navigate(R.id.action_taskListFragment_to_createTaskFragment)
        }
    }
}