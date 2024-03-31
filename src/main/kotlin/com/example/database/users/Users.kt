package com.example.database.users

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.Table
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

object Users: Table() {
    private val id = Users.integer("id").autoIncrement().uniqueIndex()
    private val username = Users.varchar("username", 30)
    private val password = Users.varchar("password", 200)
    private val salt = Users.varchar("salt", 100)
    private val role = Users.varchar("role", 20)

    fun getUserByUserName(username: String): UserDTO? {
        return transaction {
            try {
                val result = Users.selectAll().where(Users.username.eq(username)).single()
                UserDTO(
                    id = result[Users.id],
                    username = result[Users.username],
                    password = result[password],
                    salt = result[salt],
                    role = result[role]
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    fun insert(user: User): Boolean {
        return transaction {
            try {
                Users.insert {
                    it[username] = user.username
                    it[password] = user.password
                    it[salt] = user.salt
                    if(user.role != "") {
                        it[role] = user.role
                    }
                }
                true
            } catch (e: Exception) {
                print(e.toString())
                false
            }
        }
    }
}