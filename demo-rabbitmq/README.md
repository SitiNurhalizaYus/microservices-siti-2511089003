# Demo RabbitMQ - Producer & Consumer Dasar

Folder ini **terpisah** dari 5 microservices utama (auth-service, order, product, email-service,
eureka-service). Isinya adalah contoh paling dasar dari pola **producer-consumer** memakai RabbitMQ,
yang menjadi fondasi konsep dari pola yang sama dipakai antara Order Service dan Email Service.

## Apa yang didemonstrasikan

- **Producer** (port 8080): menerima POST request lewat `/api/send`, lalu mempublish pesan JSON
  ke dua queue RabbitMQ (`consumerQueue` dan `emailQueue`).
- **Consumer** (port 8086): mendengarkan (`@RabbitListener`) queue `consumerQueue`, lalu mencetak
  pesan yang masuk ke console.

Ini **bukan** bagian dari flow bisnis order/email yang sebenarnya — murni demo konsep dasar
agar mudah dijelaskan secara terpisah tanpa kerumitan JWT, Eureka, atau database.

## Prasyarat

- Java 17 dan Maven terinstall (atau pakai `./mvnw` / `mvnw.cmd` yang sudah disertakan).
- **RabbitMQ harus sudah jalan** di `localhost:5672` sebelum menjalankan producer/consumer.
  Cara termudah: jalankan RabbitMQ lewat Docker (terpisah dari project utama):
  ```bash
  docker run -d --name demo-rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3.12-management
  ```
  (Kalau project utama `microservices-siti` sedang jalan lewat `docker compose up`, RabbitMQ-nya
  sudah otomatis tersedia di `localhost:5672` juga — tidak perlu jalankan RabbitMQ tambahan.)

## Cara Menjalankan Manual (tanpa Docker, sesuai arahan dosen)

Jalankan di **2 terminal terpisah**, dari folder masing-masing (`demo-rabbitmq/consumer` dan
`demo-rabbitmq/producer`).

### Terminal 1 — Jalankan Consumer dulu

```bash
cd demo-rabbitmq/consumer
mvnw.cmd spring-boot:run
```

Tunggu sampai muncul log `Started ConsumerApplication`. Consumer sekarang sedang "mendengarkan"
queue `consumerQueue`, siap menerima pesan kapan saja.

### Terminal 2 — Jalankan Producer

```bash
cd demo-rabbitmq/producer
mvnw.cmd spring-boot:run
```

Tunggu sampai muncul log `Started ProducerApplication`.

### Kirim Pesan Percobaan (lewat Postman atau curl)

```bash
curl -X POST http://localhost:8080/api/send \
  -H "Content-Type: application/json" \
  -d "{\"orderId\": 1, \"item\": \"Laptop\", \"qty\": 2}"
```

Atau lewat Postman:
- Method: `POST`
- URL: `http://localhost:8080/api/send`
- Body → raw → JSON: `{"orderId": 1, "item": "Laptop", "qty": 2}`

### Hasil yang Diharapkan

Di **Terminal 2 (Producer)**, akan muncul log:
```
Sent: {"orderId":1,"item":"Laptop","qty":2}
```

Di **Terminal 1 (Consumer)**, hampir bersamaan akan muncul log:
```
Received: {"orderId":1,"item":"Laptop","qty":2}
```

Kalau kedua log ini muncul, berarti demo producer-consumer berhasil — pesan benar-benar mengalir
lewat RabbitMQ dari Producer ke Consumer secara asynchronous.

## Menghentikan

Tekan `Ctrl+C` di masing-masing terminal untuk menghentikan Producer dan Consumer.

## Kaitan dengan Project Utama

| Demo (folder ini) | Project Utama |
|---|---|
| Producer kirim pesan manual lewat endpoint `/api/send` | Order Service kirim pesan otomatis setelah order berhasil disimpan |
| Consumer cuma `println` pesan yang masuk | Email Service betul-betul mengirim email lewat SMTP |
| Tidak ada validasi JWT, tidak ada Eureka discovery aktif dipakai | Order Service validasi JWT ke Auth Service via Eureka sebelum proses |
| Queue: `consumerQueue` & `emailQueue` (nama beda, tidak dipakai bersama) | Queue: `emailQueue` (dipakai konsisten Order → Email) |
