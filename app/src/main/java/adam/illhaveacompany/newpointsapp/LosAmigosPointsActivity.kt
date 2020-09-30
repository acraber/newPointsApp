package adam.illhaveacompany.newpointsapp

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_los_amigos_points.*

class LosAmigosPointsActivity : AppCompatActivity() {

    private val context: Context = this
    private val thisActivity: Activity = this
    private val usingNumberPicker = true
    private val methodsHandler = Methods()

    private val numberOfPointsAllowed = 50
    private val pointsTableName = "PointsTable"
    private val qrCode = "AAA"


        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_los_amigos_points)

            methodsHandler.setVariablesInMethods(numberOfPointsAllowed,pointsTableName, context, thisActivity, usingNumberPicker)

            methodsHandler.showButtonIfUserHasFiftyPoints(redeemPointsBtn)
        scanBtn.setOnClickListener{
            methodsHandler.doneWithShowingSpinner = false
        }//27

        methodsHandler.setProgressBarAndPointsNumber(
            methodsHandler.getPointsValueFromDb(), progressBar, pointsNumberTextView
        )

        scanBtn.setOnClickListener{
            methodsHandler.startPointsAddingProcess()
        }

        redeemPointsBtn.setOnClickListener {
            methodsHandler.redeemPoints(
                progressBar, pointsNumberTextView,
                redeemPointsBtn
            )
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                if(result.contents == qrCode) {
                   methodsHandler.qrScanSuccess(
                       redeemPointsBtn,
                       pointsNumberTextView, progressBar
                   )
                }else {
                    Toast.makeText(this, "Barcode Not Recognized", Toast.LENGTH_LONG).show()
                }
            } else {
                Toast.makeText(this, "No Results", Toast.LENGTH_LONG).show()
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
    } //7



}