# SMART UMKM: Aplikasi POS Mobile untuk UMKM

SMART UMKM adalah aplikasi *Point of Sale* (POS) berbasis *mobile* yang dirancang khusus untuk membantu UMKM (Usaha Mikro, Kecil, dan Menengah) dalam mengelola penjualan mereka. Aplikasi ini dibangun menggunakan Android Studio dengan bahasa pemrograman Kotlin dan terdiri dari dua bagian utama: aplikasi admin dan aplikasi pengguna (kasir). Data aplikasi ini didukung oleh penyimpanan *offline* SQLite dan *online* dengan *backend* API dari aplikasi Laravel.

## Fitur-fitur Utama

### Aplikasi Admin:

*   **Dashboard Transaksi:** Menampilkan data transaksi secara keseluruhan.
*   **Manajemen Kategori:** CRUD (Create, Read, Update, Delete) atau kelola kategori produk.
*   **Manajemen Produk:** CRUD atau kelola data produk.
*   **Manajemen Pengguna:** CRUD atau kelola data pengguna serta hak akses (admin/kasir).

### Aplikasi Pengguna (Kasir):

*   **Daftar Produk:** Menampilkan daftar produk yang tersedia.
*   **Transaksi:** Melakukan transaksi penjualan.
*   **Data Transaksi:** Menampilkan data transaksi per pengguna.
*   **Profil:** Menampilkan informasi profil pengguna.

## Teknologi yang Digunakan

*   **Android Studio:** Lingkungan pengembangan terpadu (IDE) untuk pengembangan aplikasi Android.
*   **Kotlin:** Bahasa pemrograman modern untuk pengembangan Android.
*   **SQLite:** Database *embedded* untuk penyimpanan data *offline*.
*   **Laravel:** *Framework* PHP untuk pengembangan *backend* API.

## Arsitektur Aplikasi

Aplikasi ini menggunakan arsitektur *client-server*, di mana aplikasi *mobile* (klien) berkomunikasi dengan *backend* API (server) yang dibangun dengan Laravel. Data transaksi dan informasi lainnya disimpan di *database* MySQL (untuk online) dan SQLite (untuk offline).

## Cara Menjalankan Aplikasi

1.  **Aplikasi Admin:**
    *   Buka proyek `SMART_UMKM` di Android Studio.
    *   Pastikan semua *dependency* sudah terinstal.
    *   Klik tombol "Run" untuk menjalankan aplikasi.
    *   atau bisa install applikasi pada mobile device
2.  **Aplikasi Pengguna:**
    *   Buka proyek `SMART_UMKM_USER` di Android Studio.
    *   Pastikan semua *dependency* sudah terinstal.
    *   Klik tombol "Run" untuk menjalankan aplikasi.
    *   atau bisa install applikasi pada mobile device
3.  **Backend API:**
    *   Buka proyek `SMART_UMKM_API` di *text editor* atau IDE (misalnya PHPStorm).
    *   Konfigurasi *database* dan *environment* yang diperlukan.
    *   Jalankan *server* Laravel.

## Cara Menggunakan Aplikasi

1.  **Aplikasi Admin:**
    *   Login dengan *username* dan *password* yang valid.
    *   Navigasi menu untuk mengelola kategori, produk, pengguna, dan melihat data transaksi.
2.  **Aplikasi Pengguna:**
    *   Login dengan *username* dan *password* yang valid.
    *   Lihat daftar produk dan lakukan transaksi penjualan.
    *   Lihat data transaksi Anda dan informasi profil.

## Repositori

*   **SMART UMKM Admin:** [https://github.com/ferynnd/SMART_UMKM](https://github.com/ferynnd/SMART_UMKM)
*   **SMART UMKM User:** [https://github.com/ferynnd/SMART_UMKM_USER](https://github.com/ferynnd/SMART_UMKM_USER)
*   **Laravel API:** [https://github.com/ferynnd/SMART_UMKM_API](https://github.com/ferynnd/SMART_UMKM_API)


## Kontak

Ferry Fernando

*   Email: ferryfernandno164@gmail.com
*   LinkedIn: [In](linkedin.com/in/ferryfernandoo)
*   GitHub: [Github](github.com/ferynnd)

## Catatan Tambahan

Dokumentasi ini mungkin tidak mencakup semua detail proyek. Jika Anda memiliki pertanyaan lebih lanjut, jangan ragu untuk menghubungi saya.

Terima kasih telah menggunakan SMART UMKM!
