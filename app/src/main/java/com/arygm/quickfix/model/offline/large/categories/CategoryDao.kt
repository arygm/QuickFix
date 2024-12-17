package com.arygm.quickfix.model.offline.large.categories

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {
  @Query("SELECT * FROM categories WHERE id = :id")
  suspend fun getCategoryById(id: String): CategoryEntity?

  @Query("SELECT * FROM categories") fun getAllCategories(): Flow<List<CategoryEntity>>

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertCategory(category: CategoryEntity)

  @Query("DELETE FROM categories WHERE id = :id") suspend fun deleteCategory(id: String)
}
