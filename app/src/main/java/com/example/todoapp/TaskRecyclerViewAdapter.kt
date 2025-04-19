package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskRecyclerViewAdapter(private var taskList: List<Task>): RecyclerView.Adapter<TaskRecyclerViewAdapter.TaskViewHolder>() {
    inner class TaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        var icon: ImageView = itemView.findViewById(R.id.task_item_star_icon)
        val checkBox: CheckBox = itemView.findViewById(R.id.task_check_box)
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
        holder.checkBox.isChecked = task.isSelected

        if (task.isStarred) {
            holder.icon.setImageResource(R.drawable.ic_star_filled)
        } else {
            holder.icon.setImageResource(R.drawable.ic_star_empty)
        }
    }

    fun updateData(newItems: List<Task>) {
        taskList = newItems
        notifyDataSetChanged()
    }
}