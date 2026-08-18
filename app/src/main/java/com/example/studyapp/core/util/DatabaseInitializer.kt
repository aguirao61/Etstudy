package com.example.studyapp.core.util

import android.content.Context
import com.example.studyapp.core.AppDatabase
import com.example.studyapp.questions.data.tables.modules_table.ModuleLocalEntity
import com.example.studyapp.questions.data.tables.courses_table.CourseLocalEntity
import com.example.studyapp.questions.data.tables.user_courses_table.UserCoursesLocalEntity
import com.example.studyapp.questions.data.tables.user_modules_table.UserModulesLocalEntity
import com.example.studyapp.user_profile.data.tables.user_banner_table.UserBannerLocalEntity
import com.example.studyapp.user_profile.data.tables.user_icon_table.UserIconLocalEntity
import androidx.room.withTransaction
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class DatabaseInitializer(
    private val context: Context,
    private val database: AppDatabase
) {
    private val csvReader = CsvReader(context)

    suspend fun initializeData() = withContext(Dispatchers.IO) {
        database.withTransaction {
            // 1. Sincronizar Asignaturas (Courses)
            val coursesCsv = csvReader.readCourses()
            val existingCourses = database.coursesDao.getAllCourses()
            val codeToId = existingCourses.associate { it.courseCode to it.uniqueCourseId }
            
            val coursesToUpsert = coursesCsv.map { csv ->
                csv.copy(uniqueCourseId = codeToId[csv.courseCode] ?: 0)
            }
            database.coursesDao.upsertCourses(coursesToUpsert)
            
            // Eliminar las que ya no están en el CSV
            val csvCodes = coursesCsv.map { it.courseCode }.toSet()
            val coursesToDelete = existingCourses.filter { it.courseCode !in csvCodes }
            database.coursesDao.deleteCourses(coursesToDelete)

            // Obtenemos los IDs actualizados
            val coursesInDb = database.coursesDao.getAllCourses()
            val courseCodeToId = coursesInDb.associate { it.courseCode to it.uniqueCourseId }
            val courseNameToId = coursesInDb.associate { it.courseName.trim() to it.uniqueCourseId }

            // 2. Sincronizar Módulos
            val modulesCsv = csvReader.readModules()
            val existingModules = database.modulesDao.getAllModules()
            // Clave única para módulo: (IdAsignatura, NumeroModulo)
            val moduleKeyToId = existingModules.associate { 
                (it.uniqueCourseId to it.moduleNumber) to it.uniqueModuleId 
            }

            val modulesToUpsert = modulesCsv.map { row ->
                val courseId = courseCodeToId[row.courseCode] ?: 0
                ModuleLocalEntity(
                    uniqueModuleId = moduleKeyToId[courseId to row.moduleNumber] ?: 0,
                    uniqueCourseId = courseId,
                    moduleName = row.moduleName,
                    moduleNumber = row.moduleNumber
                )
            }
            database.modulesDao.upsertModules(modulesToUpsert)

            // Eliminar módulos obsoletos
            val csvModuleKeys = modulesCsv.map { (courseCodeToId[it.courseCode] ?: 0) to it.moduleNumber }.toSet()
            val modulesToDelete = existingModules.filter { (it.uniqueCourseId to it.moduleNumber) !in csvModuleKeys }
            database.modulesDao.deleteModules(modulesToDelete)

            val modulesInDb = database.modulesDao.getAllModules()
            // Para el mapeo de relaciones (index 1-based del CSV de módulos)
            val moduleIds = modulesInDb.sortedBy { it.uniqueModuleId }.map { it.uniqueModuleId }

            // 3. Sincronizar Preguntas (IDs fijos en CSV)
            val questionsCsv = csvReader.readQuestions()
            database.questionsDao.upsertQuestions(questionsCsv)
            
            val existingQuestions = database.questionsDao.getAllQuestions()
            val csvQuestionIds = questionsCsv.map { it.uniqueQuestionId }.toSet()
            val questionsToDelete = existingQuestions.filter { it.uniqueQuestionId !in csvQuestionIds }
            database.questionsDao.deleteQuestions(questionsToDelete)

            // 4. Cargar Respuestas (Borrado y re-inserción simple ya que no hay datos de usuario vinculados a answerId)
            database.answersDao.deleteAllAnswers()
            val answersCsv = csvReader.readAnswers()
            database.answersDao.insertOrUpdateAnswers(answersCsv)

            // 5. Cargar Relaciones Módulo-Pregunta (Borrado y re-inserción)
            database.relationModuleQuestionDao.deleteAllRelations()
            val relationsCsv = csvReader.readQuestionModuleRelations()
            val relationsToInsert = relationsCsv.map { rel ->
                // Mapeo del ID virtual (1-based index del CSV) al ID real generado
                val realModuleId = moduleIds.getOrNull(rel.uniqueModuleId - 1) ?: rel.uniqueModuleId
                rel.copy(uniqueModuleId = realModuleId)
            }
            database.relationModuleQuestionDao.insertOrUpdateRelations(relationsToInsert)

            // 6. Sincronizar Banners
            val bannersCsv = csvReader.readBanners()
            val existingBanners = database.bannerDao.getAllBanners()
            val contentToId = existingBanners.associate { it.bannerContent to it.uniqueBannerId }
            
            val bannersToUpsert = bannersCsv.map { csv ->
                // Intentamos buscar por nombre exacto o nombre aproximado (sin acentos en la primera palabra clave)
                val courseId = courseNameToId[csv.bannerSubject.trim()]
                csv.copy(
                    uniqueBannerId = contentToId[csv.bannerContent] ?: 0,
                    uniqueCourseId = courseId
                )
            }
            database.bannerDao.upsertBanners(bannersToUpsert)
            
            val csvBannerContents = bannersCsv.map { it.bannerContent }.toSet()
            val bannersToDelete = existingBanners.filter { it.bannerContent !in csvBannerContents }
            database.bannerDao.deleteBanners(bannersToDelete)

            val bannersInDb = database.bannerDao.getAllBanners()

            // 6.1 Sincronizar Iconos
            val iconsCsv = csvReader.readIcons()
            val existingIcons = database.iconDao.getAllIcons()
            
            // Mapeo por nombre e imagen para encontrar IDs existentes
            val nameToId = existingIcons.associate { it.iconName.trim().lowercase() to it.uniqueIconId }
            val imageToId = existingIcons.associate { it.iconImage.trim().lowercase() to it.uniqueIconId }

            val iconsToUpsert = iconsCsv.map { csv ->
                val normName = csv.iconName.trim().lowercase()
                val normImage = csv.iconImage.trim().lowercase()
                val existingId = nameToId[normName] ?: imageToId[normImage]
                
                csv.copy(uniqueIconId = existingId ?: 0)
            }
            database.iconDao.upsertIcons(iconsToUpsert)

            // Eliminar iconos obsoletos que ya no están en el CSV
            val csvNames = iconsCsv.map { it.iconName.trim().lowercase() }.toSet()
            val csvImages = iconsCsv.map { it.iconImage.trim().lowercase() }.toSet()
            
            val iconsToDelete = existingIcons.filter { 
                it.iconName.trim().lowercase() !in csvNames && 
                it.iconImage.trim().lowercase() !in csvImages 
            }
            if (iconsToDelete.isNotEmpty()) {
                database.iconDao.deleteIcons(iconsToDelete)
            }

            val iconsInDb = database.iconDao.getAllIcons()

            // 7. ACTUALIZAR TABLAS DE USUARIO (Garantizar que todos los usuarios tienen todas las asignaturas/módulos)
            val userIds = database.userProfileDao.getAllUserIds()
            
            for (userId in userIds) {
                initializeUserTables(userId, coursesInDb, modulesInDb, bannersInDb, iconsInDb)
            }

            // Sincronizar IDs de asignaturas en user_banners si estaban en NULL
            database.userBannerDao.syncCourseIds()
        }
    }

    suspend fun initializeUser(userId: Int) = withContext(Dispatchers.IO) {
        val courses = database.coursesDao.getAllCourses()
        val modules = database.modulesDao.getAllModules()
        val banners = database.bannerDao.getAllBanners()
        val icons = database.iconDao.getAllIcons()
        
        initializeUserTables(userId, courses, modules, banners, icons)
    }

    private suspend fun initializeUserTables(
        userId: Int,
        courses: List<CourseLocalEntity>,
        modules: List<ModuleLocalEntity>,
        banners: List<com.example.studyapp.user_profile.data.tables.banner_table.BannerLocalEntity>,
        icons: List<com.example.studyapp.user_profile.data.tables.icon_table.IconLocalEntity> = emptyList()
    ) {
        val userCourses = courses.map {
            UserCoursesLocalEntity(uniqueUserId = userId, uniqueCourseId = it.uniqueCourseId)
        }
        val userModules = modules.map {
            UserModulesLocalEntity(uniqueUserId = userId, uniqueModuleId = it.uniqueModuleId)
        }
        val userBanners = banners.map {
            UserBannerLocalEntity(
                uniqueUserId = userId, 
                uniqueBannerId = it.uniqueBannerId,
                uniqueCourseId = it.uniqueCourseId
            )
        }
        val userIcons = icons.map {
            UserIconLocalEntity(uniqueUserId = userId, uniqueIconId = it.uniqueIconId)
        }

        if (userCourses.isNotEmpty()) database.userCoursesDao.insertUserCoursesIgnore(userCourses)
        if (userModules.isNotEmpty()) database.userModulesDao.insertUserModulesIgnore(userModules)
        if (userBanners.isNotEmpty()) database.userBannerDao.initializeUserBanners(userBanners)
        if (userIcons.isNotEmpty()) database.userIconDao.initializeUserIcons(userIcons)
    }
}
