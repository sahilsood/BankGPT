package com.example.aichatapp.screens


import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.example.aichatapp.R
import com.example.aichatapp.ui.theme.Purple40
import com.example.aichatapp.ui.theme.Purple80
import com.example.aichatapp.ui.theme.PurpleGrey80

@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
fun HomeScreen(navController: NavHostController? = null) {
    var selectedIndex by remember { mutableIntStateOf(0) }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                ),
                actions = {
                    IconButton(onClick = { /* TODO: Handle messages */ }) {
                        Icon(
                            imageVector = Icons.Default.Email, // or use painterResource(id = R.drawable.ic_message)
                            contentDescription = "Messages",
                            tint = Color.White
                        )
                    }
                    IconButton(onClick = { /* TODO: Handle profile */ }) {
                        Icon(
                            imageVector = Icons.Default.AccountCircle, // or your own drawable
                            contentDescription = "Profile",
                            tint = Color.White
                        )
                    }
                },
                navigationIcon = {
                    navController?.previousBackStackEntry?.let {
                        IconButton(onClick = { navController.navigateUp() }) {
                            Icon(
                                imageVector = Icons.Filled.ArrowBack,
                                contentDescription = "Back"
                            )
                        }
                    }
                }
            )
        }, bottomBar = {
            NavigationBar(
                containerColor = Purple40,
                tonalElevation = 4.dp
            ) {
                NavigationBarItem(
                    selected = selectedIndex == 0,
                    onClick = { selectedIndex = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleGrey80,
                        unselectedIconColor = Color.White,
                        selectedTextColor = PurpleGrey80,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.Transparent // no background highlight
                    )
                )
                NavigationBarItem(
                    selected = selectedIndex == 1,
                    onClick = { selectedIndex = 1 },
                    icon = { Icon(Icons.Default.Email, contentDescription = "Messages") },
                    label = { Text("Messages") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleGrey80,
                        unselectedIconColor = Color.White,
                        selectedTextColor = PurpleGrey80,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.Transparent // no background highlight
                    )
                )
                NavigationBarItem(
                    selected = selectedIndex == 2,
                    onClick = { selectedIndex = 2 },
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleGrey80,
                        unselectedIconColor = Color.White,
                        selectedTextColor = PurpleGrey80,
                        unselectedTextColor = Color.White,
                        indicatorColor = Color.Transparent // no background highlight
                    )
                )
            }
        }
    ) { paddingValues ->
        HomeScreen1(paddingValues = paddingValues, navController)
    }
}


@Composable
fun HomeScreen1(
    paddingValues: PaddingValues,
    navController: NavHostController?
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(PurpleGrey80)
            .padding(paddingValues)
            .padding(16.dp)
    ) {
        // Top Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Image Placeholder
            ProfileImage(imageUrl = "https://sahilsood.com/img/sahilnew.png")
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        style = SpanStyle(
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = Color.Black
                        )
                    ) {
                        append("Welcome back \n")
                    }

                    withStyle(style = SpanStyle(fontWeight = FontWeight.Bold, fontSize = 18.sp)) {
                        append("Sahil Sood")
                    }
                }, textAlign = TextAlign.Start,
                modifier = Modifier
                    .padding(start = 18.dp, end = 18.dp)
                    .weight(1f)
            )
            IconButton(onClick = { /* TODO: Search action */ }) {
                Icon(
                    painterResource(id = android.R.drawable.ic_menu_search),
                    contentDescription = "Search"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            "Dashboard",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        // Balance Card
        Card(
            colors = CardDefaults.cardColors(containerColor = Purple40),
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Your Balance",
                    color = Color.White,
                    fontSize = 14.sp,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "$12,435.00",
                    fontSize = 32.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Account Number\n*** **** 3424", color = Color.White, fontSize = 12.sp)
                    Text("Expired Date\n25/12/2029", color = Color.White, fontSize = 12.sp)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Buttons
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(4.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Button(
                onClick = { /* Send action */ },
                colors = ButtonDefaults.buttonColors(containerColor = Purple40),
                modifier = Modifier.weight(1f)
            ) {
                Text("Send")
            }
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = { /* Request action */ },
                colors = ButtonDefaults.buttonColors(containerColor = Purple40),
                modifier = Modifier.weight(1f)
            ) {
                Text("Request")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Overview Section
        Text(
            "Overview",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )

        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            OverviewCard("Income", "$4,092.00", Purple40, modifier = Modifier.weight(1f))
            OverviewCard("Spending", "$1,254.00", Purple40, modifier = Modifier.weight(1f))
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Transfer Section
        Text(
            "Quick Links",
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
        )
        Column(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Button(
                onClick = { navController?.navigate(route = ChatScreen) },
                colors = ButtonDefaults.buttonColors(containerColor = Purple40),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.padding(4.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    ProfileImage(imageUrl = "https://img.icons8.com/?size=100&id=kTuxVYRKeKEY&format=png&color=000000")
                    Spacer(modifier = Modifier.width(8.dp)) // Space between icon and text
                    Text("Transfer with Generative AI")
                }
            }
        }
        Card(
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Purple40),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            shape = RoundedCornerShape(12.dp)
        ) {
            Row(
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(
                    painter = rememberAsyncImagePainter("https://www.middlesexfederal.com/image%20library/content-images/retail%20banking/fdic%20logo/fdic-logo.png"),
                    contentDescription = "FDIC Logo",
                    modifier = Modifier
                        .size(40.dp)
                        .padding(end = 12.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = "This bank is FDIC insured",
                    style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                    textAlign = TextAlign.Start
                )
            }
        }
    }
}

@Composable
fun ProfileImage(imageUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Profile Image",
        modifier = Modifier
            .size(48.dp)
            .clip(CircleShape),
        contentScale = ContentScale.Crop,
    )
}

@Composable
fun OverviewCard(
    title: String,
    amount: String,
    backgroundColor: Color,
    modifier: Modifier
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        modifier = modifier
            .padding(4.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, fontSize = 14.sp, color = Color.White, fontWeight = FontWeight.Bold)
            Text(amount, fontSize = 20.sp, color = Color.White, fontWeight = FontWeight.Bold)
        }
    }
}
