package com.example.formfit.ui.screens
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.formfit.R
import com.example.formfit.datastore.UserDataStore
import com.example.formfit.models.UserData
import com.example.formfit.viewmodel.ProfileViewModel

@Composable
fun ProfileScreen(
    onLoggedOut: () -> Unit,
    navController : NavController,
    viewModel : ProfileViewModel = viewModel(
        factory = ProfileViewModel.Factory(LocalContext.current)
    )
) {
    val loggedOut by viewModel.loggedOut.collectAsState()
    val user by UserDataStore
        .getUser(LocalContext.current)
        .collectAsState(
            initial = UserData(
                null,
                null,
                null,
                null))

    val username = user.username

    val profilePictureUrl = user.profilePictureUrl

    val email = user.email

    val issues = viewModel.issues

    val scrollState = rememberScrollState()

    LaunchedEffect(loggedOut) {
        if (loggedOut) onLoggedOut()
    }

    LaunchedEffect(Unit) {
        viewModel.getIssues()
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ){
        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = "My Profile",
            modifier = Modifier
                .fillMaxWidth(),
            color = Color.White,
            fontSize = 18.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(30.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            AsyncImage(
                model = profilePictureUrl ?: R.drawable.ic_blank_profile_picture,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape),
            )

            Spacer(modifier = Modifier.width(20.dp))

            Column {
                Text(
                    text = username ?: "",
                    color = Color.White,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = email ?: "",
                    color = Color.Gray,
                    fontSize = 14.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 5.dp)
        ) {
            Text(
                text = "Flagged Issues",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (issues.isEmpty()) {
                Row {
                    Icon(
                        painter = painterResource(R.drawable.ic_error),
                        contentDescription = "Issue Bullet Point",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                    )

                    Text(
                        text = "None",
                        color = Color.White
                    )
                }
            }
            else {
                issues.forEach { issue ->
                    Row {
                        Icon(
                            painter = painterResource(R.drawable.ic_error),
                            contentDescription = "Issue Bullet Point",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .padding(horizontal = 10.dp)
                        )

                        Text(
                            text = issue.description,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(5.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(
                text = "My Account",
                color = Color.Gray,
                fontSize = 14.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Edit Profile",
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .clickable {
                        navController.navigate("edit_profile")
                    }
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Log Out",
                color = Color.Red,
                modifier = Modifier
                    .clickable {
                        viewModel.logout()
                    }
            )
        }
    }
}