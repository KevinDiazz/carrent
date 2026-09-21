<h1 align="center">🛠️ Carrent API</h1>

<p align="center">
  <b>API REST de alquiler de coches</b><br/>
  Autenticación JWT · Disponibilidad por fechas y oficina · Gestión de flota y reservas
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Spring_Boot_4-6DB33F?style=for-the-badge&logo=springboot&logoColor=white" alt="Spring Boot" />
  <img src="https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Java" />
  <img src="https://img.shields.io/badge/Spring_Security-6DB33F?style=for-the-badge&logo=springsecurity&logoColor=white" alt="Spring Security" />
  <img src="https://img.shields.io/badge/Spring_Data_JPA-6DB33F?style=for-the-badge&logo=hibernate&logoColor=white" alt="Spring Data JPA" />
  <img src="https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white" alt="MySQL" />
  <img src="https://img.shields.io/badge/MapStruct-EA2D2E?style=for-the-badge&logo=java&logoColor=white" alt="MapStruct" />
  <img src="https://img.shields.io/badge/JJWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white" alt="JJWT" />
  <img src="https://img.shields.io/badge/Docker-2496ED?style=for-the-badge&logo=docker&logoColor=white" alt="Docker" />
  <img src="https://img.shields.io/badge/Render-46E3B7?style=for-the-badge&logo=render&logoColor=white" alt="Render" />
</p>

<p align="center">
  <a href="https://www.youtube.com/watch?v=Z3fM00dWvZQ"><img src="https://img.shields.io/badge/▶_VIDEO_DEMOSTRACIÓN-YouTube-FF0000?style=for-the-badge&logo=youtube&logoColor=white" alt="Video demostración" /></a>
</p>

---

## 🗺️ Índice

