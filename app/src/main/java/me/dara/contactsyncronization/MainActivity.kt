package me.dara.contactsyncronization

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.activity_main.*
import me.dara.mylib.AvatarDrawable

class MainActivity : AppCompatActivity() {

  companion object {
    const val READ_CONTACTS_CODE = 0
  }


  private val contactSynchronization by lazy {
    ContactSynchronization(applicationContext)
  }
  private val contactAdapter: ContactsAdapter by lazy {
    ContactsAdapter()
  }
  private var disposable: Disposable? = null

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    recyclerView.layoutManager = LinearLayoutManager(applicationContext)
    recyclerView.adapter = contactAdapter
    checkPermission()
  }

  private fun checkPermission() {
    if (isGranted()) {
      granted()
    } else {
      ActivityCompat.requestPermissions(this,
          arrayOf(android.Manifest.permission.READ_CONTACTS), READ_CONTACTS_CODE)
    }
  }

  private fun isGranted(): Boolean {
    return ContextCompat
        .checkSelfPermission(this, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED
  }

  private fun granted() {
    disposable = contactSynchronization.contactsSubject.subscribe {
      for (contact in it) {
        val avatarDrawable = AvatarDrawable.Builder()
            .firstName(contact.name)
            .lastName(contact.lastName)
            .height(60)
            .width(60)
            .startColor(ContextCompat.getColor(applicationContext,R.color.colorAccent))
            .endColor(ContextCompat.getColor(applicationContext,R.color.colorAccent))
            .create()

        for (number in contact.phoneNumbers) {
          val contactView = ContactView(avatarDrawable, contact.name + contact.lastName, number)
          contactAdapter.addItem(contactView)
        }

      }
    }
    contactSynchronization.start()
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions:
  Array<out String>, grantResults: IntArray) {
    when (requestCode) {
      READ_CONTACTS_CODE -> {
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
          granted()
        }
      }
    }
  }

  override fun onDestroy() {
    super.onDestroy()
    disposable?.let {
      if (!it.isDisposed)
        it.dispose()
    }
  }


}
