package om.laroslav.angrymonsterjayden

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Button

class CustomGameActivity : Activity() {
    private lateinit var vision: CheckBox; private lateinit var gun: CheckBox; private lateinit var clown: CheckBox; private lateinit var sleep: CheckBox
    private lateinit var timeText: TextView; private lateinit var seek: SeekBar
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_custom_game)
        vision = findViewById(R.id.visionCheck); gun = findViewById(R.id.gunCheck); clown = findViewById(R.id.clownCheck); sleep = findViewById(R.id.sleepCheck)
        timeText = findViewById(R.id.timeText); seek = findViewById(R.id.timeSeek); seek.progress = 20; updateTime()
        listOf(vision, gun, clown, sleep).forEach { it.setOnCheckedChangeListener { _, _ -> updatePreview() } }
        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) = updateTime()
            override fun onStartTrackingTouch(s: SeekBar?) = Unit
            override fun onStopTrackingTouch(s: SeekBar?) = Unit
        })
        findViewById<Button>(R.id.startButton).setOnClickListener {
            val drawable = if (vision.isChecked) R.drawable.normalmonsor else R.drawable.notseemonstor
            GameActivity.start(this, GameConfig(vision.isChecked, gun.isChecked, clown.isChecked, sleep.isChecked, false, seconds(), drawable))
        }
        updatePreview()
    }
    private fun seconds() = 100 + seek.progress
    private fun updateTime() { val s = seconds(); timeText.text = "Время: %02d:%02d".format(s / 60, s % 60) }
    private fun updatePreview() {
        findViewById<ImageView>(R.id.monsterImage).setImageResource(if (vision.isChecked) R.drawable.normalmonsor else R.drawable.notseemonstor)
        findViewById<ImageView>(R.id.visionOverlay).visibility = if (vision.isChecked) View.VISIBLE else View.GONE
        findViewById<ImageView>(R.id.noVisionX).visibility = if (vision.isChecked) View.GONE else View.VISIBLE
        findViewById<ImageView>(R.id.gunX).visibility = if (gun.isChecked) View.GONE else View.VISIBLE
        findViewById<ImageView>(R.id.clownX).visibility = if (clown.isChecked) View.GONE else View.VISIBLE
        findViewById<ImageView>(R.id.sleepX).visibility = if (sleep.isChecked) View.GONE else View.VISIBLE
    }
}
