package tw.edu.pu.csim.tcyang.s1132236

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.widget.Toast

// ExamScreen.kt
@Composable
fun ExamScreen(
    examViewModel: ExamViewModel = viewModel()
) {
    val density = LocalDensity.current
    val context = LocalContext.current // 獲取 Context 以便顯示 Toast

    // 從 ViewModel 獲取資料
    val screenWidthPx = examViewModel.screenWidthPx
    val screenHeightPx = examViewModel.screenHeightPx
    val author = examViewModel.authorInfo
    val currentScore = examViewModel.score

    // 尺寸轉換
    val iconSizeDp: Dp = with(density) { examViewModel.iconSizePx.toDp() }
    val serviceIconSizeDp: Dp = with(density) { examViewModel.serviceIconSizePx.toDp() }
    val halfScreenHeightDp: Dp = with(density) { (screenHeightPx / 2).toDp() }

    // ** 實作 Toast 彈出訊息 **
    val toastMessage = examViewModel.toastMessage
    LaunchedEffect(toastMessage) {
        if (toastMessage != null) {
            Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show() // 使用 LENGTH_LONG 確保訊息可見
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF000)) // 黃色背景
    ) {
        // --- 1. 中央主要內容區 (Column) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // 使用合理的邊距
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // 圖片
            Image(
                painter = painterResource(id = R.drawable.happy),
                contentDescription = "考試圖片",
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(30.dp))

            // 標題
            Text(text = "瑪利亞基金會服務大考驗", fontSize = 20.sp, color = Color.Black)

            // 作者資訊
            Text(text = author, fontSize = 18.sp, color = Color.Black)

            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "螢幕大小 : ${screenWidthPx} * ${screenHeightPx} px", fontSize = 18.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(10.dp))

            // 第四行文字 (只顯示分數)
            Text(
                text = "成績 : ${currentScore}分", // ** 移除碰撞訊息文字 **
                fontSize = 18.sp,
                color = Color.Black
            )
            // 增加底部邊距，確保文字區域整體向上抬升，避開底部的角色圖示
            Spacer(modifier = Modifier.height(iconSizeDp))
        }

        // --- 2. 服務圖示 (會動的圖示) ---
        Image(
            painter = painterResource(id = examViewModel.currentServiceIcon.iconResourceId),
            contentDescription = "下落的服務圖示",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(serviceIconSizeDp)
                .offset(x = examViewModel.currentServiceIcon.xOffsetDp, y = examViewModel.currentServiceIcon.yOffsetDp)
                .pointerInput(Unit) {
                    detectDragGestures { change, dragAmount ->
                        change.consume()
                        examViewModel.updateXOffset(dragAmount.x.toDp())
                    }
                }
        )

        // --- 3. 角色圖示佈局 ---
        // 嬰幼兒 (左中)
        CharacterIcon(
            resourceId = R.drawable.role0, size = iconSizeDp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 0.dp, y = halfScreenHeightDp - iconSizeDp)
        )
        // 兒童 (右中)
        CharacterIcon(
            resourceId = R.drawable.role1, size = iconSizeDp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 0.dp, y = halfScreenHeightDp - iconSizeDp)
        )
        // 成人 (左下)
        CharacterIcon(resourceId = R.drawable.role2, size = iconSizeDp, modifier = Modifier.align(Alignment.BottomStart))
        // 一般民眾 (右下)
        CharacterIcon(resourceId = R.drawable.role3, size = iconSizeDp, modifier = Modifier.align(Alignment.BottomEnd))
    }
}

// 輔助 Composable 函式 (角色圖示)
@Composable
fun CharacterIcon(resourceId: Int, size: Dp, modifier: Modifier) {
    Image(
        painter = painterResource(id = resourceId),
        contentDescription = null,
        modifier = modifier.size(size),
        contentScale = ContentScale.Fit
    )
}