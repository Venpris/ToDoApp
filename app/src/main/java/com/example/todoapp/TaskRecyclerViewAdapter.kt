package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class TaskRecyclerViewAdapter(
    private var taskList: List<Task>,
    private val taskDao: TaskDao
) : RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder>() {

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var icon: ImageView = itemView.findViewById(R.id.task_item_star_icon)
        val radioButton: RadioButton = itemView.findViewById(R.id.task_radio_button)
        val text: TextView = itemView.findViewById(R.id.task_title)
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

        holder.icon.setOnClickListener {
            val updatedTask = task.copy(isStarred = !task.isStarred)

            CoroutineScope(Dispatchers.IO).launch {
                taskDao.updateTasks(updatedTask)
            }

            if (!task.isStarred) {
                holder.icon.setImageResource(R.drawable.ic_star_filled)
            } else {
                holder.icon.setImageResource(R.drawable.ic_star_empty)
            }
        }
    }

    fun updateData(newItems: List<Task>) {
        taskList = newItems
        notifyDataSetChanged()
    }
}