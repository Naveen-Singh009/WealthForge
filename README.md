# WealthForge Portfolio Management - API Flow and Integration Guide

This project is a microservices-based portfolio management platform where:
- An investor can create multiple portfolios.
- An investor can buy/sell stocks independently in each portfolio.
- An investor can transfer funds between own portfolios.
- An investor can view per-portfolio and overall performance.

## 1. Service Structure

| Service | Default Port | Main Responsibility |
|---|---:|---|
| Eureka Server | `8761` | Service discovery |
| Auth Service | `8080` | Registration, login, JWT, MFA (OTP) |
| Admin Service | `8083` | Company/stock master data and stock inventory quantity |
| Advisor Service | `8084` | Advisor onboarding, assignment, advice, chatbot |
| Investor Service | `8085` | Investor-facing APIs, trade orchestration, notifications |
| Notification Service | `8087` | Email + websocket notifications |
| Portfolio Service | `8088` | Portfolio, holdings, trade execution, performance |

## 2. Runtime Architecture Flow

```text
Client
  -> Auth Service (JWT token)
  -> Investor Service (Bearer token)
      -> Admin Service (stock availability + quantity update)
      -> Portfolio Service (actual buy/sell in selected portfolio)
      -> Notification Service (trade confirmation)
      -> Advisor Service (advisor list / chatbot advice)
```

## 3. Maven Setup and Run

Use this Maven setup in terminal (Windows CMD):

```bat
set "MAVEN_HOME=C:\Users\naveen.singh\tools\apache-maven-3.9.12"
set "PATH=%MAVEN_HOME%\bin;%PATH%"
```

Recommended startup order:
1. Eureka server
2. Auth service
3. Admin service
4. Advisor service
5. Notification service
6. Portfolio service
7. Investor service

Run each service from its own folder:

```bat
mvn clean spring-boot:run
```

## 4. Authentication and Authorization

Use header on protected APIs:

```http
Authorization: Bearer <jwt-token>
```

JWT includes:
- `sub` = email
- `role` = `ROLE_INVESTOR` / `ROLE_ADVISOR` / `ROLE_ADMIN`
- `userId` = numeric user id used as authenticated principal in downstream services

Role mapping summary:
- `ROLE_ADMIN`: full admin endpoints
- `ROLE_ADVISOR`: advisor endpoints (+ some investor read endpoints)
- `ROLE_INVESTOR`: investor flows and portfolio operations

### 4.1 Role-Wise Registration APIs

| Role | API to create login user | API to create profile | API to login |
|---|---|---|---|
| Investor | `POST /auth/register` with `"role":"INVESTOR"` | Investor profile row is required in `investor_db.investor` | `POST /auth/login` |
| Advisor | No direct auth registration API in current code | `POST /api/advisor/register` | `POST /auth/login` after role is `ADVISOR` in auth DB |
| Admin | No direct auth registration API in current code | Not required | `POST /auth/login` after role is `ADMIN` in auth DB |

Current code enforces public registration only for investors in auth-service.

### 4.2 Clean Onboarding Steps (Investor, Advisor, Admin)

1. Register investor login user:
```http
POST http://localhost:8080/auth/register
Content-Type: application/json

{
  "name": "Investor One",
  "email": "investor1@example.com",
  "password": "invest123",
  "role": "INVESTOR"
}
```
2. Login and get JWT:
```http
POST http://localhost:8080/auth/login
Content-Type: application/json

{
  "email": "investor1@example.com",
  "password": "invest123"
}
```
3. Ensure investor-service master row exists with same id as auth `users.id`:
```sql
INSERT INTO investor (investor_id, investor_name, balance, email)
VALUES (1, 'Investor One', 100000, 'investor1@example.com');
```
4. For advisor login user, create auth user first using `/auth/register` and then promote role in auth DB:
```sql
UPDATE users SET role='ADVISOR' WHERE email='advisor1@example.com';
```
5. Create advisor profile:
```http
POST http://localhost:8084/api/advisor/register
Content-Type: application/json

{
  "name": "Advisor One",
  "email": "advisor1@example.com",
  "phone": "9999999999"
}
```
6. For admin login user, create auth user first using `/auth/register` and then promote role in auth DB:
```sql
UPDATE users SET role='ADMIN' WHERE email='admin1@example.com';
```
7. Use `/auth/login` for admin and advisor and pass returned token as `Bearer`.

