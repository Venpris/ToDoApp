package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.ImageView
import android.widget.RadioButton
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SubtaskRecyclerViewAdapter(
    private var subtaskList: MutableList<Subtask>,
    private val taskDao: TaskDao,
    private val onSubtaskDeleted: ((Subtask) -> Unit)? = null
) :
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
        holder.radioButton.isChecked = false

        holder.radioButton.setOnClickListener {
            val fadeOut = AlphaAnimation(1.0f, 0.0f)
            fadeOut.duration = 300
            fadeOut.fillAfter = true

            fadeOut.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    val currentPosition = holder.adapterPosition
                    if (currentPosition != RecyclerView.NO_POSITION) {
                        val removedSubtask = subtaskList.removeAt(currentPosition)
                        notifyItemRemoved(currentPosition)
                        notifyItemRangeChanged(currentPosition, subtaskList.size)

                        if (removedSubtask.id > 0) {
                            CoroutineScope(Dispatchers.IO).launch {
                                taskDao.deleteSubtask(removedSubtask)
                            }
                        }

                        onSubtaskDeleted?.invoke(removedSubtask)
                    }
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })

            holder.itemView.startAnimation(fadeOut)
        }
    }

    fun updateData(subtasks: List<Subtask>) {
        subtaskList.clear()
        subtaskList.addAll(subtasks)
        notifyDataSetChanged()
    }

    fun addSubtask(subtask: Subtask) {
        subtaskList.add(subtask)
        notifyItemInserted(subtaskList.size - 1)
    }

    fun getSubtasks(): List<Subtask> = subtaskList.toList()
}