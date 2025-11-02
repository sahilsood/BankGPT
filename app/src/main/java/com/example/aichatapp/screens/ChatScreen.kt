package com.example.aichatapp.screens

import PayRequestOptions
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.aichatapp.ACTION
import com.example.aichatapp.ChatViewModel
import com.example.aichatapp.R
import com.example.aichatapp.TRANSFER_AMOUNT
import com.example.aichatapp.TRANSFER_CONFIRM
import com.example.aichatapp.TRANSFER_DATE
import com.example.aichatapp.TRANSFER_RECIPIENT
import com.example.aichatapp.ZELLE_ACTION
import com.example.aichatapp.ZELLE_CONFIRM
import com.example.aichatapp.ZELLE_RECIPIENT
import com.example.aichatapp.data.ChatUiEvent
import com.example.aichatapp.screens.components.ChatActionSelector
import com.example.aichatapp.screens.components.TypingDots
import com.example.aichatapp.screens.components.transfer.AmountInputField
import com.example.aichatapp.screens.components.transfer.DateInputField
import com.example.aichatapp.screens.components.transfer.RecipientSelector
import com.example.aichatapp.screens.components.transfer.TransferConfirmItem
import com.example.aichatapp.screens.components.zelle.ZelleContactList
import com.example.aichatapp.ui.theme.Purple40
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

