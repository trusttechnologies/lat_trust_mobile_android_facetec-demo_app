package com.trust.poc

import ZoomProcessors.LivenessCheckProcessor
import ZoomProcessors.Processor
import ZoomProcessors.ZoomGlobalState
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.facetec.zoom.sdk.ZoomSDK
import com.facetec.zoom.sdk.ZoomSessionActivity
import com.facetec.zoom.sdk.ZoomSessionResult
import com.google.gson.Gson
import com.microblink.MicroblinkSDK
import com.microblink.activity.BarcodeScanActivity
import com.microblink.entities.recognizers.Recognizer
import com.microblink.entities.recognizers.RecognizerBundle
import com.microblink.entities.recognizers.blinkbarcode.BarcodeType
import com.microblink.entities.recognizers.blinkbarcode.barcode.BarcodeRecognizer
import com.microblink.uisettings.ActivityRunner
import com.microblink.uisettings.BarcodeUISettings
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    var processor: Processor? = null
    var zoomSessionResult: ZoomSessionResult? = null
    var barcodeRecognizer: BarcodeRecognizer? = null
    var recognizerBundle: RecognizerBundle? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        MicroblinkSDK.setLicenseKey(id_micro_blink, this)



        initZoom()


        button2.setOnClickListener {
            processor = LivenessCheckProcessor(this, Processor.SessionTokenErrorCallback { })
        }

        button.setOnClickListener {
            initPdf()


        }

    }


    private fun initPdf() {
        barcodeRecognizer = BarcodeRecognizer()

        barcodeRecognizer?.setScanPdf417(true)

        recognizerBundle = RecognizerBundle(barcodeRecognizer)

        val settings = BarcodeUISettings(recognizerBundle!!)

        ActivityRunner.startActivityForResult(this, 200, settings)
    }

    private fun initZoom() {
        ZoomSDK.initialize(
            this,
            ZoomGlobalState.DeviceLicenseKeyIdentifier,
            ZoomGlobalState.PublicFaceMapEncryptionKey,
            object : ZoomSDK.InitializeCallback() {
                override fun onCompletion(p0: Boolean) {

                    if (p0) {

                    }
                }
            })
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 200) {
            if (resultCode == BarcodeScanActivity.RESULT_OK) {
                data?.let {
                    recognizerBundle?.loadFromIntent(it)


                    val result = barcodeRecognizer?.result

                    if (result?.resultState == Recognizer.Result.State.Valid) {
                        Toast.makeText(this, result.stringData, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }

        data?.let {
            zoomSessionResult = ZoomSessionActivity.getZoomSessionResultFromActivityResult(it)
        } ?: Log.e(javaClass.simpleName, "INTENT IS NULL")



        zoomSessionResult?.let {
            textView.text = it.countOfZoomSessionsPerformed.toString()
        }


    }
}

const val id_micro_blink =
    "sRwAAAANY29tLnRydXN0LnBvY2JZsXX9nt+sfTyhFy0xWx46IFB7xx3jCk4Bs6lhW4pL/kr16Hg6SZWP6EZ4DM+CyK21QJBK/QkQ2qlRR37LxQ63LuT4uDrUNKcEqwRfbbSFquDz95PmLmIHURvKMaKzuyzrWqo="