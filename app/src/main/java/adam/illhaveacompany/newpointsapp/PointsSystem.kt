package adam.illhaveacompany.newpointsapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_points_system.*

class PointsSystem : AppCompatActivity() {

    val numberOfPointsAllowed = 20
    val thisTable = "PointsTable"
    private val methodsHandler = Methods()
    val qrCode = "AAA"
    val pointsToAdd = 0
    val toastMessage = "Please Work"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_points_system)
        scanBtn.setOnClickListener{
            methodsHandler.doneWithShowingSpinner = false
        }//27

        methodsHandler.setVariablesInMethods(numberOfPointsAllowed,thisTable, qrCode, toastMessage)

        methodsHandler.toast(this)




        methodsHandler.showButtonIfUserHasFiftyPoints(
            thisTable,
            redeemPointsBtn,
            this,
            numberOfPointsAllowed
        )

        methodsHandler.setProgressBarAndPointsNumber(
            methodsHandler.getPointsValueFromDb(
                thisTable,
                this
            ), progressBar, pointsNumberTextView, numberOfPointsAllowed
        )


        scanBtn.setOnClickListener{
            methodsHandler.show(thisTable, this, this, numberOfPointsAllowed)
        }

        redeemPointsBtn.setOnClickListener {
            methodsHandler.redeemPoints(
                thisTable, progressBar, pointsNumberTextView,
                redeemPointsBtn, this, numberOfPointsAllowed
            )
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                if(result.contents == qrCode) {
                   methodsHandler.qrScanSuccess(
                       thisTable, this, application, redeemPointsBtn, numberOfPointsAllowed,
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