package dev.kelompokceria.smart_umkm.controller

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.databinding.CardUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.model.UserRole
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AdminUserAdapter(var userList: List<User>, private var userViewModel: UserViewModel, val userEditNavigate : (userName: String, userEmail: String, userPhone :String, userUsername : String, userPassword : String, userRole: String) -> Unit) : RecyclerView.Adapter<AdminUserAdapter.userViewHolder>() {



    class userViewHolder( var view: CardUserBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return userViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {

//        var database: AppDatabase
        val user = userList.get(position)
        val view = holder.view

        view.textViewName.text = user.name
        view.textViewEmail.text = user.email
        view.textViewPhone.text = user.phone
        view.textViewUsername.text = user.username
        view.textViewPassword.text = user.password
        view.textViewRole.text = user.role.toString()

        view.buttonEdit.setOnClickListener {
            userEditNavigate(user.name,user.email, user.phone, user.username,user.password,user.role.toString())
        }

        view.Expand.setOnClickListener {
                if (view.groupProfile.visibility == View.GONE) {
                    view.groupProfile.visibility = View.VISIBLE  // Tampilkan
                } else {
                    view.groupProfile.visibility = View.GONE  // Sembunyikan
                }
        }

        view.buttonDelete.setOnClickListener {
            val userDelete = user.username
            CoroutineScope(Dispatchers.IO).launch {
                userViewModel.userDelete(userDelete)
                withContext(Dispatchers.Main) {
                    notifyItemRemoved(position)
                    notifyItemRangeChanged(position, itemCount)
                }
            }
        }
//
//       view.buttonDelete.setOnClickListener {
//            val userDelete = user.username
//            AlertDialog.Builder(context)
//                .setTitle("DELETE WARNING")
//                .setMessage("Are you sure you want to delete this user ${userDelete.toString()}?")
//                .setPositiveButton("DELETE") { dialog, _ ->
//                    CoroutineScope(Dispatchers.IO).launch {
//                        try {
//                            database.userDao().delUser(userDelete)
//                            withContext(Dispatchers.Main) {
//                                notifyItemRemoved(position)
//                                notifyItemRangeChanged(position, itemCount)
//                            }
//                        } catch (e: Exception) {
//                            withContext(Dispatchers.Main) {
//                                Toast.makeText(context, "Failed to delete: ${e.message}", Toast.LENGTH_SHORT).show()
//                            }
//                        }
//                    }
//                    dialog.dismiss()
//                }
//                .setNegativeButton("CANCEL") { dialog, _ ->
//                    dialog.dismiss()
//                }
//                .create()
//                .show()
//        }


    }



}