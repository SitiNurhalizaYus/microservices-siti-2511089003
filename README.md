# Praktikum Arsitektur Berbasis Layanan (Microservices)

Repository ini berisi hasil praktikum mata kuliah **Arsitektur Berbasis Layanan**.
Setiap pertemuan berisi implementasi dan dokumentasi yang diupload secara bertahap.

---

# Daftar Isi

* [Pertemuan 1](#pertemuan-1---setup-environment) - Setup Environment
* [Pertemuan 2](#pertemuan-2---rest-api-sederhana)  - REST API Sederhana
* [Pertemuan 3](#pertemuan-3---service-discovery) - Service Discovery
* [Pertemuan 4](#pertemuan-4---api-gateway) - API Gateway

---

# Pertemuan 1 - Setup Environment

Pada pertemuan ini dilakukan instalasi tools yang dibutuhkan untuk pengembangan microservices.

### Tools yang diinstall:

* Java JDK
* Visual Studio Code
* Docker Desktop
* Postman

### Verifikasi Instalasi

Cek Java

```java
java -version
```

Cek Docker

```java
docker --version
```

Test Docker Container

```java
docker run hello-world
```

### Dokumentasi

* hasil `java -version`
   <img width="1173" height="340" alt="Screenshot 2026-03-08 232549" src="https://github.com/user-attachments/assets/832ab9af-d3e9-488a-9d27-005b2e7e779f" />

* docker berhasil diinstall
   <img width="1543" height="1016" alt="image" src="https://github.com/user-attachments/assets/d56d6c30-1409-4115-97d0-4705088191f9" />

* hasil `docker --version`
   <img width="1199" height="386" alt="image" src="https://github.com/user-attachments/assets/cd54385f-9337-4424-9a0a-a2f07eade9ff" />

* hasil `docker run hello-world`
   <img width="1919" height="980" alt="Screenshot 2026-03-08 232758" src="https://github.com/user-attachments/assets/b982c6c0-9c7c-4a3c-be4a-956716362965" />

* postman berhasil diinstall 
   <img width="1919" height="1019" alt="image" src="https://github.com/user-attachments/assets/f7452a82-0b4b-46ce-9313-980e060331a8" />

* hasil `.\mvnw spring-boot:run`
   <img width="1920" height="1080" alt="Screenshot (1211)" src="https://github.com/user-attachments/assets/97fd98f6-ee79-4424-a4ca-7959f5499138" />

---

# Pertemuan 2 - REST API Sederhana

Pada pertemuan ini dibuat REST API sederhana menggunakan Spring Boot untuk mengelola data produk dengan operasi CRUD (Create, Read, Update, Delete).

### Tujuan Praktikum

* Memahami konsep REST API
* Mengimplementasikan operasi CRUD menggunakan Spring Boot
* Menguji endpoint API menggunakan Postman
* Mendokumentasikan hasil praktikum di GitHub

### Tools yang Digunakan

* Java
* Spring Boot
* Maven
* Visual Studio Code
* Postman

#

### Pembuatan Project Spring Boot

Project dibuat menggunakan **Spring Initializr**.

Buka website berikut:

```java
https://start.spring.io
```

Konfigurasi project:

| Konfigurasi | Nilai             |
| ----------- | ----------------- |
| Project     | Maven             |
| Language    | Java              |
| Spring Boot | 3.x               |
| Group       | com.microservices |
| Artifact    | product-service   |
| Packaging   | Jar               |
| Java        | 17                |

Dependencies yang digunakan:

```java
Spring Web
```

Setelah konfigurasi selesai klik **Generate** untuk mengunduh project.

<img width="1919" height="868" alt="Screenshot 2026-03-08 235044" src="https://github.com/user-attachments/assets/1da9cc1b-300e-4fe5-823d-e459098a56eb" />


#

### Menjalankan Project
Extract project yang telah diunduh kemudian buka folder project menggunakan **Visual Studio Code**.

Struktur project awal:

```java
product-service
│
├── src
├── pom.xml
└── mvnw
```

Setelah implementasi REST API, struktur project menjadi:

```java
product-service
│
├── controller
│   └── ProductController.java
│
├── model
│   └── Product.java
│
├── service
│   └── ProductService.java
│
└── ProductServiceApplication.java
```

<img width="1919" height="1020" alt="Screenshot 2026-03-09 002156" src="https://github.com/user-attachments/assets/461baa5a-e54a-4706-93d4-e2b96e0b2e72" />

Masuk ke folder project:

```java
cd product-service
```

Jalankan aplikasi:

```java
./mvnw spring-boot:run
```

Server berjalan pada:

```java
http://localhost:8080
```
<img width="1919" height="1022" alt="Screenshot 2026-03-09 012353" src="https://github.com/user-attachments/assets/e3ae5e3f-a16f-4a2b-8102-b5a505b850c9" />


#

### Endpoint REST API

| Method | Endpoint       | Deskripsi                |
| ------ | -------------- | ------------------------ |
| GET    | /products      | Menampilkan semua produk |
| POST   | /products      | Menambahkan produk       |
| PUT    | /products/{id} | Mengupdate produk        |
| DELETE | /products/{id} | Menghapus produk         |

#
### Pengujian API Menggunakan Postman
#

### Create Product

Method:

```java
POST
```

Endpoint:

```java
http://localhost:8080/products
```

Body JSON:

```java
{
   "id":1,
   "name":"Mechanical Keyboard",
   "price":850000,
   "category":"Electronics"
}
```
Sebelum memasukan produk
<img width="1908" height="1010" alt="Screenshot 2026-03-09 001619" src="https://github.com/user-attachments/assets/690e3c2d-3894-4f9f-a870-fcf2aef8dae6" />

Setelah menambahkan produk
<img width="1919" height="1021" alt="Screenshot 2026-03-09 001606" src="https://github.com/user-attachments/assets/a047d6f4-e3a6-4c24-b5fa-f56b95ee0df1" />


#

### Read Product

Method:

```java
GET
```

Endpoint:

```java
http://localhost:8080/products
```

Melihat produk yang sudah ditambahkan sebelumnya
<img width="1919" height="1022" alt="Screenshot 2026-03-09 001729" src="https://github.com/user-attachments/assets/8abc42fa-ab87-4d3b-9dc4-30bb1e74c66e" />


#

### Update Product

Method:

```java
PUT
```

Endpoint:

```java
http://localhost:8080/products/1
```

Body JSON:

```java
{
   "id":1,
   "name":"Mechanical Keyboard RGB",
   "price":950000,
   "category":"Electronics"
}
```

Melakukan update data pada field  ```"price":850000``` menjadi ```"price":950000```
<img width="1919" height="1022" alt="Screenshot 2026-03-09 001850" src="https://github.com/user-attachments/assets/3aa53324-b2a0-4294-aee8-9a7ebb9fcfb1" />

Data berhasil diperbarui
<img width="1919" height="1022" alt="Screenshot 2026-03-09 001933" src="https://github.com/user-attachments/assets/6859c41e-6858-43e5-bada-90ead4fb4e4d" />


#

## Delete Product

Method:

```java
DELETE
```

Endpoint:

```java
http://localhost:8080/products/1
```
Menghapus data produk pada ```"id":1```
<img width="1919" height="1020" alt="Screenshot 2026-03-09 002006" src="https://github.com/user-attachments/assets/ad585bb5-8f9d-437a-a11a-d2e18d41ad00" />

Data produk ```"id":1``` berhasil dihapus
<img width="1915" height="1013" alt="Screenshot 2026-03-09 002019" src="https://github.com/user-attachments/assets/faf6e7af-97de-427e-9862-834533334b97" />


#

### Kesimpulan

Pada praktikum ini berhasil dibuat REST API CRUD menggunakan **Spring Boot** untuk mengelola data produk. API ini menyediakan endpoint untuk membuat, membaca, memperbarui, dan menghapus data produk.

---

# Pertemuan 3 - Service Discovery

Pada pertemuan ini diimplementasikan **Service Discovery menggunakan Eureka**.

Komponen sistem:

* Eureka Server
* Focus Session Service

Arsitektur:

```java
Client
   ↓
Eureka Server
   ↓
Microservice
```

Menjalankan Eureka Server:

```java
./mvnw spring-boot:run
```

Dashboard Eureka:

```java
http://localhost:8761
```

### Dokumentasi

* Dashboard Eureka
* Service yang berhasil register

---

# Pertemuan 4 - API Gateway

Pada pertemuan ini dibuat **API Gateway menggunakan Spring Cloud Gateway**.

Fungsi API Gateway:

* menjadi pintu masuk utama sistem
* mengarahkan request ke service yang sesuai

Arsitektur sistem:

```java
Client
   ↓
API Gateway
   ↓
Eureka Server
   ↓
Microservice
```

Contoh akses API melalui Gateway:

```java
http://localhost:8080/sessions
```

### Dokumentasi

* Konfigurasi API Gateway
* Hasil request API melalui Gateway

---

# Author
> _Nama: Siti Nurhaliza Yus_ |
> _NIM: 2511089003_ |
> _Kelas: TRPL- Alih Kredit_


