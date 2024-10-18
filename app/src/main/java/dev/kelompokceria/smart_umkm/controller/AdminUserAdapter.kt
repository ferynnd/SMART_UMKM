package dev.kelompokceria.smart_umkm.controller

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Email
import android.provider.ContactsContract.CommonDataKinds.Phone
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import dev.kelompokceria.smart_umkm.R
import dev.kelompokceria.smart_umkm.data.database.AppDatabase
import dev.kelompokceria.smart_umkm.databinding.CardUserBinding
import dev.kelompokceria.smart_umkm.model.User
import dev.kelompokceria.smart_umkm.viewmodel.UserViewModel

class AdminUserAdapter( var userList: List<User>) : RecyclerView.Adapter<AdminUserAdapter.userViewHolder>() {


    class userViewHolder( var view: CardUserBinding) : RecyclerView.ViewHolder(view.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userViewHolder {
        val binding = CardUserBinding.inflate(LayoutInflater.from(parent.context),parent,false)
        return userViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    override fun onBindViewHolder(holder: userViewHolder, position: Int) {

        val user = userList.get(position)
        val view = holder.view

        view.textViewName.text = user.name
        view.textViewEmail.text = user.email
        view.textViewPhone.text = user.phone
        view.textViewUsername.text = user.username
        view.textViewPassword.text = user.password
        view.textViewRole.text = user.role.toString()

        view.buttonEdit.setOnClickListener {
//            Toast.makeText(context,"TEXT",)
        }

        view.Expand.setOnClickListener {
                if (view.groupProfile.visibility == View.GONE) {
                    view.groupProfile.visibility = View.VISIBLE  // Tampilkan
                } else {
                    view.groupProfile.visibility = View.GONE  // Sembunyikan
                }
        }

    }



}