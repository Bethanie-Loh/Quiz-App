package com.bethanie.quizApp.core.di

import com.bethanie.quizApp.data.repo.QuizRepo
import com.bethanie.quizApp.data.repo.UserRepo
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