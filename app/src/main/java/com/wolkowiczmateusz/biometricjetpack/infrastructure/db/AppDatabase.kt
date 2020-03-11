package pl.yameo.merchant

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.converter.UuidConverter
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.ReposDao
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.model.GithubRepoEntity
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.helpers.SingletonHolder
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.user.UsersDao
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.user.model.UserEntity

@Database(
    entities = [
        UserEntity::class,
        GithubRepoEntity::class
    ],
    version = 1
)
@TypeConverters(
    value = [
        UuidConverter::class
    ]
)
internal abstract class AppDatabase : RoomDatabase() {

    abstract fun usersDao(): UsersDao
    abstract fun reposDao(): ReposDao

    companion object : SingletonHolder<AppDatabase, Context>({
        Room.databaseBuilder(it.applicationContext, AppDatabase::class.java, "biometricjetpack.db")
            .build()
    })
}


