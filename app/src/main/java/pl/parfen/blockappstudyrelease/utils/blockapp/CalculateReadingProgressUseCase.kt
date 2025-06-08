package pl.parfen.blockappstudyrelease.utils.blockapp

class CalculateReadingProgressUseCase {
    /**
     * Рассчитать новый прогресс книги по количеству текущих и общих строк.
     * Возвращает значение прогресса в процентах (0.0 - 100.0).
     */
    fun invoke(currentLine: Int, totalLines: Int): Float {
        if (totalLines <= 0) return 0f
        return (currentLine.toFloat() / totalLines.toFloat()) * 100f
    }
}