## 5. End-to-End Business Flows

### 5.1 Onboard and Login
1. Register/login investor using `/auth/register` and `/auth/login`.
2. Promote advisor/admin roles in auth DB (current code path), then login using `/auth/login`.
3. Create advisor profile using `/api/advisor/register`.
4. If MFA enabled, verify OTP using `/auth/verify-login-otp`.
5. Use returned JWT for all protected APIs.

### 5.2 Portfolio Creation and Independent Trading
1. Create Portfolio A and Portfolio B.
2. Buy stock in Portfolio A using its `portfolioId`.
3. Buy/sell another stock in Portfolio B using a different `portfolioId`.
4. Holdings and cash are managed separately per portfolio.

### 5.3 Buy Flow (Investor -> Admin -> Portfolio -> Notification)
1. Investor calls `POST /api/investor/buy` with `portfolioId`, `symbol`, `quantity`.
2. Investor service checks stock in admin service.
3. Admin service decreases market available quantity (`update-quantity-buy`).
4. Investor service calls portfolio service `/{id}/buy`.
5. Portfolio service debits that portfolio cash, updates holding, records transaction.
6. Notification service sends trade confirmation.

### 5.4 Sell Flow
1. Investor calls `POST /api/investor/sell`.
2. Admin service increases market quantity (`update-quantity-sell`).
3. Portfolio service credits cash and reduces/removes holding.
4. Notification is sent.

### 5.5 Overall Performance
1. Per-portfolio performance from portfolio service.
2. Overall performance aggregates all portfolios for the same investor.

## 6. Full API Catalog (with JSON)

## 6.1 Auth Service (`http://localhost:8080`)

### POST `/auth/register` (Public)
Note: Current implementation accepts public registration only for role `INVESTOR`.

Request:
```json
{
  "name": "Naveen",
  "email": "naveen@example.com",
  "password": "secret123",
  "role": "INVESTOR"
}
```
Response:
```json
{
  "message": "User registered successfully!"
}
```

### POST `/auth/login` (Public)
Request:
```json
{
  "email": "naveen@example.com",
  "password": "secret123"
}
```
Response without MFA:
```json
{
  "token": "<jwt>",
  "type": "Bearer",
  "email": "naveen@example.com",
  "role": "ROLE_INVESTOR"
}
```
Response with MFA enabled:
```json
{
  "message": "OTP sent to email. Verify to complete login."
}
```

### POST `/auth/verify-login-otp` (Public)
Request:
```json
{
  "email": "naveen@example.com",
  "otp": "123456"
}
```
Response:
```json
{
  "token": "<jwt>",
  "type": "Bearer",
  "email": "naveen@example.com",
  "role": "ROLE_INVESTOR"
}
```

### POST `/auth/mfa?enable=true|false` (Authenticated)
Optional query param `email` can be provided when auth principal is absent.
Response:
```json
{
  "message": "MFA updated"
}
```

### POST `/auth/logout` (Authenticated)
Response:
```json
{
  "message": "Logged out successfully. Remove token from client."
}
```

### Test endpoints in auth service
- `GET /api/admin/dashboard`
- `GET /api/admin/users`
- `GET /api/advisor/clients`
- `GET /api/advisor/recommendations`
- `GET /api/investor/portfolio`
- `GET /api/investor/trading`

## 6.2 Admin Service (`http://localhost:8083`)

### Company APIs

#### GET `/api/admin/companies`
Response:
```json
[
  {
    "id": 1,
    "companyName": "Apple Inc.",
    "symbol": "AAPL",
    "sector": "Technology",
    "currentPrice": 178.5,
    "available_quantity": 100000,
    "lastUpdated": "2026-03-04T09:10:00"
  }
]
```

#### POST `/api/admin/companies`
Request:
```json
{
  "companyName": "Apple Inc.",
  "symbol": "AAPL",
  "sector": "Technology",
  "currentPrice": 178.5,
  "available_quantity": 100000
}
```

#### PUT `/api/admin/companies/{id}`
Request same as create company.

