import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.data.model.BookProgress

object BookTempStorage {
    private val tempBookProgressList = mutableListOf<BookProgress>()

    fun addTempProgress(progress: BookProgress) {
        tempBookProgressList.removeAll { it.title == progress.title }
        tempBookProgressList.add(progress)
    }

    fun getTempProgress(title: String): BookProgress? {
        return tempBookProgressList.find { it.title == title }
    }

    fun getAllTempProgress(): List<BookProgress> = tempBookProgressList.toList()

    fun clear() = tempBookProgressList.clear()
}
