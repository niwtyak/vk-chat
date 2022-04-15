data class Chat(
    val id: Int,
    val userIds: Pair<Int,Int>,
    val messages: MutableList<Message> = mutableListOf(),
    var count: Int = 1
)