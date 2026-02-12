API Requests for BlueTide Backend

Base URL (local):

    http://localhost:8080

Notes:
- Protected endpoints require an Authorization header: `Authorization: Bearer <JWT>`
- All JSON examples use compact example values; adjust as needed.

---

1) Public

GET /api/public/health
- Description: Health check
- Headers: none
- Example:

```bash
curl -X GET "http://localhost:8080/api/public/health"
```

GET /api/public/info
- Description: Basic service info
- Example:

```bash
curl -X GET "http://localhost:8080/api/public/info"
```

---

2) Authentication

POST /api/auth/register
- Description: Register new user
- Body: RegisterRequest

Sample body:

```json
{
  "displayName": "Alice Owner",
  "email": "alice@example.com",
  "password": "secret123",
  "role": "OWNER",
  "phoneNumber": "+1234567890"
}
```

Example:

```bash
curl -X POST "http://localhost:8080/api/auth/register" \
  -H "Content-Type: application/json" \
  -d '{"displayName":"Alice Owner","email":"alice@example.com","password":"secret123"}'
```

POST /api/auth/login
- Description: Login and receive tokens
- Body: LoginRequest

Sample body:

```json
{
  "email": "alice@example.com",
  "password": "secret123"
}
```

Example:

```bash
curl -X POST "http://localhost:8080/api/auth/login" \
  -H "Content-Type: application/json" \
  -d '{"email":"alice@example.com","password":"secret123"}'
```

POST /api/auth/refresh
- Description: Refresh access token using refresh token
- Body: RefreshTokenRequest

Sample body:

```json
{ "refreshToken": "<REFRESH_TOKEN>" }
```

POST /api/auth/oauth2/google
- Description: Authenticate using Google id token (body contains token)
- Body: OAuth2Request

Sample body:

```json
{ "token": "<GOOGLE_ID_TOKEN>" }
```

POST /api/auth/oauth2/google/access-token
- Description: Authenticate using Google access token
- Body: OAuth2Request

POST /api/auth/logout
- Description: Logout (stateless JWT — client usually drops token)
- No body

GET /api/auth/me
- Description: Get current user info
- Headers: Authorization: Bearer <JWT>

```bash
curl -H "Authorization: Bearer <JWT>" "http://localhost:8080/api/auth/me"
```

POST /api/auth/change-password
- Description: Change password for authenticated user
- Headers: Authorization: Bearer <JWT>
- Body: ChangePasswordRequest

Sample body:

```json
{
  "currentPassword": "secret123",
  "newPassword": "newSecret456"
}
```

---

3) Users

GET /api/users
- Description: List users
- Headers: Authorization: Bearer <JWT> (if protected by security)

```bash
curl -X GET "http://localhost:8080/api/users"
```

GET /api/users/{id}
- Description: Get user by id

```bash
curl -X GET "http://localhost:8080/api/users/USER_ID"
```

POST /api/users
- Description: Create user (saves full `User` model)
- Sample body:

```json
{
  "email": "bob@example.com",
  "password": "pwd12345",
  "displayName": "Bob",
  "role": "MANAGER",
  "companyId": "company123",
  "ownedPropertyIds": ["prop1","prop2"],
  "phoneNumber": "+1234567890",
  "profileImageUrl": "https://example.com/avatar.png",
  "isActive": true,
  "provider": "LOCAL",
  "providerId": null
}
```

PUT /api/users/{id}
- Description: Update user (supply fields to save)
- Example: same body as POST but without id or with id omitted (server sets id path)

DELETE /api/users/{id}
- Description: Delete user

---

4) Companies

GET /api/companies
GET /api/companies/{id}
POST /api/companies
- Body (Company):

```json
{
  "name": "Acme Property Management",
  "managerIds": ["user1","user2"]
}
```

PUT /api/companies/{id}
DELETE /api/companies/{id}

---

5) Properties

GET /api/properties
GET /api/properties/{id}
POST /api/properties
- Sample body (Property):

```json
{
  "address": "123 Main St, Springfield",
  "type": "APARTMENT",
  "environments": 3,
  "floors": 1,
  "hasPool": false,
  "parkingSpots": 1,
  "ownerId": "user123",
  "managerId": "manager456",
  "lastMaintenance": "2026-02-01T12:00:00Z"
}
```

PUT /api/properties/{id}
DELETE /api/properties/{id}

---

6) Inventory

GET /api/inventory
GET /api/inventory/{id}
POST /api/inventory
- Sample body (Inventory):

```json
{
  "propertyId": "prop1",
  "name": "Fridge",
  "brand": "CoolCo",
  "model": "X100",
  "state": "FUNCTIONAL",
  "photos": ["https://example.com/i1.png"]
}
```

PUT /api/inventory/{id}
DELETE /api/inventory/{id}

---

7) Maintenance

GET /api/maintenance
GET /api/maintenance/{id}
POST /api/maintenance
- Sample body (Maintenance):

```json
{
  "inventoryId": "inv1",
  "date": "2026-02-10T09:00:00Z",
  "description": "Quarterly filter replacement",
  "frequency": "QUARTERLY",
  "isCompleted": false
}
```

PUT /api/maintenance/{id}
DELETE /api/maintenance/{id}

---

8) Service Requests

GET /api/services
GET /api/services/{id}
POST /api/services
- Sample body (ServiceRequest):

```json
{
  "propertyId": "prop1",
  "requesterId": "user123",
  "serviceType": "PLUMBING",
  "description": "Leaky faucet in kitchen",
  "status": "OPEN"
}
```

PUT /api/services/{id}
DELETE /api/services/{id}

---

9) Invoices

GET /api/invoices
GET /api/invoices/{id}
POST /api/invoices
- Sample body (Invoice):

```json
{
  "propertyId": "prop1",
  "relatedEntityId": "sr123",
  "serviceType": "PLUMBING",
  "amount": 125.50,
  "paymentDate": "2026-02-10T00:00:00Z",
  "invoiceUrl": "https://files.example.com/invoice123.pdf"
}
```

PUT /api/invoices/{id}
DELETE /api/invoices/{id}

---

10) Notifications

GET /api/notifications
GET /api/notifications/{id}
POST /api/notifications
- Sample body (Notification):

```json
{
  "userId": "user123",
  "type": "SERVICE_UPDATE",
  "relatedEntityId": "sr123",
  "message": "Your service request has been scheduled",
  "read": false
}
```

PUT /api/notifications/{id}
DELETE /api/notifications/{id}

---

Quick tips / next steps
- I can convert this into a Postman collection or a VSCode REST Client `.http` file (one file with all requests) if you'd like. Tell me which format you prefer and I'll create it under `./docs` or `./requests`.
- I can also create a tiny script that generates `curl` commands for every existing entity id in the test fixtures if you want to run end-to-end tests.

If you'd like a Postman collection or `.http` file, say which one and I'll generate it and add it to the repo.

