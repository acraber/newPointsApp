package adam.illhaveacompany.newpointsapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_store_list.*

class StoreList : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_store_list)

        losAmigosButton.setOnClickListener{
            val intent = Intent(this, LosAmigosPointsActivity::class.java)
            startActivity(intent)
        }

        bobsBurgersButton.setOnClickListener{
            val intent = Intent(this, SecondPointsSystem::class.java)
            startActivity(intent)
        }

        wetzelsPretzelsButton.setOnClickListener{
            val intent = Intent(this, WetzelsPretzelsActivity::class.java)
            startActivity(intent)
        }
    }
}