package com.example.techsmithsample.data.roomdb

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.techsmithsample.data.network.response.Product
import kotlinx.coroutines.flow.Flow


@Dao
interface ProductDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addToCart(product: Product):Long

    @Query("SELECT * FROM product_tb")
    fun getCartProducts(): Flow<List<Product>>

    @Query("SELECT SUM(price * quantity) AS subTotal FROM product_tb")
    fun getTotalAmount(): Flow<Int>

    @Query("SELECT * FROM product_tb WHERE id=:id")
    fun getCartProduct(id: Int): Flow<Product>

    @Query("SELECT COUNT(*) AS cartCount FROM product_tb")
    fun getCartCount(): Int

    @Delete
    fun deleteCartProduct(product: Product)
}
