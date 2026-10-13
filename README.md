# bank-backend

## Configuration

Set these environment variables before starting the application:

- `DB_URL`: JDBC URL for the MySQL database.
- `DB_USERNAME`: database account name.
- `DB_PASSWORD`: database password.

Do not commit real credentials or put them in source control. Rotate any database credential that was previously committed.

## Security baseline

- The development database-reset endpoint has been removed.
- Database schema changes are not applied automatically at startup; use reviewed migrations.
- SQL statement logging is disabled by default.
- Only `POST /api/auth/login` and `POST /api/uzytkownicy` are currently allowed without authentication.
- All other HTTP routes are denied until the application implements and verifies a real authenticated session/token and per-user authorization.

This intentionally restricts the API. Do not expose it as a usable banking service until authentication, authorization, and security tests are implemented.
