package com.example.todoapp

import android.app.DatePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker

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

        val timePicker = MaterialTimePicker
            .Builder()
            .setTitleText("Select time")
            .build()

        view.findViewById<Button>(R.id.btn_set_date).setOnClickListener {
            if (parentFragmentManager.findFragmentByTag("DATE_PICKER") == null) {
                val dpFragment = DatePickerFragment()
                dpFragment.show(parentFragmentManager, "DATE_PICKER")
            }
        }

        view.findViewById<Button>(R.id.btn_set_time).setOnClickListener {
            if (parentFragmentManager.findFragmentByTag("TIME_PICKER") == null) {
                timePicker.show(parentFragmentManager, "TIME_PICKER")
            }
        }

        taskDao.getSubtasksForTask(1).observe(viewLifecycleOwner) { list ->
            subtaskAdapter.updateData(list)
        }
    }
}