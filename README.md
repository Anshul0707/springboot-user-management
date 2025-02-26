# User Management Service

## Overview

This Spring Boot application exposes four endpoints:

1. `/create_user` – Create a new user with validations.
2. `/get_users` – Retrieve users based on filters (user_id, mob_num, or manager_id).
3. `/delete_user` – Delete a user by user_id or mob_num.
4. `/update_user` – Update user(s) with individual and bulk update (only manager_id in bulk) support.

## Test Data for Managers

The managers table is pre-populated with:

- **Manager One**:
    - manager_id: 11111111-1111-1111-1111-111111111111
    - full_name: Manager One
    - email: manager.one@example.com
- **Manager Two**:
    - manager_id: 22222222-2222-2222-2222-222222222222
    - full_name: Manager Two
    - email: manager.two@example.com

## API Documentation

Swagger UI is available at: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

## Notes

- The application uses H2 in-memory database; adjust the configuration as needed.
- Detailed logging and error handling are implemented.
- All endpoints validate for missing keys and data constraints.
- For bulk update via `/update_user`, only the manager_id field is allowed.
