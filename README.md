# MarketPulse

## Run
1) docker compose up -d
2) export JWT_SECRET='change-me-please-change-me-please-change-me-please'
3) mvn spring-boot:run

## API
- POST /api/auth/register
- POST /api/auth/login
- GET /api/me
- GET /api/marketplaces
- POST /api/jobs/enqueue

## Notes
- JWT_SECRET must be at least 32 bytes
- This stage includes auth, workspaces, job queue, job dedupe, and seed marketplaces
