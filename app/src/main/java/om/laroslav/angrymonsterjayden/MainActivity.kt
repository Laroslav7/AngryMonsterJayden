package om.laroslav.angrymonsterjayden

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button

class MainActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.playButton).setOnClickListener { startActivity(Intent(this, LevelSelectActivity::class.java)) }
        findViewById<Button>(R.id.customButton).setOnClickListener { startActivity(Intent(this, CustomGameActivity::class.java)) }
    }
}
