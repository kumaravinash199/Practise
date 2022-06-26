package com.avinash.b_task.database

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface UserDao {

    @Insert
    fun insertUser(user: UserEntity)

    @Update
    fun updateUser(user: UserEntity)

    @Delete
    fun deleteUser(user: UserEntity)

    @Query("DELETE FROM User")
    fun deleteAll(): Int

    @Query("SELECT * FROM User")
    fun getUser(): LiveData<List<UserEntity>>

    @Query("SELECT * FROM User WHERE user_name LIKE :userName")
    fun getUsername(userName: String): UserEntity?

}