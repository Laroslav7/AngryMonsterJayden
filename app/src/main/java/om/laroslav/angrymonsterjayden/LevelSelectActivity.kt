package om.laroslav.angrymonsterjayden

import android.app.Activity
import android.media.MediaPlayer
import android.os.Bundle
import android.text.InputType
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.SeekBar
import android.widget.TextView

class LevelSelectActivity : Activity() {
    private val levels = listOf(
        GameConfig(false, false, false, false, false, 120, R.drawable.notseemonstor),
        GameConfig(false, true, false, false, false, 180, R.drawable.level2),
        GameConfig(true, true, false, false, false, 200, R.drawable.level3),
        GameConfig(false, false, false, true, false, 260, R.drawable.level4),
        GameConfig(false, false, true, true, false, 280, R.drawable.levev5),
        GameConfig(true, true, true, false, false, 300, R.drawable.level6),
        GameConfig(false, false, true, true, true, 340, R.drawable.level7),
        GameConfig(true, true, true, true, true, 390, R.drawable.level8)
    )
    private var selectedIndex = 0
    private lateinit var previewPanel: LinearLayout
    private lateinit var previewMonster: ImageView
    private lateinit var previewTitle: TextView
    private lateinit var previewInfo: TextView
    private lateinit var livesText: TextView
    private lateinit var playersText: TextView
    private lateinit var livesSeek: SeekBar
    private lateinit var playersSeek: SeekBar
    private lateinit var namesContainer: LinearLayout
    private val nameInputs = mutableListOf<EditText>()
    private var previewPlayer: MediaPlayer? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_select)
        previewPanel = findViewById(R.id.previewPanel)
        previewMonster = findViewById(R.id.previewMonster)
        previewTitle = findViewById(R.id.previewTitle)
        previewInfo = findViewById(R.id.previewInfo)
        livesText = findViewById(R.id.levelLivesText)
        playersText = findViewById(R.id.levelPlayersText)
        livesSeek = findViewById(R.id.levelLivesSeek)
        playersSeek = findViewById(R.id.levelPlayersSeek)
        namesContainer = findViewById(R.id.levelNamesContainer)
        livesSeek.setOnSeekBarChangeListener(simpleSeek { updateLivesAndPlayers() })
        playersSeek.setOnSeekBarChangeListener(simpleSeek { updateLivesAndPlayers() })
        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }
        findViewById<Button>(R.id.startLevelButton).setOnClickListener { startSelectedLevel() }
        val grid = findViewById<GridLayout>(R.id.levelGrid)
        levels.forEachIndexed { index, _ ->
            val button = Button(this, null, 0, R.style.JaydenButton).apply {
                text = "Уровень ${index + 1}"
                textSize = 21f
                setOnClickListener { showPreview(index) }
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 150.dp
                    height = 62.dp
                    setMargins(6.dp, 6.dp, 6.dp, 6.dp)
                }
            }
            grid.addView(button)
        }
    }

    private fun showPreview(index: Int) {
        selectedIndex = index
        val level = levels[index]
        previewPanel.visibility = View.VISIBLE
        previewMonster.setImageResource(level.monsterDrawable)
        previewTitle.text = "Уровень ${index + 1}"
        previewInfo.text = buildString {
            appendLine("Монстр: ${monsterDescription(level)}")
            appendLine("Видит: ${yesNo(level.vision)}")
            appendLine("Стреляет: ${yesNo(level.gun)}")
            appendLine("Хочет спать: ${yesNo(level.sleepy)}")
            appendLine("Клоунада: ${yesNo(level.clown)}")
            appendLine("Ультра режим: ${yesNo(level.ultra)}")
            append("Таймер: ${formatTime(level.seconds)}")
        }
        playPreviewMusic()
        updateLivesAndPlayers()
    }

    private fun startSelectedLevel() {
        stopPreviewMusic()
        val level = levels[selectedIndex]
        GameActivity.start(this, level.copy(lives = lives(), playerNames = playerNames()))
    }

    private fun simpleSeek(onChanged: () -> Unit) = object : SeekBar.OnSeekBarChangeListener {
        override fun onProgressChanged(s: SeekBar?, p: Int, fromUser: Boolean) = onChanged()
        override fun onStartTrackingTouch(s: SeekBar?) = Unit
        override fun onStopTrackingTouch(s: SeekBar?) = Unit
    }

    private fun lives() = 1 + livesSeek.progress
    private fun playersCount() = 2 + playersSeek.progress

    private fun updateLivesAndPlayers() {
        livesText.text = "Жизни: ${lives()}"
        val count = playersCount()
        playersText.text = "Игроков: $count"
        while (nameInputs.size < count) addNameInput(nameInputs.size + 1)
        while (nameInputs.size > count) namesContainer.removeView(nameInputs.removeAt(nameInputs.lastIndex))
    }

    private fun addNameInput(number: Int) {
        val input = EditText(this).apply {
            hint = "Имя игрока $number"
            setText("Игрок $number")
            textSize = 16f
            inputType = InputType.TYPE_CLASS_TEXT or InputType.TYPE_TEXT_FLAG_CAP_WORDS
            setSingleLine(true)
            setTextColor(0xFFFFFFFF.toInt())
            setHintTextColor(0xCCFFFFFF.toInt())
        }
        nameInputs.add(input)
        namesContainer.addView(input, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 40.dp))
    }

    private fun playPreviewMusic() {
        stopPreviewMusic()
        previewPlayer = MediaPlayer.create(this, R.raw.start).also { it.start() }
    }

    private fun stopPreviewMusic() {
        previewPlayer?.release()
        previewPlayer = null
    }

    override fun onDestroy() {
        stopPreviewMusic()
        super.onDestroy()
    }

    private fun playerNames() = nameInputs.mapIndexed { index, input -> input.text.toString().ifBlank { "Игрок ${index + 1}" } }
    private fun monsterDescription(level: GameConfig) = if (level.vision) "видит" else "не видит"
    private fun yesNo(value: Boolean) = if (value) "да" else "нет"
    private fun formatTime(seconds: Int) = "%02d:%02d".format(seconds / 60, seconds % 60)
}
