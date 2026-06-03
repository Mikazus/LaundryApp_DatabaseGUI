# 🧺 Pais Clean Wash - Laundry App 

> Aplikasi manajemen laundry berbasis Java dengan database SQL Server (SSMS).
> Proyek Akhir Mata Kuliah Basis Data — Teknik Informatika, Universitas Brawijaya.

---

## 👥 Anggota Tim

| Nama                    | NIM             |
|-------------------------|-----------------|
| *Jonathan Simamora*     | *255150200111045*              |
| *Mikhael Lucien Then*   | *255150201111036* |
| *Almer Firdaus Widjokongko* | *255150201111039* |
| *Surya Adiningrat* | *255150219111005*       |

---

## 📋 Deskripsi Aplikasi

Aplikasi ini mengelola operasional laundry, meliputi:
- Manajemen **pelanggan** (tambah, edit, hapus, cari)
- Manajemen **layanan** dan daftar harga
- Pencatatan **transaksi** masuk dan keluar
- Pembaruan **status** order laundry
- **Laporan** pendapatan per periode

---

## 🗂️ Struktur Proyek

```
laundry-app/
├── database/
│   └── schema.sql              ← Script SQL: buat tabel, seed data, prosedur
├── src/
│   └── main/java/com/laundry/
│       ├── config/
│       │   └── DBConnection.java   ← Konfigurasi koneksi JDBC
│       ├── model/
│       │   ├── Pelanggan.java      
│       │   ├── Layanan.java
│       │   ├── Transaksi.java
│       │   └── DetailTransaksi.java
│       └── ui/
│           └── MainApp.java        ← Entry point 
├── pom.xml                     ← Maven dependencies (JDBC sudah ada)
├── .gitignore
└── README.md
```

---

## ⚙️ Prerequisites

Pastikan semua tools berikut sudah terinstall sebelum clone repo:

| Tool                              | Versi Min | Download |
|-----------------------------------|-----------|---------|
| JDK                               | 17+ | https://adoptium.net |
| IntelliJ IDEA /Netbeans           | 2023+ | https://www.jetbrains.com/idea |
| SQL Server                        | 2019+ | https://www.microsoft.com/sql-server |
| SQL Server Management Studio (SSMS) | 19+ | https://aka.ms/ssmsfullsetup |
| Git                               | Latest | https://git-scm.com |



---

## 🚀 Setup

### LANGKAH 1 — Clone Repositori

```bash
git clone https://github.com/Mikazus/LaundryApp_DatabaseGUI.git
cd LaundryApp_DatabaseGUI
```

---

### LANGKAH 2 — Setup SQL Server

#### 2a. Aktifkan SQL Server

1. Buka **SQL Server Configuration Manager**
2. Pastikan service **SQL Server (SQLEXPRESS)** berstatus **Running**
3. Di **SQL Server Network Configuration → Protocols for SQLEXPRESS**:
   - Aktifkan **TCP/IP** → klik kanan → Enable
   - Double click TCP/IP → tab **IP Addresses** → scroll ke bawah
   - Di bagian **IPAll**, set **TCP Port = 1433**
4. Restart service SQL Server

#### 2b. Aktifkan SQL Server Authentication

1. Buka SSMS, login dengan **Windows Authentication**
2. Klik kanan server di Object Explorer → **Properties**
3. Tab **Security** → pilih **SQL Server and Windows Authentication mode**
4. Klik **OK**, lalu **restart SQL Server service**

#### 2c. Set Password untuk User `sa`

1. Di SSMS: **Security → Logins → sa** → klik kanan → Properties
2. Tab **General** → isi password (contoh: `Admin123!`)
3. Tab **Status** → Login = **Enabled**
4. Klik **OK**

#### 2d. Jalankan Script SQL

1. Buka SSMS
2. Klik **New Query**
3. Buka file `database/schema.sql` dari repo ini
4. Klik **Execute (F5)**
5. Pastikan muncul pesan: `Database LaundryDB berhasil dibuat!`

