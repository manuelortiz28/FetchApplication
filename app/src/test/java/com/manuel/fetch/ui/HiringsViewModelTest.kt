package com.manuel.fetch.ui

import android.util.Log
import com.manuel.fetch.model.Hiring
import com.manuel.fetch.model.HiringGroup
import com.manuel.fetch.model.SortOrder
import com.manuel.fetch.repo.HiringRepository
import io.mockk.coEvery
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.unmockkStatic
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs


@OptIn(ExperimentalCoroutinesApi::class)
class HiringsViewModelTest {
    private val hiringRepository: HiringRepository = mockk()
    private val viewModel: HiringsViewModel = HiringsViewModel(hiringRepository)

    private val mockHiringGroups = listOf(
        HiringGroup(3, listOf(Hiring(4, "John"), Hiring(2, "Bob"), Hiring(3, "Benny"), Hiring(5, "Ross"))),
        HiringGroup(1, listOf(Hiring(1, "Alice"), Hiring(2, "Ryan"), Hiring(5, "Danny"))),
        HiringGroup(2, listOf(Hiring(3, "Charlie")))
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
        mockkStatic(Log::class)
        every { Log.d(any(), any(), any()) } returns 0
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        unmockkStatic(Log::class)
    }

    @Test
    fun `processIntent with LoadData should update state with fetched hirings in the original order`() = runTest {
        // Arrange
        coEvery { hiringRepository.fetchHirings() } returns mockHiringGroups
        assertIs<HiringsViewModel.MviState.Initial>(viewModel.state.first())

        // Act
        viewModel.processIntent(HiringsViewModel.Intent.LoadData)
        val secondState = viewModel.state.first()

        // Assert
        assertIs<HiringsViewModel.MviState.Data>(secondState)
        assertEquals(mockHiringGroups, secondState.hiringGroups)
        assertEquals(SortOrder.ASC, secondState.groupOrder)
        assertEquals(SortOrder.ASC, secondState.hiringOrder)

        assertEquals(3, secondState.hiringGroups[0].id)
        assertEquals(1, secondState.hiringGroups[1].id)
        assertEquals(2, secondState.hiringGroups[2].id)

        val firstGroup = secondState.hiringGroups[0]
        assertEquals("John", firstGroup.hirings[0].name)
        assertEquals("Bob", firstGroup.hirings[1].name)
        assertEquals("Benny", firstGroup.hirings[2].name)
        assertEquals("Ross", firstGroup.hirings[3].name)
    }

    @Test
    fun `processIntent with SortGroups should toggle group order and sort groups`() = runTest {
        // Arrange
        coEvery { hiringRepository.fetchHirings() } returns mockHiringGroups
        assertIs<HiringsViewModel.MviState.Initial>(viewModel.state.first())

        viewModel.processIntent(HiringsViewModel.Intent.LoadData)
        val secondState = viewModel.state.first()
        assertIs<HiringsViewModel.MviState.Data>(secondState)

        // Act DESC
        viewModel.processIntent(HiringsViewModel.Intent.SortGroups)

        // Assert DESC
        val thirdState = viewModel.state.first()
        assertIs<HiringsViewModel.MviState.Data>(thirdState)

        assertEquals(SortOrder.DESC, thirdState.groupOrder)
        assertEquals(SortOrder.ASC, thirdState.hiringOrder)

        // Assert hiring groups are sorted by ID in descending order
        assertEquals(3, thirdState.hiringGroups[0].id)
        assertEquals(2, thirdState.hiringGroups[1].id)
        assertEquals(1, thirdState.hiringGroups[2].id)

        // Assert hirings within each group are kept in the original order
        val firstGroup = thirdState.hiringGroups[0]
        assertEquals("John", firstGroup.hirings[0].name)
        assertEquals("Bob", firstGroup.hirings[1].name)
        assertEquals("Benny", firstGroup.hirings[2].name)
        assertEquals("Ross", firstGroup.hirings[3].name)

        // Act ASC
        viewModel.processIntent(HiringsViewModel.Intent.SortGroups)

        // Assert ASC
        val fourthState = viewModel.state.first()
        assertIs<HiringsViewModel.MviState.Data>(fourthState)

        assertEquals(SortOrder.ASC, fourthState.groupOrder)
        assertEquals(SortOrder.ASC, fourthState.hiringOrder)

        // Assert hiring groups are sorted by ID in ascending order
        assertEquals(1, fourthState.hiringGroups[0].id)
        assertEquals(2, fourthState.hiringGroups[1].id)
        assertEquals(3, fourthState.hiringGroups[2].id)

        // Assert hirings within each group are kept in the original order
        val lastGroup = fourthState.hiringGroups[2]
        assertEquals("John", lastGroup.hirings[0].name)
        assertEquals("Bob", lastGroup.hirings[1].name)
        assertEquals("Benny", lastGroup.hirings[2].name)
        assertEquals("Ross", lastGroup.hirings[3].name)
    }

