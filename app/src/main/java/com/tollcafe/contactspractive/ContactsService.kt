package com.tollcafe.contactspractive

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.provider.ContactsContract
import android.util.Log

class ContactsService : Service() {

    private val TAG="ContactsService"
    override fun onCreate() {
        super.onCreate()
        Log.d(TAG,"onCreate")
    }
    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG,"onStartCommand")
        val contacts =getContactsList()

        val broadCastIntent = Intent("CONTACTS_UPDATED")
        broadCastIntent.putParcelableArrayListExtra("contacts",contacts)
        intent.setPackage(packageName)
        sendBroadcast(broadCastIntent)
        return START_STICKY
    }

    private fun getContactsList(): ArrayList<Contact> {
        val contacts=ArrayList<Contact>()
        val cursor = contentResolver.query(
            ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
            null,
            null,
            null,
            null
        )
        cursor?.let {
            while (it.moveToNext()){
                val name=it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME),)
                val phoneNumber=it.getString(it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER))
                contacts.add(Contact(name,phoneNumber))
            }
            it.close()
        }
        return contacts
    }
}