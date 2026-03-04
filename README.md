# WealthForge Pro - Advanced Portfolio Management Platform

WealthForge Pro is a microservices-based portfolio platform for **Investor**, **Financial Advisor**, and **Admin** users.

## Implemented Outcome (as requested)

- Investor registration (`/auth/register` with role `INVESTOR`) now **automatically creates/updates** the investor profile in `investor-service` investor table using the **same user id** as auth.
- Investor can create **multiple portfolios**, each with different initial balance.
- Investor can **buy/sell** in different portfolios independently.
- Investor can **transfer funds between own portfolios** using `/api/investor/transfer`.
- Investor can track **portfolio-wise transaction history** and **overall performance**.
- Admin market data (`admin-service` stocks) is used for current holdings valuation in portfolio analytics (no hardcoded market prices).
- Admin investors list is served from investor-service, so newly registered investors appear in `/api/admin/investors`.

## Services and Ports

| Service | Port | Responsibility |
|---|---:|---|
| Eureka Server | 8761 | Service discovery |
| Auth Service | 8080 | Register/Login/Logout/JWT/MFA |
| Investor Service | 8085 | Investor APIs, buy/sell orchestration, history proxy |
| Admin Service | 8083 | Company + stock market data CRUD, investors list |
| Advisor Service | 8084 | Advisor register, allocations, suggestions |
| Portfolio Service | 8088 | Portfolio creation, holdings, transfers, performance, transactions |
| Notification Service | 8087 | Trade notifications |

## User Stories to API Mapping

## 1) Investor Stories

### Register / Login / Logout
- `POST /register` (alias) or `POST /auth/register`
- `POST /login` (alias) or `POST /auth/login`
- `POST /logout` (alias) or `POST /auth/logout`

`/auth/register` sample:
```json
{
  "name": "Investor One",
  "email": "investor1@example.com",
  "password": "invest123",
  "role": "INVESTOR",
  "initialBalance": 100000
}
```

### List market data + advisor search
- `GET /api/investor/list` (market stocks)
- `GET /api/investor/searchAdvisor`

### Buy / Sell / Transfer
- `POST /api/investor/buy`
- `POST /api/investor/sell`
- `POST /api/investor/transfer` (portfolio-to-portfolio transfer)

Buy request:
```json
{
  "portfolioId": 11,
  "symbol": "AAPL",
  "quantity": 5
}
```

Sell request:
```json
{
  "portfolioId": 11,
  "assetName": "AAPL",
  "quantity": 2
}
```

Transfer request:
```json
{
  "fromPortfolioId": 11,
  "toPortfolioId": 12,
  "amount": 3000
}
```

### History + Performance
- `GET /api/investor/history` (all portfolio transactions for logged-in investor)
- `GET /api/investor/history/{portfolioId}`
- `GET /api/investor/portfolios/{portfolioId}/performance`
- `GET /api/investor/portfolios/overall-performance`

## 2) Financial Advisor Stories

- `POST /api/advisor/register`
- `GET /api/advisor/list/{advisorId}` or `GET /api/advisor/list?advisorId=...` (allocated investors)
- `POST /api/advisor/suggest/{advisorId}` or `POST /api/advisor/advice/{advisorId}`
- `GET /api/advisor/listInvestors/{advisorId}`
- Login/logout via auth:
  - `POST /auth/login`
  - `POST /auth/logout`

## 3) Admin Stories

- List all companies: `GET /api/admin/companies` or `GET /api/admin/companiesList`
- List all investors: `GET /api/admin/investors` or `GET /api/admin/investorsList`
- Add company/stock market data:
  - `POST /api/admin/companies` or `POST /api/admin/add`
  - `POST /api/admin/stocks`
- Update company/stock data:
  - `PUT /api/admin/companies/{id}` or `PUT /api/admin/update/{id}`
  - `PUT /api/admin/stocks/update-price`
- Delete company/stock data:
  - `DELETE /api/admin/companies/{id}` or `DELETE /api/admin/delete/{id}`
  - `DELETE /api/admin/stocks/id/{id}`
  - `DELETE /api/admin/stocks/symbol/{symbol}`

## Important Flow Notes

1. Investor buy/sell flow:
- investor-service validates market stock with admin-service
- admin-service quantity updates are applied
- portfolio-service executes buy/sell in selected portfolio
- notification-service sends confirmation

2. History source:
- Investor history endpoints are backed by portfolio-service transaction records.

3. Performance source:
- Portfolio holdings valuation uses admin-service stock prices.

4. Security:
- Use JWT from `auth-service` in `Authorization: Bearer <token>` for protected endpoints.

## Startup Order

1. Eureka server
2. Auth service
3. Admin service
4. Advisor service
5. Notification service
6. Portfolio service
7. Investor service

Run each service:
```bat
mvn clean spring-boot:run
```
