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
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import androidx.constraintlayout.widget.ConstraintLayout

class EditTaskFragment : Fragment(), CreateSubtaskDialogFragment.OnSubtaskCreatedListener {
    private lateinit var subtaskRv: RecyclerView
    private lateinit var subtaskAdapter: SubtaskRecyclerViewAdapter
    private lateinit var taskDao: TaskDao
    val args: EditTaskFragmentArgs by navArgs()

    private var originalSubtasks: List<Subtask> = emptyList()
    private var currentSubtasks: MutableList<Subtask> = mutableListOf()
    private var deletedSubtasks: MutableList<Subtask> = mutableListOf()
    private var newSubtasks: MutableList<Subtask> = mutableListOf()
    private var modifiedSubtasks: MutableList<Subtask> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initToolbar()

        val db = AppDatabase.getDatabase(requireContext())
        taskDao = db.taskDao()

        var editTask = Task(
            id = 0,
            title = "",
            description = "",
            dateEpochDay = 0L,
            timeNanoOfDay = 0L,
            categoryId = 0,
            isStarred = false
        )

        var selectedDateInMillis: Long? = null
        var selectedHour: Int? = null
        var selectedMinute: Int? = null

        subtaskAdapter = SubtaskRecyclerViewAdapter(
            currentSubtasks,
            onSubtaskDeleted = { deletedSubtask ->
                if (deletedSubtask.id > 0) {
                    deletedSubtasks.add(deletedSubtask)
                } else {
                    newSubtasks.removeAll { it.title == deletedSubtask.title }
                }
                updateButtonMargins()
            },
            onSubtaskEdit = { subtask ->
                val editDialog = EditSubtaskDialogFragment.newInstance(subtask.id, subtask.title)
                editDialog.setOnSubtaskEditedListener(object : EditSubtaskDialogFragment.OnSubtaskEditedListener {
                    override fun onSubtaskEdited(subtaskId: Int, newTitle: String) {
                        val index = currentSubtasks.indexOfFirst { it.id == subtaskId }
                        if (index != -1) {
                            val updatedSubtask = currentSubtasks[index].copy(title = newTitle)
                            currentSubtasks[index] = updatedSubtask
                            subtaskAdapter.notifyItemChanged(index)

                            if (subtaskId > 0) {
                                modifiedSubtasks.removeAll { it.id == subtaskId }
                                modifiedSubtasks.add(updatedSubtask)
                            } else {
                                val newIndex = newSubtasks.indexOfFirst { it.title == subtask.title }
                                if (newIndex != -1) {
                                    newSubtasks[newIndex] = updatedSubtask
                                }
                            }
                        }
                    }
                })
                editDialog.show(parentFragmentManager, "EditSubtaskDialog")
            }
        )
        subtaskRv = view.findViewById(R.id.rv_subtasks)
        subtaskRv.adapter = subtaskAdapter
        subtaskRv.layoutManager = LinearLayoutManager(requireContext())

        taskDao.getSubtasksForTask(args.taskId).observe(viewLifecycleOwner) { subtasks ->
            originalSubtasks = subtasks
            val oldSize = currentSubtasks.size
            currentSubtasks.clear()
            subtaskAdapter.notifyItemRangeRemoved(0, oldSize)
            currentSubtasks.addAll(subtasks)
            subtaskAdapter.notifyItemRangeInserted(0, subtasks.size)
            updateButtonMargins()
        }

        taskDao.getTaskById(args.taskId).observe(viewLifecycleOwner) { task ->
            view.findViewById<TextInputLayout>(R.id.title_input).editText?.setText(task.title)
            view.findViewById<TextInputLayout>(R.id.description_input).editText?.setText(task.description)

            val dateButton = view.findViewById<Button>(R.id.btn_set_date)
            val selectedDate = Instant.ofEpochMilli(task.dateEpochDay * 24 * 60 * 60 * 1000)
                .atZone(ZoneId.of("UTC"))
                .toLocalDate()
            selectedDateInMillis = task.dateEpochDay * 24 * 60 * 60 * 1000L
            dateButton.text = selectedDate.format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
            dateButton.setTextColor(resources.getColor(R.color.dark_gray, null))

            val timeButton = view.findViewById<Button>(R.id.btn_set_time)

            val taskTime = task.timeNanoOfDay
            if (taskTime != null) {
                val selectedTime = LocalTime.ofNanoOfDay(taskTime)
                selectedHour = selectedTime.hour
                selectedMinute = selectedTime.minute
                timeButton.text = selectedTime.format(DateTimeFormatter.ofPattern("h:mm a"))
            }

            editTask = task.copy()
        }

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
            val description = view.findViewById<TextInputLayout>(R.id.description_input).editText?.text.toString()

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

            val updatedTask = editTask.copy(
                title = title,
                description = description,
                dateEpochDay = epochDay,
                timeNanoOfDay = timeNanoOfDay
            )

            Thread {
                taskDao.updateTasks(updatedTask)

                deletedSubtasks.forEach { subtask ->
                    if (subtask.id > 0) {
                        taskDao.deleteSubtask(subtask)
                    }
                }

                newSubtasks.forEach { subtask ->
                    taskDao.insertSubtask(subtask)
                }

                modifiedSubtasks.forEach { subtask ->
                    taskDao.updateSubtask(subtask)
                }

                activity?.runOnUiThread {
                    Toast.makeText(
                        requireContext(),
                        "Task updated successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    parentFragmentManager.popBackStack()
                }
            }.start()
        }
    }

    override fun onSubtaskCreated(title: String) {
        val newSubtask = Subtask(
            id = 0, // auto-generated by Room
            taskId = args.taskId,
            title = title
        )

        newSubtasks.add(newSubtask)
        currentSubtasks.add(newSubtask)
        subtaskAdapter.notifyItemInserted(currentSubtasks.size - 1)
        updateButtonMargins()
    }

    private fun updateButtonMargins() {
        val addSubtaskButton = view?.findViewById<Button>(R.id.btn_add_subtask)
        val submitButton = view?.findViewById<Button>(R.id.btn_submit)

        if (addSubtaskButton != null && submitButton != null) {
            val isRecyclerViewVisible = subtaskAdapter.itemCount > 0
            val marginTopDp = if (isRecyclerViewVisible) 12 else 24
            val marginTopPx = (marginTopDp * resources.displayMetrics.density).toInt()
            val marginBottomPx = (24 * resources.displayMetrics.density).toInt()

            val addSubtaskParams = addSubtaskButton.layoutParams as ConstraintLayout.LayoutParams
            addSubtaskParams.topMargin = marginTopPx
            addSubtaskParams.bottomMargin = marginBottomPx
            addSubtaskButton.layoutParams = addSubtaskParams

            val submitParams = submitButton.layoutParams as ConstraintLayout.LayoutParams
            submitParams.topMargin = marginTopPx
            submitParams.bottomMargin = marginBottomPx
            submitButton.layoutParams = submitParams
        }
    }

    private fun initToolbar() {
        val toolbar: MaterialToolbar = view?.findViewById(R.id.header_toolbar)!!
        toolbar.title = getString(R.string.edit_task)
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
                resources.getColor(android.R.color.holo_red_dark, null)
            )
            return false
        }
        return true
    }
}