private const val TAG = "BankGPT"

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
            LaunchedEffect(Unit) {
                Log.d(TAG, "[SYSTEM] Chat session started. Awaiting user input...")
            }
            ChatScreen(
                paddingValues = paddingValues,
                uriState = uriState,
                imagePicker = imagePicker
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
    val bitmap = getBitmap(uriState = uriState)

    // NEW: Set to track which message IDs have already been logged
    val loggedMessageIds = remember { mutableStateOf(setOf<java.util.UUID>()) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues)
            .imePadding()
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
                    UserChatItem(prompt = chat.prompt, bitmap = chat.bitmap)
                } else {
                    // NEW: Manually check if the message has been logged.
                    // This is the most reliable method and does not affect UI order.
                    if (chat.id !in loggedMessageIds.value) {
                        LaunchedEffect(Unit) {
                            // Then log the action the AI is taking
                            when (chat.type) {
                                ACTION -> {
                                    Log.d(
                                        TAG,
                                        "[Bank GPT] Intent detected. Prompting for action confirmation: \n ${chat.prompt}"
                                    )
                                }

                                TRANSFER_AMOUNT -> {
                                    Log.d(
                                        TAG,
                                        "[Bank GPT] Requesting parameter: TRANSFER_AMOUNT: \n ${chat.prompt}"
                                    )
                                }

                                TRANSFER_DATE -> {
                                    Log.d(
                                        TAG,
                                        "[Bank GPT] Requesting parameter: TRANSFER_DATE: \n ${chat.prompt}"
                                    )
                                }

                                TRANSFER_RECIPIENT -> {
                                    Log.d(
                                        TAG,
                                        "[Bank GPT] Requesting parameter: TRANSFER_RECIPIENT: \n ${chat.prompt}"
                                    )
                                }

                                TRANSFER_CONFIRM, ZELLE_CONFIRM -> {
                                    Log.d(
                                        TAG,
                                        "[Bank GPT] All parameters collected. Prompting for final confirmation: \n ${chat.prompt}"
                                    )
                                }

                                ZELLE_ACTION -> {
                                    Log.d(
                                        TAG,
                                        "[Bank GPT] Requesting parameter: ZELLE_ACTION (Send/Request): \n ${chat.prompt}"
                                    )
                                }

                                ZELLE_RECIPIENT -> {
                                    Log.d(
                                        TAG,
                                        "[Bank GPT] Requesting parameter: ZELLE_RECIPIENT: \n ${chat.prompt}"
                                    )
                                }

                                else -> {
                                    Log.d(
                                        TAG,
                                        "[Bank GPT] Responded with the message: \n ${chat.prompt}"
                                    )
                                }
                            }
                            // "Remember" that this message has been logged
                            loggedMessageIds.value = loggedMessageIds.value + chat.id
                        }
                    }

                    Column {
                        when (chat.type) {
                            ACTION -> {
                                ChatActionSelector(
                                    message = chat.message, onConfirm = { action ->
                                        Log.v(TAG, ".")
                                        Log.v(TAG, "..")
                                        Log.v(TAG, "...")
                                        Log.i(TAG, "[Sahil] Confirmed action: '$action'")
                                        chatViewModel.onEvent(
                                            event = ChatUiEvent.SendPrompt(
                                                prompt = action,
                                                bitmap = bitmap
                                            )
                                        )
                                    },
                                    defaultSelected = chat.selected,
                                    logoPainter = painterResource(R.drawable.bank_logo1)
                                )
                            }

                            TRANSFER_AMOUNT -> {
                                AmountInputField(
                                    message = chat.message,
                                    amount = amount,
                                    onAmountChange = { amount = it },
                                    onAmountSubmitted = { enteredAmount ->
                                        Log.v(TAG, ".")
                                        Log.v(TAG, "..")
                                        Log.v(TAG, "...")
                                        Log.i(TAG, "[Sahil] Provided amount: '$enteredAmount'")
                                        chatViewModel.onEvent(
                                            event = ChatUiEvent.SendPrompt(
                                                prompt = enteredAmount,
                                                bitmap = bitmap
                                            )
                                        )
                                    },
                                    logoPainter = painterResource(R.drawable.bank_logo1)
                                )
                            }

                            TRANSFER_DATE -> {
                                DateInputField(
                                    message = chat.message,
                                    onDateSubmitted = { selectedDate ->
                                        Log.v(TAG, ".")
                                        Log.v(TAG, "..")
                                        Log.v(TAG, "...")
                                        Log.i(TAG, "[Sahil] Provided date: '$selectedDate'")
                                        chatViewModel.onEvent(
                                            event = ChatUiEvent.SendPrompt(
                                                prompt = selectedDate,
                                                bitmap = bitmap
                                            )
                                        )
                                    },
                                    logoPainter = painterResource(R.drawable.bank_logo1)
                                )
                            }

                            TRANSFER_CONFIRM, ZELLE_CONFIRM -> {
                                TransferConfirmItem(
                                    amount = chat.amount,
                                    recipient = chat.recipient,
                                    date = chat.date,
                                    message = chat.message,
                                    logoPainter = painterResource(R.drawable.bank_logo1),
                                ) {
                                    Log.v(TAG, ".")
                                    Log.v(TAG, "..")
                                    Log.v(TAG, "...")
                                    Log.i(TAG, "[Sahil] Final confirmation: '$it'")
                                    chatViewModel.onEvent(
                                        event = ChatUiEvent.SendPrompt(prompt = it, bitmap = bitmap)
                                    )
                                }
                            }

                            TRANSFER_RECIPIENT -> {
                                RecipientSelector(
                                    message = chat.message, onConfirm = { recipient ->
                                        Log.v(TAG, ".")
                                        Log.v(TAG, "..")
                                        Log.v(TAG, "...")
                                        Log.i(TAG, "[Sahil] Selected recipient: '$recipient'")
                                        chatViewModel.onEvent(
                                            event = ChatUiEvent.SendPrompt(
                                                prompt = recipient,
                                                bitmap = bitmap
                                            )
                                        )
                                    },
                                    logoPainter = painterResource(R.drawable.bank_logo1)
                                )
                            }

                            ZELLE_ACTION -> {
                                PayRequestOptions(
                                    message = chat.message,
                                    payIcon = painterResource(id = R.drawable.send),
                                    requestIcon = painterResource(id = R.drawable.recieve),
                                    logoPainter = painterResource(R.drawable.bank_logo1)
                                ) {
                                    Log.v(TAG, ".")
                                    Log.v(TAG, "..")
                                    Log.v(TAG, "...")
                                    Log.i(TAG, "[Sahil] Selected Zelle action: '$it'")
                                    chatViewModel.onEvent(
                                        event = ChatUiEvent.SendPrompt(prompt = it, bitmap = bitmap)
                                    )
                                }
                            }

                            ZELLE_RECIPIENT -> {
                                ZelleContactList(
                                    message = chat.message,
                                    logoPainter = painterResource(R.drawable.bank_logo1)
                                ) {
                                    Log.v(TAG, ".")
                                    Log.v(TAG, "..")
                                    Log.v(TAG, "...")
                                    Log.i(TAG, "[Sahil] Selected Zelle contact: '$it'")
                                    chatViewModel.onEvent(
                                        event = ChatUiEvent.SendPrompt(prompt = it, bitmap = bitmap)
                                    )
                                }
                            }

                            else -> {
                                ModelChatItem(
                                    message = chat.message.ifEmpty { chat.prompt },
                                    logoPainter = painterResource(R.drawable.bank_logo1)
                                )
                            }
                        }
                    }
                }
            }
        }

        val isLoading by chatViewModel.isLoading.collectAsState()
        if (isLoading) {
            LaunchedEffect(Unit) {
                var dots = ""
                while (true) {
                    dots = if (dots.length < 3) "$dots." else "."
                    Log.v(TAG, dots)
                    delay(500)
                }
            }
            TypingDots()
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, start = 8.dp, end = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // ... (Image picker code remains the same)

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
                        Log.v(TAG, ".")
                        Log.v(TAG, "..")
                        Log.v(TAG, "...")
                        Log.i(TAG, "[Sahil] Sent prompt: \"${chatState.prompt}\"")
                        chatViewModel.onEvent(ChatUiEvent.SendPrompt(chatState.prompt, bitmap))
                        uriState.update { "" }
                    },
                imageVector = Icons.AutoMirrored.Rounded.Send,
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
fun ModelChatItem(
    message: String,
    logoPainter: Painter
) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 100.dp, bottom = 22.dp)
    ) {
        Text(
            text = message,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onTertiary,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.tertiary)
                .padding(16.dp)
        )
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