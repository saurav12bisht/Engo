package com.project.engo.chat_screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.engo.profile.User

@Composable
fun ChatScreen(
    navController: NavHostController,
    chatUserId: String,
    viewModel: ChatViewModel = viewModel()
) {
    val auth = remember { FirebaseAuth.getInstance() }
    val realTimeDb = remember { FirebaseDatabase.getInstance() }
    val scope = rememberCoroutineScope()

    var chatUser by remember { mutableStateOf<User?>(null) }
    var messages by remember { mutableStateOf(listOf<Message>()) }
    var messageText by remember { mutableStateOf(TextFieldValue("")) }

    val grammarResult by viewModel.grammarResult.collectAsState()

    // Fetch chat user info
    LaunchedEffect(chatUserId) {
        val userRef = realTimeDb.getReference("users").child(chatUserId)
        userRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                chatUser = snapshot.getValue(User::class.java)
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    // Fetch messages
    LaunchedEffect(chatUserId) {
        val currentUid = auth.currentUser?.uid ?: return@LaunchedEffect
        val messagesRef = realTimeDb.getReference("messages/$currentUid/$chatUserId")
        messagesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<Message>()
                for (child in snapshot.children) {
                    val msg = child.getValue(Message::class.java)
                    msg?.let { list.add(it) }
                }
                messages = list.sortedBy { it.time }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }

    Scaffold { innerPadding ->
        Column(modifier = Modifier.padding(innerPadding)) {

            // Top bar
            chatUser?.let { user ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (user.photoUrl != null) {
                        Image(
                            painter = rememberAsyncImagePainter(user.photoUrl),
                            contentDescription = "User Image",
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(user.displayName ?: "User", style = MaterialTheme.typography.titleMedium)
                }
            }

            Divider()

            // Messages
            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(8.dp)
                    .verticalScroll(scrollState)
            ) {
                messages.forEach { msg ->
                    val isMe = msg.senderId == auth.currentUser?.uid
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = if (isMe) Arrangement.End else Arrangement.Start
                    ) {
                        Text(
                            text = msg.message,
                            modifier = Modifier
                                .background(
                                    color = if (isMe) Color(0xFFDCF8C6) else Color.White,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                }
            }

            // Grammar suggestion
            // Grammar suggestion
            grammarResult?.let { result ->
                if (!result.isCorrect && result.suggestion != null) {
                    Text(
                        text = "Suggestion: ${result.suggestion}",
                        color = Color.Blue,
                        modifier = Modifier
                            .padding(8.dp)
                            .clickable {
                                // Replace the input text with only the suggestion
                                messageText = TextFieldValue(result.suggestion)
                                viewModel.clearGrammarResult() // reset grammar result
                            },
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }


            // Input + Send
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                BasicTextField(
                    value = messageText,
                    onValueChange = { messageText = it },
                    modifier = Modifier
                        .weight(1f)
                        .background(Color(0xFFF0F0F0), RoundedCornerShape(20.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    textStyle = TextStyle(color = Color.Black)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Button(onClick = {
                    viewModel.sendMessage(chatUserId, messageText.text)
                    messageText = TextFieldValue("")
                }) {
                    Text("Send")
                }
            }
        }
    }

}

// Message data class
data class Message(
    val senderId: String = "",
    val chatId: String = "",
    val message: String = "",
    val time: Long = System.currentTimeMillis()
)

