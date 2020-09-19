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
    var pointsToAdd : Int = 0
    var doneWithShowingSpinner = false
    val thisTable = "SecondPointsTable"
    val methodsHandler = Methods()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_points_system)
        scanBtn.setOnClickListener{
            doneWithShowingSpinner = false
            show(thisTable)
        }//27


        methodsHandler.showButtonIfUserHasFiftyPoints(thisTable, redeemPointsBtn, this)

        methodsHandler.setProgressBarAndPointsNumber(methodsHandler.getPointsValueFromDb(thisTable, this),progressBar,pointsNumberTextView)

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
                    methodsHandler.addPointsToDb(pointsToAdd, thisTable, this, application)//24
                    methodsHandler.showButtonIfUserHasFiftyPoints(thisTable, redeemPointsBtn, this)

                    methodsHandler.setProgressBarAndPointsNumber(methodsHandler.getPointsValueFromDb(thisTable, this),
                        progressBar, pointsNumberTextView)

                    Toast.makeText(this, "$pointsToShowThatAreAdding Points added", Toast.LENGTH_LONG).show()
                    pointsToShowThatAreAdding = 0
                    pointsToAdd = 0

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

    private fun show(tableName: String) {
        val d = Dialog(this)
        d.setTitle("NumberPicker")
        d.setContentView(R.layout.dialog)
        val b1: Button = d.findViewById(R.id.setButton) as Button
        val b2: Button = d.findViewById(R.id.cancelButton) as Button
        val numberPicker = d.findViewById(R.id.numberPicker1) as NumberPicker
        numberPicker.maxValue = 25
        numberPicker.minValue = 1
        numberPicker.wrapSelectorWheel = false

        b1.setOnClickListener{
            var totalPointsAfterAdding = 0
            pointsToAdd = numberPicker.value
            d.dismiss()
            doneWithShowingSpinner = true
            totalPointsAfterAdding = pointsToAdd + methodsHandler.getPointsValueFromDb(tableName, this)
            if(totalPointsAfterAdding >= 50){
                pointsToShowThatAreAdding = 50 - methodsHandler.getPointsValueFromDb(tableName, this)
            }else{
                pointsToShowThatAreAdding = pointsToAdd
            }

            val builder = AlertDialog.Builder(this)
            builder.setTitle("Adding ${pointsToShowThatAreAdding} points")
            builder.setPositiveButton("SCAN") { dialogInterface: DialogInterface, i: Int ->
                Toast.makeText(this, "$pointsToShowThatAreAdding points are being added", Toast.LENGTH_LONG).show()
                methodsHandler.scanCode(this)
            }
            builder.setNegativeButton("GO BACK") { dialogInterface: DialogInterface, i: Int ->
                Toast.makeText(this, "Scan cancelled", Toast.LENGTH_SHORT).show()
            }

            if(totalPointsAfterAdding >= 50){
                builder.setMessage("A Los Amigos employee must verify points before scanning.\n\nThe maximum total points allowed is 50\n\n" +
                        "Any points above a total of 50 will not be added")
                totalPointsAfterAdding = 0
                builder.show()
            }else{
                builder.setMessage("A Los Amigos employee must verify points before scanning.")
                totalPointsAfterAdding = 0
                builder.show()
            }

        }//31 and also //6 earlier

        b2.setOnClickListener {
            d.dismiss()
        }
        d.show()
    }//26








}