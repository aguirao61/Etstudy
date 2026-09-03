package com.example.studyapp.di

import android.content.Context
import androidx.room.Room
import com.example.studyapp.core.AppDatabase
import com.example.studyapp.core.util.DatabaseInitializer
import com.example.studyapp.core.domain.GameEngine
import com.example.studyapp.questions.data.repository.SubjectRepositoryImpl
import com.example.studyapp.questions.domain.repositories.SubjectRepository
import com.example.studyapp.user_profile.data.repositories.BannerRepositoryImpl
import com.example.studyapp.user_profile.data.repositories.DailyMissionRepositoryImpl
import com.example.studyapp.user_profile.data.repositories.IconRepositoryImpl
import com.example.studyapp.user_profile.data.repositories.TrophyRepositoryImpl
import com.example.studyapp.user_profile.data.repositories.UserProfileRepositoryImpl
import com.example.studyapp.user_profile.domain.repositories.BannerRepository
import com.example.studyapp.user_profile.domain.repositories.DailyMissionRepository
import com.example.studyapp.user_profile.domain.repositories.IconRepository
import com.example.studyapp.user_profile.domain.repositories.TrophyRepository
import com.example.studyapp.user_profile.domain.repositories.UserRepository

interface AppModule {
    val database: AppDatabase
    val userRepository: UserRepository
    val trophyRepository: TrophyRepository
    val bannerRepository: BannerRepository
    val iconRepository: IconRepository
    val dailyMissionRepository: DailyMissionRepository
    val subjectRepository: SubjectRepository
    val gameEngine: GameEngine
}

class AppModuleImpl(
    private val context: Context
) : AppModule {

    override val database: AppDatabase by lazy {
        Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "study_app_db"
        )
            .fallbackToDestructiveMigration(false)
            .build()
    }

    private val databaseInitializer: DatabaseInitializer by lazy {
        DatabaseInitializer(context, database)
    }

    override val userRepository: UserRepository by lazy {
        UserProfileRepositoryImpl(
            userDao = database.userProfileDao,
            iconDao = database.iconDao,
            bannerDao = database.bannerDao,
            userCoursesDao = database.userCoursesDao,
            databaseInitializer = databaseInitializer
        )
    }

    override val trophyRepository: TrophyRepository by lazy {
        TrophyRepositoryImpl(database.trophyDao, database.userTrophyDao)
    }

    override val bannerRepository: BannerRepository by lazy {
        BannerRepositoryImpl(database.bannerDao, database.userBannerDao, database.userProfileDao)
    }

    override val iconRepository: IconRepository by lazy {
        IconRepositoryImpl(database.iconDao, database.userIconDao)
    }

    override val dailyMissionRepository: DailyMissionRepository by lazy {
        DailyMissionRepositoryImpl(database.dailyMissionDao)
    }

    override val subjectRepository: SubjectRepository by lazy {
        SubjectRepositoryImpl(
            coursesDao = database.coursesDao,
            modulesDao = database.modulesDao,
            questionsDao = database.questionsDao,
            answersDao = database.answersDao,
            userCoursesDao = database.userCoursesDao,
            userModulesDao = database.userModulesDao,
            failedQuestionsDao = database.failedQuestionsDao
        )
    }

    override val gameEngine: GameEngine by lazy {
        GameEngine(userRepository, subjectRepository, bannerRepository, iconRepository, dailyMissionRepository)
    }
}
