package com.example.database.categories

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object Categories: Table() {
    val categoryTitle = Categories.varchar("title", 25).uniqueIndex()

    fun insert(categoryDTO: CategoryDTO) {
        transaction {
            Categories.insert {
                it[categoryTitle] = categoryDTO.categoryTitle
            }
        }
    }

    fun fetchAll(): List<CategoryDTO> {
        val brandsList = transaction {
            Categories.selectAll().toList()
        }
        val categoryDTO = mutableListOf<CategoryDTO>()
        brandsList.forEach {
            categoryDTO.add(CategoryDTO(
                categoryTitle = it[categoryTitle]
            ))
        }
        return categoryDTO
    }

    fun checkExistence(title: String): Boolean {
        return try {
            transaction {
                Categories.selectAll().where(categoryTitle.eq(title)).single()
                true
            }
//            SELECT items.id, items.title, items.brand, items.category FROM items WHERE categories.title = 'coats'
//            SELECT items.id, items.title, items.brand, items.category FROM items WHERE items.category = 'coats'
//            CASCADE DELete
        } catch (e: Exception) {
            false
        }
    }

    fun delete(title: String) {
        transaction {
            Categories.deleteWhere { categoryTitle.eq(title) }
        }
    }

    fun update(titleOld: String, titleNew: String) {
        transaction {
            Categories.update({ categoryTitle eq titleOld }) {
                it[categoryTitle] = titleNew
            }
        }
    }
}