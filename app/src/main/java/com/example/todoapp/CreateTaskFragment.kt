package com.example.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import java.time.Instant
import java.time.ZoneId
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class CreateTaskFragment : Fragment(), CreateSubtaskDialogFragment.OnSubtaskCreatedListener {
    private lateinit var subtaskRv: RecyclerView
    private lateinit var subtaskAdapter: SubtaskRecyclerViewAdapter
    private val pendingSubtasks = mutableListOf<Subtask>()
    val args: CreateTaskFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()

        val db = AppDatabase.getDatabase(requireContext())
        val taskDao = db.taskDao()

        subtaskAdapter = SubtaskRecyclerViewAdapter(
            mutableListOf(),
            taskDao
        ) { deletedSubtask ->
            pendingSubtasks.removeAll { it.title == deletedSubtask.title }
        }
        subtaskRv = view.findViewById(R.id.rv_subtasks)
        subtaskRv.adapter = subtaskAdapter
        subtaskRv.layoutManager = LinearLayoutManager(requireContext())

        val datePicker = MaterialDatePicker
            .Builder
            .datePicker()
            .setTheme(R.style.DatePickerTheme)
            .setTitleText("Select date")
            .build()

        val timePicker = MaterialTimePicker
            .Builder()
            .setTheme(R.style.TimePickerTheme)
            .setTitleText("Select time")
            .build()

        var selectedDateInMillis: Long? = null
        var selectedHour: Int? = null
        var selectedMinute: Int? = null

        val dateButton = view.findViewById<Button>(R.id.btn_set_date)
        val timeButton = view.findViewById<Button>(R.id.btn_set_time)

        dateButton.setOnClickListener {
            if (parentFragmentManager.findFragmentByTag("DATE_PICKER") == null) {
                datePicker.show(parentFragmentManager, "DATE_PICKER")
            }
        }

        datePicker.addOnPositiveButtonClickListener { selection ->
            selectedDateInMillis = selection
            dateButton.setTextColor(resources.getColor(R.color.dark_gray, null))
            
            val selectedDate = Instant.ofEpochMilli(selection)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()
            val formattedDate = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            dateButton.text = formattedDate
        }

        timeButton.setOnClickListener {
            if (parentFragmentManager.findFragmentByTag("TIME_PICKER") == null) {
                timePicker.show(parentFragmentManager, "TIME_PICKER")
            }
        }

        timePicker.addOnPositiveButtonClickListener {
            selectedHour = timePicker.hour
            selectedMinute = timePicker.minute

            val selectedTime = LocalTime.of(timePicker.hour, timePicker.minute)
            val formattedTime = selectedTime.format(DateTimeFormatter.ofPattern("h:mm a"))
            timeButton.text = formattedTime
        }

        view.findViewById<Button>(R.id.btn_add_subtask).setOnClickListener {
            val dialog = CreateSubtaskDialogFragment()
            dialog.setOnSubtaskCreatedListener(this)
            if (parentFragmentManager.findFragmentByTag("CREATE_SUBTASK_DIALOG") == null) {
                dialog.show(parentFragmentManager, "CREATE_SUBTASK_DIALOG")
            }
        }

        view.findViewById<Button>(R.id.btn_submit).setOnClickListener {
            val title = view.findViewById<TextInputLayout>(R.id.title_input).editText?.text.toString()
            val description =  view.findViewById<TextInputLayout>(R.id.description_input).editText?.text.toString()

            if (!validateForm(view, title, selectedDateInMillis, dateButton)) {
                return@setOnClickListener
            }

            val epochDay = Instant.ofEpochMilli(selectedDateInMillis!!)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()
                .toEpochDay()

            val timeNanoOfDay = if (selectedHour != null && selectedMinute != null) {
                (selectedHour!! * 3600L + selectedMinute!! * 60L) * 1_000_000_000L
            } else {
                null
            }

            val newTask = Task(
                id = 0, // auto-generated by Room
                title = title,
                description = description,
                dateEpochDay = epochDay,
                timeNanoOfDay = timeNanoOfDay,
                categoryId = args.categoryId,
                isStarred = false
            )

            Thread {
                val taskIds = taskDao.insertAll(newTask)
                val taskId = taskIds.first().toInt()

                val subtasksToInsert = pendingSubtasks.map { subtask ->
                    subtask.copy(taskId = taskId)
                }
                if (subtasksToInsert.isNotEmpty()) {
                    taskDao.insertSubtasks(*subtasksToInsert.toTypedArray())
                }

                activity?.runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Task created successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    parentFragmentManager.popBackStack()
                }
            }.start()
        }
    }

    override fun onSubtaskCreated(title: String) {
        // Create a temporary subtask (ID will be set when task is saved)
        val newSubtask = Subtask(
            id = 0, // temporary ID
            taskId = 0, // will be set when task is created
            title = title
        )
        pendingSubtasks.add(newSubtask)
        subtaskAdapter.addSubtask(newSubtask)
    }

    private fun initToolbar() {
        val toolbar: MaterialToolbar = view?.findViewById(R.id.header_toolbar)!!
        toolbar.title = getString(R.string.new_task)
        toolbar.setNavigationOnClickListener { _ ->
            parentFragmentManager.popBackStack()
        }
    }

    private fun validateForm(
        view: View,
        title: String,
        selectedDateInMillis: Long?,
        dateButton: Button
    ): Boolean {
        val titleInputLayout = view.findViewById<TextInputLayout>(R.id.title_input)
        val isTitleValid = validateTitle(title, titleInputLayout)
        val isDateValid = validateDate(selectedDateInMillis, dateButton)

        if (!isTitleValid && !isDateValid) {
            Toast.makeText(requireContext(), "Title and date are required", Toast.LENGTH_SHORT).show()
        } else if (!isTitleValid) {
            Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
        } else if (!isDateValid) {
            Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT).show()
        }

        return isTitleValid && isDateValid
    }

    private fun validateTitle(title: String, titleInputLayout: TextInputLayout): Boolean {
        if (title.isEmpty()) {
            titleInputLayout.error = "Title is required"
            return false
        }
        titleInputLayout.error = null
        return true
    }

    private fun validateDate(selectedDateInMillis: Long?, dateButton: Button): Boolean {
        if (selectedDateInMillis == null) {
            dateButton.setTextColor(
                resources.getColor(
                    com.google.android.material.R.color.design_default_color_error,
                    null
                )
            )
            return false
        }
        return true
    }
}