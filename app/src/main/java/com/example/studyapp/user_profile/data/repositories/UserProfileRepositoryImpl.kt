package com.example.studyapp.user_profile.data.repositories

import com.example.studyapp.core.util.DatabaseInitializer
import com.example.studyapp.questions.data.tables.user_courses_table.UserCoursesDao
import com.example.studyapp.user_profile.data.mappers.toDomainModel
import com.example.studyapp.user_profile.data.mappers.toLocalEntity
import com.example.studyapp.user_profile.data.tables.banner_table.BannerDao
import com.example.studyapp.user_profile.data.tables.icon_table.IconDao
import com.example.studyapp.user_profile.data.tables.user_table.UserProfileDao
import com.example.studyapp.user_profile.domain.models.User
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import java.util.Calendar

class UserProfileRepositoryImpl(
    private val userDao: UserProfileDao,
    private val iconDao: IconDao,
    private val bannerDao: BannerDao,
    private val userCoursesDao: UserCoursesDao,
    private val databaseInitializer: DatabaseInitializer
) : UserRepository {

    override fun getUserFlow(userId: Int): Flow<User?> = combine(
        userDao.getUserFlow(userId),
        userCoursesDao.getCompletedCoursesCountFlow(userId)
    ) { entity, completedCount ->
        if (entity == null) null
        else {
            val iconImage = entity.equippedIconId?.let { iconDao.getIconByUid(it) }
            val bannerEntity = entity.equippedBannerId?.let { bannerDao.getBannerByUid(it) }
            
            entity.toDomainModel(
                iconImage = iconImage,
                bannerEntity = bannerEntity,
                dynamicCompletedCourses = completedCount
            )
        }
    }

    override fun getAllUsers(): Flow<List<User>> {
        return userDao.getAllUsersWithIconsFlow().map { entities ->
            entities.map { it.user.toDomainModel(iconImage = it.equippedIconUrl) }
        }
    }

    override suspend fun getUserById(userId: Int): User? {
        // 1. Obtener la entidad local del usuario
        val userEntity = userDao.getUserById(userId) ?: return null

        // 2. Obtener el string de la imagen del icono usando getIconByUid
        val iconImage = userEntity.equippedIconId?.let { iconId ->
            iconDao.getIconByUid(iconId)
        }

        // 3. Obtener el objeto BannerLocalEntity completo usando getBannerByUid
        val bannerEntity = userEntity.equippedBannerId?.let { bannerId ->
            bannerDao.getBannerByUid(bannerId)
        }

        // 4. Obtener el conteo dinámico de cursos completados
        val completedCount = userCoursesDao.getCompletedCoursesCount(userId)

        // 5. Transformar a modelo de Dominio
        return userEntity.toDomainModel(
            iconImage = iconImage,
            bannerEntity = bannerEntity,
            dynamicCompletedCourses = completedCount
        )
    }

    override suspend fun insertUser(user: User): Int {
        val userId = userDao.insertUser(user.toLocalEntity()).toInt()
        databaseInitializer.initializeUser(userId)
        return userId
    }

    override suspend fun updateUser(user: User) {
        val existingUser = userDao.getUserById(user.uniqueUserId)

        userDao.updateUser(
            user.toLocalEntity(
                equippedIconId = existingUser?.equippedIconId,
                equippedBannerId = existingUser?.equippedBannerId
            )
        )
    }

    override suspend fun updateUserTheme(userId: Int, theme: String) {
        userDao.updateUserTheme(userId, theme)
    }

    override suspend fun updateEquippedCosmetics(userId: Int, iconId: Int?, bannerId: Int?) {
        userDao.updateEquippedCosmetics(userId, iconId, bannerId)
    }

    override suspend fun deleteUser(userId: Int) {
        userDao.deleteUserById(userId)
    }

    override suspend fun checkAndResetStreak(userId: Int) {
        val user = getUserById(userId) ?: return
        if (user.lastStreakUpdate == 0L) return

        val currentTime = System.currentTimeMillis()
        if (currentTime < user.lastStreakUpdate) return

        val lastCalendar = Calendar.getInstance().apply { timeInMillis = user.lastStreakUpdate }
        val currentCalendar = Calendar.getInstance().apply { timeInMillis = currentTime }

        val isSameDay = lastCalendar.get(Calendar.YEAR) == currentCalendar.get(Calendar.YEAR) &&
                lastCalendar.get(Calendar.DAY_OF_YEAR) == currentCalendar.get(Calendar.DAY_OF_YEAR)

        if (isSameDay) return

        val yesterdayCalendar = Calendar.getInstance().apply {
            timeInMillis = currentTime
            add(Calendar.DAY_OF_YEAR, -1)
        }
        val wasYesterday = lastCalendar.get(Calendar.YEAR) == yesterdayCalendar.get(Calendar.YEAR) &&
                lastCalendar.get(Calendar.DAY_OF_YEAR) == yesterdayCalendar.get(Calendar.DAY_OF_YEAR)

        if (!wasYesterday) {
            updateUser(user.copy(currentStreak = 0))
        }
    }
}
