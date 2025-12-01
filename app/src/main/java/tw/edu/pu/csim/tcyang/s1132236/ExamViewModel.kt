package tw.edu.pu.csim.tcyang.s1132236

import android.app.Application
import android.util.DisplayMetrics
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel

// ExamViewModel.kt
class ExamViewModel(application: Application) : AndroidViewModel(application) {

    // 螢幕寬度 (像素)
    var screenWidthPx by mutableStateOf(0)
        private set // 只能在 ViewModel 內部修改

    // 螢幕高度 (像素)
    var screenHeightPx by mutableStateOf(0)
        private set // 只能在 ViewModel 內部修改

    // 作者資訊，請修改成您的系級與姓名
    var authorInfo by mutableStateOf("作者 : 資訊二A 李念恩")
        private set

    // 分數
    var score by mutableStateOf(0)
        private set

    // 在 ViewModel 初始化時讀取螢幕尺寸
    init {
        getScreenDimensions()
    }

    // 讀取螢幕尺寸的方法
    private fun getScreenDimensions() {
        val displayMetrics: DisplayMetrics = getApplication<Application>().resources.displayMetrics
        screenWidthPx = displayMetrics.widthPixels
        screenHeightPx = displayMetrics.heightPixels
    }

    // 更新分數的方法 (如果未來需要)
    fun updateScore(newScore: Int) {
        score = newScore
    }
}