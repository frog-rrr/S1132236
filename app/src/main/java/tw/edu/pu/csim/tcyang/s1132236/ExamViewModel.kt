package tw.edu.pu.csim.tcyang.s1132236

import android.app.Application
import android.util.DisplayMetrics
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import android.graphics.RectF
import android.util.Log

// 角色圖示資訊
data class Character(
    val id: Int,
    val name: String,
    var collisionRectPx: RectF = RectF()
)

// 服務圖示的狀態資料類
data class ServiceIconState(
    val iconResourceId: Int,
    var xOffsetDp: Dp = 0.dp,
    var yOffsetDp: Dp = 0.dp
)

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    // --- 靜態/尺寸資訊 ---
    var screenWidthPx by mutableStateOf(0)
        private set
    var screenHeightPx by mutableStateOf(0)
        private set
    var density by mutableStateOf(1f)
        private set

    var authorInfo by mutableStateOf("作者 : 資訊工程系 S1132236 楊子青")
    var score by mutableStateOf(0)
    var collisionMessage by mutableStateOf("")
        private set

    val iconSizePx: Int = 300 // 角色圖示尺寸
    val serviceIconSizePx: Int = 300 // 服務圖示尺寸

    val serviceIconResources = listOf(
        R.drawable.service1,
        R.drawable.service2,
        R.drawable.service3
    )

    // --- 角色碰撞區域 ---
    val characters: List<Character> = listOf(
        Character(id = R.drawable.role0, name = "嬰幼兒"), // 左中
        Character(id = R.drawable.role1, name = "兒童"),    // 右中
        Character(id = R.drawable.role2, name = "成人"),    // 左下
        Character(id = R.drawable.role3, name = "一般民眾") // 右下
    )


    // --- 動態狀態 ---
    var currentServiceIcon by mutableStateOf(ServiceIconState(iconResourceId = serviceIconResources.random()))
        private set

    private var gameJob: Job? = null
    private val dropAmountPx: Int = 20

    private var isGameInitialized = false
    private var isResetting = false // 新增旗標，防止在 delay 期間重複觸發碰撞

    init {
        getScreenDimensions()
    }

    private fun getScreenDimensions() {
        val displayMetrics: DisplayMetrics = getApplication<Application>().resources.displayMetrics
        screenWidthPx = displayMetrics.widthPixels
        screenHeightPx = displayMetrics.heightPixels
        density = displayMetrics.density

        if (screenHeightPx > 0 && !isGameInitialized) {
            calculateCharacterPositions()
            startGameLoop()
            isGameInitialized = true
            Log.d("GameDebug", "遊戲已啟動，螢幕高: $screenHeightPx")
        }
    }

    private fun pxToDp(px: Int): Dp {
        return (px / if (density == 0f) 1f else density).dp
    }

    private fun calculateCharacterPositions() {
        if (screenHeightPx == 0) return

        val halfScreenHeight = screenHeightPx / 2

        characters[0].collisionRectPx.set(
            0f, (halfScreenHeight - iconSizePx).toFloat(), iconSizePx.toFloat(), halfScreenHeight.toFloat()
        )
        characters[1].collisionRectPx.set(
            (screenWidthPx - iconSizePx).toFloat(), (halfScreenHeight - iconSizePx).toFloat(), screenWidthPx.toFloat(), halfScreenHeight.toFloat()
        )
        characters[2].collisionRectPx.set(
            0f, (screenHeightPx - iconSizePx).toFloat(), iconSizePx.toFloat(), screenHeightPx.toFloat()
        )
        characters[3].collisionRectPx.set(
            (screenWidthPx - iconSizePx).toFloat(), (screenHeightPx - iconSizePx).toFloat(), screenWidthPx.toFloat(), screenHeightPx.toFloat()
        )
    }


    private fun resetServiceIcon() {
        isResetting = false // 重置完成後，允許再次碰撞
        val initialX = pxToDp(screenWidthPx / 2 - serviceIconSizePx / 2)

        currentServiceIcon = ServiceIconState(
            iconResourceId = serviceIconResources.random(),
            xOffsetDp = initialX,
            yOffsetDp = 0.dp
        )
        collisionMessage = ""
    }

    private fun handleCollision(characterName: String) {
        if (isResetting) return // 如果正在重置，忽略新的碰撞
        isResetting = true

        Log.d("GameDebug", "碰撞成功: $characterName")
        score += 10
        collisionMessage = "(碰撞${characterName}圖示)"

        // ** 延遲 1 秒後才重置圖示和訊息 **
        viewModelScope.launch {
            delay(1000)
            resetServiceIcon()
        }
    }

    private fun handleMiss() {
        if (isResetting) return // 如果正在重置，忽略新的掉落
        isResetting = true

        Log.d("GameDebug", "掉落底部")
        collisionMessage = "(掉到最下方)"

        // ** 延遲 1 秒後才重置圖示和訊息 **
        viewModelScope.launch {
            delay(1000)
            resetServiceIcon()
        }
    }

    private fun startGameLoop() {
        resetServiceIcon()

        gameJob?.cancel()
        gameJob = viewModelScope.launch {
            val dropAmountDp = pxToDp(dropAmountPx)

            val screenBottomYPx = screenHeightPx - serviceIconSizePx

            while (true) {
                delay(100)

                // 只有在非重置狀態下才執行下落和碰撞檢測
                if (!isResetting) {
                    val newYDp = currentServiceIcon.yOffsetDp + dropAmountDp
                    val newYPx = (newYDp.value * density).toInt()
                    val currentX = (currentServiceIcon.xOffsetDp.value * density).toInt()

                    // 1. 檢查是否碰撞角色
                    val serviceRect = RectF(
                        currentX.toFloat(),
                        newYPx.toFloat(),
                        (currentX + serviceIconSizePx).toFloat(),
                        (newYPx + serviceIconSizePx).toFloat()
                    )

                    var collidedCharacter: Character? = null
                    for (character in characters) {
                        if (RectF.intersects(serviceRect, character.collisionRectPx)) {
                            collidedCharacter = character
                            break
                        }
                    }

                    if (collidedCharacter != null) {
                        handleCollision(collidedCharacter.name)
                    }
                    // 2. 檢查是否碰撞下方邊界
                    else if (newYPx >= screenBottomYPx) {
                        handleMiss()
                    }
                    // 3. 繼續下落
                    else {
                        currentServiceIcon = currentServiceIcon.copy(
                            yOffsetDp = newYDp
                        )
                    }
                }
            }
        }
    }

    fun updateXOffset(dragAmount: Dp) {
        if (isResetting) return // 重置期間不能移動

        val newX = currentServiceIcon.xOffsetDp + dragAmount
        val screenWidthDp = pxToDp(screenWidthPx)
        val iconWidthDp = pxToDp(serviceIconSizePx)

        val minX = 0.dp
        val maxX = screenWidthDp - iconWidthDp

        currentServiceIcon = currentServiceIcon.copy(
            xOffsetDp = newX.coerceIn(minX, maxX)
        )
    }

    override fun onCleared() {
        super.onCleared()
        gameJob?.cancel()
    }
}