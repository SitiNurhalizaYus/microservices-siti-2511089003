# ELK Stack - Centralized Logging per Service

Folder ini berisi konfigurasi untuk Logstash dan Filebeat. Elasticsearch dan Kibana
tidak butuh file config khusus (pakai environment variable saja di docker-compose.yaml).

## Arsitektur Logging

```
[5 Microservices]  --tulis log ke file-->  [shared volume app-logs]
       |                                            |
       |                                    [Filebeat membaca]
       |                                            |
       |                                            v
       |                                      [Logstash :5044]
       |                                   (parsing + tambah label
       |                                    service_name per baris log)
       |                                            |
       |                                            v
       |                                    [Elasticsearch :9200]
       |                              (index: microservices-<service>-<tanggal>)
       |                                            |
       |                                            v
       +------------------------------------>   [Kibana :5601]
                                          (browse & filter log per service)
```

**Poin penting:** hanya SATU Elasticsearch dan SATU Kibana untuk semua service
(sesuai arahan dosen), tapi setiap baris log diberi label `service_name` dan disimpan
ke index Elasticsearch yang terpisah per service per hari, contoh:
- `microservices-auth-service-2026.06.28`
- `microservices-order-service-2026.06.28`
- `microservices-product-service-2026.06.28`
- `microservices-email-service-2026.06.28`
- `microservices-eureka-service-2026.06.28`

Sehingga di Kibana, log tetap bisa difilter dan dilihat terpisah per service, baik lewat
nama index maupun lewat field `service_name`.

## Cara setiap service menulis log

Setiap service (lihat `application.properties` masing-masing) dikonfigurasi menulis log ke
file `/var/log/app/<nama-service>.log` di dalam container, contoh:
- auth-service → `/var/log/app/auth-service.log`
- order-service → `/var/log/app/order-service.log`
- dst.

Kelima service "berbagi" satu Docker named volume (`app-logs`), sehingga Filebeat (yang
mount volume yang sama secara read-only) bisa membaca semua file log itu sekaligus.

## Cara Menjalankan

ELK Stack ini **otomatis ikut jalan** bersama `docker compose up -d` (tidak perlu profile
khusus). Karena cukup berat (Elasticsearch minimal butuh ~512MB-1GB RAM), pastikan laptop
kamu punya cukup resource bebas sebelum menjalankan semuanya sekaligus.

```bash
docker compose up -d --build
```

Tunggu lebih lama dari biasanya (Elasticsearch & Kibana startup-nya cukup lama, bisa 1-3 menit).

Cek statusnya:
```bash
docker compose ps
```

## Verifikasi di Kibana

1. Buka `http://localhost:5601`
2. Menu utama (hamburger icon kiri atas) → **Stack Management** → **Index Patterns** (atau **Data Views** di versi baru)
3. Klik **Create data view** / **Create index pattern**
4. Index pattern: `microservices-*` (tanda bintang mencakup semua service)
5. Time field: pilih `@timestamp`
6. Klik **Create**
7. Buka menu **Discover** (ikon kompas di sidebar kiri) untuk mulai melihat log yang masuk
8. Untuk filter per service, ketik di search bar: `service_name: "auth-service"`
   (ganti nama service sesuai yang ingin dilihat)

## Troubleshooting

**Tidak ada log muncul di Kibana setelah beberapa menit:**
```bash
# Cek Filebeat berhasil baca file log
docker compose logs filebeat --tail=50

# Cek Logstash menerima & memproses data
docker compose logs logstash --tail=50

# Cek index sudah terbentuk di Elasticsearch
curl http://localhost:9200/_cat/indices?v
```

**Elasticsearch gagal start / langsung exit:**
Biasanya karena `vm.max_map_count` terlalu kecil di sistem host (umum terjadi di Linux,
lebih jarang di Windows/Docker Desktop). Kalau terjadi, jalankan di WSL2 terminal:
```bash
sudo sysctl -w vm.max_map_count=262144
```
