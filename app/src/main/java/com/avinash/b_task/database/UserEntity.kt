package com.avinash.b_task.database

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "User",)
data class UserEntity(

    @PrimaryKey(autoGenerate = true)
    val id: Long,

    @ColumnInfo(name = "user_name")
    val userName: String,

    @ColumnInfo(name = "password_text")
    val password: String

)
