package com.bethanie.quiz_app.core.di

import com.bethanie.quiz_app.data.repo.QuizRepo
import com.bethanie.quiz_app.data.repo.UserRepo
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)

class RepoModule {

    @Provides
    @Singleton
    fun provideUserRepo(): UserRepo = UserRepo()

    @Provides
    @Singleton
    fun provideQuizRepo(userRepo: UserRepo): QuizRepo = QuizRepo(userRepo)
}