#### DELETE `/api/admin/companies/{id}`
Response:
```json
"Company deleted successfully"
```

### Investor API

#### GET `/api/admin/investors`
Response:
```json
[
  {
    "id": 1,
    "name": "Investor One",
    "email": "inv1@example.com",
    "phone": "9999999999"
  }
]
```

### Stock APIs

#### POST `/api/admin/stocks`
Request:
```json
{
  "symbol": "AAPL",
  "name": "Apple Inc",
  "sector": "Technology",
  "currentPrice": 178.5,
  "availableQuantity": 50000
}
```
Response:
```json
"Stock added successfully"
```

#### GET `/api/admin/stocks`
#### GET `/api/admin/stocks/{symbol}`
#### GET `/api/admin/stocks/price/{symbol}`

#### PUT `/api/admin/stocks/update-price`
Request:
```json
{
  "symbol": "AAPL",
  "price": 180.25
}
```

#### PUT `/api/admin/stocks/update-quantity-buy`
Request:
```json
{
  "symbol": "AAPL",
  "quantity": 10
}
```

#### PUT `/api/admin/stocks/update-quantity-sell`
Request:
```json
{
  "symbol": "AAPL",
  "quantity": 10
}
```

#### DELETE `/api/admin/stocks/id/{id}`
#### DELETE `/api/admin/stocks/symbol/{symbol}`

## 6.3 Portfolio Service (`http://localhost:8088`)

All responses are wrapped:
```json
{
  "success": true,
  "message": "text",
  "data": {}
}
```

### POST `/api/portfolios`
Request:
```json
{
  "name": "Growth Portfolio",
  "balance": 100000
}
```
Response `data`:
```json
{
  "id": 11,
  "investorId": 1,
  "name": "Growth Portfolio",
  "balance": 100000.0,
  "createdAt": "2026-03-04T09:20:00"
}
```

### GET `/api/portfolios/my`
### GET `/api/portfolios/{id}`

### GET `/api/portfolios/{id}/holdings`
Response `data` element example:
```json
{
  "id": 200,
  "assetSymbol": "AAPL",
  "assetType": "STOCK",
  "quantity": 5.0,
  "averagePrice": 178.5,
  "currentPrice": 178.5,
  "currentValue": 892.5,
  "profitLoss": 0.0
}
```

### GET `/api/portfolios/{id}/performance`
Response `data`:
```json
{
  "portfolioId": 11,
  "portfolioName": "Growth Portfolio",
  "totalInvested": 892.5,
  "currentValue": 892.5,
  "profitLoss": 0.0,
  "gainPercent": 0.0,
  "holdings": []
}
```

### GET `/api/portfolios/{id}/allocation`
Same payload shape as holdings.

### POST `/api/portfolios/{id}/buy`
Request:
```json
{
  "assetSymbol": "AAPL",
  "quantity": 5,
  "price": 178.5,
  "assetType": "STOCK"
}
```

### POST `/api/portfolios/{id}/sell`
Request:
```json
{
  "assetSymbol": "AAPL",
  "quantity": 2,
  "price": 181,
  "assetType": "STOCK"
}
```

### POST `/api/portfolios/transfer`
Request:
```json
{
  "fromPortfolioId": 11,
  "toPortfolioId": 12,
  "amount": 5000
}
```

### GET `/api/portfolios/overall-performance`
Response `data`:
```json
{
  "totalPortfolios": 2,
  "totalInvested": 25000.0,
  "currentMarketValue": 26800.0,
  "cashBalance": 75000.0,
  "netWorth": 101800.0,
  "profitLoss": 1800.0,
  "gainPercent": 7.2
}
```

### Advisor/Admin views in portfolio service
- `GET /api/advisor/portfolios/{investorId}`
- `GET /api/admin/portfolios`

## 6.4 Investor Service (`http://localhost:8085`)

### Market and advisor discovery

#### GET `/api/investor/companyList`
#### GET `/api/investor/stockList`
#### GET `/api/investor/searchAdvisor`
#### GET `/api/investor/advisor/list/all`

#### GET `/api/investor/getAdvice?question=...`
Response:
```json
"Use SIP strategy and diversify across sectors."
```

### Trade and transfer APIs

