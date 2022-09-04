package com.connor.anonfiles.model.room

import androidx.room.*
import androidx.sqlite.db.SupportSQLiteQuery

@Dao
interface FileDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFile(file: FileData): Long

    @Delete
    suspend fun delete(file: FileData): Int

    @Query("DELETE FROM FileData WHERE fileID = :fileId")
    suspend fun deleteFile(fileId: String)

    @RawQuery
    suspend fun vacuumDb(supportSQLiteQuery: SupportSQLiteQuery): Long

    @Query("select * from FileData")
    suspend fun loadAllFile(): List<FileData>
}