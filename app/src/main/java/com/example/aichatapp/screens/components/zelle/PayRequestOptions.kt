import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import com.example.aichatapp.R

@Preview
@Composable
fun PayRequestOptions(
    message: String = "Select an option:",
    logoPainter: Painter = painterResource(id = R.drawable.ic_launcher_foreground),
    payIcon: Painter = painterResource(id = R.drawable.ic_launcher_foreground),
    requestIcon: Painter = painterResource(id = R.drawable.ic_launcher_foreground),
    onOptionSelected: (String) -> Unit = {}
) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 100.dp, bottom = 22.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = message,
                color = MaterialTheme.colorScheme.onTertiary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOptionSelected("Send") },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = payIcon,
                        contentDescription = "Pay Icon",
                        modifier = Modifier.size(48.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Send",
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .height(56.dp)
                        .background(Color(0xFFE0E0E0))
                )

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onOptionSelected("Request") },
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = requestIcon,
                        contentDescription = "Request Icon",
                        modifier = Modifier.size(48.dp),
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiary)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Request",
                        color = MaterialTheme.colorScheme.onTertiary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }

        Image(
            painter = logoPainter,
            contentDescription = "Bank Logo",
            modifier = Modifier
                .size(36.dp)
                .absoluteOffset(x = (-20).dp, y = (-10).dp)
                .clip(CircleShape)
                .background(Color.White)
                .border(2.dp, Color.White, CircleShape)
                .align(Alignment.TopStart)
        )
    }
}