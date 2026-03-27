# API Gateway (Nginx) - Single Entry Point

Nginx berfungsi sebagai **API Gateway**: satu pintu masuk (port 80) yang mengarahkan
request ke microservice yang sesuai berdasarkan path URL, sesuai prinsip
"Routing" pada API Gateway pattern.

## Cara Mengaktifkan

Nginx tidak otomatis ikut jalan bersama `docker compose up -d` biasa (supaya tidak
menambah beban resource kalau sedang tidak didemokan). Aktifkan secara eksplisit:

```bash
docker compose --profile with-gateway up -d
```

Pastikan service lain (auth, order, product) sudah jalan duluan, karena Nginx
butuh mereka untuk routing.

## Pemetaan Routing

| Request ke Gateway (port 80) | Diarahkan ke | Service Asli |
|---|---|---|
| `http://localhost/api/auth/**` | `auth-service:8081/api/auth/**` | Auth Service |
| `http://localhost/api/users/**` | `auth-service:8081/api/users/**` | Auth Service |
| `http://localhost/products` | `product-service:8083/products` | Product Service |
| `http://localhost/orders` | `order-service:8082/orders` | Order Service |
| `http://localhost/gateway/health` | (dijawab langsung oleh Nginx) | - |

## Contoh Testing Lewat Gateway

Daripada akses langsung ke `localhost:8081/api/auth/login`, sekarang bisa lewat
gateway di `localhost/api/auth/login` (tanpa port, karena Nginx default port 80):

```bash
curl -X POST http://localhost/api/auth/login \
  -H "Content-Type: application/json" \
  -d "{\"username\": \"siti\", \"password\": \"password123\"}"
```

```bash
curl http://localhost/products
```

```bash
curl -X POST http://localhost/orders \
  -H "Authorization: Bearer <token>" \
  -H "Content-Type: application/json" \
  -d "{\"productId\": 1, \"quantity\": 2, \"email\": \"siti@test.com\"}"
```

Header `Authorization` (untuk JWT) tetap diteruskan apa adanya ke Order Service,
jadi flow validasi token tetap berfungsi normal walau request lewat gateway.

## Kenapa Ini Berguna (Konsep API Gateway)

Tanpa gateway, klien (Postman/frontend) harus tahu port spesifik tiap service
(8081 untuk auth, 8082 untuk order, 8083 untuk product, dst). Dengan gateway,
klien cukup tahu **satu alamat** (port 80), dan gateway yang menentukan ke mana
request itu harus diteruskan berdasarkan path-nya — ini menyederhanakan sisi
klien dan memungkinkan service di belakang gateway berubah tanpa klien perlu tahu.

## Mematikan Gateway

```bash
docker compose --profile with-gateway down
```
