package com.example.aichatapp.screens

import PayRequestOptions
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.absoluteOffset
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.compose.material.icons.rounded.Send
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.aichatapp.TRANSFER_AMOUNT
import com.example.aichatapp.ChatViewModel
import com.example.aichatapp.R
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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

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
                    Column {
                        when (chat.type) {
                            ACTION -> {
                                ChatActionSelector(
                                    message = chat.message, onConfirm = { action ->
                                        chatViewModel.onEvent(
                                            event = ChatUiEvent.SendPrompt(
                                                prompt = action,
                                                bitmap = bitmap
                                            )
                                        )
                                    },
                                    defaultSelected = chat.selected,
                                    logoPainter = painterResource(R.drawable.bank_logo)
                                )
                            }

                            TRANSFER_AMOUNT -> {
                                AmountInputField(
                                    message = chat.message,
                                    amount = amount,
                                    onAmountChange = { amount = it },
                                    onAmountSubmitted = { enteredAmount ->
                                        chatViewModel.onEvent(
                                            event = ChatUiEvent.SendPrompt(
                                                prompt = enteredAmount,
                                                bitmap = bitmap
                                            )
                                        )
                                    },
                                    logoPainter = painterResource(R.drawable.bank_logo)
                                )
                            }

                            TRANSFER_DATE -> {
                                DateInputField(
                                    message = chat.message,
                                    onDateSubmitted = { selectedDate ->
                                        chatViewModel.onEvent(
                                            event = ChatUiEvent.SendPrompt(
                                                prompt = selectedDate,
                                                bitmap = bitmap
                                            )
                                        )
                                    },
                                    logoPainter = painterResource(R.drawable.bank_logo)
                                )
                            }

                            TRANSFER_CONFIRM, ZELLE_CONFIRM -> {
                                TransferConfirmItem(
                                    amount = chat.amount,
                                    recipient = chat.recipient,
                                    date = chat.date,
                                    message = chat.message,
                                    logoPainter = painterResource(R.drawable.bank_logo),
                                ) {
                                    chatViewModel.onEvent(
                                        event = ChatUiEvent.SendPrompt(
                                            prompt = it,
                                            bitmap = bitmap
                                        )
                                    )
                                }
                            }

                            TRANSFER_RECIPIENT -> {
                                RecipientSelector(
                                    message = chat.message, onConfirm = { recipient ->
                                        chatViewModel.onEvent(
                                            event = ChatUiEvent.SendPrompt(
                                                prompt = recipient,
                                                bitmap = bitmap
                                            )
                                        )
                                    },
                                    logoPainter = painterResource(R.drawable.bank_logo)
                                )
                            }

                            ZELLE_ACTION -> {
                                PayRequestOptions(
                                    message = chat.message,
                                    payIcon = painterResource(id = R.drawable.send),
                                    requestIcon = painterResource(id = R.drawable.recieve),
                                    logoPainter = painterResource(R.drawable.bank_logo)
                                ) {
                                    chatViewModel.onEvent(
                                        event = ChatUiEvent.SendPrompt(
                                            prompt = it,
                                            bitmap = bitmap
                                        )
                                    )
                                }
                            }

                            ZELLE_RECIPIENT -> {
                                ZelleContactList(
                                    message = chat.message,
                                    logoPainter = painterResource(R.drawable.bank_logo)
                                ) {
                                    chatViewModel.onEvent(
                                        event = ChatUiEvent.SendPrompt(
                                            prompt = it,
                                            bitmap = bitmap
                                        )
                                    )
                                }
                            }

                            else -> {
                                ModelChatItem(
                                    prompt = chat.prompt,
                                    logoPainter = painterResource(R.drawable.bank_logo)
                                )
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
    prompt: String,
    logoPainter: Painter
) {
    Box(
        modifier = Modifier
            .padding(start = 16.dp, end = 100.dp, bottom = 22.dp)
    ) {
        // Chat Bubble
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

        // Floating Logo - Top Left Corner (partially outside bubble)
        Image(
            painter = logoPainter,
            contentDescription = "Bank Logo",
            modifier = Modifier
                .size(36.dp)
                .absoluteOffset(x = (-20).dp, y = (-10).dp)
                .clip(CircleShape)
                .background(Color.White) // optional, matches screenshot
                .border(2.dp, Color.White, CircleShape)
                .align(Alignment.TopStart)
        )
    }
}