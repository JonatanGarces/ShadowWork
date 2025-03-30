package com.quieroestarcontigo.shadowwork.data.local.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.quieroestarcontigo.shadowwork.data.model.UserSession

@Dao
interface SessionDao {
    @Query("SELECT * FROM user_session LIMIT 1")
    fun getSession(): LiveData<UserSession?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSession(session: UserSession)

    @Query("DELETE FROM user_session")
    suspend fun clearSession()

    @Query("SELECT * FROM user_session LIMIT 1")
    suspend fun getSessionNow(): UserSession?
}