    @Test
    fun `processIntent with SortHirings should toggle hiring order and sort hirings`() = runTest {
        // Arrange
        coEvery { hiringRepository.fetchHirings() } returns mockHiringGroups
        assertIs<HiringsViewModel.MviState.Initial>(viewModel.state.first())

        viewModel.processIntent(HiringsViewModel.Intent.LoadData)
        val secondState = viewModel.state.first()
        assertIs<HiringsViewModel.MviState.Data>(secondState)

        // Act DESC
        viewModel.processIntent(HiringsViewModel.Intent.SortHirings)

        // Assert DESC
        val thirdState = viewModel.state.first()
        assertIs<HiringsViewModel.MviState.Data>(thirdState)

        assertEquals(SortOrder.ASC, thirdState.groupOrder)
        assertEquals(SortOrder.DESC, thirdState.hiringOrder)

        // Assert hiring groups are kept in the original order
        val firstGroup = thirdState.hiringGroups[0]
        val secondGroup = thirdState.hiringGroups[1]
        val thirdGroup = thirdState.hiringGroups[2]

        assertEquals(3, firstGroup.id)
        assertEquals(1, secondGroup.id)
        assertEquals(2, thirdGroup.id)

        // Assert hirings within each group are sorted by name in descending order
        assertEquals(listOf("Ross", "John", "Bob", "Benny"), firstGroup.hirings.map { it.name })
        assertEquals(listOf("Ryan", "Danny", "Alice"), secondGroup.hirings.map { it.name })
        assertEquals(listOf("Charlie"), thirdGroup.hirings.map { it.name })

        // Act DESC
        viewModel.processIntent(HiringsViewModel.Intent.SortHirings)

        // Assert ASC
        val fourthState = viewModel.state.first()
        assertIs<HiringsViewModel.MviState.Data>(fourthState)

        assertEquals(SortOrder.ASC, fourthState.groupOrder)
        assertEquals(SortOrder.ASC, fourthState.hiringOrder)

        // Assert hiring groups are kept in the original order
        val firstGroupAsc = fourthState.hiringGroups[0]
        val secondGroupAsc = fourthState.hiringGroups[1]
        val thirdGroupAsc = fourthState.hiringGroups[2]

        assertEquals(3, firstGroupAsc.id)
        assertEquals(1, secondGroupAsc.id)
        assertEquals(2, thirdGroupAsc.id)

        // Assert hirings within each group are sorted by name in ascending order
        assertEquals(listOf("Benny", "Bob", "John", "Ross"), firstGroupAsc.hirings.map { it.name })
        assertEquals(listOf("Alice", "Danny", "Ryan"), secondGroupAsc.hirings.map { it.name })
        assertEquals(listOf("Charlie"), thirdGroupAsc.hirings.map { it.name })
    }

    @Test
    fun `processIntent with LoadData should update state with Error when fetchHirings throws exception`() = runTest {
        // Arrange
        val exception = RuntimeException("Failed to fetch hirings")
        coEvery { hiringRepository.fetchHirings() } throws exception
        assertIs<HiringsViewModel.MviState.Initial>(viewModel.state.first())

        // Act
        viewModel.processIntent(HiringsViewModel.Intent.LoadData)
        val secondState = viewModel.state.first()

        // Assert
        assertIs<HiringsViewModel.MviState.Error>(secondState)
        assertEquals(exception, secondState.error)
    }
}