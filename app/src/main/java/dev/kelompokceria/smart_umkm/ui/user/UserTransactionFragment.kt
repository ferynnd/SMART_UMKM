package dev.kelompokceria.smart_umkm.ui.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.FragmentUserTransactionBinding
import dev.kelompokceria.smart_umkm.model.Product
import dev.kelompokceria.smart_umkm.model.Transaksi
import dev.kelompokceria.smart_umkm.viewmodel.ProductViewModel
import dev.kelompokceria.smart_umkm.viewmodel.TransactionViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.util.Date

    private lateinit var binding: FragmentUserTransactionBinding

    private lateinit var transactionViewModel: TransactionViewModel
    private lateinit var productViewModel: ProductViewModel

    private lateinit var username : String

    private lateinit var products: List<Product>
    private var selectedProduct: Product? = null

class UserTransactionFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
         transactionViewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
         productViewModel = ViewModelProvider(this).get(ProductViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentUserTransactionBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        username = arguments?.getString("KEY_USERNAME").toString()
         setupProductSpinner()
        setupCustomerPaymentListener()
        setupAddTransactionButton()
        return binding.root
    }

      private fun setupProductSpinner() {
        productViewModel.allProducts.observe(viewLifecycleOwner) { productList ->
            products = productList
            val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, products.map { it.name })
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.productSpinner.adapter = adapter

            binding.productSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                    selectedProduct = products[position]
                    updateProductInfo()
                }

                override fun onNothingSelected(parent: AdapterView<*>) {
                    selectedProduct = null
                    updateProductInfo()
                }
            }
        }
    }

    private fun setupCustomerPaymentListener() {
        binding.customerPaymentEditText.doAfterTextChanged {
            updateChangeAmount()
        }
        binding.Jumlah.doAfterTextChanged {
            updateChangeAmount()
        }
    }

    private fun updateProductInfo() {
        val product = selectedProduct
        if (product != null) {
            binding.productPriceTextView.text = formatCurrency(product.price)
        } else {
            binding.productPriceTextView.text = ""
            binding.cashbackTextView.text = ""
            binding.discountTextView.text = ""
        }
        updateChangeAmount()
    }

    private fun updateChangeAmount() {
        val productPrice = selectedProduct?.price ?: 0.0
        val customerPayment = binding.customerPaymentEditText.text.toString().toDoubleOrNull() ?: 0.0
        val Quantity = binding.Jumlah.text.toString().toDoubleOrNull() ?: 0.0
        val total = productPrice * Quantity
        val change = customerPayment - total
        binding.changeTextView.text = formatCurrency(total)
        binding.cashbackTextView.text = formatCurrency(change)

    }

    private fun setupAddTransactionButton() {
        binding.addTransactionButton.setOnClickListener {
            addTransaction(username)
        }
    }

    private fun addTransaction(username: String) {
        val product = selectedProduct
        val customerPayment = binding.customerPaymentEditText.text.toString().toDoubleOrNull()

        if (product == null || customerPayment == null) {
            Toast.makeText(context, "Please select a product and enter customer payment", Toast.LENGTH_SHORT).show()
            return
        }

        val cashback = binding.cashbackTextView.text.toString()
        val totall = binding.changeTextView.text.toString()
        val nama = binding.productSpinner.selectedItem.toString()

        val transaction = Transaksi(
            transactionTime = Date(),
            transactionUser = username,
            transactionProduct = nama,
            transactionTotal =  totall,
            transactionCashback = cashback
        )

        lifecycleScope.launch {
            try {
                transactionViewModel.insert(transaction)
                Toast.makeText(context, "Transaction added successfully", Toast.LENGTH_SHORT).show()
                 parentFragmentManager.beginTransaction()
                .replace(R.id.nav_host_fragment_user, DashboardFragment())
                .addToBackStack(null)
                .commit()
            } catch (e:Exception){
                 Toast.makeText(context, "Failed", Toast.LENGTH_SHORT).show()
            }
        }


    }

    private fun formatCurrency(amount: Double): String {
        return NumberFormat.getCurrencyInstance().format(amount)
    }


}