package com.example.mobileinteraction

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import com.google.android.gms.common.moduleinstall.ModuleInstall
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest
import com.google.android.gms.tflite.java.TfLite
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.codescanner.GmsBarcodeScannerOptions
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning

class Invest : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_invest)



        //make sure the QR scanner module's available
        val moduleInstallClient = ModuleInstall.getClient(this)
        val optionalModuleApi = GmsBarcodeScanning.getClient(this)
        val moduleInstallRequest =
            ModuleInstallRequest.newBuilder()
                .addApi(optionalModuleApi)
                // Add more APIs if you would like to request multiple optional modules.
                // .addApi(...)
                // Set the listener if you need to monitor the download progress.
                // .setListener(listener)
                .build()

        moduleInstallClient
            .installModules(moduleInstallRequest)
            .addOnSuccessListener {
                if (it.areModulesAlreadyInstalled()) {
                    // Modules are already installed when the request is sent.
                    //onQRScan("Module already installed!")
                }
            }
            .addOnFailureListener { e ->
                // Handle failure…
                //onQRScan(e.toString())
            }
    }

    fun scanQRCode(view: View) {


        //configure the scanner to only look for QR codes
        val options = GmsBarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_QR_CODE)
            .build()

        //new scanner using options from before
        val scanner = GmsBarcodeScanning.getClient(this, options)

        //start the scan and handle results
        scanner.startScan()
            .addOnSuccessListener { barcode ->
                // Task completed successfully
                //Log.d("hi","successfully finished QR scanner")
                val rawValue: String? = barcode.rawValue;
                onQRScan(rawValue)
            }
            .addOnCanceledListener {
                // Task canceled
                //Log.d("hi","cancelled QR scanner")
                onQRScan("Scan Cancelled!")
            }
            .addOnFailureListener { e ->
                // Task failed with an exception
                //Log.d("hi","failed QR scanner")
                onQRScan(e.toString())
            }
    }

    fun onQRScan(qrValue: String?) {
        //set on-screen text to represent the scanned QR code
        val textView: TextView = findViewById<TextView>(R.id.scannedStockText)
        textView.text = qrValue
    }
}