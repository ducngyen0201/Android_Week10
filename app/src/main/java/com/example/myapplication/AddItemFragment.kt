package com.example.myapplication

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.data.Item
import com.example.myapplication.databinding.FragmentAddItemBinding

/**
 * Fragment to add or edit an item in the database.
 */
class AddItemFragment : Fragment() {
    
    private val navigationArgs: AddItemFragmentArgs by navArgs()
    
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao()
        )
    }
    
    private var _binding: FragmentAddItemBinding? = null
    private val binding get() = _binding!!
    
    lateinit var item: Item
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddItemBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val id = navigationArgs.itemId
        if (id > 0) {
            // Editing existing item
            viewModel.retrieveItem(id).observe(viewLifecycleOwner) { selectedItem ->
                item = selectedItem
                bind(item)
            }
        } else {
            // Adding new item
            binding.saveButton.setOnClickListener {
                addNewItem()
            }
        }
        
        binding.cancelButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }
    
    /**
     * Binds views with the passed in item data for editing.
     */
    private fun bind(item: Item) {
        binding.apply {
            itemName.setText(item.itemName, TextView.BufferType.SPANNABLE)
            itemPrice.setText(item.itemPrice.toString(), TextView.BufferType.SPANNABLE)
            itemQuantity.setText(item.quantityInStock.toString(), TextView.BufferType.SPANNABLE)
            
            saveButton.setOnClickListener { updateItem() }
        }
    }
    
    /**
     * Inserts the new Item into database and navigates up to list fragment.
     */
    private fun addNewItem() {
        if (isEntryValid()) {
            viewModel.addNewItem(
                binding.itemName.text.toString(),
                binding.itemPrice.text.toString(),
                binding.itemQuantity.text.toString(),
            )
            findNavController().navigateUp()
        }
    }
    
    /**
     * Updates an existing Item in the database and navigates up to list fragment.
     */
    private fun updateItem() {
        if (isEntryValid()) {
            viewModel.updateItem(
                navigationArgs.itemId,
                binding.itemName.text.toString(),
                binding.itemPrice.text.toString(),
                binding.itemQuantity.text.toString()
            )
            findNavController().navigateUp()
        }
    }
    
    /**
     * Returns true if the EditTexts are not empty
     */
    private fun isEntryValid(): Boolean {
        return viewModel.isEntryValid(
            binding.itemName.text.toString(),
            binding.itemPrice.text.toString(),
            binding.itemQuantity.text.toString()
        )
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        // Hide keyboard.
        val inputMethodManager = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as
                InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(requireActivity().currentFocus?.windowToken, 0)
        _binding = null
    }
}
