package tw.edu.pu.csim.tcyang.s1132236

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
    // 獲取當前的螢幕密度
    val density = LocalDensity.current

    // 從 ViewModel 獲取資料
    val screenWidthPx = examViewModel.screenWidthPx
    val screenHeightPx = examViewModel.screenHeightPx
    val author = examViewModel.authorInfo
    val currentScore = examViewModel.score
    val iconSizePx = examViewModel.iconSizePx

    // 1. 將 300px 轉換為 Dp 單位
    val iconSizeDp: Dp = with(density) { iconSizePx.toDp() }

    // 2. 計算螢幕高度的 1/2，並轉換為 Dp 單位
    val halfScreenHeightDp: Dp = with(density) { (screenHeightPx / 2).toDp() }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF000)) // 黃色背景
    ) {
        // --- 1. 中央主要內容區 (Column) ---
        // 使用 .align(Alignment.Center) 確保它仍在 Box 中置中
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.Center, // 垂直置中
            horizontalAlignment = Alignment.CenterHorizontally // 水平置中
        ) {
            // 圖片
            Image(
                painter = painterResource(id = R.drawable.happy),
                contentDescription = "考試圖片",
                contentScale = ContentScale.Fit
            )

            Spacer(modifier = Modifier.height(30.dp))

            // 第一行文字
            Text(
                text = "瑪利亞基金會服務大考驗",
                fontSize = 20.sp,
                color = Color.Black
            )

            // 移除 Spacer (不再有間隔)

            // 第二行文字 (作者資訊)
            Text(
                text = author,
                fontSize = 18.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 第三行文字 (螢幕尺寸)
            Text(
                text = "螢幕大小 : ${screenWidthPx} * ${screenHeightPx} px",
                fontSize = 18.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(10.dp))

            // 第四行文字 (分數)
            Text(
                text = "成績 : ${currentScore}分",
                fontSize = 18.sp,
                color = Color.Black
            )
        }

        // --- 2. 角色圖示佈局 ---

        // ** (A) 嬰幼兒 (Baby) - 左邊切齊, 下方切齊 1/2 高度 **
        CharacterIcon(
            resourceId = R.drawable.role0,
            size = iconSizeDp,
            modifier = Modifier
                .align(Alignment.TopStart) // 初始定位在左上角
                .offset(
                    x = 0.dp, // 切齊左邊
                    y = halfScreenHeightDp - iconSizeDp // Y 軸定位: (螢幕高度/2) - 圖示尺寸
                )
        )

        // ** (B) 兒童 (Child) - 右邊切齊, 下方切齊 1/2 高度 **
        CharacterIcon(
            resourceId = R.drawable.role1,
            size = iconSizeDp,
            modifier = Modifier
                .align(Alignment.TopEnd) // 初始定位在右上角
                .offset(
                    x = 0.dp, // 切齊右邊
                    y = halfScreenHeightDp - iconSizeDp // Y 軸定位: (螢幕高度/2) - 圖示尺寸
                )
        )

        // ** (C) 成人 (Adult) - 左邊切齊, 下方切齊螢幕底部 **
        CharacterIcon(
            resourceId = R.drawable.role2,
            size = iconSizeDp,
            // 靠左下對齊，其底部已切齊 Box 的底部
            modifier = Modifier.align(Alignment.BottomStart)
        )

        // ** (D) 一般民眾 (Citizen) - 右邊切齊, 下方切齊螢幕底部 **
        CharacterIcon(
            resourceId = R.drawable.role3,
            size = iconSizeDp,
            // 靠右下對齊，其底部已切齊 Box 的底部
            modifier = Modifier.align(Alignment.BottomEnd)
        )
    }
}

// 輔助 Composable 函式，用於減少重複程式碼，並設定固定尺寸
@Composable
fun CharacterIcon(resourceId: Int, size: Dp, modifier: Modifier) {
    Image(
        painter = painterResource(id = resourceId),
        contentDescription = null, // 不需要描述
        modifier = modifier
            .size(size), // 寬高皆為 iconSizeDp (精確的 300px)
        contentScale = ContentScale.Fit
    )
}