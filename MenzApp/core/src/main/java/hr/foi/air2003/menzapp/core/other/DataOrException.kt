package hr.foi.air2003.menzapp.core.other


data class DataOrException<T, E : Exception?>(
    val data: T?,
    val exception: E?
)