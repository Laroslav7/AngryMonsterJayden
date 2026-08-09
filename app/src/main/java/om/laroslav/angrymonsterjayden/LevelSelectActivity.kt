package om.laroslav.angrymonsterjayden

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.GridLayout

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_level_select)
        findViewById<Button>(R.id.backButton).setOnClickListener { finish() }
        val grid = findViewById<GridLayout>(R.id.levelGrid)
        levels.forEachIndexed { index, config ->
            val button = Button(this, null, 0, R.style.JaydenButton).apply {
                text = "Уровень ${index + 1}"
                textSize = 22f
                setOnClickListener { GameActivity.start(this@LevelSelectActivity, config) }
                layoutParams = GridLayout.LayoutParams().apply {
                    width = 150.dp
                    height = 72.dp
                    setMargins(8.dp, 8.dp, 8.dp, 8.dp)
                }
            }
            grid.addView(button)
        }
    }
}
