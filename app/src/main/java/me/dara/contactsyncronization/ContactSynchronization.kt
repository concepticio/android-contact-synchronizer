package me.dara.contactsyncronization

import android.content.Context
import android.os.SystemClock
import android.provider.ContactsContract
import android.util.Log
import io.reactivex.subjects.PublishSubject

/**
 * @author sardor
 */
class ContactSynchronization(val context: Context) {


  val contactsSubject = PublishSubject.create<List<Contact>>()!!


  fun start() {
    if (contactsSubject.hasObservers()) {

      val startTime = SystemClock.elapsedRealtime()

      val contactsMap = getContactsWithLookupKey()

      val contactList = getContactList(contactsMap)

      val endTime = SystemClock.elapsedRealtime()

      val difference = endTime - startTime

      contactsSubject.onNext(contactList)

    } else {
      throw Exception("Does not have any observables please subscribe at least one observer")
    }
  }

  fun getContactsWithLookupKey(): HashMap<String, Contact> {

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
    return contactsMap
  }

  fun getContactList(contactsMap: HashMap<String, Contact>): List<Contact> {
    val contactList = ArrayList<Contact>()
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
        contactList.add(newContact)
      }
    }

    userInfoCursor.close()

    return contactList

  }

}