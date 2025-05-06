package pl.parfen.blockappstudyrelease.data.mapper


import pl.parfen.blockappstudyrelease.data.model.Book
import pl.parfen.blockappstudyrelease.data.model.BookEntity

fun Book.toEntity(): BookEntity {
    return BookEntity(
        id = id,
        title = title,
        file = file,
        language = language,
        ageGroup = ageGroup,
        author = author,
        isUserBook = isUserBook,
        storageType = storageType,
        pages = pages,
        progress = progress,
        fileUri = fileUri
    )
}

fun BookEntity.toBook(): Book {
    return Book(
        id = id,
        title = title,
        file = file,
        language = language,
        ageGroup = ageGroup,
        author = author,
        isUserBook = isUserBook,
        storageType = storageType,
        pages = pages,
        progress = progress,
        fileUri = fileUri
    )
}
