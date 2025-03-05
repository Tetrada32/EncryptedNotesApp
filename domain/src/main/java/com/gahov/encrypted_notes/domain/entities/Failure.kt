package com.gahov.encrypted_notes.domain.entities

sealed class Failure {

    data class DataSourceException(val throwable: Throwable) : Failure()

}