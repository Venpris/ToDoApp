package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView

class SubtaskRecyclerViewAdapter(private var subtaskList: List<Subtask>) :
    RecyclerView.Adapter<SubtaskRecyclerViewAdapter.SubtaskViewHolder>() {
    inner class SubtaskViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.findViewById(R.id.task_item_star_icon)
        val radioButton: RadioButton = itemView.findViewById(R.id.task_radio_button)
        val text: TextView = itemView.findViewById(R.id.task_title)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SubtaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_item, parent, false)
        return SubtaskViewHolder(view)
    }

    override fun getItemCount(): Int {
        return subtaskList.size
    }

    override fun onBindViewHolder(holder: SubtaskViewHolder, position: Int) {
        val subtask = subtaskList[position]

        holder.icon.isVisible = false
        holder.text.text = subtask.title
    }

    fun updateData(subtasks: List<Subtask>) {
        subtaskList = subtasks
        notifyDataSetChanged()
    }
}