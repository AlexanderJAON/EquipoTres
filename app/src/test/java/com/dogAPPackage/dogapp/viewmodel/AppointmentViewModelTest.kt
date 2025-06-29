package com.dogAPPackage.dogapp.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.dogAPPackage.dogapp.model.Appointment
import com.dogAPPackage.dogapp.repository.AppointmentRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.*
import org.mockito.Mockito.*
import org.mockito.kotlin.mock

@OptIn(ExperimentalCoroutinesApi::class)
class AppointmentViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var appointmentRepository: AppointmentRepository
    private lateinit var viewModel: AppointmentViewModel

    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        appointmentRepository = mock()
        viewModel = AppointmentViewModel(appointmentRepository)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `getBreedsFromApi should update breedsList`() = runTest {
        // Arrange
        val mockBreeds = listOf("Labrador", "Poodle")
        `when`(appointmentRepository.getAllBreeds()).thenReturn(mockBreeds)
        val observer = mock<Observer<List<String>>>()
        viewModel.breedsList.observeForever(observer)

        // Act
        viewModel.getBreedsFromApi()
        advanceUntilIdle()

        // Assert
        verify(observer).onChanged(mockBreeds)
        verify(appointmentRepository).getAllBreeds()

        viewModel.breedsList.removeObserver(observer)
    }

    @Test
    fun `saveAppointment should update saveResult to true`() = runTest {
        // Arrange
        val mockAppointment = Appointment(
            id = "1",
            petName = "Firulais",
            breed = "Labrador",
            ownerName = "Juan",
            phone = "1234567890",
            symptom = "Bota demasiado pelo",
            imageUrl = "https://dog.ceo/api/breeds/image/random"
        )
        `when`(appointmentRepository.saveAppointment(mockAppointment)).thenReturn("1")
        val observer = mock<Observer<Boolean>>()
        viewModel.saveResult.observeForever(observer)

        // Act
        viewModel.saveAppointment(mockAppointment)
        advanceUntilIdle()

        // Assert
        verify(observer).onChanged(true)
        verify(appointmentRepository).saveAppointment(mockAppointment)

        viewModel.saveResult.removeObserver(observer)
    }

    @Test
    fun `getRandomImageByBreed should update imageUrl`() = runTest {
        // Arrange
        val mockImageUrl = "https://dog.ceo/api/breeds/image/random"
        `when`(appointmentRepository.getRandomImageByBreed("Labrador")).thenReturn(mockImageUrl)
        val observer = mock<Observer<String?>>()
        viewModel.imageUrl.observeForever(observer)

        // Act
        viewModel.getRandomImageByBreed("Labrador")
        advanceUntilIdle()

        // Assert
        verify(observer).onChanged(mockImageUrl)
        verify(appointmentRepository).getRandomImageByBreed("Labrador")

        viewModel.imageUrl.removeObserver(observer)
    }

    @Test
    fun `getListAppointments should update listAppointments`() = runTest {
        // Arrange
        val mockAppointments = listOf(
            Appointment(id = "1", petName = "Rex"),
            Appointment(id = "2", petName = "Luna")
        )
        `when`(appointmentRepository.getListAppointment()).thenReturn(mockAppointments)
        val observer = mock<Observer<List<Appointment>>>()
        viewModel.listAppointments.observeForever(observer)

        // Act
        viewModel.getListAppointments()
        advanceUntilIdle()

        // Assert
        verify(observer).onChanged(mockAppointments)
        verify(appointmentRepository).getListAppointment()

        viewModel.listAppointments.removeObserver(observer)
    }

    @Test
    fun `deleteAppointment should update operationSuccess to true`() = runTest {
        // Arrange
        val mockAppointment = Appointment(
            id = "1",
            petName = "Firulais",
            breed = "Labrador",
            ownerName = "Juan"
        )
        val observer = mock<Observer<Boolean?>>()
        viewModel.operationSuccess.observeForever(observer)

        // Act
        viewModel.deleteAppointment(mockAppointment)
        advanceUntilIdle()

        // Assert
        verify(observer).onChanged(true)
        verify(appointmentRepository).deleteAppointment(mockAppointment)

        viewModel.operationSuccess.removeObserver(observer)
    }

}
