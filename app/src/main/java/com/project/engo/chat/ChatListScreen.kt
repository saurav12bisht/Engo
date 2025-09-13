package com.project.engo.chat

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.project.engo.profile.User

@Composable
fun ChatListScreen(navController: NavHostController) {
    val realTimeDb = remember { FirebaseDatabase.getInstance() }
    var userList by remember { mutableStateOf(listOf<User>()) }

    // Fetch users from Realtime Database
    LaunchedEffect(Unit) {
        val usersRef = realTimeDb.getReference("users")
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val list = mutableListOf<User>()
                for (child in snapshot.children) {
                    val user = child.getValue(User::class.java)
                    user?.let { list.add(it) }
                }
                userList = list
            }

            override fun onCancelled(error: DatabaseError) {
                // handle error if needed
            }
        })
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(), contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(userList.size) { user ->
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .clickable {
                        // Navigate to ChatScreen with userId
                        navController.navigate("chat/${userList[user].uid}")
                    },
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Profile Image
                if (userList[user].photoUrl != null) {
                    Image(
                        painter = rememberAsyncImagePainter(userList[user].photoUrl),
                        contentDescription = "Profile Image",
                        modifier = Modifier
                            .size(50.dp)
                            .clip(CircleShape)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                // Name
                Text(
                    text = userList[user].displayName ?: "No Name",
                    style = MaterialTheme.typography.bodyLarge
                )
            }

            Divider() // Divider between users
        }
    }
}
