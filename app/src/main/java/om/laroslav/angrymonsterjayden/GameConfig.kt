package om.laroslav.angrymonsterjayden

data class GameConfig(
    val vision: Boolean,
    val gun: Boolean,
    val clown: Boolean,
    val sleepy: Boolean,
    val ultra: Boolean,
    val seconds: Int,
    val monsterDrawable: Int
)

val Int.dp: Int get() = (this * android.content.res.Resources.getSystem().displayMetrics.density).toInt()
