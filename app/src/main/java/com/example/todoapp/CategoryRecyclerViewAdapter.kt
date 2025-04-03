package com.example.todoapp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.recyclerview.widget.RecyclerView

class CategoryRecyclerViewAdapter(private val categoryList: List<Category>): RecyclerView.Adapter<CategoryRecyclerViewAdapter.CategoryViewHolder>() {
    inner class CategoryViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val button: Button = itemView.findViewById(R.id.category_button)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): CategoryViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.category_item, parent, false)
        return CategoryViewHolder(view)
    }

    override fun getItemCount(): Int {
        TODO("Not yet implemented")
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categoryList[position]
        val context = holder.itemView.context

        if (category.isStarredCategory) {
            holder.button.text = "" // Clear text
            holder.button.setCompoundDrawablesWithIntrinsicBounds(
                R.drawable.ic_star_filled, 0, 0, 0
            )
            holder.button.contentDescription = context.getString(R.string.ic_star_desc)
        } else {
            holder.button.text = category.name
            holder.button.setCompoundDrawablesWithIntrinsicBounds(
                0, 0, 0, 0
            )
            holder.button.contentDescription = ""
        }
    }
}