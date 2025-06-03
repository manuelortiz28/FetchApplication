package com.manuel.fetch.ui.di

import com.manuel.fetch.di.ServiceModule
import com.manuel.fetch.service.HiringDto
import com.manuel.fetch.service.HiringsService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ActivityRetainedComponent
import dagger.hilt.components.SingletonComponent
import dagger.hilt.testing.TestInstallIn
import io.mockk.coEvery
import io.mockk.mockk
import javax.inject.Singleton

// This class is used to provide a mock implementation of the HiringsService for testing purposes.
class HiringServiceHolder(val hiringsService: HiringsService)

@Module
@TestInstallIn(
    components = [ActivityRetainedComponent::class],
    replaces = [ServiceModule::class]
)
object FakeServiceModule {
    // Provides the mock HiringsService to the dependency graph.
    @Provides
    fun provideMockHiringsService(serviceHolder: HiringServiceHolder): HiringsService = serviceHolder.hiringsService
}

@Module
@InstallIn(SingletonComponent::class)
object FakeApplicationModule {
    @Provides
    @Singleton
    fun provideHiringServiceHolder(): HiringServiceHolder {
        val mockHirings = listOf(
            // Group 3
            HiringDto(1, 3, "John"), HiringDto(2, 3, "Bob"), HiringDto(3, 3, ""), HiringDto(4, 3, "Benny"), HiringDto(5, 3, "Ross"),
            // Group 1
            HiringDto(20, 1, "Alice"), HiringDto(24, 1, null), HiringDto(22, 1, "Ryan"), HiringDto(25, 1, "Danny"),
            // Group 2
            HiringDto(12, 2, "Charlie"), HiringDto(11, 2, "Thomas"), HiringDto(14, 2, null)
        )

        val mockService = mockk<HiringsService> {
            coEvery { fetchHirings() } returns mockHirings
        }

        return HiringServiceHolder(mockService)
    }
}
