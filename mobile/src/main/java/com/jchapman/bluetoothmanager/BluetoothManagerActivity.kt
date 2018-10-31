package com.jchapman.bluetoothmanager

import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.util.AttributeSet
import android.util.Log
import android.view.View
import android.widget.TextView
import java.util.jar.Manifest

class BluetoothManagerActivity : AppCompatActivity() {

    private val RECORD_REQUEST_CODE = 101

    var mBluetoothHeadset: BluetoothHeadset? = null
    // Get the default adapter
    val mBluetoothAdapter: BluetoothAdapter? = BluetoothAdapter.getDefaultAdapter()

    private val mProfileListener = object : BluetoothProfile.ServiceListener {

        override fun onServiceConnected(profile: Int, proxy: BluetoothProfile) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = proxy as BluetoothHeadset
            }
        }

        override fun onServiceDisconnected(profile: Int) {
            if (profile == BluetoothProfile.HEADSET) {
                mBluetoothHeadset = null
            }
        }
    }

    fun CheckPermissions()
    {
        if (mBluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
            return
        }
        val permissionsArray: MutableList<String> = ArrayList()
        var coarsePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
        if(coarsePermission != PackageManager.PERMISSION_GRANTED)
        {
            permissionsArray.add(android.Manifest.permission.ACCESS_COARSE_LOCATION)
        }
        var finePermission = ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
        if(finePermission != PackageManager.PERMISSION_GRANTED)
        {
            permissionsArray.add(android.Manifest.permission.ACCESS_FINE_LOCATION)
        }
        if(permissionsArray.count() > 0)
        {
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_COARSE_LOCATION, android.Manifest.permission.ACCESS_FINE_LOCATION),RECORD_REQUEST_CODE)
            return
        }

        mBluetoothAdapter?.startDiscovery()
    }

    fun SetFilters()
    {
        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mBroadcastReceiver, filter)

        var startedFilter =  IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_STARTED)
        registerReceiver(mBroadcastReceiver, startedFilter)

        var finishedFilter = IntentFilter(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)
        registerReceiver(mBroadcastReceiver, finishedFilter)
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_manager)

        CheckPermissions()

        SetFilters()

        var didStart = mBluetoothAdapter?.startDiscovery()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            RECORD_REQUEST_CODE -> {

                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    Log.i("Main", "Permission has been denied by user")
                } else {
                    Log.i("Main", "Permission has been granted by user")
                    CheckPermissions()
                }
            }
        }    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(mBroadcastReceiver)
    }

    override fun onResume() {
        super.onResume()

        if(mBluetoothAdapter?.isDiscovering == true)
        {
           Log.d("DEBUG", "Still discovering")
        }
        else
        {
            mBluetoothAdapter?.startDiscovery()
        }

    }
    private val mBroadcastReceiver = object : BroadcastReceiver()
    {
        override fun onReceive(context: Context?, intent: Intent?) {
            var action: String = intent!!.action
            when(action)
            {
                BluetoothDevice.ACTION_FOUND -> {
                    // Discovery has found a device. Get the BluetoothDevice
                    // object and its info from the Intent.
                    val device: BluetoothDevice =
                                    intent!!.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE)
                    val deviceName = device.name
                    val deviceHardwareAddress = device.address // MAC address
                    val textView = findViewById<TextView>(R.id.txtvw_devices)
                    val currentText = textView.text
                    textView.text = "$currentText \n $deviceName"
                }
                BluetoothAdapter.ACTION_DISCOVERY_STARTED -> {
                    Log.d("DEBUG", "Still discovering")

                }
                BluetoothAdapter.ACTION_DISCOVERY_FINISHED -> {
                    Log.d("DEBUG", "Still discovering")

                }
            }

        }

    }

}
