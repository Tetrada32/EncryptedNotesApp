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
            id = dbModel.uid,
            title = dbModel.title,
            content = dbModel.content,
            isPinned = dbModel.isPinned
        )
    }

    override fun toDomain(dbModelList: List<NoteDTO>?): List<NoteEntity> {
        return dbModelList?.map { toDomain(it) } ?: emptyList()
    }

    override fun toDatabase(domainModelList: List<NoteEntity>?): List<NoteDTO> {
        return domainModelList?.map { toDatabase(it) } ?: emptyList()
    }
}