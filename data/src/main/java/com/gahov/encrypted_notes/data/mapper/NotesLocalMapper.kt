package com.gahov.encrypted_notes.data.mapper

import com.gahov.encrypted_notes.data.common.DbMapper
import com.gahov.encrypted_notes.data.storage.entities.NoteDTO
import com.gahov.encrypted_notes.domain.entities.NoteEntity

class NotesLocalMapper : DbMapper<NoteEntity, NoteDTO> {

    override fun toDatabase(domainModel: NoteEntity): NoteDTO {
        return NoteDTO(
            title = domainModel.title,
            content = domainModel.content,
            isPinned = domainModel.isPinned
        )
    }

    override fun toDomain(dbModel: NoteDTO): NoteEntity {
        return NoteEntity(
            title = dbModel.title,
            content = dbModel.content,
            isPinned = dbModel.isPinned
        )
    }
}