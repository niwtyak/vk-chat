object ChatService {
    private var users: MutableList<User> = mutableListOf(User(1), User(2), User(3))
    private var usersGlobalId: Int = 3
    private var chats: MutableList<Chat> = mutableListOf()
    private var chatsGlobalId: Int = 0

    class NoSuchUserException(id: Int) : RuntimeException("User not found id:$id")
    class ChatNotFoundException(id: Int) : RuntimeException("Chat not found id:$id")
    class MessagesNotFoundException(id: Int) : RuntimeException("Message not found id:$id")


    fun user(id: Int): User = users[id - 1]

    fun User.messageTo(id: Int, message: String): Pair<Int, Int> {
        users.find { it.id == this.id || it.id == id } ?: throw NoSuchUserException(id)

        chats.filter { it.userIds == this.id to id || it.userIds == id to this.id }
            .map {
                it.messages.add(Message(++it.count, message, it.userIds.second == id))
                return it.id to it.count
            }
            .ifEmpty {
                chats.add(Chat(++chatsGlobalId, this.id to id, mutableListOf(Message(1, message))))
                return chatsGlobalId to 1
            }

        throw RuntimeException("Something went wrong...")
    }

    fun getChats(id: Int): List<Chat> = chats.filter { it.userIds.first == id || it.userIds.second == id }
        .ifEmpty {
            users.find { it.id == id } ?: throw NoSuchUserException(id)
            println("Нет сообщений")
            listOf()
        }


    fun getUnreadChatsCounts(id: Int): Int {
        users.find { it.id == id } ?: throw NoSuchUserException(id)

        return chats.filter { it.userIds.first == id || it.userIds.second == id }
            .count {
                it.messages.count { message ->
                    !message.read && ((it.userIds.first == id && !message.sentTo) || (it.userIds.second == id && message.sentTo))
                } != 0
            }
    }

    fun getMessages(chatId: Int, messageId: Int, count: Int): List<Message> {
        val chat = chats.find { it.id == chatId } ?: throw ChatNotFoundException(chatId)
        return chat.messages.filter { it.id >= messageId }.ifEmpty { throw MessagesNotFoundException(messageId) }
            .onEach { it.read = !it.read }
            .take(count).sortedBy { it.time }
    }

    fun deleteChat(id: Int): Boolean = chats.remove(chats.find { it.id == id } ?: throw ChatNotFoundException(id))


    fun deleteMessage(chatId: Int, id: Int): Boolean {
        val chat = chats.find { it.id == chatId } ?: throw ChatNotFoundException(chatId)
        val result =
            chat.messages.remove(chat.messages.find { it.id == id } ?: throw MessagesNotFoundException(id))
        --chat.count
        chats.removeIf { it.count == 0 }
        return result
    }

    fun editMessage(chatId: Int, id: Int, message: String): Boolean {
        val chat = chats.find { it.id == chatId } ?: throw ChatNotFoundException(chatId)
        val oldMessage = chat.messages.find { it.id == id } ?: throw MessagesNotFoundException(id)
        oldMessage.text = message
        return true
    }

    fun clear() {
        users = mutableListOf(User(1), User(2), User(3))
        usersGlobalId = 3
        chats = mutableListOf()
        chatsGlobalId = 0
    }

}


