package com.example.android.politicalpreparedness.database

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.android.politicalpreparedness.network.models.Election
import com.example.android.politicalpreparedness.network.models.VoterInfo
import com.example.android.politicalpreparedness.representative.model.Representative
import com.example.android.politicalpreparedness.representative.model.RepresentativeLocalDB

@Dao
interface ElectionDao {

    //TODO: Add insert query
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(vararg election: Election)

    //TODO: Add select all election query
    @Query("select * from election_table")
    fun getAllElections() : LiveData<List<Election>>

    //TODO: Add select single election query
    @Query("select * from election_table where id = :electionId")
    suspend fun getElection(electionId: String) : Election

    //TODO: Add delete query
    @Query("delete from election_table where electionDay <= DATE('now')")
    fun cleanElections()

    //TODO: Add clear query
    @Query("delete from election_table")
    fun deleteAllElections()

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertVoterInfo(vararg voterInfo: VoterInfo)

    @Query("select * from voter_info_table where id = :electionId")
    suspend fun getVoterInfoById(electionId: Int) : VoterInfo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRepresentatives(vararg representative: RepresentativeLocalDB)

    @Query("select * from representative_table where address = :address order by `order`")
    suspend fun getRepresentativesByAddress(address : String) : List<RepresentativeLocalDB>

    @Query("select * from representative_table where role = :role")
    suspend fun getRepresentativeByRole(role : String) : List<RepresentativeLocalDB>
}