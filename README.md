# Expense Tracker API

A REST API for tracking personal expenses, built with Java, Spring Boot and PostgreSQL.

## Features
- Add, view, update and delete expenses
- Input validation with clear error messages
- Consistent JSON error responses (404 for missing expenses)
- Stores expense records in PostgreSQL using Spring Data JPA
- Filter expenses by category (case-insensitive)
- Pagination and sorting for the expense list
- Monthly spending summary grouped by category (SQL aggregation)

## Tech Stack
- Java 21
- Spring Boot 4
- Spring Data JPA
- PostgreSQL
- Maven

## Setup
1. Install Java 21 and PostgreSQL.
2. Create a database named `expense_tracker`.
3. Set the environment variable `DB_PASSWORD` to your PostgreSQL password.
4. Run `./mvnw spring-boot:run` (on Windows: `mvnw.cmd spring-boot:run`).
5. The API runs at `http://localhost:8080`.

## API Endpoints
| Method | URL | What it does | Success status |
|---|---|---|---|
| POST | /api/expenses | Adds a new expense | 201 |
| GET | /api/expenses | 	Retrieves all expenses | 200 |
| GET | /api/expenses/{id} | 	Retrieves an expense by ID | 200 |
| PUT | /api/expenses/{id} | 	Updates an existing expense | 200 |
| DELETE | /api/expenses/{id} | Deletes an expense | 204 |
| GET | /api/expenses/summary?year=2026&month=11 | Total spent per category for a month | 200 |

## Query Parameters
`GET /api/expenses` accepts these optional parameters:

| Parameter | Example | What it does |
|---|---|---|
| category | `?category=food` | Returns only that category (ignores capital letters) |
| page | `?page=0` | Which page to return, starting from 0 |
| size | `?size=5` | How many expenses per page |
| sort | `?sort=expenseDate,desc` | Sorts by a field, `asc` or `desc` |

Example: `GET /api/expenses?category=Food&page=0&size=5&sort=expenseDate,desc`
## Example Request
`POST /api/expenses`

```json
{
  "title": "Lunch",
  "amount": 150.50,
  "category": "Food",
  "expenseDate": "2026-11-06"
}
```
## Example Response (Monthly Summary)
`GET /api/expenses/summary?year=2026&month=11`

```json
[
  { "category": "Food", "total": 420.00 },
  { "category": "Travel", "total": 80.00 }
]
```

## What I Learned
- Classes in `src/test` aren't part of the running app. I put my exception handler there by mistake, so validation errors kept using Spring's default response until I moved it to `src/main`.
- A `@RestControllerAdvice` class handles errors for every endpoint in one place, so the controller methods don't repeat error checks and every error has the same JSON shape.
- `findById` returns an `Optional`, so I used `orElseThrow` to return a 404 when an expense doesn't exist.
- PostgreSQL doesn't reuse ids. After I deleted expenses 1 and 2, the next expense got id 3.