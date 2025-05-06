package pl.parfen.blockappstudyrelease.ui.preview

sealed class BookPreviewEvent {
    data class ScrollUpdated(val profileId: Int, val firstVisibleLine: Int) : BookPreviewEvent()
    object CompleteReading : BookPreviewEvent()
}
