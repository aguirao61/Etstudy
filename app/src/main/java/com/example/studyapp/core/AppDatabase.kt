package com.example.studyapp.core

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.studyapp.questions.data.tables.answers_table.AnswersDao
import com.example.studyapp.questions.data.tables.answers_table.AnswersLocalEntity
import com.example.studyapp.questions.data.tables.courses_table.CourseLocalEntity
import com.example.studyapp.questions.data.tables.courses_table.CoursesDao
import com.example.studyapp.questions.data.tables.failed_questions_table.FailedQuestionsDao
import com.example.studyapp.questions.data.tables.failed_questions_table.FailedQuestionsLocalEntity
import com.example.studyapp.questions.data.tables.modules_table.ModuleLocalEntity
import com.example.studyapp.questions.data.tables.modules_table.ModulesDao
import com.example.studyapp.questions.data.tables.questions_table.QuestionLocalEntity
import com.example.studyapp.questions.data.tables.questions_table.QuestionsDao
import com.example.studyapp.questions.data.tables.relationship_tables.RelationModuleQuestionDao
import com.example.studyapp.questions.data.tables.relationship_tables.RelationModuleQuestionLocalEntity
import com.example.studyapp.questions.data.tables.user_courses_table.UserCoursesDao
import com.example.studyapp.questions.data.tables.user_courses_table.UserCoursesLocalEntity
import com.example.studyapp.questions.data.tables.user_modules_table.UserModulesDao
import com.example.studyapp.questions.data.tables.user_modules_table.UserModulesLocalEntity
import com.example.studyapp.user_profile.data.tables.banner_table.BannerDao
import com.example.studyapp.user_profile.data.tables.banner_table.BannerLocalEntity
import com.example.studyapp.user_profile.data.tables.icon_table.IconDao
import com.example.studyapp.user_profile.data.tables.icon_table.IconLocalEntity
import com.example.studyapp.user_profile.data.tables.trophy_table.TrophyDao
import com.example.studyapp.user_profile.data.tables.trophy_table.TrophyLocalEntity
import com.example.studyapp.user_profile.data.tables.user_banner_table.UserBannerDao
import com.example.studyapp.user_profile.data.tables.user_banner_table.UserBannerLocalEntity
import com.example.studyapp.user_profile.data.tables.user_icon_table.UserIconDao
import com.example.studyapp.user_profile.data.tables.user_icon_table.UserIconLocalEntity
import com.example.studyapp.user_profile.data.tables.user_table.UserProfileDao
import com.example.studyapp.user_profile.data.tables.user_table.UserLocalEntity
import com.example.studyapp.user_profile.data.tables.user_trophy_table.UserTrophyDao
import com.example.studyapp.user_profile.data.tables.user_trophy_table.UserTrophyLocalEntity

@Database(
    entities = [
        UserLocalEntity::class,
        BannerLocalEntity::class,
        TrophyLocalEntity::class,
        IconLocalEntity::class,
        UserBannerLocalEntity::class,
        UserTrophyLocalEntity::class,
        CourseLocalEntity::class,
        ModuleLocalEntity::class,
        QuestionLocalEntity::class,
        AnswersLocalEntity::class,
        RelationModuleQuestionLocalEntity::class,
        UserCoursesLocalEntity::class,
        UserModulesLocalEntity::class,
        FailedQuestionsLocalEntity::class,
        UserIconLocalEntity::class
    ],
    version = 12,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    //User Profile
    abstract val userProfileDao: UserProfileDao
    abstract val bannerDao: BannerDao
    abstract val trophyDao: TrophyDao
    abstract val iconDao: IconDao
    abstract val userBannerDao: UserBannerDao
    abstract val userTrophyDao: UserTrophyDao
    abstract val userIconDao: UserIconDao

    //Questions
    abstract val coursesDao: CoursesDao
    abstract val modulesDao: ModulesDao
    abstract val questionsDao: QuestionsDao
    abstract val answersDao: AnswersDao
    abstract val relationModuleQuestionDao: RelationModuleQuestionDao
    abstract val userCoursesDao: UserCoursesDao
    abstract val userModulesDao: UserModulesDao
    abstract val failedQuestionsDao: FailedQuestionsDao
}
