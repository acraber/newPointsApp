package adam.illhaveacompany.newpointsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_main.*
/*  Possible problems
    - when adding a table to the database, the app crashes unless I uninstall and re-install. This could
        be bad when updating.
    - When changing tables to handle different types of rewards, we might need to completely erase the tables and start new ones. The user having the table
        stored on their devices still might cause a crash. It might also wipe points that people have rightfully earned.

    Notes
    - ever time someone redeems points update
    - make sure they have wifi
    28th, first, second (11-3 28th and second, 11 on the first)
*/
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        getStartedButton.setOnClickListener {
            val intent = Intent(this, StoreList::class.java)
            startActivity(intent)
        }
    }
}