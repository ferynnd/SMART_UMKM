package dev.kelompokceria.smart_umkm.ui.admin

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.FragmentCreateProductBinding

class CreateProductFragment : Fragment() {

    private lateinit var binding: FragmentCreateProductBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCreateProductBinding.inflate(inflater,container,false)
        return binding.root
//        return inflater.inflate(R.layout.fragment_create_product, container, false)
    }

}