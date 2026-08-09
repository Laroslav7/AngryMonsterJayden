package om.laroslav.angrymonsterjayden

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.Bundle
import android.os.CountDownTimer
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView

class GameActivity : Activity() {
    private lateinit var background: ImageView; private lateinit var monster: ImageView; private lateinit var message: TextView; private lateinit var timer: TextView
    private lateinit var loseButton: Button; private lateinit var menuButton: Button; private lateinit var winBanner: ImageView; private lateinit var playerPanel: LinearLayout
    private val playerLives = mutableListOf<Int>()
    private var player: MediaPlayer? = null; private var countDown: CountDownTimer? = null; private var config = GameConfig(false, false, false, false, false, 120, R.drawable.notseemonstor)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState); setContentView(R.layout.activity_game)
        background = findViewById(R.id.backgroundImage); monster = findViewById(R.id.monsterImage); message = findViewById(R.id.messageText); timer = findViewById(R.id.timerText)
        loseButton = findViewById(R.id.loseButton); menuButton = findViewById(R.id.menuButton); winBanner = findViewById(R.id.winBanner); playerPanel = findViewById(R.id.playerPanel)
        config = readConfig(); findViewById<View>(R.id.gameMonsterStage).visibility = View.GONE; loseButton.setOnClickListener { showLose() }; menuButton.setOnClickListener { goHome() }
        startHidePhase()
    }

    private fun readConfig() = GameConfig(
        intent.getBooleanExtra(EXTRA_VISION, false), intent.getBooleanExtra(EXTRA_GUN, false), intent.getBooleanExtra(EXTRA_CLOWN, false),
        intent.getBooleanExtra(EXTRA_SLEEPY, false), intent.getBooleanExtra(EXTRA_ULTRA, false), intent.getIntExtra(EXTRA_SECONDS, 120), intent.getIntExtra(EXTRA_DRAWABLE, R.drawable.notseemonstor),
        intent.getIntExtra(EXTRA_LIVES, 1), intent.getStringArrayExtra(EXTRA_PLAYERS)?.toList() ?: listOf("Игрок 1", "Игрок 2")
    )

    private fun startHidePhase() {
        background.setImageResource(R.drawable.basik); message.text = "Прячься!"; timer.visibility = View.VISIBLE
        // Музыка в игровом раунде отключена: звук используется только в предпросмотре/меню.
        countDown = object : CountDownTimer(14_000, 1_000) {
            override fun onTick(ms: Long) { timer.text = ((14_000 - ms) / 1_400 + 1).coerceAtMost(10).toString() }
            override fun onFinish() = startGameplay()
        }.start()
    }

    private fun startGameplay() {
        message.visibility = View.GONE; findViewById<View>(R.id.gameMonsterStage).visibility = View.VISIBLE; monster.setImageResource(config.monsterDrawable); applyCustomOverlays(); setupPlayers(); loseButton.visibility = View.VISIBLE
        countDown = object : CountDownTimer(config.seconds * 1000L, 1_000) {
            override fun onTick(ms: Long) { val s = (ms / 1000).toInt(); timer.text = "%02d:%02d".format(s / 60, s % 60) }
            override fun onFinish() = showWin()
        }.start()
    }

    private fun showLose() {
        countDown?.cancel(); stopAudio(); background.setImageResource(R.drawable.fon); monster.setImageResource(R.drawable.lose); hideGameOverlays(); findViewById<View>(R.id.gameMonsterStage).visibility = View.VISIBLE
        message.visibility = View.GONE; timer.visibility = View.GONE; winBanner.visibility = View.GONE; playerPanel.visibility = View.GONE; loseButton.visibility = View.GONE; menuButton.text = "В главное меню"; menuButton.visibility = View.VISIBLE
    }

    private fun showWin() {
        stopAudio(); background.setImageResource(R.drawable.win); findViewById<View>(R.id.gameMonsterStage).visibility = View.GONE; message.visibility = View.GONE; timer.visibility = View.GONE; playerPanel.visibility = View.GONE; loseButton.visibility = View.GONE; winBanner.visibility = View.VISIBLE; menuButton.text = "Далее"; menuButton.visibility = View.VISIBLE
        AnimatorSet().apply {
            playSequentially(
                ObjectAnimator.ofFloat(background, View.SCALE_X, 1f, 1.25f), ObjectAnimator.ofFloat(background, View.SCALE_Y, 1f, 1.25f),
                ObjectAnimator.ofFloat(background, View.SCALE_X, 1.25f, 1f), ObjectAnimator.ofFloat(background, View.SCALE_Y, 1.25f, 1f)
            ); duration = 900; interpolator = AccelerateDecelerateInterpolator(); start()
        }
    }

    private fun setupPlayers() {
        playerPanel.removeAllViews()
        playerLives.clear()
        config.playerNames.forEachIndexed { index, name ->
            playerLives.add(config.lives.coerceIn(1, 3))
            val row = Button(this, null, 0, R.style.JaydenButton).apply {
                textSize = 20f
                text = playerText(name, playerLives[index])
                setOnClickListener {
                    if (playerLives[index] > 0) playerLives[index]--
                    text = playerText(name, playerLives[index])
                }
            }
            playerPanel.addView(row, LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 54.dp))
        }
        playerPanel.visibility = View.VISIBLE
    }

    private fun playerText(name: String, lives: Int): String {
        val hearts = if (lives > 0) "❤".repeat(lives) else "нет жизней"
        return "$name  $hearts"
    }

    private fun applyCustomOverlays() {
        val customBase = config.monsterDrawable == R.drawable.normalmonsor || config.monsterDrawable == R.drawable.notseemonstor
        setOverlay(R.id.gameVisionOverlay, customBase && config.vision)
        setOverlay(R.id.gameShotOverlay, customBase && config.gun)
        setOverlay(R.id.gameSleepOverlay, customBase && config.sleepy)
        setOverlay(R.id.gameClownOverlay, customBase && config.clown)
    }

    private fun hideGameOverlays() {
        setOverlay(R.id.gameVisionOverlay, false)
        setOverlay(R.id.gameShotOverlay, false)
        setOverlay(R.id.gameSleepOverlay, false)
        setOverlay(R.id.gameClownOverlay, false)
    }

    private fun setOverlay(id: Int, visible: Boolean) {
        findViewById<View>(id).visibility = if (visible) View.VISIBLE else View.GONE
    }

    private fun play(resId: Int) { stopAudio(); player = MediaPlayer.create(this, resId).also { it.start() } }
    private fun stopAudio() { player?.release(); player = null }
    private fun goHome() { startActivity(Intent(this, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)); finish() }
    override fun onDestroy() { countDown?.cancel(); stopAudio(); super.onDestroy() }

    companion object {
        private const val EXTRA_VISION = "vision"; private const val EXTRA_GUN = "gun"; private const val EXTRA_CLOWN = "clown"; private const val EXTRA_SLEEPY = "sleepy"; private const val EXTRA_ULTRA = "ultra"; private const val EXTRA_SECONDS = "seconds"; private const val EXTRA_DRAWABLE = "drawable"; private const val EXTRA_LIVES = "lives"; private const val EXTRA_PLAYERS = "players"
        fun start(context: Context, c: GameConfig) = context.startActivity(Intent(context, GameActivity::class.java).putExtra(EXTRA_VISION, c.vision).putExtra(EXTRA_GUN, c.gun).putExtra(EXTRA_CLOWN, c.clown).putExtra(EXTRA_SLEEPY, c.sleepy).putExtra(EXTRA_ULTRA, c.ultra).putExtra(EXTRA_SECONDS, c.seconds).putExtra(EXTRA_DRAWABLE, c.monsterDrawable).putExtra(EXTRA_LIVES, c.lives).putExtra(EXTRA_PLAYERS, c.playerNames.toTypedArray()))
    }
}
