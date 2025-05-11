# DogApp - Proyecto de Citas Veterinarias

**DogApp** es una aplicación para gestionar las citas de los pacientes en una veterinaria. La app permite a los administradores gestionar citas, agregar nuevas, editar y ver detalles de las citas, todo ello con un diseño de interfaz intuitivo y funcional. 

## Descripción

La aplicación está desarrollada en **Kotlin** utilizando el patrón arquitectónico **MVVM** y **Room** como base de datos local para el almacenamiento de las citas. Además, utiliza **Retrofit** para hacer consumo de APIs externas y obtener imágenes relacionadas con las razas de los perros.

## Características principales

- **Login con biometría**: Los usuarios pueden ingresar a la app utilizando la autenticación biométrica, mejorando la experiencia del usuario.
- **Gestión de citas**: Los administradores pueden ver las citas agendadas, crear nuevas citas, editar y eliminar citas.
- **Interfaz amigable**: Un diseño que optimiza la experiencia del usuario con una interfaz intuitiva y dinámica.

## Estructura del Proyecto

Este proyecto sigue la estructura **MVVM** (Model-View-ViewModel) con las siguientes carpetas:

- **data**: Contiene las clases relacionadas con la base de datos, como `AppointmentDAO` y `AppointmentDb`.
- **model**: Define los modelos de datos como `Appointment` y `AppointmentModelResponse`.
- **repository**: Define la lógica de negocio para acceder a los datos a través de `AppointmentRepository`.
- **utils**: Contiene utilidades como `Constants`.
- **view**: Contiene los fragmentos (`AddAppointmentFragment`, `HomeAppointmentFragment`, etc.), adaptadores y view holders.
- **viewModel**: Contiene las clases `ViewModel` para manejar la lógica de UI.
- **webservice**: Define las clases para interactuar con las APIs, como `ApiService`, `ApiUtils` y `RetrofitClient`.

## Requisitos

- **Android Studio** (última versión recomendada).
- **Kotlin** como lenguaje principal.
- **Room** para almacenamiento local.
- **Retrofit** para consumir APIs externas.
- **Lottie** para las animaciones de huella digital.

## Instalación

Para instalar y correr la aplicación en tu entorno local:

1. **Clonar el repositorio**:
   ```bash
   git clone https://github.com/AlexanderJAON/DogApp.git