- [¿Qué es Carrent API?](#-qué-es-carrent-api)
- [¿Qué demuestra este proyecto?](#-qué-demuestra-este-proyecto)
- [Stack tecnológico](#-stack-tecnológico)
- [Modelo de dominio](#-modelo-de-dominio)
- [Funcionalidades implementadas](#-funcionalidades-implementadas)
- [Retos técnicos resueltos](#-retos-técnicos-resueltos)
- [Arquitectura del proyecto](#-arquitectura-del-proyecto)
---

## 🚙 ¿Qué es Carrent API?

Carrent API es el **backend REST** que da servicio a la plataforma de alquiler de coches Carrent: gestiona oficinas, modelos de vehículo, flota y reservas, calcula disponibilidad real por rango de fechas y protege cada operación según el rol del usuario autenticado.

El proyecto está dividido en dos repositorios independientes que se comunican por API REST:

| ⚙️ Backend (este repo) | 🖥️ Frontend |
|:---:|:---:|
| Spring Boot 4 · desplegado en Render | Angular 22 · desplegado en Vercel |

---

## 👨‍💻 ¿Qué demuestra este proyecto?

- ✅ Arquitectura en capas clásica y limpia: `controller → service → repository`, con **DTOs** dedicados de entrada/salida y mapeo automático con **MapStruct**
- ✅ Autenticación **JWT** propia (sin librerías de terceros de auth) con cookies **httpOnly**, access + refresh token, y protección **CSRF** con el patrón double-submit-cookie
- ✅ Autorización granular por **roles** (`USER` / `ADMIN`) y por método HTTP en cada recurso, definida en `SecurityConfig`
- ✅ Consultas de disponibilidad con **JPA Criteria API**: subconsulta correlacionada que descarta coches con reservas confirmadas solapadas en el rango de fechas pedido
- ✅ Filtrado dinámico de flota con **Spring Data JPA Specifications** (precio, combustible, transmisión, oficina, modelo, estado) sin construir queries manuales
- ✅ Manejo de errores centralizado con `@RestControllerAdvice` y **13 excepciones de dominio propias** (coche no disponible, oficina en uso, credenciales inválidas...), cada una con su código HTTP correcto
- ✅ Validación de entrada con Bean Validation en todos los DTOs de request
- ✅ Contenedorizado con **Docker** y desplegado en Render conectado a MySQL gestionado (Aiven)

---

## 🛠 Stack Tecnológico

| Tecnología | Uso |
|---|---|
| 🍃 **Spring Boot 4** | Framework de la API REST |
| ☕ **Java** | Lenguaje principal |
| 🔐 **Spring Security** | Autenticación y autorización |
| 🔑 **JJWT** | Generación y verificación de JSON Web Tokens |
| 🗄️ **Spring Data JPA + Hibernate** | Persistencia y Specifications dinámicas |
| 🐬 **MySQL** | Base de datos relacional (Aiven Cloud) |
| 🧩 **MapStruct** | Mapeo entidad ↔ DTO sin boilerplate |
| ✅ **Bean Validation** | Validación declarativa de DTOs |
| 🐳 **Docker** | Empaquetado para despliegue |
| ☁️ **Render** | Hosting del backend |

---

## 🧬 Modelo de dominio

```
User ──< Reservations >── Car ──> CarModel
                            └──> Office
RefreshToken ──> User
```

- **User**: credenciales, rol (`USER` / `ADMIN`)
- **Office**: puntos de recogida/devolución
- **CarModel**: catálogo de modelos (marca, tipo de combustible, transmisión...)
- **Car**: unidad física de flota, asociada a una oficina y un modelo, con estado (`AVAILABLE`, etc.)
- **Reservations**: reserva de un coche por un usuario en un rango de fechas, con estado (`CONFIRMED`, etc.)
- **RefreshToken**: token de renovación de sesión, ligado a un usuario

---

## ✨ Funcionalidades implementadas

### 🔐 Autenticación
- Registro y login con contraseña cifrada (`BCrypt`)
- Sesión en cookie httpOnly (`access_token` / `refresh_token`), endpoint de refresco y logout
- Protección CSRF con cookie `XSRF-TOKEN` legible + cabecera `X-XSRF-TOKEN`

### 🚗 Coches y disponibilidad
- CRUD de coches y modelos de coche (`ADMIN`)
- Búsqueda de disponibilidad (`GET /cars/available`) por oficina y rango de fechas, público
- Filtrado de flota por precio, combustible, transmisión, oficina, modelo y estado

### 🏢 Oficinas y reservas
- CRUD de oficinas
- Creación, consulta y cancelación de reservas, con reglas de negocio (fechas válidas, coche disponible, acceso solo al propietario de la reserva o a un `ADMIN`)

---

## 📚 Retos técnicos resueltos

### 📅 Disponibilidad real sin solapes
Comprobar si un coche está libre en un rango de fechas no es un simple `WHERE`: hay que descartar cualquier coche con una reserva **confirmada** cuyo rango se solape con el pedido. Se resolvió con una `Specification` de JPA que arma una subconsulta correlacionada (`NOT EXISTS`) sobre `Reservations`, comparando `startDate`/`endDate` con operadores estrictos para que los rangos que solo se tocan en el borde no cuenten como solape.

### 🍪 Cookies de sesión entre dominios distintos
El frontend (Vercel) y esta API (Render) viven en dominios distintos. Las cookies httpOnly con `SameSite=None; Secure` seguían bloqueándose como cookies de terceros en navegadores modernos. La solución final vive en el frontend (proxy same-origin vía `vercel.json`), pero aquí se mantuvo la configuración de cookie correcta (`SameSite`, `Secure`, `httpOnly`) y CORS con `allowCredentials(true)` y origen explícito, en lugar de recurrir a `localStorage` para el JWT.

### 🧯 Errores de dominio, no genéricos
En lugar de dejar que Spring devuelva un 500 genérico o un stacktrace, cada regla de negocio (coche no disponible, oficina en uso, email duplicado, matrícula duplicada, reserva ya cancelada...) tiene su propia excepción y se traduce en `GlobalExceptionHandler` al código HTTP y mensaje adecuados, con un `ErrorResponse` consistente en toda la API.

---

## 📂 Arquitectura del proyecto

```
carrent/
├── Dockerfile
├── pom.xml
└── src/main/java/com/kevin/carrent/
    ├── config/            # SecurityConfig, CORS, CSRF, entry point / access denied
    ├── controller/        # Car, CarModel, Office, Reservation, User
    ├── service/            # Lógica de negocio (Car, CarModel, Office, Reservation, User, Jwt, RefreshToken)
    ├── repository/         # Spring Data JPA
    ├── specification/      # Filtros y disponibilidad dinámicos (Criteria API)
    ├── entity/             # Car, CarModel, Office, Reservations, User, RefreshToken
    ├── dto/                # Requests / Responses por caso de uso
    ├── mapper/             # MapStruct: entidad ↔ DTO
    ├── enums/              # CarStatus, ReservationStatus, Role
    ├── exception/           # Excepciones de dominio + GlobalExceptionHandler
    └── security/            # JwtAuthenticationFilter
```
