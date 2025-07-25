package com.example.todoapp

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.ImageView
import android.widget.PopupMenu
import android.widget.TextView
import android.view.MotionEvent
import androidx.annotation.MenuRes
import androidx.lifecycle.LiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.tabs.TabLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskListFragment : Fragment() {
    private lateinit var taskRv: RecyclerView
    private lateinit var taskAdapter: TaskRecyclerViewAdapter
    private var selectCategoryId: Int? = null
    private var currentTaskLiveData: LiveData<List<Task>>? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_task_list, container, false)
    }

    @SuppressLint("InflateParams", "ClickableViewAccessibility")
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
            selectCategoryId = newCategoryId
        }

        parentFragmentManager.setFragmentResultListener(
            "categoryNewNameRequest",
            viewLifecycleOwner
        ) { _, bundle ->
            val categoryId = bundle.getInt("categoryId")
            val newName = bundle.getString("newName", "")

            lifecycleScope.launch(Dispatchers.IO) {
                val updatedCategory = Category(categoryId, newName)
                categoryDao.updateCategories(updatedCategory)
                selectCategoryId = categoryId
            }
        }

        taskRv = view.findViewById(R.id.task_recycler_view)
        taskRv.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        taskAdapter = TaskRecyclerViewAdapter(emptyList(), taskDao) { task ->
            val action = TaskListFragmentDirections.actionTaskListFragmentToEditTaskFragment(task.id)
            findNavController().navigate(action)
        }
        taskRv.adapter = taskAdapter

        categoryDao.getAll().observe(viewLifecycleOwner) { categoryList ->
            categories.removeAllTabs()
            categories.clearOnTabSelectedListeners()

            categories.addTab(categories.newTab().setIcon(R.drawable.ic_star_filled).setTag(0))

            for (category in categoryList) {
                val tab = categories.newTab().setText(category.name).setTag(category.id)
                categories.addTab(tab)

                val tabView = tab.view
                tabView.setOnLongClickListener {
                    val categoryId = tab.tag as Int
                    val currentName = tab.text.toString()
                    if (categoryId <= 0 || categoryId == 1) {
                        false
                    } else {
                        showMenu(it, R.menu.category_menu, categoryId, currentName, categoryDao)
                        true
                    }
                }
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

            for (i in 0 until categories.tabCount) {
                val tab = categories.getTabAt(i)
                if (tab?.tag as? Int == -1) {
                    val tabView = tab.view

                    tabView.setOnTouchListener { _, event ->
                        if (event.action == MotionEvent.ACTION_UP) {
                            findNavController().navigate(R.id.action_taskListFragment_to_createCategoryFragment)

                            if (categories.tabCount >= 2) {
                                val previousTab = categories.selectedTabPosition
                                tabView.post {
                                    categories.getTabAt(previousTab)?.select()
                                }
                            }
                            return@setOnTouchListener true
                        }
                        false
                    }
                    break
                }
            }

            categories.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
                override fun onTabSelected(tab: TabLayout.Tab) {
                    val categoryId = tab.tag as Int
                    currentTaskLiveData?.removeObservers(viewLifecycleOwner)
                    currentTaskLiveData = when (categoryId) {
                        // Starred
                        0 -> taskDao.getStarredTasks()
                        // New Category
                        -1 -> null
                        // All
                        1 -> taskDao.getAll()
                        else -> taskDao.filterTasksByCategory(categoryId)
                    }

                    currentTaskLiveData?.observe(viewLifecycleOwner) { tasks ->
                        taskAdapter.updateData(tasks)
                    }

                    if (categoryId == -1) {
                        findNavController().navigate(R.id.action_taskListFragment_to_createCategoryFragment)
                        if (categories.tabCount >= 2) categories.getTabAt(1)?.select()
                    }
                }

                override fun onTabUnselected(tab: TabLayout.Tab) {
                }

                override fun onTabReselected(tab: TabLayout.Tab) {
                }
            })

            var tabWasSelected = false

            selectCategoryId?.let { categoryId ->
                for (i in 0 until categories.tabCount) {
                    val tab = categories.getTabAt(i)
                    if (tab?.tag as? Int == categoryId) {
                        tab.select()
                        selectCategoryId = null
                        tabWasSelected = true
                        return@let
                    }
                }
            }

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
            val categoryId = categories.getTabAt(categories.selectedTabPosition)?.tag as Int ?: 1
            val action = TaskListFragmentDirections.actionTaskListFragmentToCreateTaskFragment(categoryId)
            findNavController().navigate(action)
        }
    }

    private fun showMenu(v: View, @MenuRes menuRes: Int, categoryId: Int, currentName: String, categoryDao: CategoryDao) {
        val popup = PopupMenu(requireContext(), v)
        popup.menuInflater.inflate(menuRes, popup.menu)

        popup.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.rename_category -> {
                    val dialog = RenameCategoryDialogFragment.newInstance(categoryId, currentName)
                    if (parentFragmentManager.findFragmentByTag("RENAME_CATEGORY_DIALOG") == null) {
                        dialog.show(parentFragmentManager, "RENAME_CATEGORY_DIALOG")
                    }
                    true
                }

                R.id.delete_category -> {
                    categoryDao.getCategoryById(categoryId).observe(viewLifecycleOwner) { category ->
                        if (category != null) {
                            categoryDao.getCategoryById(categoryId)
                                .removeObservers(viewLifecycleOwner)
                            lifecycleScope.launch(Dispatchers.IO) {
                                categoryDao.deleteCategories(category)
                                selectCategoryId = 1
                            }
                        }
                    }
                    true
                }

                else -> false
            }
        }

        popup.show()
    }
}
