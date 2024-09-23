package com.tollcafe.contactspractive

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.activity.result.registerForActivityResult
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private val TAG = "MainActivity"
    private lateinit var contactAdapter: ContactsAdapter
    private val contactsList = ArrayList<Contact>()

    private val permissionsLauncher =
        registerForActivityResult(RequestPermission()) { isGranted: Boolean ->
            if (isGranted) {
                // 如果权限被授予，启动服务
                startContactService()
            } else {
                // 权限未被授予，可以展示提示信息或处理
                Toast.makeText(this, "需要权限才能读取联系人", Toast.LENGTH_SHORT).show()
            }
        }

    private fun startContactService() {
        val intent = Intent(this, ContactsService::class.java)
        startService(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        contactAdapter = ContactsAdapter(contactsList)
        val recyclerView: RecyclerView = findViewById(R.id.rv_contact)
        recyclerView.adapter = contactAdapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // 在 API 26 及以上版本，使用带有 `flags` 参数的 `registerReceiver`
            registerReceiver(contactsReceiver, IntentFilter("CONTACTS_UPDATED"), Context.RECEIVER_EXPORTED)
        } else {
            // 在 API 26 以下版本，使用常规的 `registerReceiver`
            registerReceiver(contactsReceiver, IntentFilter("CONTACTS_UPDATED"))
        }

        findViewById<Button>(R.id.btn_start_service).setOnClickListener {
            checkAndRequestPermission()
        }
        checkAndRequestPermission()
    }

    private fun checkAndRequestPermission() {
        if(ContextCompat.checkSelfPermission(this,Manifest.permission.READ_CONTACTS)==PackageManager.PERMISSION_GRANTED){
            startContactService()
        }else{
            permissionsLauncher.launch(Manifest.permission.READ_CONTACTS)
        }
    }

    private val contactsReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, p1: Intent?) {
            Log.d("ContactsReceiver", "onReceive")
            val intent = p1!!
            val updatedContacts: ArrayList<Contact>? =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    // API 33 及以上使用此方法
                    intent.getParcelableArrayListExtra("contacts", Contact::class.java)
                } else {
                    // 兼容旧版本
                    @Suppress("DEPRECATION")
                    intent.getParcelableArrayListExtra<Contact>("contacts")
                }

            if (updatedContacts != null) {
                contactsList.clear()
                contactsList.addAll(updatedContacts)
                contactAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
        LocalBroadcastManager.getInstance(this).unregisterReceiver(contactsReceiver)
    }
}