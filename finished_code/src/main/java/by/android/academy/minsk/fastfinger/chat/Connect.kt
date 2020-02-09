package by.android.academy.minsk.fastfinger.chat

import by.android.academy.minsk.fastfinger.WEB_SOCKET_SERVER_URL
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch
import okhttp3.*
import okio.ByteString
import java.util.concurrent.TimeUnit

sealed class Frame {
    data class NewMessage(val text: String) : Frame()
    data class ConnectionClosed(val reason: String?) : Frame()
    object Connecting : Frame()
    object Connected : Frame()
    data class ReconnectingIn(val seconds: Int) : Frame()
    data class ConnectionError(val errorMessage: String) : Frame()
}

fun connectToChat(messagesToSend: ReceiveChannel<String>): Flow<Frame> = callbackFlow {
    val client = OkHttpClient.Builder()
        .readTimeout(500, TimeUnit.MILLISECONDS)
        .build()

    val request = Request.Builder()
        .url("$WEB_SOCKET_SERVER_URL/chat")
        .build()

    val webSocketListener = object : WebSocketListener() {

        override fun onOpen(webSocket: WebSocket, response: Response) {
            //TODO(14): offer a frame
            offer(Frame.Connected)
        }

        override fun onMessage(webSocket: WebSocket, text: String) {
            if (text.isNotEmpty()) {
                //TODO(14): offer a frame
                offer(Frame.NewMessage(text))
            }
        }

        override fun onMessage(webSocket: WebSocket, bytes: ByteString) {
            val text = bytes.hex()
            if (text.isEmpty()) {
                //TODO(14): offer a frame
                offer(Frame.NewMessage(text))
            }
        }

        override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
            //TODO(14): offer a frame
            offer(Frame.ConnectionError(t.message ?: "failed with unknown reason"))
            close()
        }

        override fun onClosing(webSocket: WebSocket, code: Int, reason: String) {
            if (!isClosedForSend) {
                //TODO(14): offer a frame
                offer(Frame.ConnectionClosed(reason))
                close()
            }
        }
    }
    val socket = client.newWebSocket(request, webSocketListener)
    //TODO(14): offer a frame
    offer(Frame.Connecting)
    //TODO(19): from channel to socket
    launch {
        for (message in messagesToSend) {
            socket.send(message)
        }
    }
    awaitClose {
        socket.close(1001, "by client initiative")
    }
}