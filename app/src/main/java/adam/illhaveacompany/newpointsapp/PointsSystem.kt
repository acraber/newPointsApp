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
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_points_system.*

class PointsSystem : AppCompatActivity() {

    var pointsToShowThatAreAdding = 0
    var pointsToAdd = 0
    var doneWithShowingSpinner = false
    val thisTable = "SecondPointsTable"
    val methodsHandler = Methods()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_points_system)
        scanBtn.setOnClickListener{
            doneWithShowingSpinner = false
        }//27


        methodsHandler.showButtonIfUserHasFiftyPoints(thisTable, redeemPointsBtn, this)

        methodsHandler.setProgressBarAndPointsNumber(methodsHandler.getPointsValueFromDb(thisTable, this),progressBar,pointsNumberTextView)

        scanBtn.setOnClickListener{
            methodsHandler.show(thisTable,this, this)
        }

        redeemPointsBtn.setOnClickListener {
            methodsHandler.redeemPoints(thisTable, progressBar, pointsNumberTextView,
                redeemPointsBtn, this)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)
        if (result != null) {
            if (result.contents != null) {
                if(result.contents == "1191512") {
                    methodsHandler.addPointsToDb(methodsHandler.pointsToAdd, thisTable, this, application)//24
                    methodsHandler.showButtonIfUserHasFiftyPoints(thisTable, redeemPointsBtn, this)

                    methodsHandler.setProgressBarAndPointsNumber(methodsHandler.getPointsValueFromDb(thisTable, this),
                        progressBar, pointsNumberTextView)

                    Toast.makeText(this, "$pointsToShowThatAreAdding Points added", Toast.LENGTH_LONG).show()
                    pointsToShowThatAreAdding = 0
                    methodsHandler.pointsToAdd = 0

                    if(methodsHandler.isThereMoreThanOneSetOfPoints(thisTable, this)){
                        val databaseHandler = DatabaseHandler(this)
                        databaseHandler.deleteFirstRow(thisTable)
                        databaseHandler.close()
                    }
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