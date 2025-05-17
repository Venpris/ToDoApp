package com.example.todoapp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.textfield.TextInputLayout
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class CreateCategoryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_category, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val db = AppDatabase.getDatabase(requireContext())
        val categoryDao = db.categoryDao()

        initToolbar()
        view.findViewById<View>(R.id.btn_done).setOnClickListener {
            val name = view.findViewById<TextInputLayout>(R.id.title_input).editText?.text.toString()
            val category = Category(id = 0, name = name)

            if (!validateTitle(name, view.findViewById(R.id.title_input))) return@setOnClickListener

            lifecycleScope.launch(Dispatchers.IO) {
                categoryDao.insertAll(category)
                val categoryId = categoryDao.getLastInsertedCategory()
                lifecycleScope.launch(Dispatchers.Main) {
                    Toast.makeText(
                        requireContext(),
                        "Category created successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                    parentFragmentManager.setFragmentResult(
                        "newCategoryRequest",
                        Bundle().apply {
                            putInt("categoryId", categoryId)
                        }
                    )
                    findNavController().popBackStack()
                }
            }
        }
    }

    private fun initToolbar() {
        val toolbar: MaterialToolbar = view?.findViewById(R.id.header_toolbar)!!
        toolbar.title = getString(R.string.new_category)
        toolbar.setNavigationOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun validateTitle(title: String, titleInputLayout: TextInputLayout): Boolean {
        if (title.isEmpty()) {
            titleInputLayout.error = "Title is required"
            Toast.makeText(requireContext(), "Title is required", Toast.LENGTH_SHORT).show()
            return false
        }
        titleInputLayout.error = null
        return true
    }
}