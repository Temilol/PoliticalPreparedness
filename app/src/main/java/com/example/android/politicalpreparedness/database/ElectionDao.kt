package com.example.android.politicalpreparedness.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.android.politicalpreparedness.network.models.Election

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveElection(reminder: Election)

    //TODO: Add select all election query
    @Query("select * from election_table")
    suspend fun getElectionList(): List<Election>

    //TODO: Add select single election query
    @Query("select * from election_table where id = :election_id")
    suspend fun getElectionById(election_id: Int): Election?

    //TODO: Add delete query
    @Query("DELETE FROM election_table WHERE id = :id")
    suspend fun deleteElectionById(id: Int): Int

    //TODO: Add clear query
    @Query("DELETE FROM election_table")
    suspend fun deleteAllElections(): Int
}