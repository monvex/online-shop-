package com.example.database.items
import com.example.database.brands.Brands
import com.example.database.categories.Categories
import com.example.routing.ItemReceiveRemote
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.transaction

object Items: Table() {
    private val id = Items.integer("id").autoIncrement()
    private val itemTitle = Items.varchar("title", 30)
    private val brand = Items.varchar("brand", 25).uniqueIndex().references(Brands.brandTitle)
    private val category = Items.varchar("category", 25).uniqueIndex().references(Categories.categoryTitle)
    private val price = Items.double("price")


    fun insert(item: ItemReceiveRemote) {
        transaction {
            Items.insert {
                it[itemTitle] = item.itemTitle
                it[brand] = item.brand
                it[category] = item.category
                it[price] = item.price
            }
        }
    }

    fun delete(id: Int) {
        transaction {
            Items.deleteWhere { Items.id.eq(id) }
        }
    }

    fun update(id: Int, newItemDTO: ItemDTO) {
        transaction {
            Items.update({ Items.id eq id}) {
                it[itemTitle] = newItemDTO.itemTitle
                it[brand] = newItemDTO.brand
                it[category] = newItemDTO.category
                it[price] = newItemDTO.price
            }
        }
    }

    fun checkExistence(title: String): Boolean {
        return try {
            transaction {
                Items.selectAll().where(itemTitle.eq(title)).single()
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    fun checkExistence(id: Int): Boolean {
        return try {
            transaction {
                val itemModel = Items.selectAll().where(Items.id.eq(id)).single()
                true
            }
        } catch (e: Exception) {
            false
        }
    }

    fun fetchPaginatedItems(page: Int, size: Int): List<ItemDTO> {
        val skip = size * (page - 1)

        val itemsList = transaction {
            Items.selectAll().limit(size, skip.toLong()).toList()
        }
        val itemsDTO = mutableListOf<ItemDTO>()
        itemsList.forEach {
            itemsDTO.add(ItemDTO(
                itemTitle = it[itemTitle],
                brand = it[brand],
                category = it[category],
                id = it[id],
                price = it[price]
            ))
        }
        return itemsDTO
    }

    fun fetchItem(id: Int): ItemDTO {
        return transaction {
            val result = Items.selectAll().where(Items.id.eq(id)).single()
            ItemDTO(
                id = result[Items.id],
                itemTitle = result[itemTitle],
                brand = result[brand],
                category = result[category],
                price = result[price]
            )
        }

    }
}

