package com.example.todoapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.TextView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout

class TaskListFragment : Fragment() {
    private lateinit var taskRv: RecyclerView
    private lateinit var taskAdapter: TaskRecyclerViewAdapter
    private var pendingCategoryIdToSelect: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    @SuppressLint("InflateParams")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        val taskDao = db.taskDao()
        val categoryDao = db.categoryDao()
        val categories: TabLayout = view.findViewById(R.id.tab_layout)

        parentFragmentManager.setFragmentResultListener(
            "newCategoryRequest",
            viewLifecycleOwner
        ) { _, bundle ->
            val newCategoryId = bundle.getInt("categoryId")
            pendingCategoryIdToSelect = newCategoryId
        }

        taskRv = view.findViewById(R.id.task_recycler_view)
        taskRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        taskAdapter = TaskRecyclerViewAdapter(emptyList(), taskDao)
        taskRv.adapter = taskAdapter

        taskDao.getAll().observe(viewLifecycleOwner) { list ->
            taskAdapter.updateData(list)
        }

        categoryDao.getAll().observe(viewLifecycleOwner) { categoryList ->
            categories.removeAllTabs()

            categories.addTab(categories.newTab().setIcon(R.drawable.ic_star_filled).setTag(0))

            for (category in categoryList) {
                categories.addTab(
                    categories.newTab().setText(category.name).setTag(category.id)
                )
            }

            val newListTab = categories.newTab().setTag(-1)
            val tabView =
                LayoutInflater.from(requireContext()).inflate(R.layout.custom_tab_layout, null)
            val tabIcon = tabView.findViewById<ImageView>(R.id.tab_icon)
            val tabText = tabView.findViewById<TextView>(R.id.tab_text)

            tabIcon.setImageResource(R.drawable.ic_material_add)
            tabText.text = getString(R.string.new_category)

            newListTab.customView = tabView
            categories.addTab(newListTab)

            var tabWasSelected = false

            pendingCategoryIdToSelect?.let { categoryId ->
                for (i in 0 until categories.tabCount) {
                    val tab = categories.getTabAt(i)
                    if (tab?.tag as? Int == categoryId) {
                        tab.select()
                        pendingCategoryIdToSelect = null
                        tabWasSelected = true
                        return@let
                    }
                }
            }

            categories.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val categoryId = tab.tag as Int
                    if (categoryId == 0) {
                        taskDao.getStarredTasks()
                            .observe(viewLifecycleOwner) { tasks ->
                                taskAdapter.updateData(tasks)
                            }
                    } else if (categoryId == -1) {
                        findNavController().navigate(R.id.action_taskListFragment_to_createCategoryFragment)
                    } else {
                        taskDao.filterTasksByCategory(categoryId)
                            .observe(viewLifecycleOwner) { tasks ->
                                taskAdapter.updateData(tasks)
                            }
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                }
            })

            if (categories.tabCount >= 2) {
                if (!tabWasSelected) {
                    categories.getTabAt(1)?.select()
                }
            }
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