package com.example.aichatapp.screens

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DisplayMode
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.aichatapp.ChatViewModel
import com.example.aichatapp.R
import com.example.aichatapp.data.ChatUiEvent
import com.example.aichatapp.ui.theme.Purple40
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(navController: NavHostController) {
    val uriState = MutableStateFlow("")

    val imagePicker = rememberLauncherForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri: Uri? ->
        uri?.let {
            uriState.update { it }
        }
    }
    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Purple40
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(text = stringResource(id = R.string.app_name)) },
                    colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        titleContentColor = MaterialTheme.colorScheme.onPrimary
                    ),
                    navigationIcon = {
                        if (navController.previousBackStackEntry != null) {
                            IconButton(onClick = { navController.navigateUp() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = "Back",
                                    tint = Color.White
                                )
                            }
                        }
                    }
                )
            }
        ) { paddingValues ->
            ChatScreen(
                paddingValues = paddingValues,
                uriState = uriState,
                imagePicker = imagePicker
            )
        }
    }
}

@Composable
fun TypingDots() {
    val infiniteTransition = rememberInfiniteTransition()
    val delay = listOf(0, 150, 300)
    Row(
        modifier = Modifier
            .padding(16.dp)
            .wrapContentWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        delay.forEachIndexed { index, delayMillis ->
            val scale by infiniteTransition.animateFloat(
                initialValue = 0.5f,
                targetValue = 1.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(600, delayMillis, easing = LinearOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .scale(scale)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
            )
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ChatScreen(
    paddingValues: PaddingValues,
    uriState: MutableStateFlow<String>,
    imagePicker: ActivityResultLauncher<PickVisualMediaRequest>,
    showImageUploadOption: Boolean = false
) {
    val chatViewModel = viewModel<ChatViewModel>()
    val chatState = chatViewModel.chatState.collectAsState().value
    var amount by remember { mutableStateOf("") }
    var selectedDateMillis by remember { mutableStateOf<Long?>(null) }
    val bitmap = getBitmap(uriState = uriState)
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
    ) {
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            reverseLayout = true
        ) {
            itemsIndexed(chatState.chatList) { _, chat ->
                if (chat.isFromUser) {
                    UserChatItem(chat.prompt, chat.bitmap)
                } else {
                    Column {
                        when (chat.type) {
                            "action" -> {
                                ChatActionSelector(onConfirm = { action ->
                                    chatViewModel.onEvent(ChatUiEvent.SendPrompt(action, bitmap))
                                })
                            }

                            "amount" -> {
                                AmountInputField(
                                    amount = amount,
                                    onAmountChange = { amount = it },
                                    onAmountSubmitted = { enteredAmount ->
                                        chatViewModel.onEvent(
                                            ChatUiEvent.SendPrompt(
                                                enteredAmount,
                                                bitmap
                                            )
                                        )
                                    }
                                )
                            }

                            "date" -> {
                                DateInputField(
                                    selectedDateMillis = selectedDateMillis,
                                    onDateChange = { selectedDateMillis = it },
                                    onDateSubmitted = { selectedDate ->
                                        chatViewModel.onEvent(
                                            ChatUiEvent.SendPrompt(
                                                selectedDate,
                                                bitmap
                                            )
                                        )
                                    }
                                )
                            }

                            "transfer" -> {
                                TransferItem(amount, selectedDateMillis)
                            }

                            "recipient" -> {
                                RecipientSelector(onConfirm = { recipient ->
                                    chatViewModel.onEvent(ChatUiEvent.SendPrompt(recipient, bitmap))
                                })
                            }

                            else -> {
                                ModelChatItem(chat.prompt)
                            }
                        }
                    }
                }
            }
        }

        if (chatViewModel.isLoading.collectAsState().value) {
            TypingDots()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (showImageUploadOption) {
                Column {
                    bitmap?.let {
                        Image(
                            bitmap = it.asImageBitmap(),
                            contentScale = ContentScale.Crop,
                            contentDescription = "picked image",
                            modifier = Modifier
                                .size(40.dp)
                                .padding(bottom = 2.dp)
                                .clip(RoundedCornerShape(12.dp))
                        )
                    }
                    Icon(
                        modifier = Modifier
                            .size(40.dp)
                            .clickable {
                                imagePicker.launch(
                                    PickVisualMediaRequest
                                        .Builder()
                                        .setMediaType(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                        .build()
                                )
                            },
                        imageVector = Icons.Rounded.AddPhotoAlternate,
                        contentDescription = "Add photo",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            TextField(
                modifier = Modifier.weight(1f),
                value = chatState.prompt,
                onValueChange = {
                    chatViewModel.onEvent(ChatUiEvent.UpdatePrompt(it))
                },
                placeholder = { Text("Type a message") },
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Icon(
                modifier = Modifier
                    .size(40.dp)
                    .clickable {
                        chatViewModel.onEvent(ChatUiEvent.SendPrompt(chatState.prompt, bitmap))
                        uriState.update { "" }
                    },
                imageVector = Icons.Rounded.Send,
                contentDescription = "Send message",
                tint = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@Composable
fun getBitmap(uriState: MutableStateFlow<String>): Bitmap? {
    val uri = uriState.collectAsState().value
    val imageState: AsyncImagePainter.State = rememberAsyncImagePainter(
        model = ImageRequest.Builder(LocalContext.current).data(uri).size(Size.ORIGINAL).build()
    ).state
    return if (imageState is AsyncImagePainter.State.Success) {
        imageState.result.drawable.toBitmap()
    } else {
        null
    }
}

@Composable
fun UserChatItem(prompt: String, bitmap: Bitmap?) {
    Column(
        modifier = Modifier
            .padding(start = 100.dp, bottom = 22.dp)
    ) {
        bitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentScale = ContentScale.Crop,
                contentDescription = "image",
                modifier = Modifier
                    .height(260.dp)
                    .fillMaxWidth()
                    .padding(bottom = 2.dp)
                    .clip(RoundedCornerShape(12.dp))
            )
        }
        Text(
            text = prompt,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onPrimary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.primary)
                .padding(16.dp)
        )
    }
}

@Composable
fun ModelChatItem(prompt: String) {
    Column(
        modifier = Modifier
            .padding(end = 100.dp, bottom = 22.dp)
    ) {
        Text(
            text = prompt,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(16.dp)
        )
    }
}

@Composable
fun ChatActionSelector(
    defaultSelected: String = "transfer",
    onConfirm: (String) -> Unit
) {
    var selectedAction by remember { mutableStateOf(defaultSelected) }
    var confirmed by remember { mutableStateOf(false) }

    if (!confirmed) {
        Column(
            modifier = Modifier
                .padding(end = 100.dp, bottom = 22.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(16.dp)
        ) {
            Text(
                text = "Which type of transaction would you like to make?",
                color = MaterialTheme.colorScheme.onTertiary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            val options = listOf(
                "transfer" to "Transfer",
                "bill_pay" to "Bill Pay",
                "zelle" to "Zelle",
                "wire_transfer" to "Wire Transfer"
            )

            options.forEach { (value, label) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (selectedAction == value)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.surface
                        )
                        .clickable { selectedAction = value }
                        .padding(12.dp)
                ) {
                    RadioButton(
                        selected = selectedAction == value,
                        onClick = { selectedAction = value },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = label,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }

            Button(
                onClick = {
                    confirmed = true
                    onConfirm(selectedAction)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Confirm", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}

@Composable
fun RecipientSelector(
    onConfirm: (String) -> Unit
) {
    val recipients = listOf(
        "Sam" to "•••7801",
        "Tim" to "•••8012"
    )

    var selectedRecipient by remember { mutableStateOf(recipients.first().first) }
    var confirmed by remember { mutableStateOf(false) }

    if (!confirmed) {
        Column(
            modifier = Modifier
                .padding(end = 100.dp, bottom = 22.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(16.dp)
        ) {
            Text(
                text = "Who would you like to send money to?",
                color = MaterialTheme.colorScheme.onTertiary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            recipients.forEach { (name, account) ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            if (selectedRecipient == name)
                                MaterialTheme.colorScheme.onPrimary
                            else
                                MaterialTheme.colorScheme.surface
                        )
                        .clickable { selectedRecipient = name }
                        .padding(12.dp)
                ) {
                    RadioButton(
                        selected = selectedRecipient == name,
                        onClick = { selectedRecipient = name },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = MaterialTheme.colorScheme.primary
                        )
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = name.replaceFirstChar { it.uppercase() },
                            fontSize = 15.sp,
                            color = MaterialTheme.colorScheme.onSurface,
                            fontWeight = FontWeight.Medium
                        )
                        Text(
                            text = "Acct: $account",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                }
            }

            Button(
                onClick = {
                    confirmed = true
                    onConfirm(selectedRecipient)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                Text(text = "Confirm", fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            }
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TransferItem(amount: String, selectedDateMillis: Long?) {
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
fun AmountInputField(
    amount: String,
    onAmountChange: (String) -> Unit,
    onAmountSubmitted: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val keyboardController = LocalSoftwareKeyboardController.current

    Column(
        modifier = modifier
            .padding(end = 100.dp, bottom = 22.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.tertiary)
    ) {
        Text(
            text = "Please enter amount:",
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp, top = 5.dp)
        )

        TextField(
            modifier = Modifier
                .padding(bottom = 16.dp, start = 16.dp, end = 16.dp)
                .clip(RoundedCornerShape(12.dp)),
            value = amount,
            onValueChange = {
                if (it.length <= 6 && it.all { char -> char.isDigit() }) {
                    onAmountChange(it)
                }
            },
            visualTransformation = CurrencyAmountInputVisualTransformation(
                fixedCursorAtTheEnd = true
            ),
            keyboardOptions = KeyboardOptions.Default.copy(
                keyboardType = KeyboardType.Number,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = {
                    val numericAmount = amount.toDoubleOrNull()?.div(100)
                    numericAmount?.let {
                        onAmountSubmitted(String.format(Locale.US, "%.2f", it))
                    }
                    keyboardController?.hide()
                }
            ),
            singleLine = true
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateInputField(
    selectedDateMillis: Long?,
    onDateChange: (Long?) -> Unit,
    onDateSubmitted: (String) -> Unit,
    @SuppressLint("ModifierParameter") modifier: Modifier = Modifier
) {
    val state = rememberDatePickerState(
        initialSelectedDateMillis = selectedDateMillis,
        initialDisplayMode = DisplayMode.Input
    )

    // Sync selected date with parent state
    LaunchedEffect(state.selectedDateMillis) {
        onDateChange(state.selectedDateMillis)
    }

    Column(
        modifier = modifier
            .padding(end = 100.dp, bottom = 22.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.tertiary)
    ) {
        Text(
            text = "Please select a date:",
            color = Color.White,
            modifier = Modifier.padding(start = 16.dp, top = 5.dp)
        )

        DatePicker(
            state = state,
            modifier = Modifier
                .padding(16.dp)
                .clip(RoundedCornerShape(12.dp)),
            showModeToggle = false
        )

        Button(
            onClick = {
                val formattedDate = selectedDateMillis?.let { millis ->
                    Instant.ofEpochMilli(millis)
                        .atZone(ZoneId.systemDefault())
                        .toLocalDate()
                        .format(DateTimeFormatter.ofPattern("MM/dd/yyyy"))
                }
                if (formattedDate != null) {
                    onDateSubmitted(formattedDate)
                }
            },
            modifier = Modifier
                .align(Alignment.End)
                .padding(end = 16.dp, bottom = 16.dp)
        ) {
            Text(text = "Ok")
        }
    }
}