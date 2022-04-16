import java.time.LocalDateTime

data class Message (
    val id:Int,
    val text:String,
    val sentTo:Boolean = true,
    val isRead:Boolean = false,
    val time: LocalDateTime = LocalDateTime.now(),
)
