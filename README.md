#Prefijo

`/user-service`

# User Service API - Resumen y Errores Detectados

---

## Endpoints

### Obtener todos los usuarios

- **GET** `/api/users`  
- Estado: ✔️ Check  
- Observaciones: No hay manejo de excepciones, cualquier error devuelve 500.

---

### Obtener un usuario por ID

- **GET** `/api/users/{userId}`  
- Estado: ✔️ Check

---

### Crear un usuario

- **POST** `/api/users`  

#### Errores detectados:

- Recibe `userId` como parámetro, lo que genera conflictos si el ID ya existe para otro usuario.
- No verifica unicidad en las credenciales (e.g. username único).
- No setea automáticamente el campo `createdAt`.
- Problema con la relación `Credentials` lazy fetch: cuando se intenta acceder, la relación no está en el contexto de Hibernate, provocando fallo.

#### Ejemplo de payload:
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
# Credentials API - Resumen y Errores Detectados

Este modulo no tiene en cuenta la encriptación, por lo que nada de lo que se cree aqui va lograr pasar por la autenticación del proxy-client

Obtener todas las credenciales

GET `/api/credentials` 

Todo bien

Obtener credenciales por id

GET `/api/credentials/{crendentialsId}`

Todo bien

Crear credenciales

POST `/api/credentials`

- Crea un usuario nuevo en lugar de asociar las credenciales a ese usuario


Actualizar credenciales por query param

PUT `/api/credentials/{credentialId}`

- No actualiza

Actualizar credenciales por body

PUT `/api/credentials`

- No actualiza