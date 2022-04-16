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

        val chat = chats.singleOrNull { it.userIds == this.id to id || it.userIds == id to this.id }
            ?.let {
                it.messages.add(Message(it.count + 1, message, it.userIds.second == id))
                it.copy(count = it.count + 1)
            } ?: Chat(++chatsGlobalId, this.id to id, mutableListOf(Message(1, message)))

        chats.removeIf { it.id == chat.id }
        chats.add(chat)

        return chatsGlobalId to chat.count
    }


    fun getChats(id: Int): List<Chat> =
        chats.filter { it.userIds.first == id || it.userIds.second == id }
            .ifEmpty {
                users.find { it.id == id } ?: throw NoSuchUserException(id)
                println("Нет сообщений")
                listOf()
            }


    fun getUnreadChatsCounts(id: Int): Int =
        chats.apply { users.find { it.id == id } ?: throw NoSuchUserException(id) }
            .filter { it.userIds.first == id || it.userIds.second == id }
            .count {
                it.messages.count { message ->
                    !message.isRead && ((it.userIds.first == id && !message.sentTo) || (it.userIds.second == id && message.sentTo))
                } != 0
            }


    fun getMessages(chatId: Int, messageId: Int, count: Int): List<Message> =
        chats.singleOrNull { it.id == chatId }
            .run {
                this ?: throw  ChatNotFoundException(chatId)
                messages.asSequence()
                    .drop(messageId - 1)
                    .take(count)
                    .onEachIndexed { index, message -> messages[index] = message.copy(isRead = true) }
                    .map { it.copy(isRead = true) }  //костыль потому, что возвращается список с не обновленными значениями
                    .ifEmpty { throw MessagesNotFoundException(messageId) }
                    .sortedBy { it.time }
                    .toList()
            }


    fun deleteChat(id: Int): Boolean =
        chats.remove(chats.singleOrNull { it.id == id } ?: throw ChatNotFoundException(id))


    fun deleteMessage(chatId: Int, id: Int): Boolean =
        chats.find { it.id == chatId }
            .run {
                this ?: throw ChatNotFoundException(chatId)
                val result =
                    messages.remove(messages.singleOrNull { it.id == id } ?: throw MessagesNotFoundException(id))
                messages.ifEmpty { chats.remove(this) }
                result
            }


    fun editMessage(chatId: Int, id: Int, text: String): Boolean =
        chats.find { it.id == chatId }
            .run {
                this ?: throw ChatNotFoundException(chatId)
                messages.filter { it.id == id }
                    .ifEmpty { throw MessagesNotFoundException(id) }
                    .forEachIndexed { index, message -> messages[index] = message.copy(text = text) }
                return true
            }


    fun clear() {
        users = mutableListOf(User(1), User(2), User(3))
        usersGlobalId = 3
        chats = mutableListOf()
        chatsGlobalId = 0
    }

}


