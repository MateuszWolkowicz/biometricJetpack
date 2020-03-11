package pl.yameo.merchant

import android.content.Context
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.githubrepo.ReposDao
import com.wolkowiczmateusz.biometricjetpack.infrastructure.db.user.UsersDao
import dagger.Module
import dagger.Provides

@Module
class AppDatabaseModule {

    @Provides
    internal fun appDatabase(context: Context): AppDatabase {
        return AppDatabase.getInstance(context)
    }

    @Provides
    internal fun userDao(appDatabase: AppDatabase) : UsersDao {
        return appDatabase.usersDao()
    }

    @Provides
    internal fun reposDao(appDatabase: AppDatabase) : ReposDao {
        return appDatabase.reposDao()
    }

}