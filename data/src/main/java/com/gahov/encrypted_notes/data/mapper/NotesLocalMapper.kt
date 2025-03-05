package com.gahov.encrypted_notes.data.mapper

import com.gahov.encrypted_notes.data.common.DbMapper
import com.gahov.encrypted_notes.data.storage.entities.NoteDTO
import com.gahov.encrypted_notes.domain.entities.Note

class NotesLocalMapper : DbMapper<Note, NoteDTO> {

    override fun toDatabase(domainModel: Note): NoteDTO {
        return NoteDTO(
            content = domainModel.message,
            isPinned = domainModel.isPinned,
            createdAt = domainModel.createdAt,
            updatedAt = domainModel.updatedAt,
            deletedAt = Long.MAX_VALUE
        )
    }

    override fun toDomain(dbModel: NoteDTO): Note {
        return Note(
            id = dbModel.uid,
            message = dbModel.content,
            isPinned = dbModel.isPinned,
            createdAt = dbModel.createdAt,
            updatedAt = dbModel.updatedAt,
            deletedAt = dbModel.deletedAt
        )
    }

    override fun toDomain(dbModelList: List<NoteDTO>?): List<Note> {
        return dbModelList?.map { toDomain(it) } ?: emptyList()
    }

    override fun toDatabase(domainModelList: List<Note>?): List<NoteDTO> {
        return domainModelList?.map { toDatabase(it) } ?: emptyList()
    }
}