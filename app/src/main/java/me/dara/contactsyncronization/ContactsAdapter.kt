package me.dara.contactsyncronization

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView

/**
 * @author sardor
 */
class ContactsAdapter : RecyclerView.Adapter<ContactsAdapter.ContactViewHolder>() {

  val list = mutableListOf<ContactView>()

  fun addItem(contactView: ContactView) {
    list.add(contactView)
    notifyItemInserted(itemCount)
  }

  override fun onCreateViewHolder(p0: ViewGroup, p1: Int): ContactsAdapter.ContactViewHolder {
    val view = LayoutInflater.from(p0.context)
        .inflate(R.layout.list_item_contact, p0, false)
    return ContactViewHolder(view)

  }

  override fun getItemCount(): Int = list.size

  override fun onBindViewHolder(p0: ContactsAdapter.ContactViewHolder, p1: Int) {
    p0.bind(list[p1])
  }

  class ContactViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

    val imageContact: ImageView = itemView.findViewById(R.id.image_contact)
    val textContact: TextView = itemView.findViewById(R.id.text_name_contact)
    val textStatus: TextView = itemView.findViewById(R.id.text_created_time_contact)

    fun bind(contactView: ContactView) {
      imageContact.setImageDrawable(contactView.avatarDrawable)
      textContact.text = contactView.name
      textStatus.text = contactView.status
    }
  }

}