✅ Verifikasi: di Object Explorer klik Refresh, pastikan database **LaundryDB** muncul.

---

### LANGKAH 3 — Konfigurasi Koneksi Java

Buka file:
```
src/main/java/com/laundry/config/DBConnection.java
```

Ubah bagian konfigurasi sesuai SQL Server Anda:

```java
private static final String DB_SERVER   = "localhost";
private static final String DB_INSTANCE = "SQLEXPRESS";   // atau "MSSQLSERVER" jika default
private static final String DB_NAME     = "LaundryDB";
private static final String DB_USER     = "sa";
private static final String DB_PASSWORD = "Admin123!";    // ← password yang Anda set tadi
private static final int    DB_PORT     = 1433;
```


---

### LANGKAH 4 — Buka di IntelliJ IDEA

1. Buka IntelliJ IDEA
2. **File → Open** → pilih folder `laundry-app`
3. IntelliJ akan mendeteksi `pom.xml` → klik **Trust Project**
4. Tunggu Maven selesai download dependencies (butuh internet pertama kali)
5. Pastikan di pojok kanan bawah tidak ada error merah di Maven

#### Verifikasi JDK

- **File → Project Structure → Project**
- SDK: pilih **JDK 17** atau lebih baru
- Language level: **17**

#### Verifikasi Maven Dependency

Di panel **Maven** (kanan IntelliJ) → expand **Dependencies** → pastikan ada:
```
com.microsoft.sqlserver:mssql-jdbc:12.4.2.jre11
```

Jika tidak muncul: klik tombol **Reload All Maven Projects** (ikon refresh di panel Maven).

---

### LANGKAH 5 — Test Koneksi Database

Buat file sementara untuk test, atau tambahkan sementara di `main()`:

```java
import com.laundry.config.DBConnection;

public class TestConeksi {
    public static void main(String[] args) {
        DBConnection.testConnection();
    }
}
```

Output jika berhasil:
```
[DB] Koneksi berhasil ke LaundryDB
[DB] ✓ Koneksi BERHASIL ke LaundryDB
```

Output jika gagal:
```
[DB] ✗ Koneksi GAGAL: ...
```
→ Lihat bagian **Troubleshooting** di bawah.

---

## 🗄️ Skema Database

```
Pelanggan ─────────────┐
                        ├──► Transaksi ◄──── Karyawan
Layanan ──► Detail_Transaksi ◄──────────────────────
```

### Tabel

| Tabel | Deskripsi |
|-------|-----------|
| `Pelanggan` | Data pelanggan laundry |
| `Karyawan` | Data karyawan & login |
| `Layanan` | Jenis layanan dan harga |
| `Transaksi` | Header transaksi per order |
| `Detail_Transaksi` | Rincian layanan per transaksi |

### Status Transaksi

```
Diterima → Diproses → Selesai → Diambil
```

### Stored Procedures

| Nama | Fungsi |
|------|--------|
| `sp_HitungTotal` | Menghitung dan update total bayar transaksi |
| `sp_LaporanBulanan` | Laporan pendapatan per bulan |

### Views

| Nama | Fungsi |
|------|--------|
| `vw_Transaksi_Lengkap` | Join transaksi dengan data pelanggan & karyawan |


---
## ✨ Pengembangan Lanjut & Deployment

Sebagai langkah pengembangan berikutnya, aplikasi ini juga sedang diarahkan ke versi modern berbasis **TypeScript** agar lebih mudah dikembangkan, diuji, dan di-deploy ke lingkungan web.

Versi deployment dapat diakses di:

