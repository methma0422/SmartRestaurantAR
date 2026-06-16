package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.Converters
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.SeedData
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.dao.MenuItemDao
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.dao.OrderDao
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.entity.MenuItemEntity
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.entity.OrderEntity
import lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.local.entity.toEntity

@Database(
    entities = [MenuItemEntity::class, OrderEntity::class],
    version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun menuItemDao(): MenuItemDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Migration from version 1 to 2: Add userId column to orders table
        private val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                // Add userId column to orders table
                database.execSQL("ALTER TABLE orders ADD COLUMN userId TEXT NOT NULL DEFAULT ''")
            }
        }

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "golden_oak_db"
                )
                    .addCallback(DatabaseCallback(context))
                    .addMigrations(MIGRATION_1_2)
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }

        private class DatabaseCallback(private val context: Context) : RoomDatabase.Callback() {
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // Database created. Seeding is handled dynamically on app startup by MenuRepository.
            }
        }
    }
}
