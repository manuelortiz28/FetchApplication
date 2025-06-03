package com.manuel.fetch.repo

import com.manuel.fetch.model.Hiring
import com.manuel.fetch.model.HiringGroup
import com.manuel.fetch.service.HiringDto
import com.manuel.fetch.service.HiringsService
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Test

class HiringRepositoryTest {

    private val hiringsService: HiringsService = mockk()
    private val repository: HiringRepository = HiringRepository(hiringsService)

    private val mockHirings = listOf(
        // Group 3
        HiringDto(1, 3, "John"), HiringDto(2, 3, "Bob"), HiringDto(3, 3, ""), HiringDto(4, 3, "Benny"), HiringDto(5, 3, "Ross"),
        // Group 1
        HiringDto(20, 1, "Alice"), HiringDto(24, 1, null), HiringDto(22, 1, "Ryan"), HiringDto(25, 1, "Danny"),
        // Group 2
        HiringDto(12, 2, "Charlie"), HiringDto(11, 2, "Thomas"), HiringDto(14, 2, null)
    )

    @Test
    fun `fetchHirings should return grouped and sorted hirings filtering out null and empty names`() = runBlocking {
        // Arrange
        coEvery { hiringsService.fetchHirings() } returns mockHirings

        // Act
        val actual = repository.fetchHirings()

        // Assert
        val expected = listOf(
            HiringGroup(
                id = 1,
                hirings = listOf(
                    Hiring(20, "Alice"),
                    Hiring(25, "Danny"),
                    Hiring(22, "Ryan"),
                )
            ),
            HiringGroup(
                id = 2,
                hirings = listOf(
                    Hiring(12, "Charlie"),
                    Hiring(11, "Thomas"),
                )
            ),
            HiringGroup(
                id = 3,
                hirings = listOf(
                    Hiring(4, "Benny"),
                    Hiring(2, "Bob"),
                    Hiring(1, "John"),
                    Hiring(5, "Ross"),
                )
            )
        )
        assertEquals(expected, actual)
    }

    @Test(expected = RuntimeException::class)
    fun `fetchHirings should throw exception when service call fails`() = runBlocking {
        // Arrange
        val exception = RuntimeException("Fetch Hirings exception")
        coEvery { hiringsService.fetchHirings() } throws exception

        // Act
        repository.fetchHirings()

        Unit // This line should not be reached, exception is expected
    }
}
