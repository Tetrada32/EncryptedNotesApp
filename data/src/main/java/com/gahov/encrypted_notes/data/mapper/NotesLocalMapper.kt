package com.gahov.encrypted_notes.data.mapper

import com.gahov.encrypted_notes.data.common.DbMapper
import com.gahov.encrypted_notes.data.security.manager.CryptoManager
import com.gahov.encrypted_notes.data.storage.entities.NoteDTO
import com.gahov.encrypted_notes.domain.common.getOrNull
import com.gahov.encrypted_notes.domain.entities.Note

class NotesLocalMapper(
    private val cryptoManager: CryptoManager
) : DbMapper<Note, NoteDTO> {

    override fun toDatabase(domainModel: Note): NoteDTO {
        return NoteDTO(
            content = cryptoManager.encryptToString(domainModel.message).getOrNull(),
            isPinned = domainModel.isPinned,
            createdAt = domainModel.createdAt,
            deletedAt = Long.MAX_VALUE
        )
    }

    override fun toDomain(dbModel: NoteDTO): Note {
        return Note(
            id = dbModel.uid,
            message = cryptoManager.decryptFromString(dbModel.content).getOrNull(),
            isPinned = dbModel.isPinned,
            createdAt = dbModel.createdAt,
            deletedAt = dbModel.deletedAt
        )
    }

    override fun toDomain(dbModelList: List<NoteDTO>?): List<Note> {
        return try {
            dbModelList?.map { toDomain(it) } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    override fun toDatabase(domainModelList: List<Note>?): List<NoteDTO> {
        return try {
            return domainModelList?.map { toDatabase(it) } ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }
}