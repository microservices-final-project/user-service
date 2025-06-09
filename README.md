Obtener todos los usuarios

GET `/user-service/api/users` Check

Errores:

- Cuando se crea un usuario con el objeto Credentials, como la relación es de tipo fetch lazy, cuando se hace el fetch esa relación no esta en el contexto de hibernate y se rompe el endpoint


Obtener un usuario por id

GET `/user-service/api/users/{userId}` Check

Crear un usuario

POST `/user-service/api/users` 

Errores:

- Recibe al userId como parámetro, lo cual rompe cuando el id ya es de otro usuarios

- No maneja la lógica para que las credenciales sean únicas

- No maneja el seteo de createdAt
```
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
Actualizar un usuario

PUT `/user-service/api/users` Check

Errores:

Estaba relacionando el dto a una entidad desligada a la entidad que existe en la base de datos

```
{
    "userId": "7",
    "firstName": "Alejndsdsadsro",
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