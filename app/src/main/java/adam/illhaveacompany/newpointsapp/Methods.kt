package adam.illhaveacompany.newpointsapp

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Application
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.integration.android.IntentIntegrator
import kotlinx.android.synthetic.main.activity_points_system.*

class Methods() {

   fun isThereMoreThanOneSetOfPoints(tableName: String, context: Context): Boolean {
      //returns whether there's two sets of points - found from DatabaseHandler's function
      val databaseHandler = DatabaseHandler(context)
      val twoSetsOfPoints = databaseHandler.areThereMoreThanOneSetOfPoints(tableName)
      databaseHandler.close()
      return twoSetsOfPoints
   }//17 - checks if there's more than one set of points in the database

   fun scanCode(activity: Activity) {
      val integrator = IntentIntegrator(activity)
      integrator.captureActivity = CaptureAct::class.java
      integrator.setOrientationLocked(false)
      integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
      integrator.setPrompt("Scanning Code")
      integrator.initiateScan()
   } //5

   fun areTherePointsInTheDatabase(tableName: String, context: Context) : Boolean {
      val dbHandler = DatabaseHandler(context)
      val areTherePoints = dbHandler.areTherePoints(tableName)
      dbHandler.close()
      return areTherePoints
   }//19

   fun getPointsValueFromDb(tableName: String, context: Context) : Int {
      var lastPointsValue = 0
      if(areTherePointsInTheDatabase(tableName, context)){
         val databaseHandler = DatabaseHandler(context)
         val pointsValueList = databaseHandler.getPointsValues(tableName)
         val lastPointsValueRow = pointsValueList[pointsValueList.size - 1]
         lastPointsValue = lastPointsValueRow.numberOfPoints
      }else{
         lastPointsValue = 0
      }
      return lastPointsValue
   }//18 -- checks if there's points in the database. If there are, it returns the points. If not, it returns 0

   fun addPointsToDb(points: Int, tableName: String, thisContext: Context, applicationContext: Context) {
      if(areTherePointsInTheDatabase(tableName, thisContext)) {
         val databaseHandler = DatabaseHandler(thisContext)
         val status = databaseHandler.addSecondaryPoints(points, tableName)

         if (status > -1) {
         } else {
            Toast.makeText(applicationContext, "Record save failed", Toast.LENGTH_LONG).show()
         }
         databaseHandler.close()
      }else{
         val databaseHandler = DatabaseHandler(thisContext)
         val status = databaseHandler.addFirstPoints(Points(0, points), tableName)

         if(status > -1) {
            Toast.makeText(applicationContext, "Points Successfully Added", Toast.LENGTH_LONG).show()
         }else{
            Toast.makeText(applicationContext, "Record save failed", Toast.LENGTH_LONG).show()
         }
         databaseHandler.close()
      }
   }//23


   fun showButtonIfUserHasFiftyPoints(tableName: String, redeemPointsbtnButton: Button, thisContext: Context){
      val numberOfPoints = getPointsValueFromDb(tableName, thisContext)
      if(numberOfPoints >= 50){
         redeemPointsbtnButton.visibility = View.VISIBLE
      }else{
         redeemPointsbtnButton.visibility = View.GONE
      }


   }

   fun setProgressBarAndPointsNumber(numberOfPoints: Int, progressBar: ProgressBar, pointsNumberTextView: TextView) {
      progressBar.max = 500

      if(numberOfPoints == 0)
      {
         pointsNumberTextView.text = '0'.toString()
         ObjectAnimator.ofInt(progressBar, "progress", 0).setDuration(2000).start()
      }else{
         pointsNumberTextView.text = numberOfPoints.toString()
         ObjectAnimator.ofInt(progressBar, "progress", numberOfPoints*10).setDuration(2000).start()
      }
   }

   fun redeemPointsBuilder(tableName: String, progressBar: ProgressBar, pointsNumberTextView: TextView,
                                   redeemPointsBtn: Button, thisContext: Context){
      val builder2 = AlertDialog.Builder(thisContext)
      builder2.setTitle("Redeeming Points")
      builder2.setMessage("Are you sure you would like to redeem your points?\n\nThis can only be done once\n\nIf you press yes " +
              "away from a Los Amigos employee you will lose your points with no refund.")
      builder2.setPositiveButton("YES") { dialogInterface: DialogInterface, i: Int ->
         val db = DatabaseHandler(thisContext)
         db.addFirstPoints(Points(0,0), tableName)
         db.close()
         setProgressBarAndPointsNumber(getPointsValueFromDb(tableName, thisContext), progressBar, pointsNumberTextView)
         showButtonIfUserHasFiftyPoints(tableName, redeemPointsBtn, thisContext)
         Toast.makeText(thisContext, "Points removed from account", Toast.LENGTH_SHORT).show()
         val builder4 = AlertDialog.Builder(thisContext)
         builder4.setTitle("SHOW TO EMPLOYEE")
         builder4.setCancelable(false)
         builder4.setMessage("The user has chosen to redeem their points\n\nShow this message to Los Amigos employee or points may be voided")
         builder4.setPositiveButton("Okay") { dialogInterface: DialogInterface, i: Int ->
         }

         builder4.show()
      }
      builder2.setNegativeButton("NO") { dialogInterface: DialogInterface, i: Int ->
         val builder3 = AlertDialog.Builder(thisContext)
         builder3.setTitle("NOT REDEEMING POINTS")
         builder3.setMessage("User cancelled points redemption")
         builder3.setPositiveButton("Okay") { dialogInterface: DialogInterface, i: Int ->
         }
         builder3.show()
      }
      builder2.show()
   }

   fun redeemPoints(thisTable: String, progressBar: ProgressBar, pointsNumberTextView: TextView,
                            redeemPointsBtn: Button, thisContext: Context){
      if(getPointsValueFromDb(thisTable, thisContext) >= 50){
         redeemPointsBuilder(thisTable, progressBar,pointsNumberTextView, redeemPointsBtn, thisContext)
      }else{
         Toast.makeText(thisContext, "There are not enough points to redeem", Toast.LENGTH_SHORT).show()
      }
   }

   private fun show(tableName: String, thisContext: Context, thisActivity: Activity) {
      var pointsToShowThatAreAdding = 0
      var pointsToAdd : Int = 0
      var doneWithShowingSpinner = false
      val d = Dialog(thisContext)
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
         totalPointsAfterAdding = pointsToAdd + getPointsValueFromDb(tableName, thisContext)
         if(totalPointsAfterAdding >= 50){
            pointsToShowThatAreAdding = 50 - getPointsValueFromDb(tableName, thisContext)
         }else{
            pointsToShowThatAreAdding = pointsToAdd
         }

         val builder = AlertDialog.Builder(thisContext)
         builder.setTitle("Adding ${pointsToShowThatAreAdding} points")
         builder.setPositiveButton("SCAN") { dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(thisContext, "$pointsToShowThatAreAdding points are being added", Toast.LENGTH_LONG).show()
            scanCode(thisActivity)
         }
         builder.setNegativeButton("GO BACK") { dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(thisActivity, "Scan cancelled", Toast.LENGTH_SHORT).show()
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



