package tw.edu.pu.csim.tcyang.s1132236

import android.app.Application
import android.util.DisplayMetrics
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density

// ExamViewModel.kt
class ExamViewModel(application: Application) : AndroidViewModel(application) {

    // 螢幕寬度 (像素)
    var screenWidthPx by mutableStateOf(0)
        private set

    // 螢幕高度 (像素)
    var screenHeightPx by mutableStateOf(0)
        private set

    // 螢幕密度 (Density)
    var density by mutableStateOf(1f)
        private set

    // 作者資訊
    var authorInfo by mutableStateOf("作者 : 資訊工程系 S1132236 楊子青")
        private set

    // 分數
    var score by mutableStateOf(0)
        private set

    // 角色圖示寬度/高度固定為 300px
    val iconSizePx: Int = 300

    init {
        getScreenDimensions()
    }

    // 讀取螢幕尺寸和密度
    private fun getScreenDimensions() {
        val displayMetrics: DisplayMetrics = getApplication<Application>().resources.displayMetrics
        screenWidthPx = displayMetrics.widthPixels
        screenHeightPx = displayMetrics.heightPixels
        density = displayMetrics.density
    }
}