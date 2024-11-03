package dev.kelompokceria.smart_umkm.controller

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.databinding.CardUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminUserAdapter(
    private val deleteUser: (User) -> Unit,
    private val userEditNavigate: (userImage: String, userName: String, userEmail: String, userPhone: String, userUsername: String, userPassword: String, userRole: String, userID : Int) -> Unit
) : ListAdapter<User, AdminUserAdapter.UserViewHolder>(UserDiffCallback()) {

    class UserViewHolder(var binding: CardUserBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return UserViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        val view = holder.binding

        // Load image using Glide
        user.image?.let {
            Glide.with(view.imageView.context)
                .load(it)
                .placeholder(R.drawable.picture) // Placeholder if image is unavailable
                .into(view.imageView)
        } ?: run {
            view.imageView.setImageResource(R.drawable.picture) // Default image
        }

        view.textViewName.text = user.name
        view.textViewEmail.text = user.email
        view.textViewPhone.text = user.phone
        view.textViewUsername.text = user.username
        view.textViewPassword.text = user.password
        view.textViewRole.text = user.role.toString()

        // Navigate to Edit User
        view.buttonEdit.setOnClickListener {
            userEditNavigate(user.image, user.name, user.email, user.phone, user.username, user.password, user.role.toString(), user.id)
        }

        // Toggle user details view
        view.Expand.setOnClickListener {
            view.groupProfile.visibility = if (view.groupProfile.visibility == View.GONE) {
                View.VISIBLE // Show
            } else {
                View.GONE // Hide
            }
        }

        // Delete user
        view.buttonDelete.setOnClickListener {
           deleteUser(user)
        }
    }

    fun filter(query: String) {
        val filteredList = if (query.isEmpty()) {
            currentList
        } else {
            currentList.filter { user ->
                user.name?.contains(query, ignoreCase = true) == true
            }
        }
        submitList(filteredList) // Submit filtered list
    }

    class UserDiffCallback : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.username == newItem.username // Assuming username is unique
        }

        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
}
