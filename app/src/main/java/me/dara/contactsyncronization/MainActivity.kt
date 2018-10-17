package me.dara.contactsyncronization

import android.Manifest
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat

class MainActivity : AppCompatActivity() {

  companion object {
    const val READ_CONTACTS_CODE = 0
  }

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    setContentView(R.layout.activity_main)
    checkPermission()
  }

  fun checkPermission (){
    if (isGranted()){
      granted()
    }else{
      ActivityCompat.requestPermissions(this,
              arrayOf(android.Manifest.permission.READ_CONTACTS), READ_CONTACTS_CODE)
    }
  }

  fun isGranted() : Boolean{
    return ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)== PackageManager.PERMISSION_GRANTED
  }
  fun granted(){
    val contactSynchronization = ContactSynchronization(applicationContext)
    contactSynchronization.start()
  }

  override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
    when(requestCode){
      READ_CONTACTS_CODE->{
        if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
          granted()
        }
      }
    }
  }



}
