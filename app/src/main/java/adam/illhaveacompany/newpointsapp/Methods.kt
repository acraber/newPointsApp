package adam.illhaveacompany.newpointsapp

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import com.google.zxing.integration.android.IntentIntegrator

class Methods(){
   var doneWithShowingSpinner = false
   var pointsToShowThatAreAdding = 0
   var pointsToAdd = 0
   var totalPointsAfterAdding = 0
   var toastMessage = "notright"
   var tableName = ""
   var numberOfPointsAllowed = 0

   fun setVariablesInMethods(numberOfPointsAllowed: Int, thisTable: String, qrCode: String, toastMessage: String){
      this.numberOfPointsAllowed = numberOfPointsAllowed
      this.tableName = thisTable
   }

   fun toast(thisContext: Context){
      Toast.makeText(thisContext, tableName, Toast.LENGTH_LONG).show()
   }

   private fun isThereMoreThanOneSetOfPoints(context: Context): Boolean {
      //returns whether there's two sets of points - found from DatabaseHandler's function
      val databaseHandler = DatabaseHandler(context)
      val twoSetsOfPoints = databaseHandler.areThereMoreThanOneSetOfPoints(tableName)
      databaseHandler.close()
      return twoSetsOfPoints
   }//17 - checks if there's more than one set of points in the database

