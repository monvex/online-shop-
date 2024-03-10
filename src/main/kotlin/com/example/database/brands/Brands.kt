package com.example.database.brands

import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object Brands: Table() {
    val brandTitle = Brands.varchar("title", 25).uniqueIndex()

    fun insert(brandDTO: BrandDTO) {
        transaction {
            Brands.insert {
                it[brandTitle] = brandDTO.brandTitle
            }
        }
    }

    fun fetchAll(): List<BrandDTO> {
        val brandsList = transaction {
            Brands.selectAll().toList()
        }
        val brandsDTO = mutableListOf<BrandDTO>()
        brandsList.forEach {
            brandsDTO.add(BrandDTO(
                brandTitle = it[brandTitle]
            ))
        }
        return brandsDTO
    }

    fun checkExistence(title: String): Boolean {
        return try {
            transaction {
                Brands.selectAll().where(brandTitle.eq(title)).single()
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    fun delete(title: String) {
        transaction {
            Brands.deleteWhere { brandTitle.eq(title) }
        }
    }

    fun update(titleOld: String, titleNew: String) {
        transaction {
            Brands.update({ brandTitle eq titleOld }) {
                it[brandTitle] = titleNew
            }
        }
    }
}