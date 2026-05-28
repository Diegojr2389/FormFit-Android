package com.example.formfit.ui.screens

import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.net.toUri
import com.example.formfit.R
import com.example.formfit.datastore.UserDataStore
import com.example.formfit.models.UserData
import com.example.formfit.ui.theme.OswaldFontFamily

@Composable
fun HomeScreen() {
    val context = LocalContext.current

    val user by UserDataStore
        .getUser(context)
        .collectAsState(
            initial = UserData(
                null,
                null,
                null,
                null))

    val username = user.username ?: ""

    val scrollState = rememberScrollState()

    val exerciseTabSteps = listOf(
        "Go to the Exercises tab and select an exercise",
        "Read about the exercise and tap \"Check Your Form\" to open the camera",
        "Set up your camera so your whole body is visible with good lighting",
        "Make sure your volume is turned up",
        "Get into the starting position for the exercise",
        "Say \"Start\" out loud — FormFit will begin analyzing your form immediately",
    )

    Column (
        modifier = Modifier
            .fillMaxSize()
            .padding(15.dp)
            .background(MaterialTheme.colorScheme.onBackground)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text="Welcome, $username",
            fontSize = 28.sp,
            color = Color.White,
            fontFamily = OswaldFontFamily
        )

        Spacer(modifier = Modifier.height(10.dp))

        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(12.dp)
                )
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.formfit_logo),
                    contentDescription = "Formfit Logo",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(15.dp))
                        .border(2.dp, Color.White, RoundedCornerShape(15.dp))
                )

                Spacer(modifier = Modifier.width(30.dp))

                Column {
                    Text(
                        text = "FormFit",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = OswaldFontFamily
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW,
                                "https://diegojr2389.github.io/index.html".toUri())
                            context.startActivity(intent)
                        },
                        shape = RoundedCornerShape(8.dp),
                        border = BorderStroke(1.dp, Color.White),
                    ) {
                        Text(
                            text = "Learn More",
                            color = Color.White,
                            fontFamily = OswaldFontFamily
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        Text(
            text = "Features",
            color = Color.White,
            fontSize = 28.sp,
            fontFamily = OswaldFontFamily
        )

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(12.dp)
                )
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Image(
                    painter = painterResource(R.drawable.ic_dumbbell),
                    contentDescription = "Dumbbell Icon",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(CircleShape)
                        .border(2.dp, Color.White, CircleShape)
                )

                Spacer(modifier = Modifier.width(30.dp))

                Column {
                    Text(
                        text = "Real-Time Form Feedback",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = OswaldFontFamily
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "AI-Powered",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontFamily = OswaldFontFamily
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Using machine learning and pose detection technology, FormFit " +
                        "analyzes your body movements in real time and provides instant audio feedback " +
                        "on your form — helping you work out more effectively and reduce the risk of injury.",
                fontFamily = OswaldFontFamily,
                color = Color.White,
                modifier = Modifier.padding(bottom = 5.dp)

            )

            Row {
                Icon (
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "Bullet Point",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append("AI-powered pose detection ")
                        }
                        append("— advanced machine learning analyzes your movements as you exercise")
                    },
                    fontFamily = OswaldFontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

            Row {
                Icon (
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "Bullet Point",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append("Instant audio feedback ")
                        }
                        append("— feedback is spoken out loud by your phone so you never have to look at " +
                                "the screen mid-workout")
                    },
                    fontFamily = OswaldFontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

            Row {
                Icon (
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "Bullet Point",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append("Multiple exercises ")
                        }
                        append("— supports a variety of exercises, each with information on primary " +
                                "muscles, secondary muscles, and a short description")
                    },
                    fontFamily = OswaldFontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

            Row {
                Icon (
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "Bullet Point",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append("More exercises coming soon ")
                        }
                        append("— we are constantly working on adding more exercises to the app")
                    },
                    fontFamily = OswaldFontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }

            Text(
                text = "How to get started:",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = OswaldFontFamily,
                fontSize = 20.sp,
                modifier = Modifier.padding(bottom = 5.dp)
            )

            for ((i, step) in exerciseTabSteps.withIndex()) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append("${i+1}. ")
                        }
                        append(step)
                    },
                    fontFamily = OswaldFontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

            Text(
                text = "Important: Always make sure to keep your back straight throughout your " +
                                "workout. Back posture analysis is not yet supported but will be coming in " +
                                "a future update.",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = OswaldFontFamily,
                modifier = Modifier.padding(bottom = 5.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Column(
            modifier = Modifier
                .background(
                    color = MaterialTheme.colorScheme.tertiary,
                    shape = RoundedCornerShape(12.dp)
                )
                .fillMaxWidth()
                .padding(20.dp)
        ) {
            Row(
                modifier = Modifier.padding(bottom = 10.dp)
            ) {
                Box(
                    modifier = Modifier.size(80.dp)
                ) {
                    Image(
                        painter = painterResource(R.drawable.ic_forma),
                        contentDescription = "Formfit Logo",
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                    )
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.onBackground)
                            .border(1.dp, Color.White, CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.ic_chat),
                            contentDescription = "Edit Profile Picture",
                            tint = Color.White,
                            modifier = Modifier
                                .size(16.dp)
                                .align(Alignment.Center)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(30.dp))

                Column {
                    Text(
                        text = "Forma",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontFamily = OswaldFontFamily
                    )

                    Text(
                        text = "Chatbot",
                        color = Color.Gray,
                        fontSize = 14.sp,
                        fontFamily = OswaldFontFamily
                    )
                }
            }

            HorizontalDivider(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 5.dp),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "Meet Forma, Your AI Fitness Trainer",
                fontFamily = OswaldFontFamily,
                fontSize = 18.sp,
                color = Color.White
            )

            Text(
                text = "Forma is your personal AI fitness trainer, available anytime to help you with:",
                fontFamily = OswaldFontFamily,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 5.dp)
            )

            Row {
                Icon (
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "Bullet Point",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append("Exercise advice ")
                        }
                        append("— get safe, personalized guidance on workouts and technique")
                    },
                    fontFamily = OswaldFontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

            Row {
                Icon (
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "Bullet Point",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append("Nutrition & wellness ")
                        }
                        append("— ask about diet, recovery, and general wellness")
                    },
                    fontFamily = OswaldFontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

            Row {
                Icon (
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "Bullet Point",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append("Issue tracking ")
                        }
                        append("— Forma listens for any pain, discomfort, or injuries you " +
                                "mention and automatically logs them so you can keep track of your health over time")
                    },
                    fontFamily = OswaldFontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

            Row {
                Icon (
                    painter = painterResource(R.drawable.ic_check),
                    contentDescription = "Bullet Point",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(end = 5.dp)
                )
                Text(
                    text = buildAnnotatedString {
                        withStyle(
                            style = SpanStyle(
                                color = MaterialTheme.colorScheme.primary
                            )
                        ) {
                            append("Personalized advice ")
                        }
                        append("— Forma remembers your history and tailors advice based on your known issues")
                    },
                    fontFamily = OswaldFontFamily,
                    color = Color.White,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
            }

            Text(
                text = "Note: Forma is focused exclusively on fitness, nutrition, and wellness — questions outside " +
                        "these topics won't be answered.",
                color = MaterialTheme.colorScheme.primary,
                fontFamily = OswaldFontFamily
            )
        }
    }
}