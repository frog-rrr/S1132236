package tw.edu.pu.csim.tcyang.s1132236

import android.os.Bundle
import android.view.Window
import android.view.WindowInsetsController
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.tooling.preview.Preview
import tw.edu.pu.csim.tcyang.s1132236.ui.theme.S1132236Theme

// 主 Activity 類別
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // 呼叫函式來隱藏系統列
            HideSystemBars(window)

            S1132236Theme {
                // 將應用程式的主要內容設定為 ExamScreen
                ExamScreen()
            }
        }
    }
}

/**
 * 隱藏狀態列 (Status Bar) 及導覽列 (Navigation Bar)，實現沉浸式全螢幕模式。
 */
@Composable
fun HideSystemBars(window: Window) {
    SideEffect {
        // 使用現代的 WindowInsetsController 隱藏系統列 (API 30+)
        @Suppress("DEPRECATION")
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            window.setDecorFitsSystemWindows(false)
            val controller = window.insetsController
            controller?.hide(
                android.view.WindowInsets.Type.statusBars() or
                        android.view.WindowInsets.Type.navigationBars()
            )
            // 設定沉浸式模式
            controller?.systemBarsBehavior = WindowInsetsController.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        } else {
            // 對於 API 30 以下的版本 (Android 10 或更舊)
            // 使用 systemUiVisibility 屬性來控制全螢幕
            val fullScreenFlags = (
                    View.SYSTEM_UI_FLAG_FULLSCREEN or
                            View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or
                            View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    )
            window.decorView.systemUiVisibility = fullScreenFlags
        }
    }
}

// 預覽 Composable 函式
@Preview(showBackground = true)
@Composable
fun ExamScreenPreview() {
    S1132236Theme {
        ExamScreen()
    }
}