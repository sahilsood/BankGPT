package com.example.aichatapp.screens.components.transfer

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransferConfirmItem(amount: String, selectedDateMillis: Long?) {
    val rawAmount = amount // e.g., "1121"
    val formattedAmount = rawAmount.toDoubleOrNull()?.div(100)
        ?.let { String.format(Locale.US, "%.2f", it) } ?: "0.00"
    val formattedDate = selectedDateMillis?.let {
        Instant.ofEpochMilli(it)
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
            .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
    } ?: ""
    Column(
        modifier = Modifier
            .wrapContentSize()
            .padding(16.dp)
            .clip(RoundedCornerShape(16.dp)) // Rounded corners
            .background(MaterialTheme.colorScheme.tertiary)
            .padding(16.dp), // Inner padding after background
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "\uD83C\uDF89 $${formattedAmount}\uD83C\uDF89",
            fontSize = 48.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        AccountSelector("From", "Truist Savings", "$3500", "•7801")
        AccountSelector("To", "Sahil Checking", "$3500", "•8012")
        Spacer(modifier = Modifier.height(8.dp))
        AccountSelector("Date", formattedDate, "", "")

        Button(
            onClick = { /*TODO: Handle Next*/ },
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = Color.Black
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            Text(text = "Confirm", fontSize = 25.sp)
        }
    }
}

@Composable
fun AccountSelector(label: String, accountName: String, balance: String, accountNumber: String) {
    Row {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp)
                .clickable { /*TODO: Handle Account Selection*/ },
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "$label:",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp)
            )
            Text(
                text = "$accountName   $balance",
                fontSize = 20.sp,
                color = Color.White,
                modifier = Modifier.padding(end = 16.dp)
            )
        }
    }
}