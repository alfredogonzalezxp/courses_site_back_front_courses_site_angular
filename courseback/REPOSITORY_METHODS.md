# Spring Data JPA Repository Methods

This document lists the methods available in your `UserRepository` thanks to extending `JpaRepository<User, Long>`. Even though your interface looks empty, Spring Data JPA implements all of these methods for you automatically.

---

## 1. Creating and Updating Data

| Method | Description |
| :--- | :--- |
| `save(User entity)` | Saves a given entity. If the entity has no ID, it performs an SQL `INSERT`. If the ID exists, it performs an SQL `UPDATE`. |
| `saveAll(Iterable<User> entities)` | Saves all given entities in bulk. |
| `saveAndFlush(User entity)` | Saves an entity and flushes changes instantly to the database. |

---

## 2. Reading Data (Finding)

| Method | Description | Return Type |
| :--- | :--- | :--- |
| `findById(Long id)` | Retrieves an entity by its primary key. | `Optional<User>` |
| `findAll()` | Returns all instances of the type. | `List<User>` |
| `findAllById(Iterable<Long> ids)` | Returns all instances of the type with the given IDs. | `List<User>` |
| `getReferenceById(Long id)` | Returns a reference to the entity (useful for lazy loading). | `User` |

---

## 3. Deleting Data

| Method | Description |
| :--- | :--- |
| `deleteById(Long id)` | Deletes the entity with the given id. |
| `delete(User entity)` | Deletes a given entity. |
| `deleteAllById(Iterable<Long> ids)` | Deletes all instances of the type with the given IDs. |
| `deleteAll(Iterable<User> entities)` | Deletes the given entities. |
| `deleteAll()` | Deletes all entities managed by the repository. (Use with caution). |
| `deleteAllInBatch()` | Deletes all entities in a batch call (faster, but doesn't trigger JPA cascade delete hooks). |

---

## 4. Counting and Existence Verification

| Method | Description | Return Type |
| :--- | :--- | :--- |
| `count()` | Returns the number of entities available. | `long` |
| `existsById(Long id)` | Returns whether an entity with the given id exists. | `boolean` |

---

## 5. Sorting and Pagination (Advanced Read)

| Method | Description | Return Type |
| :--- | :--- | :--- |
| `findAll(Sort sort)` | Returns all entities sorted by the given options. | `List<User>` |
| `findAll(Pageable pageable)` | Returns a `Page` of entities meeting the paging restriction provided in the `Pageable` object. | `Page<User>` |

---

## 6. Custom Methods Defined in UserRepository.java

These are the methods you explicitly declared. Spring Data translates the method names into SQL queries automatically.

| Method | Description | Return Type |
| :--- | :--- | :--- |
| `findByEmail(String email)` | Finds a user based exactly on the provided email string. | `Optional<User>` |
| `existsByEmail(String email)` | Checks if the given email exists in the database. | `boolean` |

---

## Example: Creating Custom "Derived" Queries
You can define more methods just by following the naming convention. Spring writes the SQL.

```java
// Finds all users matching an exact name
List<User> findByNombre(String nombre);

// Finds users where the name contains a specific string (LIKE %search%)
List<User> findByNombreContaining(String searchString);

// Finds users by their Role
List<User> findByRol(String rol);
```
