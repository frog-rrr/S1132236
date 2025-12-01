package tw.edu.pu.csim.tcyang.s1132236

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel

// ExamScreen.kt
@Composable
fun ExamScreen(
    // 透過 viewModel() 獲取 ExamViewModel 實例
    examViewModel: ExamViewModel = viewModel()
) {
    // 從 ViewModel 獲取資料
    val screenWidth = examViewModel.screenWidthPx
    val screenHeight = examViewModel.screenHeightPx
    val author = examViewModel.authorInfo
    val currentScore = examViewModel.score

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFF000)) // 黃色背景 (使用 RGB 十六進制值)
            .padding(16.dp), // 整個 Column 的外邊距
        verticalArrangement = Arrangement.Center, // 垂直置中
        horizontalAlignment = Alignment.CenterHorizontally // 水平置中
    ) {
        // 圖片
        Image(
            painter = painterResource(id = R.drawable.happy), // 請確保您有 logo.png 在 drawable
            contentDescription = "考試圖片",
            modifier = Modifier,
            contentScale = ContentScale.Fit // 適應內容
        )
        // 第一行文字
        Text(
            text = "瑪利亞基金會服務大考驗",
            fontSize = 20.sp,
            color = Color.Black
        )
        // 第二行文字 (作者資訊)
        Text(
            text = author,
            fontSize = 18.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp)) // 間距高度 10dp

        // 第三行文字 (螢幕尺寸)
        Text(
            text = "螢幕大小 : ${screenWidth} * ${screenHeight} px",
            fontSize = 18.sp,
            color = Color.Black
        )

        Spacer(modifier = Modifier.height(10.dp)) // 間距高度 10dp

        // 第四行文字 (分數)
        Text(
            text = "成績 : ${currentScore}分",
            fontSize = 18.sp,
            color = Color.Black
        )
    }
}