# Kubernetes Deployment - Microservices Siti

Folder ini berisi manifest Kubernetes untuk menjalankan seluruh project microservices
(Eureka, Auth, Order, Product, Email Service, RabbitMQ, MySQL x3, Prometheus, Grafana)
di **Docker Desktop Kubernetes**.

## Prasyarat

1. Docker Desktop dengan Kubernetes diaktifkan (Settings → Kubernetes → Enable Kubernetes).
2. `kubectl` sudah tersedia (otomatis terpasang bersama Docker Desktop Kubernetes).
3. Image Docker untuk 5 service (`microservices-siti-eureka-server`, `microservices-siti-auth-service`,
   `microservices-siti-order-service`, `microservices-siti-product-service`, `microservices-siti-email-service`)
   **sudah pernah di-build** lewat `docker compose build` atau `docker compose up --build` di root project ini.
   Docker Desktop Kubernetes berbagi image registry yang sama dengan Docker Engine, jadi manifest ini
   memakai `imagePullPolicy: IfNotPresent` agar langsung pakai image lokal tanpa perlu push ke registry.

## Isi Kredensial Email (Secret) — WAJIB sebelum apply

File `02-secret.yaml.example` adalah **template** yang aman di-commit ke Git (tidak berisi
kredensial asli). Sebelum `kubectl apply`, kamu perlu buat file asli dari template ini:

```bash
# 1. Copy template, buang ekstensi .example
copy k8s\02-secret.yaml.example k8s\02-secret.yaml
```

Lalu edit `k8s\02-secret.yaml` (bukan yang `.example`), isi `MAIL_USERNAME` dan
`MAIL_PASSWORD` dengan nilai **base64**:

```powershell
[Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes("emailkamu@gmail.com"))
[Convert]::ToBase64String([Text.Encoding]::UTF8.GetBytes("app_password_16_digit_gmail"))
```

Salin hasilnya ke field yang sesuai di `k8s/02-secret.yaml`. File ini (tanpa `.example`)
sudah otomatis di-ignore oleh `.gitignore`, jadi aman walau sudah diisi kredensial asli —
tidak akan ikut ter-commit ke Git.

## Cara Deploy

Jalankan dari root folder project (pastikan image sudah di-build dulu):

```bash
# 1. Pastikan image sudah ada (build dulu lewat docker compose kalau belum)
docker compose build

# 2. Apply semua manifest sesuai urutan nomor file (penting untuk dependency)
kubectl apply -f k8s/

# 3. Cek semua pod sudah Running
kubectl get pods -n microservices-siti

# 4. Cek semua service
kubectl get svc -n microservices-siti
```

Tunggu beberapa menit sampai semua pod `Running` dan `READY` (terutama MySQL butuh waktu inisialisasi,
dan service Spring Boot menunggu lewat initContainer sampai Eureka/Auth/RabbitMQ siap).

## Akses dari Browser (lewat NodePort)

Setiap service punya Service tipe `NodePort` tambahan untuk akses dari luar cluster:

| Service              | URL                                  |
|----------------------|---------------------------------------|
| Eureka Dashboard     | http://localhost:30761                |
| Auth Service         | http://localhost:30081/swagger-ui.html|
| Order Service        | http://localhost:30082/swagger-ui.html|
| Product Service      | http://localhost:30083/swagger-ui.html|
| Email Service        | http://localhost:30084                |
| RabbitMQ Management  | http://localhost:31567 (guest/guest)  |
| Prometheus           | http://localhost:30090                |
| Grafana              | http://localhost:30300 (admin/admin)  |

## Urutan File (untuk referensi, kalau mau apply manual satu-satu)

| File | Isi |
|---|---|
| `00-namespace.yaml` | Namespace `microservices-siti` |
| `01-configmap.yaml` | Env var bersama (Eureka URL, RabbitMQ host, dst) |
| `02-secret.yaml.example` | Template Secret kredensial Gmail (aman di-commit, copy & isi sendiri sebelum apply) |
| `10-12-mysql-*.yaml` | 3 instance MySQL terpisah (auth, order, product) + PVC |
| `13-rabbitmq.yaml` | RabbitMQ message broker |
| `20-eureka-server.yaml` | Eureka Server (service discovery) |
| `21-24-*-service.yaml` | Auth, Order, Product, Email Service |
| `30-34-*.yaml` | Prometheus & Grafana monitoring |

## Menghapus Semua Resource

```bash
kubectl delete -f k8s/
```

Atau hapus seluruh namespace sekaligus (lebih cepat, otomatis hapus semua resource di dalamnya):

```bash
kubectl delete namespace microservices-siti
```

## Catatan Penting

- **initContainer** dipakai pada Order, Product, Email Service untuk menunggu Eureka Server
  (dan RabbitMQ/Auth Service untuk yang membutuhkan) siap sebelum container utama start —
  ini menggantikan fungsi `depends_on: condition: service_healthy` di Docker Compose, karena
  Kubernetes tidak punya mekanisme `depends_on` bawaan.
- **PersistentVolumeClaim** dipakai untuk ketiga MySQL agar data tidak hilang saat pod restart.
- Kredensial database memakai `MYSQL_ALLOW_EMPTY_PASSWORD=yes` (sama seperti di Docker Compose) —
  cocok untuk keperluan akademik/development, **bukan untuk production**.

## Status Project
Seluruh komponen sudah diuji dan berjalan dengan baik di Docker Compose maupun Kubernetes.
