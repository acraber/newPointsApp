package adam.illhaveacompany.newpointsapp

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.DatabaseUtils
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHandler (context: Context) :
    SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION){
    companion object {
        //changed when I want to add a column
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "PointsDatabase"

        private const val TABLE_POINTS = "PointsTable"
        private const val TABLE_POINTS2 = "SecondPointsTable"
        private const val TABLE_POINTS3 = "WetzelsPretzelsPointsTable"
        //ON ADDING NEW ACTIVITIES MUST ADD

        private const val KEY_ID = "_id"
        private const val KEY_NUMBER_OF_POINTS = "numberOfPoints"
    }//8

    override fun onCreate(db: SQLiteDatabase?) {
        val CREATE_LIBRARY_TABLE = ("CREATE TABLE " + TABLE_POINTS + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NUMBER_OF_POINTS + " TEXT" + ")")
        val CREATE_SECOND_LIBRARY_TABLE = ("CREATE TABLE " + TABLE_POINTS2 + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NUMBER_OF_POINTS + " TEXT" + ")")
        val CREATE_THIRD_LIBRARY_TABLE = ("CREATE TABLE " + TABLE_POINTS3 + "("
                + KEY_ID + " INTEGER PRIMARY KEY," + KEY_NUMBER_OF_POINTS + " TEXT" + ")")
        //ON ADDING NEW ACTIVITIES MUST ADD
        db?.execSQL(CREATE_LIBRARY_TABLE)
        db?.execSQL(CREATE_SECOND_LIBRARY_TABLE)
        db?.execSQL(CREATE_THIRD_LIBRARY_TABLE)
    }//10

    override fun onUpgrade(db: SQLiteDatabase?, oldVersion: Int, newVersion: Int) {
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_POINTS")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_POINTS2")
        db!!.execSQL("DROP TABLE IF EXISTS $TABLE_POINTS3")
        //ON ADDING NEW ACTIVITIES MUST ADD
        onCreate(db)
    }//11


    fun addPoints(points: Int, tableName: String) : Long {
        if(areTherePoints(tableName)){
            deleteFirstRow(tableName)
        }

        val db = this.writableDatabase

        val contentValues = ContentValues()

        contentValues.put(KEY_NUMBER_OF_POINTS, points)

        val success = db.insert(tableName, null, contentValues)
        db.close()

        return success
    }//12

    fun areTherePoints(tableName: String) : Boolean {
        val database = this.readableDatabase
        val noOfRows = DatabaseUtils.queryNumEntries(database, tableName).toInt()

        return if (noOfRows == 0) {
            false
        } else return true
    }//14

    fun areThereMoreThanOneSetOfPoints(tableName: String) : Boolean {
        val database = this.readableDatabase
        val noOfRows = DatabaseUtils.queryNumEntries(database, tableName).toInt()

        return if (noOfRows < 2) {
            false
        } else return true
    }//15


    fun deleteFirstRow(tableName: String) {
        val db = this.readableDatabase

        val cursor: Cursor =
            db.query(tableName, null, null, null, null, null, null)
        if (cursor.moveToFirst()) {
            val rowId = cursor.getString(cursor.getColumnIndex(KEY_ID))
            db.delete(tableName, "$KEY_ID=?", arrayOf(rowId))
        }
        db.close()
    }//16

    fun getPointsValues(tableName: String) : ArrayList<Int> {

        val pointsList: ArrayList<Int> = ArrayList()
        val selectQuery = "SELECT * FROM $tableName"

        val db = this.readableDatabase
        var cursor: Cursor? = null

        try {
            cursor = db.rawQuery(selectQuery, null)
        }catch (e: SQLiteException){
            db.execSQL(selectQuery)
        }//5

        var numberOfPoints: Int

        if (cursor != null) {
            if(cursor.moveToFirst()) {
                do{
                    numberOfPoints = cursor.getInt(cursor.getColumnIndex(KEY_NUMBER_OF_POINTS))

                    val pointsRow = numberOfPoints

                    pointsList.add(pointsRow)
                } while(cursor.moveToNext())
            }
        }
        return pointsList
    }//18

    fun updatePoints (pointsToAdd : Int, tableName: String) : Long {
        val pointsValueList = getPointsValues(tableName)
        val lastPointsValueRow = pointsValueList[pointsValueList.size - 1]
        val lastPointsValue = lastPointsValueRow
        var newPointsValue = lastPointsValue + pointsToAdd

        if(newPointsValue >= 50){
            newPointsValue = 50
        }//28

        if(areTherePoints(tableName)){
            deleteFirstRow(tableName)
        }

        val db = this.writableDatabase

        val contentValues = ContentValues()

        contentValues.put(KEY_NUMBER_OF_POINTS, newPointsValue)

        val success = db.insert(tableName, null, contentValues)
        db.close()

        return success
    }//22

}