#### POST `/api/investor/buy`
Request:
```json
{
  "portfolioId": 11,
  "symbol": "AAPL",
  "quantity": 5
}
```
`investorId` field exists but is deprecated and optional.

Response:
```json
"Stock bought successfully in portfolio 11"
```

#### POST `/api/investor/sell`
Request:
```json
{
  "portfolioId": 11,
  "assetName": "AAPL",
  "quantity": 2
}
```
`investorId` field exists but is deprecated and optional.

Response:
```json
"Stock sold successfully in portfolio 11"
```

#### POST `/api/investor/transfer`
Request:
```json
{
  "toInvestorId": 2,
  "amount": 1000
}
```
`fromInvestorId` exists but is deprecated and optional.

Response:
```json
"Transfer successful"
```

### History and holding APIs

#### GET `/api/investor/transactions/{investorId}`
#### GET `/api/investor/holding/{investorId}`

Transactions response element:
```json
{
  "id": 1,
  "investorId": 1,
  "type": "TRANSFER_DEBIT",
  "assetName": "Fund Transfer",
  "quantity": 0,
  "price": 1000,
  "date": "2026-03-04"
}
```

Holdings response element:
```json
{
  "id": 11,
  "investorId": 1,
  "assetName": "AAPL",
  "quantity": 3
}
```

### Portfolio proxy APIs (Investor Service -> Portfolio Service)

#### POST `/api/investor/portfolios`
Request:
```json
{
  "name": "Income Portfolio",
  "balance": 50000
}
```

#### GET `/api/investor/portfolios`
#### GET `/api/investor/portfolios/{portfolioId}`
#### GET `/api/investor/portfolios/{portfolioId}/holdings`
#### GET `/api/investor/portfolios/{portfolioId}/performance`

#### POST `/api/investor/portfolios/transfer`
Request:
```json
{
  "fromPortfolioId": 11,
  "toPortfolioId": 12,
  "amount": 3000
}
```

#### GET `/api/investor/portfolios/overall-performance`

## 6.5 Advisor Service (`http://localhost:8084`)

### POST `/api/advisor/register` (Public)
Request:
```json
{
  "name": "Advisor One",
  "email": "advisor1@example.com",
  "phone": "9999999999"
}
```

### GET `/api/advisor/list/{advisorId}`
Returns allocated investor IDs.

### GET `/api/advisor/list/all`
Returns advisor records.

### POST `/api/advisor/suggest/{advisorId}`
Request:
```json
[
  {
    "investorId": 1,
    "question": "Is it good time to buy AAPL?",
    "adviceText": "Accumulate gradually with risk limits."
  }
]
```

### POST `/api/advisor/assign`
Request:
```json
{
  "advisorId": 1,
  "investorId": 10
}
```

### GET `/api/advisor/listInvestors/{advisorId}`
Returns investor IDs advised by advisor.

### Chatbot APIs

#### POST `/api/advisor/chatbot/add`
Request:
```json
{
  "question": "What is diversification?",
  "answer": "Diversification means spreading risk."
}
```

#### GET `/api/advisor/chatbot/ask?question=What%20is%20diversification%3F`
Response:
```json
"Diversification means spreading risk."
```

## 6.6 Notification Service (`http://localhost:8087`)

Base mappings supported:
- `/notification`
- `/api/notifications`

### POST `/api/notifications/send`
Request:
```json
{
  "email": "naveen@example.com",
  "message": "Buy order executed successfully"
}
```
Response:
```json
"Notification Sent Successfully"
```

## 7. Recommended External API Usage Pattern

Use:
1. `auth-service` for login/JWT.
2. `investor-service` for investor operations.
3. `admin-service` for admin console operations.
4. `advisor-service` for advisor console operations.

`portfolio-service` can be called directly, but investor-facing clients should usually call via investor-service to keep orchestration (admin quantity update + notification) consistent.

## 8. Important Implementation Notes

1. Buy/sell in investor-service is portfolio-aware via `portfolioId`, so trading is independent per portfolio.
2. Portfolio performance currently uses `MockMarketDataService` (hardcoded symbol prices) for current market value.
3. All services validating JWT must share the same `app.jwt.secret`.
4. Portfolio and investor flows expect `userId` claim in JWT.
