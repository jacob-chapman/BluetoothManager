package com.jchapman.bluetoothmanager

import android.bluetooth.*
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.util.AttributeSet
import android.util.Log
import android.view.View

class BluetoothManagerActivity : AppCompatActivity() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bluetooth_manager)

        if (mBluetoothAdapter?.isEnabled == false) {
            val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
            startActivityForResult(enableBtIntent, 1)
        }

        var filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        registerReceiver(mBroadcastReceiver, filter)

        var didStart = mBluetoothAdapter?.startDiscovery()
    }

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
                }
            }

        }

    }

}
