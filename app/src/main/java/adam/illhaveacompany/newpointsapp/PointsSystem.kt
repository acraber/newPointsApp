package adam.illhaveacompany.newpointsapp

import android.animation.ObjectAnimator
import android.app.Dialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.NumberPicker
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_points_system.*

class PointsSystem : AppCompatActivity() {

    private val numberOfPointsAllowed = 50
    private val thisTable = "SecondPointsTable"
    private val methodsHandler = Methods()
    private val qrCode = "1191512"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_points_system)
        scanBtn.setOnClickListener{
            methodsHandler.doneWithShowingSpinner = false
        }//27

        methodsHandler.showButtonIfUserHasFiftyPoints(thisTable, redeemPointsBtn, this, numberOfPointsAllowed)

        methodsHandler.setProgressBarAndPointsNumber(methodsHandler.getPointsValueFromDb(thisTable, this), progressBar, pointsNumberTextView, numberOfPointsAllowed)

        scanBtn.setOnClickListener{
            methodsHandler.show(thisTable,this, this, numberOfPointsAllowed)
        }

        redeemPointsBtn.setOnClickListener {
            methodsHandler.redeemPoints(thisTable, progressBar, pointsNumberTextView,
                redeemPointsBtn, this, numberOfPointsAllowed)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                if(result.contents == qrCode) {
                   methodsHandler.qrScanSuccess(thisTable,this, application, redeemPointsBtn, numberOfPointsAllowed,
                   pointsNumberTextView,progressBar)
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