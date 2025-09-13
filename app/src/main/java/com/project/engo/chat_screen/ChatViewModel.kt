package com.project.engo.chat_screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.ai.client.generativeai.GenerativeModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.project.engo.BuildConfig
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class GrammarCheckResult(
    val isCorrect: Boolean,
    val suggestion: String? = null
)

class ChatViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val realTimeDb = FirebaseDatabase.getInstance()

    private val geminiModel = GenerativeModel(
        modelName = "gemini-1.5-flash",
        apiKey = BuildConfig.apiKey
    )

    fun clearGrammarResult() {
        _grammarResult.value = null
    }

    private val _grammarResult = MutableStateFlow<GrammarCheckResult?>(null)
    val grammarResult: StateFlow<GrammarCheckResult?> = _grammarResult

    fun sendMessage(chatUserId: String, messageText: String) {
        viewModelScope.launch {
            val grammarCheck = checkGrammarWithGemini(messageText)

            if (grammarCheck.isCorrect) {
                sendToFirebase(chatUserId, messageText)
                _grammarResult.value = null
            } else {
                _grammarResult.value = grammarCheck
            }
        }
    }

    private suspend fun checkGrammarWithGemini(message: String): GrammarCheckResult {
        return try {
            val response = geminiModel.generateContent(
                "Check this sentence for grammar errors and provide suggestions: $message"
            )
            val correctedText = response.text ?: ""

            if (correctedText.trim().equals(message.trim(), ignoreCase = true)) {
                GrammarCheckResult(true)
            } else {
                // Split suggestions if multiple (optional)
                GrammarCheckResult(false, suggestion = correctedText)
            }
        } catch (e: Exception) {
            GrammarCheckResult(false, suggestion = "Error checking grammar")
        }
    }

    private fun sendToFirebase(chatUserId: String, messageText: String) {
        val currentUid = auth.currentUser?.uid ?: return
        val senderRef = realTimeDb.getReference("messages/$currentUid/$chatUserId").push()
        val chatId = senderRef.key ?: ""
        val msg = Message(
            senderId = currentUid,
            chatId = chatId,
            message = messageText,
            time = System.currentTimeMillis()
        )
        senderRef.setValue(msg)

        val receiverRef =
            realTimeDb.getReference("messages/$chatUserId/$currentUid").child(chatId)
        receiverRef.setValue(msg)
    }
}

