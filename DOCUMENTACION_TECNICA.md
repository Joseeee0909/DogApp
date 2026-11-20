# Documentación Técnica Base - DogApp

## 1. Visión General de la Arquitectura
**DogApp** está estructurada siguiendo la arquitectura **MVVM (Model-View-ViewModel)** y el patrón **Repository**, promoviendo una clara separación de responsabilidades, alta mantenibilidad y testabilidad.

### Estructura de Paquetes
```
com.dogapp/
│
├── data/
│   ├── local/
│   │   ├── dao/
│   │   │   └── CitaDao.kt           # Operaciones CRUD en Room
│   │   ├── database/
│   │   │   └── AppDatabase.kt       # Base de datos principal de Room
│   │   └── entity/
│   │       └── Cita.kt              # Entidad de persistencia local
│   ├── remote/
│   │   ├── api/
│   │   │   └── DogApiService.kt     # Interfaz Retrofit para API Dog CEO
│   │   ├── dto/
│   │   │   ├── DogResponse.kt       # Objeto de respuesta JSON
│   │   │   └── DogBreedsResponse.kt # DTO de catálogo de razas
│   │   └── RetrofitClient.kt        # Singleton de cliente Retrofit
│   └── repository/
│       ├── CitaRepository.kt        # Abstracción de acceso a datos locales
│       └── DogRepository.kt         # Abstracción de llamadas de red
│
├── view/
│   ├── fragments/
│   │   ├── LoginFragment.kt         # Pantalla de Login (Base)
│   │   ├── HomeFragment.kt          # Pantalla principal (Lista de citas)
│   │   ├── NuevaCitaFragment.kt     # Formulario de nueva cita
│   │   ├── DetalleCitaFragment.kt   # Vista detallada de cita
│   │   └── EditarCitaFragment.kt    # Edición de cita
│   ├── components/
│   │   └── CurvedToolbar.kt         # Componente reusable de barra superior curva
│   └── adapters/
│       └── CitaAdapter.kt           # ListAdapter con DataBinding para RecyclerView
│
├── viewmodel/
│   ├── CitaViewModel.kt             # Lógica de presentación y estado para Citas
│   └── DogViewModel.kt              # Lógica de presentación para integración Dog CEO
│
├── navigation/                      # Grafo de navegación en res/navigation/nav_graph.xml
│
└── MainActivity.kt                  # Actividad principal contenedora de NavHostFragment
```

---

## 2. Componentes Base y Tecnologías Utilizadas

### Persistencia con Room
- **Entidad `Cita`**:
  - `id`: Int (clave primaria autogenerada)
  - `nombreMascota`: String
  - `raza`: String
  - `propietario`: String
  - `telefono`: String
  - `fecha`: String
  - `hora`: String
  - `motivo`: String
  - `imagenUrl`: String?
  - `estado`: String
- **`CitaDao`**: Ofrece métodos suspendibles y flujos asíncronos (`LiveData<List<Cita>>`, `insertCita`, `updateCita`, `deleteCita`, `getCitaById`).
- **`AppDatabase`**: Base de datos Room singleton (`dogapp_database`).

### Integración de Red con Retrofit (API Dog CEO)
- **Base URL**: `https://dog.ceo/api/`
- **Endpoints expuestos**:
  - `getRandomDogImage()` -> Imagen aleatoria de perro.
  - `getRandomImageByBreed(breed)` -> Imagen por raza.
  - `getAllBreeds()` -> Catálogo completo de razas.

### Patrón ViewBinding / DataBinding
- Todas las vistas XML utilizan `<layout>` o ViewBinding directo.
- **REGLA**: Queda **estrictamente prohibido el uso de `findViewById()`**.

### Componente Reutilizable: `CurvedToolbar`
- Se incluye el componente personalizado `CurvedToolbar` (`com.dogapp.view.components.CurvedToolbar`).
- Uso en XML:
```xml
<com.dogapp.view.components.CurvedToolbar
    android:id="@+id/curvedToolbar"
    android:layout_width="match_parent"
    android:layout_height="wrap_content" />
```
- Uso en Kotlin:
```kotlin
binding.curvedToolbar.setTitle("Título de la Pantalla")
binding.curvedToolbar.showBackButton { findNavController().navigateUp() }
```

---

## 3. Guía de Uso para Integrantes del Equipo

### Cómo consumir Citas desde un Fragment
```kotlin
private val citaViewModel: CitaViewModel by viewModels()

// Observar lista de citas
citaViewModel.allCitas.observe(viewLifecycleOwner) { citas ->
    adapter.submitList(citas)
}

// Insertar una nueva cita
val nuevaCita = Cita(
    nombreMascota = "Max",
    raza = "Beagle",
    propietario = "Ana Gómez",
    telefono = "555-0192",
    fecha = "2025-04-10",
    hora = "11:00 AM",
    motivo = "Vacunación",
    imagenUrl = "https://images.dog.ceo/breeds/beagle/n02088364_11812.jpg"
)
citaViewModel.insertCita(nuevaCita)
```

### Cómo obtener imágenes o razas de perros con `DogViewModel`
```kotlin
private val dogViewModel: DogViewModel by viewModels()

// Obtener imagen aleatoria
dogViewModel.fetchRandomDogImage()

dogViewModel.imageUrl.observe(viewLifecycleOwner) { url ->
    url?.let {
        Glide.with(this).load(it).into(binding.ivFotoMascota)
    }
}
```

### Navegación
El grafo de navegación se encuentra en `res/navigation/nav_graph.xml`.
Para navegar entre fragments:
```kotlin
findNavController().navigate(R.id.action_homeFragment_to_nuevaCitaFragment)
```
