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
import kotlin.random.Random

// ExamViewModel.kt

// 服務圖示的狀態資料類
data class ServiceIconState(
    // 圖示 ID 現在會是 service1, service2, 或 service3
    val iconResourceId: Int,
    var xOffsetDp: Dp = 0.dp, // 水平位置 (Dp)
    var yOffsetDp: Dp = 0.dp  // 垂直位置 (Dp)
)

class ExamViewModel(application: Application) : AndroidViewModel(application) {

    // --- 靜態資訊 ---
    var screenWidthPx by mutableStateOf(0)
        private set
    var screenHeightPx by mutableStateOf(0)
        private set
    var density by mutableStateOf(1f)
        private set
    // ... (其他作者、分數、iconSizePx 資訊保持不變) ...
    var authorInfo by mutableStateOf("作者 : 資訊工程系 S1132236 楊子青")
    var score by mutableStateOf(0)
    val iconSizePx: Int = 300 // 角色圖示尺寸

    // 服務圖示資源列表 (現在有三個 ID，將從中隨機選擇)
    // ⚠️ 請確認您的 drawable 資料夾中有 service1.png, service2.png, service3.png
    val serviceIconResources = listOf(
        R.drawable.service1,
        R.drawable.service2,
        R.drawable.service3
    )

    // 服務圖示的固定尺寸 (用於碰撞計算，這裡假設為 100px 寬高)
    val serviceIconSizePx: Int = 300

    // --- 動態狀態 ---
    // 初始化時，隨機選一個圖示
    var currentServiceIcon by mutableStateOf(ServiceIconState(iconResourceId = serviceIconResources.random()))
        private set

    private var gameJob: Job? = null

    // 每 0.1 秒移動的像素值 (20px)
    private val dropAmountPx: Int = 20


    init {
        getScreenDimensions()
        startGameLoop()
    }

    private fun getScreenDimensions() {
        val displayMetrics: DisplayMetrics = getApplication<Application>().resources.displayMetrics
        screenWidthPx = displayMetrics.widthPixels
        screenHeightPx = displayMetrics.heightPixels
        density = displayMetrics.density
    }

    // 將像素值轉換為 Dp (用於 Compose 佈局)
    private fun pxToDp(px: Int): Dp {
        // 確保 density 在初始化前不會是 0
        return (px / if (density == 0f) 1f else density).dp
    }

    // 隨機產生一個新的圖示，回到螢幕上方中心
    private fun resetServiceIcon() {
        // X 軸定位: 螢幕寬度 / 2 - 圖示寬度 / 2
        val initialX = pxToDp(screenWidthPx / 2 - serviceIconSizePx / 2)

        currentServiceIcon = ServiceIconState(
            iconResourceId = serviceIconResources.random(), // *** 隨機選擇圖示 ***
            xOffsetDp = initialX,
            yOffsetDp = 0.dp // 螢幕最上方
        )
    }

    // 遊戲主循環：處理圖示的自動下落
    private fun startGameLoop() {
        resetServiceIcon()

        gameJob?.cancel()
        gameJob = viewModelScope.launch {
            val dropAmountDp = pxToDp(dropAmountPx)

            // 計算圖示底部觸發重置的 Y 座標
            val screenBottomDp = pxToDp(screenHeightPx)
            val iconBottomLineY = screenBottomDp - pxToDp(serviceIconSizePx)

            while (true) {
                delay(100) // 每 0.1 秒執行一次

                // 檢查是否碰撞底部
                val newY = currentServiceIcon.yOffsetDp + dropAmountDp

                // 碰撞檢測：如果圖示的頂部 Y 座標超過了圖示底線 Y 座標，則重置
                if (newY >= iconBottomLineY) {
                    // 碰撞螢幕下方，重置圖示
                    resetServiceIcon()
                } else {
                    // 繼續下落
                    currentServiceIcon = currentServiceIcon.copy(
                        yOffsetDp = newY
                    )
                }
            }
        }
    }

    // 處理水平拖曳，更新 X 軸位置
    fun updateXOffset(dragAmount: Dp) {
        val newX = currentServiceIcon.xOffsetDp + dragAmount
        val screenWidthDp = pxToDp(screenWidthPx)
        val iconWidthDp = pxToDp(serviceIconSizePx)

        // 限制 X 軸位置在螢幕範圍內
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