[Pais Clean Wash - TypeScript Deployment](https://pais-orcin.vercel.app/)

Beberapa fokus pengembangan lanjutan:
- Antarmuka yang lebih responsif dan siap lintas perangkat
- Struktur kode yang lebih modular untuk maintenance jangka panjang
- Deployment yang lebih cepat melalui platform hosting modern
- Fondasi yang lebih fleksibel untuk fitur web di masa depan

---

## 🔧 Troubleshooting

### ❌ Koneksi GAGAL: "Connection refused"

**Penyebab:** SQL Server belum aktif atau port 1433 belum dibuka.

**Solusi:**
1. Cek SQL Server Configuration Manager → service harus **Running**
2. Aktifkan TCP/IP dan set port 1433 (lihat Langkah 2a)
3. Coba ping: `telnet localhost 1433` di CMD

---

### ❌ Koneksi GAGAL: "Login failed for user 'sa'"

**Penyebab:** SQL Server hanya menerima Windows Authentication, atau password salah.

**Solusi:**
1. Aktifkan SQL Server Authentication (lihat Langkah 2b)
2. Pastikan password benar di `DBConnection.java`
3. Pastikan user `sa` sudah di-Enable (lihat Langkah 2c)

---

### ❌ "Class not found: SQLServerDriver"

**Penyebab:** Maven dependency belum ter-download.

**Solusi:**
1. IntelliJ → panel Maven → klik **Reload All Maven Projects**
2. Atau di terminal: `mvn dependency:resolve`
3. Pastikan ada koneksi internet

---

### ❌ Nama instance tidak ditemukan

**Penyebab:** Nama instance SQL Server berbeda.

**Solusi:**
1. Buka SSMS → lihat nama di Object Explorer (contoh: `DESKTOP-ABC\SQLEXPRESS`)
2. Bagian setelah `\` adalah nama instance
3. Update `DB_INSTANCE` di `DBConnection.java`

---

### ❌ Error saat jalankan schema.sql

**Penyebab:** Koneksi putus di tengah atau syntax error.

**Solusi:**
1. Hapus database dulu jika sudah terbuat sebagian:
   ```sql
   DROP DATABASE IF EXISTS LaundryDB;
   ```
2. Jalankan ulang `schema.sql` dari awal

---

## 🌿 Git Workflow 

```bash
# Clone pertama kali
git clone https://github.com/<username>/<repo>.git

# Buat branch baru untuk fitur
git checkout -b fitur/nama-fitur

# Setelah selesai coding
git add .
git commit -m "feat: tambah fitur [nama fitur]"
git push origin fitur/nama-fitur

# Buat Pull Request di GitHub → minta review → merge ke main
```

### Konvensi Commit Message

| Prefix | Digunakan untuk |
|--------|----------------|
| `feat:` | Fitur baru |
| `fix:` | Perbaikan bug |
| `db:` | Perubahan schema database |
| `docs:` | Update dokumentasi |
| `refactor:` | Refaktor kode |

---

## 📌 NOTE

1. **Jangan commit password** ke GitHub. Edit `DBConnection.java` lokal, jangan di-push.
2. **Jalankan `schema.sql` masing-masing** di komputer sendiri.
3. **Pull sebelum push** (`git pull origin main`) untuk menghindari konflik.
4. **Nama instance SQL Server bisa berbeda** antar komputer — setiap anggota harus set `DB_INSTANCE` sendiri.
5. Jika menggunakan **Windows Authentication** (tanpa password), lihat komentar di `DBConnection.java`.

---

## 📚 Referensi

- [Microsoft JDBC Driver Docs](https://learn.microsoft.com/en-us/sql/connect/jdbc/microsoft-jdbc-driver-for-sql-server)
- [SQL Server Express Download](https://www.microsoft.com/en-us/sql-server/sql-server-downloads)
- [IntelliJ IDEA — Maven Guide](https://www.jetbrains.com/help/idea/maven-support.html)

---

*Proyek ini dibuat untuk memenuhi tugas akhir Mata Kuliah Basis Data.*
*Fakultas Ilmu Komputer — Universitas Brawijaya*
