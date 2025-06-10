# User Service API - Resumen y Errores Detectados

## Prefijo

`/user-service`

---

## Endpoints

### ✅ Obtener todos los usuarios

* **Método:** GET
* **Ruta:** `/api/users`
* **Estado:** Correcto
* **Observaciones:**

  * No hay manejo de excepciones.
  * Cualquier error devuelve estado 500.

---

### ✅ Obtener un usuario por ID

* **Método:** GET
* **Ruta:** `/api/users/{userId}`
* **Estado:** Correcto

---

### ❌ Crear un usuario

* **Método:** POST
* **Ruta:** `/api/users`

#### Errores detectados:

* Recibe `userId` como parámetro: conflicto si el ID ya existe.
* No verifica unicidad del `username`.
* No se asigna automáticamente el campo `createdAt`.
* Relación `Credentials` con `lazy fetch` provoca error al no estar en el contexto de Hibernate.

#### Ejemplo de Payload:

```json
{
  "userId": "{{$randomInt}}",
  "firstName": "Alejandro",
  "lastName": "Cordoba",
  "imageUrl": "{{$randomUrl}}",
  "email": "{{$randomEmail}}",
  "addressDtos": [
    {
      "fullAddress": "123 Main St",
      "postalCode": "12345",
      "city": "New York"
    }
  ],
  "credential": {
    "username": "johndoe",
    "password": "securePassword123",
    "roleBasedAuthority": "ROLE_USER",
    "isEnabled": true,
    "isAccountNonExpired": true,
    "isAccountNonLocked": true,
    "isCredentialsNonExpired": true
  }
}
```

---

# Credentials API - Resumen y Errores Detectados

> ⚠️ Este módulo no implementa encriptación, por lo tanto, las credenciales creadas no son válidas para autenticación en `proxy-client`.

### ✅ Obtener todas las credenciales

* **Método:** GET
* **Ruta:** `/api/credentials`
* **Estado:** Correcto

---

### ✅ Obtener credenciales por ID

* **Método:** GET
* **Ruta:** `/api/credentials/{credentialsId}`
* **Estado:** Correcto

---

### ✅ Obtener credenciales por usuario

* **Método:** GET
* **Ruta:** `/api/credentials/username/{credentialId}`
* **Estado:** Correcto

---

### ❌ Crear credenciales

* **Método:** POST
* **Ruta:** `/api/credentials`

#### Problemas:

* Crea un nuevo usuario en lugar de asociar las credenciales a un usuario existente.

---

### ❌ Actualizar credenciales por query param

* **Método:** PUT
* **Ruta:** `/api/credentials/{credentialId}`
* **Problema:** No actualiza

---

### ❌ Actualizar credenciales por body

* **Método:** PUT
* **Ruta:** `/api/credentials`
* **Problema:** No actualiza

---

### ❌ Eliminar credenciales

* **Método:** DELETE
* **Ruta:** `/api/credentials/{credentialId}`
* **Problema:** No borra


### Ejemplo de payload

```json
{
    "credentialId": "1",
    "username": "johndsdoe",
    "password": "securePassword123",
    "roleBasedAuthority": "ROLE_USER",
    "isEnabled": true,
    "isAccountNonExpired": true,
    "isAccountNonLocked": true,
    "isCredentialsNonExpired": true
}
```

# Address API - Resumen y Errores detectados

### ✅ Obtener todas las direcciones

* **Método:** GET
* **Ruta:** `/api/address`


### ✅ Obtener dirección por id

* **Método:** GET
* **Ruta:** `/api/address/{addressId}`

### ✅ Agregar dirección a un usuario

* **Método:** POST
* **Ruta:** `/api/address`


### ❌ Editar dirección por body

* **Método:** PUT
* **Ruta:** `/api/address`
* **Problema:** No actualiza, crea una nueva

### ❌ Editar dirección por query

* **Método:** PUT
* **Ruta:** `/api/address/{addressId}`
* **Problema:** No actualiza, crea uno nueva


### ✅ Eliminar una dirección

* **Método:** DELETE
* **Ruta:** `/api/address/{addressId}`

### Ejemplo de payload

```json
{
  "addressId": 123,
  "fullAddress": "123 Main Street",
  "postalCode": "12345",
  "city": "Springfield",
  "user": {
    "userId": 1
  }
}
```

# VerificationToken API - Resumen y Errores detectados

### ❌ Obtener todos los tokens

* **Método:** GET
* **Ruta:** `/api/verificationTokens`
* **Problema:** Por el localdate, no se puede obtener porque falta una configuración con jackson

### ❌ Obtener token por id

* **Método:** GET
* **Ruta:** `/api/verificationTokens/{verificationTokenId}`
* **Problema:** Por el localdate, no se puede obtener porque falta una configuración con jackson

### ❌ Crear token a credencial

* **Método:** POST
* **Ruta:** `/api/verificationTokens`
* **Problema:** No crea una nueva, sino que sobreescribe la que llega por id


### ❌ Editar token por body

* **Método:** PUT
* **Ruta:** `/api/verificationTokens`
* **Problema:** Se puede cambiar a que credencial hace referencia el token

### ❌ Editar token por query

* **Método:** PUT
* **Ruta:** `/api/verificationTokens`
* **Problema:** Se puede cambiar a que credencial hace referencia el token

### ❌ Eliminar token

* **Método:** DELETE
* **Ruta:** `/api/verificationTokens`
* **Problema:** No elimina

