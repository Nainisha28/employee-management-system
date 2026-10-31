# Fast manual test script

1. Start MySQL, configure `DB_PASSWORD`, then `mvn clean spring-boot:run`.
2. Sign in at `http://localhost:8080` with `admin` / `Admin@12345`.
3. In MySQL Workbench run `database/reference-schema.sql`, then `database/seed.sql`.
4. Refresh browser; verify rows display. Search `Rahul`, then add department `IT` and status `Active` to verify combined filters.
5. Switch page size to 5, then sort by salary ascending and descending.
6. Add a new IT employee. Verify the API-generated employee code is the next `I####` code.
7. Transfer that employee to HR. Verify the new code is `H####`, employee ID is unchanged, and transfer history has a record.
8. Edit employee details and deactivate an employee; status should become Inactive.
9. Try invalid mobile/Aadhaar/PAN/email and salary 0. The browser and API should reject invalid values.
10. Sign out and try `GET http://localhost:8080/api/employees` in a private window; it should return 401.
11. Use the included Postman collection or Swagger UI for API demonstrations.


