package om.laroslav.angrymonsterjayden

import android.app.Activity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_select)
        previewPanel = findViewById(R.id.previewPanel)
        previewMonster = findViewById(R.id.previewMonster)
        previewTitle = findViewById(R.id.previewTitle)
        previewInfo = findViewById(R.id.previewInfo)
        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }
        findViewById<Button>(R.id.startLevelButton).setOnClickListener { GameActivity.start(this, levels[selectedIndex]) }
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
            appendLine("Видит: ${yesNo(level.vision)}")
            appendLine("Стреляет: ${yesNo(level.gun)}")
            appendLine("Хочет спать: ${yesNo(level.sleepy)}")
            appendLine("Клоунада: ${yesNo(level.clown)}")
            appendLine("Ультра режим: ${yesNo(level.ultra)}")
            append("Таймер: ${formatTime(level.seconds)}")
        }
    }

    private fun yesNo(value: Boolean) = if (value) "да" else "нет"
    private fun formatTime(seconds: Int) = "%02d:%02d".format(seconds / 60, seconds % 60)
}
