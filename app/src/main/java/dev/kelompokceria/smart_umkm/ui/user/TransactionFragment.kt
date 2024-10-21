package dev.kelompokceria.smart_umkm.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.controller.TransactionProductAdapter
import dev.kelompokceria.smart_umkm.databinding.FragmentTransactionBinding
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel

class TransactionFragment : Fragment() {

    private lateinit var binding: FragmentTransactionBinding
    private lateinit var productViewModel: ProductViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTransactionBinding.inflate(inflater, container, false)



        return binding.root
    }

    }

