package dev.kelompokceria.smart_umkm.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.FragmentFaqBinding

class FaqFragment : Fragment() {

    private lateinit var binding: FragmentFaqBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentFaqBinding.inflate(layoutInflater)

        binding.backButton.setOnClickListener {
            // Navigate back to the previous screen
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        binding.question1.setOnClickListener{
            if (binding.answer1.visibility == View.GONE){
                binding.answer1.visibility = View.VISIBLE
            } else {
                binding.answer1.visibility = View.GONE
            }
        }

        binding.question2.setOnClickListener{
            if (binding.answer2.visibility == View.GONE){
                binding.answer2.visibility = View.VISIBLE
            } else {
                binding.answer2.visibility = View.GONE
            }
        }

        binding.question3.setOnClickListener{
            if (binding.answer3.visibility == View.GONE){
                binding.answer3.visibility = View.VISIBLE
            } else {
                binding.answer3.visibility = View.GONE
            }
        }

        binding.question4.setOnClickListener{
            if (binding.answer4.visibility == View.GONE){
                binding.answer4.visibility = View.VISIBLE
            } else {
                binding.answer4.visibility = View.GONE
            }
        }

        return binding.root
    }
}