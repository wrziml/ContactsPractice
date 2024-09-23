package com.tollcafe.contactspractive

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class ContactsAdapter(private val contactsList:ArrayList<Contact>):RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    inner class ContactViewHolder(itemView: View):RecyclerView.ViewHolder(itemView){
        val contactName: TextView =itemView.findViewById(R.id.tv_contact_name)
        val contactPhoneNumber: TextView =itemView.findViewById(R.id.tv_contact_phone_number)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ContactViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.contact_item,parent,false))
    }

    override fun getItemCount()=contactsList.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val contact=contactsList[position]
        if(holder is ContactViewHolder){
            holder.contactName.text=contact.name
            holder.contactPhoneNumber.text=contact.phoneNumber
        }
    }
}