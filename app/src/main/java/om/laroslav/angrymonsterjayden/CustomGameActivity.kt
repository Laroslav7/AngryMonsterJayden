package om.laroslav.angrymonsterjayden

import android.app.Activity
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

class CustomGameActivity : Activity() {
    private var vision = true
    private var shot = true
    private var sleepy = true
    private var clown = true
    private lateinit var timeText: TextView
    private lateinit var livesText: TextView
    private lateinit var playersText: TextView
    private lateinit var timeSeek: SeekBar
    private lateinit var livesSeek: SeekBar
    private lateinit var playersSeek: SeekBar
    private lateinit var namesContainer: LinearLayout
    private val nameInputs = mutableListOf<EditText>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_custom_game)
        timeText = findViewById(R.id.timeText)
        livesText = findViewById(R.id.livesText)
        playersText = findViewById(R.id.playersText)
        timeSeek = findViewById(R.id.timeSeek)
        livesSeek = findViewById(R.id.livesSeek)
        playersSeek = findViewById(R.id.playersSeek)
        namesContainer = findViewById(R.id.playerNamesContainer)
        timeSeek.progress = 20
        livesSeek.progress = 0
        playersSeek.progress = 0
        setupToggle(R.id.visionButton) { vision = !vision }
        setupToggle(R.id.shotButton) { shot = !shot }
        setupToggle(R.id.sleepButton) { sleepy = !sleepy }
        setupToggle(R.id.clownButton) { clown = !clown }
        timeSeek.setOnSeekBarChangeListener(simpleSeek { updateTime() })
        livesSeek.setOnSeekBarChangeListener(simpleSeek { updateLives() })
        playersSeek.setOnSeekBarChangeListener(simpleSeek { updatePlayers() })
        findViewById<Button>(R.id.startButton).setOnClickListener {
            val drawable = if (vision) R.drawable.normalmonsor else R.drawable.notseemonstor
            GameActivity.start(this, GameConfig(vision, shot, clown, sleepy, false, seconds(), drawable, lives(), playerNames()))
        }
        updateTime()
        updateLives()
        updatePlayers()
        updatePreview()
    }

    private fun setupToggle(id: Int, toggle: () -> Unit) {
        findViewById<FrameLayout>(id).setOnClickListener {
            toggle()
            updatePreview()
        }
    }

    private fun simpleSeek(onChanged: () -> Unit) = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) = onChanged()
        override fun onStartTrackingTouch(s: SeekBar?) = Unit
        override fun onStopTrackingTouch(s: SeekBar?) = Unit
    }

    private fun seconds() = 100 + timeSeek.progress
    private fun lives() = 1 + livesSeek.progress
    private fun playersCount() = 2 + playersSeek.progress

    private fun updateTime() {
        val s = seconds()
        timeText.text = "Время: %02d:%02d".format(s / 60, s % 60)
    }

    private fun updateLives() {
        livesText.text = "Жизни: ${lives()}"
    }

    private fun updatePlayers() {
        val count = playersCount()
        playersText.text = "Игроков: $count"
        while (nameInputs.size < count) addNameInput(nameInputs.size + 1)
        while (nameInputs.size > count) {
            namesContainer.removeView(nameInputs.removeAt(nameInputs.lastIndex))
        }
    }

    private fun addNameInput(number: Int) {
        val input = EditText(this).apply {
            hint = "Имя игрока $number"
            setText("Игрок $number")
            textSize = 18f
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            setSingleLine(true)
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xCCFFFFFF.toInt())
        }
        nameInputs.add(input)
        namesContainer.addView(input, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 46.dp))
    }

    private fun playerNames() = nameInputs.mapIndexed { index, input -> input.text.toString().ifBlank { "Игрок ${index + 1}" } }

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
        updateStatus(R.id.shotStatus, "Можно стрелять", shot)
        updateStatus(R.id.sleepStatus, "Хочет спать", sleepy)
        updateStatus(R.id.clownStatus, "Клоунада", clown)
    }

    private fun setVisible(id: Int, visible: Boolean) {
        findViewById<View>(id).visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun updateStatus(id: Int, name: String, enabled: Boolean) {
        findViewById<TextView>(id).text = "$name: ${if (enabled) "вкл" else "выкл"}"
    }
}
