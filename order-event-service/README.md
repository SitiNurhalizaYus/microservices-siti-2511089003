# Order Event Service - Demo Event Sourcing & CQRS

Service kecil **terpisah** dari 5 microservices utama, khusus untuk mendemonstrasikan
dua konsep: **Event Sourcing** dan **CQRS** (Command Query Responsibility Segregation).
Tidak terhubung dengan flow order/email yang sebenarnya — murni demo konsep, memakai
H2 in-memory database (data reset setiap container restart).

## Konsep yang Didemonstrasikan

### Event Sourcing
Status order **tidak disimpan langsung** sebagai satu baris yang di-update terus.
Sebagai gantinya, setiap kejadian (order dibuat, dibayar, dikirim, dibatalkan)
disimpan sebagai **event terpisah** di tabel `event_store`, bersifat **append-only**
(tidak pernah diubah/dihapus setelah ditulis). Status "saat ini" dihitung ulang
(**replay**) dari seluruh riwayat event itu setiap kali dibutuhkan.

### CQRS (Command Query Responsibility Segregation)
Endpoint untuk **menulis** (command) dan **membaca** (query) dipisah secara eksplisit
di level routing maupun class:
- `OrderCommandController` + `OrderCommandService` → cuma menulis event baru
- `OrderQueryController` + `OrderProjectionService` → cuma membaca & menghitung ulang status

## Endpoint

### Command (Tulis) — `POST /commands/orders/**`

| Endpoint | Fungsi |
|---|---|
| `POST /commands/orders/create` | Catat event ORDER_CREATED |
| `POST /commands/orders/pay` | Catat event ORDER_PAID |
| `POST /commands/orders/ship` | Catat event ORDER_SHIPPED |
| `POST /commands/orders/cancel` | Catat event ORDER_CANCELLED |

### Query (Baca) — `GET /queries/orders/**`

| Endpoint | Fungsi |
|---|---|
| `GET /queries/orders` | Daftar semua order + status terkini (hasil replay) |
| `GET /queries/orders/{orderId}` | Status terkini satu order (hasil replay) |
| `GET /queries/orders/{orderId}/raw-events` | Riwayat event MENTAH (sebelum di-replay) |

## Cara Demo Lengkap

**1. Buat order baru (command):**
```bash
curl -X POST http://localhost:8085/commands/orders/create \
  -H "Content-Type: application/json" \
  -d "{\"orderId\": \"ORD-001\", \"productName\": \"Laptop\", \"quantity\": 2, \"totalPrice\": 30000000}"
```

**2. Cek status (query) — harus CREATED:**
```bash
curl http://localhost:8085/queries/orders/ORD-001
```

**3. Bayar order (command):**
```bash
curl -X POST http://localhost:8085/commands/orders/pay \
  -H "Content-Type: application/json" \
  -d "{\"orderId\": \"ORD-001\"}"
```

**4. Cek status lagi (query) — sekarang harus PAID:**
```bash
curl http://localhost:8085/queries/orders/ORD-001
```

**5. Lihat riwayat event mentah (bukti event sourcing bekerja):**
```bash
curl http://localhost:8085/queries/orders/ORD-001/raw-events
```
Harus muncul 2 baris: `ORDER_CREATED` dan `ORDER_PAID`, masing-masing dengan timestamp
sendiri — ini bukti bahwa status PAID bukan hasil "update" baris CREATED, melainkan
event baru yang ditambahkan, dan status terkini dihitung dari keduanya.

**6. Kirim order (command), lalu cek status lagi — sekarang harus SHIPPED, dengan 3 baris di raw-events.**

## Swagger UI

`http://localhost:8085/swagger-ui.html`

## Kenapa Ini Berguna (Dijelaskan Sederhana)

Kalau status disimpan sebagai 1 kolom yang langsung di-update, begitu order berubah
dari CREATED → PAID, informasi "order ini pernah CREATED kapan" jadi hilang (ke-overwrite).
Dengan Event Sourcing, seluruh riwayat tetap ada — bisa dipakai untuk audit, debugging,
atau bahkan "memutar ulang" kondisi sistem di titik waktu manapun di masa lalu.

CQRS memisahkan beban kerja: sisi tulis bisa fokus pada validasi dan konsistensi data,
sementara sisi baca bisa dioptimasi terpisah (misalnya nanti di-cache, atau dipindah ke
database lain yang lebih cocok untuk query cepat) tanpa mengganggu sisi tulis.
