package me.dara.contactsyncronization

import android.content.Context
import android.provider.ContactsContract
import android.util.Log

/**
 * @author sardor
 */
class ContactSynchronization(val context: Context) {


  fun start() {

    val startTime = System.currentTimeMillis()

    val contactsMap = HashMap<String, Contact>()

    val userPhoneCursor = context.contentResolver
        .query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI
            , null, null, null, null)


    while (userPhoneCursor?.moveToNext()!!) {

      // Phone number

      var phoneNumber = userPhoneCursor
          .getString(userPhoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
      if (phoneNumber.isEmpty()) {
        continue
      }
      //val regex = "[^0-9]".toRegex()
      //phoneNumber = regex.find(phoneNumber)?.value

      // Look up key is a unique ID for Contact object in Phone table

      var lookUpKey = userPhoneCursor
          .getString(userPhoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.LOOKUP_KEY))

      var contact = contactsMap[lookUpKey]

      if (contact == null) {
        contact = Contact("", "", mutableListOf())
        contactsMap[lookUpKey] = contact
      }
      contact.phoneNumbers.add(phoneNumber)

    }

    userPhoneCursor.close()

    val whereName = ContactsContract.Data.MIMETYPE + " = ?"
    val whereNameParams = arrayOf(ContactsContract.CommonDataKinds.StructuredName.CONTENT_ITEM_TYPE)

    val userInfoCursor = context.contentResolver.query(ContactsContract.Data.CONTENT_URI,
        null, whereName,
        whereNameParams, null)


    while (userInfoCursor?.moveToNext()!!) {
      val lookupKey = userInfoCursor.getString(userInfoCursor
          .getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.LOOKUP_KEY))
      var name = userInfoCursor.getString(userInfoCursor
          .getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.GIVEN_NAME))
      var lastName = userInfoCursor.getString(userInfoCursor
          .getColumnIndex(ContactsContract.CommonDataKinds.StructuredName.FAMILY_NAME))


      val contact = contactsMap[lookupKey]

      contact?.let {

        if (name.isNullOrEmpty())
          name = ""
        if (lastName.isNullOrEmpty())
          lastName = ""
        val newContact = Contact(name, lastName, mutableListOf())
        newContact.phoneNumbers.addAll(contact.phoneNumbers)
        contactsMap[lookupKey] = newContact

      }
      val endTime = System.currentTimeMillis()
      val difference = endTime - startTime
      Log.i("View", (difference / 1000).toString())

      for ((key, value) in contactsMap) {
        val name = value.name
        val lastName = value.lastName
        var numbers = ""
        for (i in value.phoneNumbers) {
          numbers += i
        }

        Log.i("User", "name:$name, lastName:$lastName, numbers:$numbers")
      }

    }

    userInfoCursor.close()


  }

}