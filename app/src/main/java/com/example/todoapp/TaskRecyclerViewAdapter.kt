package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.view.isEmpty

class TaskRecyclerViewAdapter(
    private var taskList: List<Task>,
    private val taskDao: TaskDao,
    private val onTaskClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageView = itemView.findViewById(R.id.task_item_star_icon)
        val radioButton: RadioButton = itemView.findViewById(R.id.task_radio_button)
        val text: TextView = itemView.findViewById(R.id.task_title)
        val subtasksContainer: LinearLayout = itemView.findViewById(R.id.subtasks_container)
        var currentTaskId: Int? = null
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]

        holder.text.text = task.title
        holder.radioButton.isChecked = false

        if (task.isStarred) {
            holder.icon.setImageResource(R.drawable.ic_star_filled)
        } else {
            holder.icon.setImageResource(R.drawable.ic_star_empty)
        }

        if (holder.currentTaskId != task.id) {
            holder.subtasksContainer.removeAllViews()
            holder.subtasksContainer.visibility = View.GONE

            CoroutineScope(Dispatchers.IO).launch {
                val subtasks = taskDao.getSubtasksForTaskSync(task.id)

                withContext(Dispatchers.Main) {
                    if (subtasks.isNotEmpty()) {
                        holder.subtasksContainer.visibility = View.VISIBLE

                        for (subtask in subtasks) {
                            val subtaskView = LayoutInflater.from(holder.itemView.context)
                                .inflate(R.layout.subtask_item, holder.subtasksContainer, false)

                            val subtaskTitle = subtaskView.findViewById<TextView>(R.id.subtask_title)
                            val subtaskRadioButton = subtaskView.findViewById<RadioButton>(R.id.subtask_radio_button)

                            subtaskTitle.text = subtask.title
                            subtaskRadioButton.isChecked = false

                            subtaskRadioButton.setOnClickListener {
                                val fadeOut = AlphaAnimation(1.0f, 0.0f)
                                fadeOut.duration = 300
                                fadeOut.fillAfter = true

                                fadeOut.setAnimationListener(object : Animation.AnimationListener {
                                    override fun onAnimationStart(animation: Animation?) {}

                                    override fun onAnimationEnd(animation: Animation?) {
                                        holder.subtasksContainer.removeView(subtaskView)

                                        if (holder.subtasksContainer.isEmpty()) {
                                            holder.subtasksContainer.visibility = View.GONE
                                        }

                                        CoroutineScope(Dispatchers.IO).launch {
                                            taskDao.deleteSubtask(subtask)
                                        }
                                    }

                                    override fun onAnimationRepeat(animation: Animation?) {}
                                })

                                subtaskView.startAnimation(fadeOut)
                            }

                            holder.subtasksContainer.addView(subtaskView)
                        }
                    }
                }
            }
        }

        holder.currentTaskId = task.id

        holder.radioButton.setOnClickListener {
            val fadeOut = AlphaAnimation(1.0f, 0.0f)
            fadeOut.duration = 300
            fadeOut.fillAfter = true

            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    val currentPosition = holder.adapterPosition
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        val updatedList = taskList.toMutableList()
                        updatedList.removeAt(currentPosition)
                        taskList = updatedList

                        notifyItemRemoved(currentPosition)
                        notifyItemRangeChanged(currentPosition, taskList.size)
                    }

                    CoroutineScope(Dispatchers.IO).launch {
                        taskDao.deleteTasks(task)
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

            holder.itemView.startAnimation(fadeOut)
        }

        holder.itemView.setOnClickListener {
            onTaskClick(task)
        }

        holder.icon.setOnClickListener {
            val updatedTask = task.copy(isStarred = !task.isStarred)

            CoroutineScope(Dispatchers.IO).launch {
                taskDao.updateTasks(updatedTask)
            }
        }
    }

    fun updateData(tasks: List<Task>) {
        taskList = tasks
        notifyDataSetChanged()
    }
}