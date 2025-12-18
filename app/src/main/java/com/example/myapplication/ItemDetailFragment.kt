package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.myapplication.data.Item
import com.example.myapplication.databinding.FragmentItemDetailBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import java.text.NumberFormat

/**
 * Fragment to display details of an item and allow editing.
 */
class ItemDetailFragment : Fragment() {
    
    private val navigationArgs: ItemDetailFragmentArgs by navArgs()
    
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao()
        )
    }
    
    private var _binding: FragmentItemDetailBinding? = null
    private val binding get() = _binding!!
    
    lateinit var item: Item
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemDetailBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val id = navigationArgs.itemId
        
        viewModel.retrieveItem(id).observe(viewLifecycleOwner) { selectedItem ->
            item = selectedItem
            bind(item)
        }
    }
    
    /**
     * Binds views with the passed in item data.
     */
    private fun bind(item: Item) {
        binding.apply {
            itemName.text = item.itemName
            itemPrice.text = NumberFormat.getCurrencyInstance().format(item.itemPrice)
            itemQuantity.text = item.quantityInStock.toString()
            
            sellButton.isEnabled = viewModel.isStockAvailable(item)
            sellButton.setOnClickListener { viewModel.sellItem(item) }
            
            deleteButton.setOnClickListener { showConfirmationDialog() }
            
            editButton.setOnClickListener { editItem() }
        }
    }
    
    /**
     * Navigate to the Edit item screen.
     */
    private fun editItem() {
        val action = ItemDetailFragmentDirections.actionItemDetailFragmentToAddItemFragment(
            getString(R.string.edit_product),
            item.id
        )
        findNavController().navigate(action)
    }
    
    /**
     * Displays an alert dialog to get the user's confirmation before deleting the item.
     */
    private fun showConfirmationDialog() {
        MaterialAlertDialogBuilder(requireContext())
            .setTitle(getString(android.R.string.dialog_alert_title))
            .setMessage(getString(R.string.delete_question))
            .setCancelable(false)
            .setNegativeButton(getString(R.string.no)) { _, _ -> }
            .setPositiveButton(getString(R.string.yes)) { _, _ ->
                deleteItem()
            }
            .show()
    }
    
    /**
     * Deletes the current item and navigates to the list fragment.
     */
    private fun deleteItem() {
        viewModel.deleteItem(item)
        findNavController().navigateUp()
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
