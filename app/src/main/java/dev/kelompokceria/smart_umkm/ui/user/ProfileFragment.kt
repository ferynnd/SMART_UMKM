package dev.kelompokceria.smart_umkm.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.FragmentProfileBinding


class ProfileFragment : Fragment() {

    private  var _binding: FragmentProfileBinding? = null

    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container,false)
        return binding.root
//        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

     override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}