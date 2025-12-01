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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// ExamScreen.kt
@Composable
fun ExamScreen(
    examViewModel: ExamViewModel = viewModel()
) {
    val density = LocalDensity.current

    // 從 ViewModel 獲取資料
    val screenWidthPx = examViewModel.screenWidthPx
    val screenHeightPx = examViewModel.screenHeightPx
    val author = examViewModel.authorInfo
    val currentScore = examViewModel.score
    val iconSizePx = examViewModel.iconSizePx
    val serviceIconState = examViewModel.currentServiceIcon

    // 計算尺寸
    val iconSizeDp: Dp = with(density) { iconSizePx.toDp() }
    val halfScreenHeightDp: Dp = with(density) { (screenHeightPx / 2).toDp() }

    // 服務圖示尺寸 (這裡為了示範，使用了較小的尺寸，您可以根據需求修改 ExamViewModel 中的 serviceIconSizePx)
    val serviceIconSizeDp: Dp = with(density) { examViewModel.serviceIconSizePx.toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF000)) // 黃色背景
    ) {
        // --- 1. 中央主要內容區 (Column) ---
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // ... (中央文字和圖片保持不變) ...
            Image(
                painter = painterResource(id = R.drawable.happy),
                contentDescription = "考試圖片",
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(30.dp))
            Text(text = "瑪利亞基金會服務大考驗", fontSize = 20.sp, color = Color.Black)
            Text(text = author, fontSize = 18.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "螢幕大小 : ${screenWidthPx} * ${screenHeightPx} px", fontSize = 18.sp, color = Color.Black)
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = "成績 : ${currentScore}分", fontSize = 18.sp, color = Color.Black)
        }

        // --- 2. 服務圖示 (會動的圖示) ---
        Image(
            painter = painterResource(id = serviceIconState.iconResourceId),
            contentDescription = "下落的服務圖示",
            contentScale = ContentScale.Fit,
            modifier = Modifier
                .size(serviceIconSizeDp) // 固定尺寸
                .offset(x = serviceIconState.xOffsetDp, y = serviceIconState.yOffsetDp) // 應用動態位置
                .pointerInput(Unit) {
                    // 偵測拖曳手勢
                    detectDragGestures { change, dragAmount ->
                        change.consume() // 消耗事件，防止事件傳遞給其他元素
                        // 將拖曳距離 (px) 轉換為 Dp，然後更新 ViewModel
                        examViewModel.updateXOffset(dragAmount.x.toDp())
                    }
                }
        )

        // --- 3. 角色圖示佈局 (保持不變) ---
        // 嬰幼兒
        CharacterIcon(
            resourceId = R.drawable.role0, size = iconSizeDp,
            modifier = Modifier
                .align(Alignment.TopStart)
                .offset(x = 0.dp, y = halfScreenHeightDp - iconSizeDp)
        )
        // 兒童
        CharacterIcon(
            resourceId = R.drawable.role1, size = iconSizeDp,
            modifier = Modifier
                .align(Alignment.TopEnd)
                .offset(x = 0.dp, y = halfScreenHeightDp - iconSizeDp)
        )
        // 成人
        CharacterIcon(resourceId = R.drawable.role2, size = iconSizeDp, modifier = Modifier.align(Alignment.BottomStart))
        // 一般民眾
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