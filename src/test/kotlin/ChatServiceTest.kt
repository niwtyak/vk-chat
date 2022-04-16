import ChatService.messageTo
import org.junit.Assert.*
import org.junit.Test

class ChatServiceTest {

    @org.junit.After
    fun tearDown() {
        ChatService.clear()
    }

    @Test
    fun messageTo() {
        assertTrue(ChatService.user(1).messageTo(2, "text") == 1 to 1)
    }

    @Test
    fun getChats() {
        val ids1 = ChatService.user(1).messageTo(2, "text1")
        val ids2 = ChatService.user(3).messageTo(1, "text2")

        assertEquals(ChatService.getChats(1).map { it.id }, listOf(ids1.first, ids2.first))
    }

    @Test
    fun getChatsEmpty() {
        assertEquals(ChatService.getChats(1), listOf<Chat>())
    }

    @Test(expected = ChatService.NoSuchUserException::class)
    fun getChatsOfNotExistingUser() {
        ChatService.getChats(5)
    }

    @Test
    fun getUnreadChatsCounts() {
        ChatService.user(2).messageTo(1, "text1")
        ChatService.user(3).messageTo(1, "text2")
        assertEquals(ChatService.getUnreadChatsCounts(1), 2)
    }

    @Test(expected = ChatService.NoSuchUserException::class)
    fun getUnreadChatsCountsOfNotExistingUser() {
        ChatService.getUnreadChatsCounts(5)
    }

    @Test
    fun getMessages() {
        val message1 = "text1"
        val message2 = "text2"
        val message3 = "text3"
        val ids1 = ChatService.user(1).messageTo(2, message1)
        val ids2 = ChatService.user(2).messageTo(1, message2)
        val ids3 = ChatService.user(1).messageTo(2, message3)

        assertEquals(
            listOf(ids1.second to message1 to true, ids2.second to message2 to true, ids3.second to message3 to true),
            ChatService.getMessages(ids1.first, ids1.second, 3).map { it.id to it.text to it.isRead}
        )
    }

    @Test(expected = ChatService.ChatNotFoundException::class)
    fun getMessagesFromNotExistingChat() {
        ChatService.getMessages(5, 1, 3)
    }

    @Test(expected = ChatService.MessagesNotFoundException::class)
    fun getNotExistingMessages() {
        ChatService.user(1).messageTo(2, "message")
        ChatService.getMessages(1, 2, 3)
    }

    @Test
    fun deleteChat() {
        val ids = ChatService.user(1).messageTo(2, "message")
        assertTrue(ChatService.deleteChat(ids.first) && ChatService.getChats(1) == listOf<Chat>())
    }

    @Test(expected = ChatService.ChatNotFoundException::class)
    fun deleteNotExistingChat() {
        assertTrue(ChatService.deleteChat(1))
    }


    @Test
    fun deleteMessage() {
        val (chatId,messageId) = ChatService.user(1).messageTo(2, "message1")
        ChatService.user(2).messageTo(1, "message2")
        assertTrue(ChatService.deleteMessage(chatId, messageId))
    }

    @Test
    fun deleteLastMessage() {
        val ids = ChatService.user(1).messageTo(2, "message1")
        assertTrue(ChatService.deleteMessage(ids.first, ids.second) && ChatService.getChats(1) == listOf<Chat>())
    }

    @Test(expected = ChatService.ChatNotFoundException::class)
    fun deleteMessageFromNotExistingChat() {
        ChatService.user(1).messageTo(2, "message1")
        ChatService.deleteMessage(2, 2)
    }

    @Test(expected = ChatService.MessagesNotFoundException::class)
    fun deleteNotExistingMessage() {
        ChatService.user(1).messageTo(2, "message1")
        ChatService.deleteMessage(1, 2)
    }

    @Test
    fun editMessage() {
        val ids = ChatService.user(1).messageTo(2, "message1")
        assertTrue(
            ChatService.editMessage(ids.first, ids.second, "new text")
                    && ChatService.getMessages(ids.first, ids.second, 1)[0].text == "new text"
        )
    }


    @Test(expected = ChatService.ChatNotFoundException::class)
    fun editMessageFromNotExistingChat() {
        ChatService.user(1).messageTo(2, "message1")
        ChatService.editMessage(2, 2, "new text")
    }

    @Test(expected = ChatService.MessagesNotFoundException::class)
    fun editNotExistingMessage() {
        ChatService.user(1).messageTo(2, "message1")
        ChatService.editMessage(1, 2, "new text")
    }
}