package com.gahov.encrypted_notes.arch.di.component

import android.app.Application
import com.gahov.encrypted_notes.EncryptedNotesApplication
import com.gahov.encrypted_notes.arch.di.module.AppModule
import dagger.BindsInstance
import dagger.Component
import dagger.MembersInjector
import javax.inject.Singleton

@Singleton
@Component(
    modules = [
        AppModule::class
    ]
)
interface AppComponent : MembersInjector<EncryptedNotesApplication> {

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun application(app: Application): Builder

        fun build(): AppComponent
    }
}