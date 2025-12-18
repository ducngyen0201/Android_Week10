package com.example.myapplication

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.myapplication.databinding.FragmentItemListBinding

/**
 * Main fragment displaying list of items in the database.
 */
class ItemListFragment : Fragment() {
    
    private var _binding: FragmentItemListBinding? = null
    private val binding get() = _binding!!
    
    private val viewModel: InventoryViewModel by activityViewModels {
        InventoryViewModelFactory(
            (activity?.application as InventoryApplication).database.itemDao()
        )
    }
    
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentItemListBinding.inflate(inflater, container, false)
        return binding.root
    }
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        val adapter = ItemListAdapter { item ->
            val action = ItemListFragmentDirections.actionItemListFragmentToItemDetailFragment(item.id)
            findNavController().navigate(action)
        }
        
        binding.recyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.adapter = adapter
        
        // Attach an observer on the allItems list to update the UI automatically when the data changes.
        viewModel.allItems.observe(viewLifecycleOwner) { items ->
            items?.let {
                adapter.submitList(it)
            }
        }
        
        binding.floatingActionButton.setOnClickListener {
            val action = ItemListFragmentDirections.actionItemListFragmentToAddItemFragment()
            findNavController().navigate(action)
        }
    }
    
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
