package com.example.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class CreateTaskFragment : Fragment() {
    private lateinit var subtaskRv: RecyclerView
    private lateinit var subtaskAdapter: SubtaskRecyclerViewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        val taskDao = db.taskDao()

        subtaskRv = view.findViewById(R.id.rv_subtasks)
        subtaskAdapter = SubtaskRecyclerViewAdapter(emptyList())
        subtaskRv.adapter = subtaskAdapter
        subtaskRv.layoutManager = LinearLayoutManager(requireContext())

        taskDao.getSubtasksForTask(1).observe(viewLifecycleOwner) { list ->
            subtaskAdapter.updateData(list)
        }
    }
}