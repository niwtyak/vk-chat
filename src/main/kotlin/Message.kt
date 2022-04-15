import java.time.LocalDateTime

data class Message (
    val id:Int,
    var text:String,
    val sentTo:Boolean = true,
    var read:Boolean = false,
    val time: LocalDateTime = LocalDateTime.now(),
)
