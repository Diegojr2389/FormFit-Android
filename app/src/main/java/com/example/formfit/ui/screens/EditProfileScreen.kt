package com.example.formfit.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.example.formfit.R
import com.example.formfit.datastore.UserDataStore
import com.example.formfit.models.UserData
import com.example.formfit.viewmodel.EditProfileViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun EditProfileScreen(
    navController: NavController,
    viewModel: EditProfileViewModel = viewModel(
        factory = EditProfileViewModel.Factory(LocalContext.current)
    )
) {
    // Tracks the press interaction state of the button (e.g. pressed, released)
    val interactionSource = remember { MutableInteractionSource() }

    // Returns true when the button is currently being pressed, false otherwise
    val isPressed by interactionSource.collectIsPressedAsState()

    val scrollState = rememberScrollState()

    val user by UserDataStore
        .getUser(LocalContext.current)
        .collectAsState(
            initial = UserData(
                null,
                null,
                null,
                null
            )
        )

    var username by remember(user.username) { mutableStateOf(user.username ?: "")}
    var usernameError by remember { mutableStateOf<String?>(null) }

    var email by remember(user.email) { mutableStateOf(user.email ?: "")}
    var emailError by remember { mutableStateOf<String?>(null) }

    val profilePictureUrl = user.profilePictureUrl

    var currentPassword by remember { mutableStateOf("") }
    var currentPasswordVisible by remember { mutableStateOf(false) }

    var newPassword by remember { mutableStateOf("") }
    var newPasswordError by remember { mutableStateOf<String?>(null)}
    var newPasswordVisible by remember { mutableStateOf(false) }

    var retypeNewPassword by remember { mutableStateOf("") }
    var retypeNewPasswordError by remember { mutableStateOf<String?>(null)}
    var retypeNewPasswordVisible by remember { mutableStateOf(false) }

    val passwordRegex = Regex("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)[A-Za-z\\d@\$!%*?&]{6,}$")
    val emailRegex = Regex("^[a-zA-Z0-9_.+-]+@[a-zA-Z0-9-]+\\.[a-zA-Z0-9-.]+$")

    val saveChangesIsEnabled =
        usernameError.isNullOrBlank() &&
        emailError.isNullOrBlank() &&
        if (currentPassword.isNotBlank()) {
            !newPassword.isBlank() &&
            !retypeNewPassword.isBlank() &&
            newPasswordError.isNullOrBlank() &&
            retypeNewPasswordError.isNullOrBlank()
        } else true


    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            viewModel.uploadProfilePicture(uri)
        }
    }

    val focusManager = LocalFocusManager.current

    val coroutineScope = rememberCoroutineScope()

    val imeVisible = WindowInsets.ime.getBottom(LocalDensity.current) > 0

    LaunchedEffect(imeVisible) {
        if (!imeVisible) {
            focusManager.clearFocus()
        }
    }

    Column (
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.onBackground)
            .verticalScroll(scrollState)
            .imePadding(),
        horizontalAlignment = Alignment.CenterHorizontally

    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
        ) {
            IconButton(
                onClick = { navController.popBackStack()},
                interactionSource = interactionSource,
                modifier = Modifier
                    .size(64.dp)
                    .background(
                        color = if (isPressed) Color(0xFF3C475A) else Color.Transparent,
                        shape = CircleShape)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_back_arrow),
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier
                        .size(30.dp)
                )
            }

            Text(
                text = "Edit Profile",
                modifier = Modifier
                    .align(Alignment.Center),
                color = Color.White,
                fontSize = 18.sp,
            )
        }

        Box(
            modifier = Modifier.size(110.dp)
        ) {
            AsyncImage(
                model = profilePictureUrl ?: R.drawable.ic_blank_profile_picture,
                contentDescription = "Profile Picture",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
            )
            Box(
                modifier = Modifier
                    .offset(x = (-8).dp, y = (-8).dp)
                    .align(Alignment.BottomEnd)
                    .size(24.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary)
                    .clickable{
                        imagePicker.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                        )
                    }
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_edit),
                    contentDescription = "Edit Profile Picture",
                    tint = Color.White,
                    modifier = Modifier
                        .size(16.dp)
                        .align(Alignment.Center)
                )
            }
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            TextField(
                value = username,
                onValueChange = {
                    username = it

                    usernameError = if (username.length < 3) {
                        "Username must be at least 3 characters"
                    } else if (username.length > 20) {
                        "Username must be at most 20 characters"
                    } else null
                },
                // Controls what the action button on the keyboard looks like
                keyboardOptions = KeyboardOptions(
                    // Shows a "Next" or arrow button instead of the default "Done" or return key
                    imeAction = ImeAction.Next
                ),
                // Defines what happens when that action button is pressed
                keyboardActions = KeyboardActions(
                    // Moves the focus to the next field below in the layout
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                label = { Text("Username") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = if (!usernameError.isNullOrBlank()) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (!usernameError.isNullOrBlank()) Color.Red else Color.White,
                    focusedLabelColor = if (!usernameError.isNullOrBlank()) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = if (!usernameError.isNullOrBlank()) Color.Red else Color.White,
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (!usernameError.isNullOrBlank()) {
                Text(
                    text = usernameError!!,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            TextField(
                value = email,
                onValueChange = {
                    email = it
                    emailError = if (!emailRegex.matches(it)) {
                        "Please enter a valid email address"
                    } else null
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                label = { Text("Email") },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    focusedIndicatorColor = if (!emailError.isNullOrBlank()) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (!emailError.isNullOrBlank()) Color.Red else Color.White,
                    focusedLabelColor = if (!emailError.isNullOrBlank()) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = if (!emailError.isNullOrBlank()) Color.Red else Color.White,
                ),
                modifier = Modifier.fillMaxWidth()
            )

            if (!emailError.isNullOrBlank()) {
                Text(
                    text = emailError!!,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            TextField(
                value = currentPassword,
                onValueChange = { currentPassword = it },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                label = { Text("Current password") },
                visualTransformation = if (currentPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { currentPasswordVisible = !currentPasswordVisible }) {
                        Icon(
                            imageVector = if (currentPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (currentPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                ),
                modifier = Modifier.fillMaxWidth()
            )


            TextField(
                value = newPassword,
                onValueChange = {
                    newPassword = it
                    newPasswordError = if (!passwordRegex.matches(it)) {
                        "Password must be 6+ characters and must contain an uppercase letter, lowercase letter and a number"
                    } else null
                },
                keyboardOptions = KeyboardOptions(
                    imeAction = ImeAction.Next
                ),
                keyboardActions = KeyboardActions(
                    onNext = { focusManager.moveFocus(FocusDirection.Down) }
                ),
                label = { Text("New password") },
                visualTransformation = if (newPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { newPasswordVisible = !newPasswordVisible }) {
                        Icon(
                            imageVector = if (newPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (newPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = MaterialTheme.colorScheme.onBackground,
                    focusedIndicatorColor = if (!newPasswordError.isNullOrBlank()) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (!newPasswordError.isNullOrBlank()) Color.Red else Color.White,
                    focusedLabelColor = if (!newPasswordError.isNullOrBlank()) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = if (!newPasswordError.isNullOrBlank()) Color.Red else Color.White,
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .onFocusChanged {
                        if (it.isFocused) {
                            coroutineScope.launch {
                                // if keyboard not open, increase delay to wait for keyboard
                                if (!imeVisible) {
                                    delay(400)
                                }
                                // scroll to the bottom of the screen
                                scrollState.animateScrollTo(
                                    value = scrollState.maxValue,
                                    animationSpec = tween(durationMillis = 500)
                                )
                            }
                        }
                    },
                enabled = !currentPassword.isEmpty()
            )

            if (!newPasswordError.isNullOrBlank()) {
                Text(
                    text = newPasswordError!!,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }

            TextField(
                value = retypeNewPassword,
                onValueChange = {
                    retypeNewPassword = it
                    retypeNewPasswordError = if (newPassword != it) {
                        "New password does not match"
                    } else null
                },
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                // Dismisses keyboard and removes focus from all fields when "Done" is pressed
                keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                label = { Text("Re-type new password") },
                visualTransformation = if (retypeNewPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    IconButton(onClick = { retypeNewPasswordVisible = !retypeNewPasswordVisible }) {
                        Icon(
                            imageVector = if (retypeNewPasswordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                            contentDescription = if (retypeNewPasswordVisible) "Hide password" else "Show password"
                        )
                    }
                },
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Color.Transparent,
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = MaterialTheme.colorScheme.onBackground,
                    focusedIndicatorColor = if (!retypeNewPasswordError.isNullOrBlank()) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = if (!retypeNewPasswordError.isNullOrBlank()) Color.Red else Color.White,
                    focusedLabelColor = if (!retypeNewPasswordError.isNullOrBlank()) Color.Red else MaterialTheme.colorScheme.primary,
                    unfocusedLabelColor = if (!retypeNewPasswordError.isNullOrBlank()) Color.Red else Color.White,
                ),
                modifier = Modifier.fillMaxWidth(),
                enabled = !currentPassword.isEmpty()
            )

            if (!retypeNewPasswordError.isNullOrBlank()) {
                Text(
                    text = retypeNewPasswordError!!,
                    color = Color.Red,
                    fontSize = 12.sp
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        if (!viewModel.updateProfileErrorMessage.isNullOrBlank()) {
            Text(
                text = viewModel.updateProfileErrorMessage!!,
                color = Color.Red,
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
        else if (!viewModel.updateProfileSuccessMessage.isNullOrBlank()) {
            currentPassword = ""
            newPassword = ""
            retypeNewPassword = ""

            Text(
                text = viewModel.updateProfileSuccessMessage!!,
                color = Color.Green,
                fontSize = 12.sp
            )
        }

        Button(
            onClick = {
                viewModel.updateProfile(
                    username,
                    email,
                    currentPassword.ifBlank { null },
                    newPassword.ifBlank { null })
            },
            enabled = (saveChangesIsEnabled),
            border = if (saveChangesIsEnabled) null else BorderStroke(1.dp, Color.Gray),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                disabledContainerColor = MaterialTheme.colorScheme.onBackground,
            )
        ) {
            Text(
                text = "Save Changes",
                color = Color.White
            )
        }
    }
}