package om.laroslav.angrymonsterjayden

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.SeekBar
import android.widget.TextView

class CustomGameActivity : Activity() {
    private var vision = true
    private var shot = true
    private var sleepy = true
    private var clown = true
    private lateinit var timeText: TextView
    private lateinit var seek: SeekBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_game)
        timeText = findViewById(R.id.timeText)
        seek = findViewById(R.id.timeSeek)
        seek.progress = 20
        updateTime()
        setupToggle(R.id.visionButton) { vision = !vision }
        setupToggle(R.id.shotButton) { shot = !shot }
        setupToggle(R.id.sleepButton) { sleepy = !sleepy }
        setupToggle(R.id.clownButton) { clown = !clown }
        seek.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) = updateTime()
            override fun onStartTrackingTouch(s: SeekBar?) = Unit
            override fun onStopTrackingTouch(s: SeekBar?) = Unit
        })
        findViewById<Button>(R.id.startButton).setOnClickListener {
            val drawable = if (vision) R.drawable.normalmonsor else R.drawable.notseemonstor
            GameActivity.start(this, GameConfig(vision, shot, clown, sleepy, false, seconds(), drawable))
        }
        updatePreview()
    }

    private fun setupToggle(id: Int, toggle: () -> Unit) {
        findViewById<FrameLayout>(id).setOnClickListener {
            toggle()
            updatePreview()
        }
    }

    private fun seconds() = 100 + seek.progress

    private fun updateTime() {
        val s = seconds()
        timeText.text = "Время: %02d:%02d".format(s / 60, s % 60)
    }

    private fun updatePreview() {
        findViewById<ImageView>(R.id.monsterImage).setImageResource(if (vision) R.drawable.normalmonsor else R.drawable.notseemonstor)
        setVisible(R.id.visionOverlay, vision)
        setVisible(R.id.shotOverlay, shot)
        setVisible(R.id.sleepOverlay, sleepy)
        setVisible(R.id.clownOverlay, clown)
        setVisible(R.id.noVisionX, !vision)
        setVisible(R.id.visionX, !vision)
        setVisible(R.id.shotX, !shot)
        setVisible(R.id.sleepX, !sleepy)
        setVisible(R.id.clownX, !clown)
        updateStatus(R.id.visionStatus, "Зрение", vision)
        updateStatus(R.id.shotStatus, "Стрельба", shot)
        updateStatus(R.id.sleepStatus, "Сон", sleepy)
        updateStatus(R.id.clownStatus, "Клоунада", clown)
    }

    private fun setVisible(id: Int, visible: Boolean) {
        findViewById<View>(id).visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun updateStatus(id: Int, name: String, enabled: Boolean) {
        findViewById<TextView>(id).text = "$name: ${if (enabled) "вкл" else "выкл"}"
    }
}
