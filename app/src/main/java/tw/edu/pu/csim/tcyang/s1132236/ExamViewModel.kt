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
    val correctServiceId: Int,          // 正確對應的服務圖示 ID
    val correctServiceName: String,     // 正確對應的服務名稱
    var collisionRectPx: RectF = RectF()
)

// 服務圖示的狀態資料類
data class ServiceIconState(
    val iconResourceId: Int,
    val serviceName: String,            // 當前服務的名稱
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

    // ** UI 狀態：用於觸發 Toast 顯示 **
    var toastMessage by mutableStateOf<String?>(null)
        private set

    // collisionMessage 已不再用於顯示，但保留
    var collisionMessage by mutableStateOf("")
        private set

    val iconSizePx: Int = 300 // 角色圖示尺寸
    val serviceIconSizePx: Int = 300 // 服務圖示尺寸

    // ** 服務資源列表，包含資源 ID 和名稱 **
    // 假設您的資源 ID 依序為 R.drawable.service0, R.drawable.service1, ...
    val serviceIconDetails = listOf(
        Pair(R.drawable.service0, "極早期療育"), // service0
        Pair(R.drawable.service1, "離島服務"),    // service1
        Pair(R.drawable.service2, "極重多障"),    // service2
        Pair(R.drawable.service3, "輔具服務")     // service3
    )

    // --- 角色碰撞區域：修正對應關係 ---
    val characters: List<Character> = listOf(
        // role0 (嬰幼兒) -> service0 (極早期療育)
        Character(id = R.drawable.role0, name = "嬰幼兒", correctServiceId = R.drawable.service0, correctServiceName = "極早期療育"),
        // role1 (兒童) -> service1 (離島服務)
        Character(id = R.drawable.role1, name = "兒童", correctServiceId = R.drawable.service1, correctServiceName = "離島服務"),
        // role2 (成人) -> service2 (極重多障)
        Character(id = R.drawable.role2, name = "成人", correctServiceId = R.drawable.service2, correctServiceName = "極重多障"),
        // role3 (一般民眾) -> service3 (輔具服務)
        Character(id = R.drawable.role3, name = "一般民眾", correctServiceId = R.drawable.service3, correctServiceName = "輔具服務")
    )


    // --- 動態狀態 ---
    var currentServiceIcon by mutableStateOf(
        ServiceIconState(
            iconResourceId = serviceIconDetails.random().first,
            serviceName = serviceIconDetails.random().second
        )
    )
        private set

    private var gameJob: Job? = null
    private val dropAmountPx: Int = 20

    private var isGameInitialized = false
    private var isResetting = false // 暫停遊戲下落的旗標

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

    // 輔助函式：根據服務 ID 取得正確對應的角色名稱
    private fun getCorrectRoleName(serviceId: Int): String {
        return characters.find { it.correctServiceId == serviceId }?.name ?: "未知角色"
    }


    private fun resetServiceIcon() {
        isResetting = false // 重置完成後，允許再次下落
        val initialX = pxToDp(screenWidthPx / 2 - serviceIconSizePx / 2)

        val randomService = serviceIconDetails.random()

        currentServiceIcon = ServiceIconState(
            iconResourceId = randomService.first,
            serviceName = randomService.second,
            xOffsetDp = initialX,
            yOffsetDp = 0.dp
        )
        collisionMessage = ""
        toastMessage = null // 清空 Toast 狀態
    }

    private fun handleCollision(collidedCharacter: Character) {
        if (isResetting) return
        isResetting = true

        val currentServiceId = currentServiceIcon.iconResourceId
        val currentServiceName = currentServiceIcon.serviceName

        val isCorrect = currentServiceId == collidedCharacter.correctServiceId

        // 1. 執行分數判斷
        if (isCorrect) {
            score += 1
            Log.d("GameDebug", "碰撞結果: 正確，分數 +1")
        } else {
            score -= 1
            Log.d("GameDebug", "碰撞結果: 錯誤，分數 -1")
        }

        // 2. 準備 Toast 訊息 (無論對錯，都顯示正確的對應關係)
        val correctRoleName = getCorrectRoleName(currentServiceId)
        val resultText = "${currentServiceName}，屬於 ${correctRoleName} 方面的服務"

        toastMessage = resultText

        // ** 暫停 3 秒，然後重置遊戲 (下一題) **
        viewModelScope.launch {
            delay(3000)
            resetServiceIcon()
        }
    }

    private fun handleMiss() {
        if (isResetting) return
        isResetting = true

        val currentServiceId = currentServiceIcon.iconResourceId
        val currentServiceName = currentServiceIcon.serviceName
        val correctRoleName = getCorrectRoleName(currentServiceId)

        // 掉落底部時，仍顯示該服務的正確答案
        val missText = "${currentServiceName}，屬於 ${correctRoleName} 方面的服務"
        Log.d("GameDebug", "掉落底部，不計分。")

        toastMessage = missText

        // ** 暫停 3 秒，然後重置遊戲 (下一題) **
        viewModelScope.launch {
            delay(3000)
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
                        handleCollision(collidedCharacter)
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