   private fun scanCode(activity: Activity) {
      val integrator = IntentIntegrator(activity)
      integrator.captureActivity = CaptureAct::class.java
      integrator.setOrientationLocked(false)
      integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE_TYPES)
      integrator.setPrompt("Scanning Code")
      integrator.initiateScan()
   } //5

   private fun areTherePointsInTheDatabase(context: Context): Boolean {
      val dbHandler = DatabaseHandler(context)
      val areTherePoints = dbHandler.areTherePoints(tableName)
      dbHandler.close()
      return areTherePoints
   }//19

   fun getPointsValueFromDb(context: Context): Int {
      var lastPointsValue = 0
      if (areTherePointsInTheDatabase(context)) {
         val databaseHandler = DatabaseHandler(context)
         val pointsValueList = databaseHandler.getPointsValues(tableName)
         val lastPointsValueRow = pointsValueList[pointsValueList.size - 1]
         lastPointsValue = lastPointsValueRow.numberOfPoints
      } else {
         lastPointsValue = 0
      }
      return lastPointsValue
   }//18 -- checks if there's points in the database. If there are, it returns the points. If not, it returns 0

   private fun addPointsToDb(
      points: Int,
      thisContext: Context,
      applicationContext: Context
   ) {
      if (areTherePointsInTheDatabase(thisContext)) {
         val databaseHandler = DatabaseHandler(thisContext)
         val status = databaseHandler.addSecondaryPoints(points, tableName)
         if (status > -1) {
         } else {
            Toast.makeText(applicationContext, "Record save failed", Toast.LENGTH_LONG).show()
         }
         databaseHandler.close()
      } else {
         val databaseHandler = DatabaseHandler(thisContext)
         val status = databaseHandler.addFirstPoints(Points(0, points), tableName)

         if (status > -1) {
            Toast.makeText(applicationContext, "Points Successfully Added", Toast.LENGTH_LONG)
               .show()
         } else {
            Toast.makeText(applicationContext, "Record save failed", Toast.LENGTH_LONG).show()
         }
         databaseHandler.close()
      }
   }//23


   fun showButtonIfUserHasFiftyPoints(
      redeemPointsbtnButton: Button,
      thisContext: Context, numberOfPointsAllowed: Int
   ) {
      val numberOfPoints = getPointsValueFromDb(thisContext)
      if (numberOfPoints >= numberOfPointsAllowed) {
         redeemPointsbtnButton.visibility = View.VISIBLE
      } else {
         redeemPointsbtnButton.visibility = View.GONE
      }


   }

   fun setProgressBarAndPointsNumber(
      numberOfPoints: Int,
      progressBar: ProgressBar,
      pointsNumberTextView: TextView,
      numberOfPointsAllowed: Int
   ) {
      progressBar.max = numberOfPointsAllowed*10

      if (numberOfPoints == 0) {
         pointsNumberTextView.text = '0'.toString()
         ObjectAnimator.ofInt(progressBar, "progress", 0).setDuration(2000).start()
      } else {
         pointsNumberTextView.text = numberOfPoints.toString()
         ObjectAnimator.ofInt(progressBar, "progress", numberOfPoints * 10).setDuration(2000)
            .start()
      }
   }

   private fun redeemPointsBuilder(
      progressBar: ProgressBar, pointsNumberTextView: TextView,
      redeemPointsBtn: Button, thisContext: Context, numberOfPointsAllowed: Int
   ) {
      val builder2 = AlertDialog.Builder(thisContext)
      builder2.setTitle("Redeeming Points")
      builder2.setMessage(
         "Are you sure you would like to redeem your points?\n\nThis can only be done once\n\nIf you press yes " +
                 "away from a Los Amigos employee you will lose your points with no refund."
      )
      builder2.setPositiveButton("YES") { dialogInterface: DialogInterface, i: Int ->
         val db = DatabaseHandler(thisContext)
         db.addFirstPoints(Points(0, 0), tableName)
         db.close()
         setProgressBarAndPointsNumber(
            getPointsValueFromDb(thisContext),
            progressBar,
            pointsNumberTextView, numberOfPointsAllowed
         )
         showButtonIfUserHasFiftyPoints(
            redeemPointsBtn,
            thisContext,
            numberOfPointsAllowed
         )
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

   fun redeemPoints(
      thisTable: String, progressBar: ProgressBar, pointsNumberTextView: TextView,
      redeemPointsBtn: Button, thisContext: Context, numberOfPointsAllowed: Int
   ) {
      if (getPointsValueFromDb(thisContext) >= numberOfPointsAllowed) {
         redeemPointsBuilder(
            progressBar,
            pointsNumberTextView,
            redeemPointsBtn,
            thisContext,
            numberOfPointsAllowed
         )
      } else {
         Toast.makeText(thisContext, "There are not enough points to redeem", Toast.LENGTH_SHORT)
            .show()
      }
   }

   fun show(
      thisContext: Context,
      thisActivity: Activity,
      numberOfPointsAllowed: Int
   ) {
      pointsToAdd = 0
      doneWithShowingSpinner = false
      val d = Dialog(thisContext)
      d.setTitle("NumberPicker")
      d.setContentView(R.layout.dialog)
      val b1: Button = d.findViewById(R.id.setButton) as Button
      val b2: Button = d.findViewById(R.id.cancelButton) as Button
      val numberPicker = d.findViewById(R.id.numberPicker1) as NumberPicker
      numberPicker.maxValue = 25
      numberPicker.minValue = 1
      numberPicker.wrapSelectorWheel = false

      b1.setOnClickListener {
         totalPointsAfterAdding = 0
         pointsToAdd = numberPicker.value
         d.dismiss()
         doneWithShowingSpinner = true
         totalPointsAfterAdding = pointsToAdd + getPointsValueFromDb(thisContext)
         if (totalPointsAfterAdding >= numberOfPointsAllowed) {
            pointsToShowThatAreAdding = numberOfPointsAllowed - getPointsValueFromDb(
               thisContext
            )
            pointsToAdd = pointsToShowThatAreAdding
         } else {
            pointsToShowThatAreAdding = pointsToAdd
         }

         val builder = AlertDialog.Builder(thisContext)
         builder.setTitle("Adding ${pointsToShowThatAreAdding} points")
         builder.setPositiveButton("SCAN") { dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(
               thisContext,
               "$pointsToShowThatAreAdding points are being added",
               Toast.LENGTH_LONG
            ).show()
            scanCode(thisActivity)
         }
         builder.setNegativeButton("GO BACK") { dialogInterface: DialogInterface, i: Int ->
            Toast.makeText(thisActivity, "Scan cancelled", Toast.LENGTH_SHORT).show()
         }

         if (totalPointsAfterAdding >= numberOfPointsAllowed) {
            builder.setMessage(
               "A Los Amigos employee must verify points before scanning.\n\nThe maximum total points allowed is $numberOfPointsAllowed\n\n" +
                       "Any points above a total of $numberOfPointsAllowed will not be added"
            )
            totalPointsAfterAdding = 0
            builder.show()
         } else {
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

   fun qrScanSuccess(
      thisContext: Context,
      applicationContext: Context,
      redeemPointsBtn: Button,
      numberOfPointsAllowed: Int,
      pointsNumberTextView: TextView,
      progressBar: ProgressBar
   ){
      addPointsToDb(pointsToAdd, thisContext, applicationContext)//24
      showButtonIfUserHasFiftyPoints(redeemPointsBtn, thisContext, numberOfPointsAllowed)

      setProgressBarAndPointsNumber(
         getPointsValueFromDb(thisContext),
         progressBar, pointsNumberTextView, numberOfPointsAllowed
      )

      Toast.makeText(thisContext, "$pointsToShowThatAreAdding Points added", Toast.LENGTH_LONG).show()
      pointsToShowThatAreAdding = 0
      pointsToAdd = 0

      if(isThereMoreThanOneSetOfPoints(thisContext)){
         val databaseHandler = DatabaseHandler(thisContext)
         databaseHandler.deleteFirstRow(tableName)
         databaseHandler.close()
      }}



}