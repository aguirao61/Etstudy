package com.example.studyapp.di

import android.content.Context
import androidx.room.Room
import com.example.studyapp.core.AppDatabase
import com.example.studyapp.core.util.DatabaseInitializer
import com.example.studyapp.questions.data.repository.SubjectRepositoryImpl
import com.example.studyapp.questions.domain.repositories.SubjectRepository
import com.example.studyapp.user_profile.data.repositories.BannerRepositoryImpl
import com.example.studyapp.user_profile.data.repositories.TrophyRepositoryImpl
import com.example.studyapp.user_profile.data.repositories.UserProfileRepositoryImpl
import com.example.studyapp.user_profile.domain.repositories.BannerRepository
import com.example.studyapp.user_profile.domain.repositories.TrophyRepository
import com.example.studyapp.user_profile.domain.repositories.UserRepository
import com.example.studyapp.user_profile.domain.use_cases.AddExperienceUseCase
import com.example.studyapp.user_profile.domain.use_cases.ProcessQuizResultsUseCase

interface AppModule {
    val database: AppDatabase
    val userRepository: UserRepository
    val trophyRepository: TrophyRepository
    val bannerRepository: BannerRepository
    val subjectRepository: SubjectRepository
    val addExperienceUseCase: AddExperienceUseCase
    val processQuizResultsUseCase: ProcessQuizResultsUseCase
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
            .fallbackToDestructiveMigration()
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

    override val addExperienceUseCase: AddExperienceUseCase by lazy {
        AddExperienceUseCase(userRepository)
    }

    override val processQuizResultsUseCase: ProcessQuizResultsUseCase by lazy {
        ProcessQuizResultsUseCase(userRepository, subjectRepository, bannerRepository)
    }
}
