package com.example.wellbee.frontend.screens.Edukasi

// Model data artikel edukasi
data class ArticleData(
    val id: String,
    val title: String,
    val categories: List<String>,
    val readTime: String,
    val imageRes: Int? = null,
    val content: String
)

// Sumber data dummy (sementara hardcode)
object EducationArticles {
    val articles = listOf(
        ArticleData(
            id = "1",
            title = "Cara Mengatasi Stress Sehari-hari",
            categories = listOf("Kesehatan Mental", "Relaksasi"),
            readTime = "5 menit",
            content = """
                Pernahkah Anda bertanya-tanya bagaimana cara mengelola stres yang muncul setiap hari?

                Stres adalah hal yang wajar, namun jika dibiarkan menumpuk, dapat memengaruhi kesehatan fisik maupun mental. 
                Beberapa langkah sederhana seperti mengatur napas, rutin berolahraga ringan, dan meluangkan waktu untuk diri sendiri 
                bisa membantu menurunkan tingkat stres.

                Cobalah untuk mengidentifikasi pemicu stres utama Anda, lalu buatlah rencana kecil untuk mengelolanya satu per satu. 
                Jangan ragu untuk bercerita kepada orang yang Anda percaya ketika merasa kewalahan.
            """.trimIndent()
        ),
        ArticleData(
            id = "2",
            title = "Pola Makan Sehat untuk Aktivitas Padat",
            categories = listOf("Nutrisi", "Gaya Hidup"),
            readTime = "4 menit",
            content = """
                Memiliki aktivitas yang padat bukan berarti Anda harus mengabaikan pola makan sehat.

                Kunci utama adalah memilih makanan yang bernutrisi seimbang—mengandung karbohidrat kompleks, protein, lemak sehat, 
                serta vitamin dan mineral. Hindari terlalu sering mengonsumsi makanan cepat saji dan minuman tinggi gula.

                Siapkan bekal sederhana dari rumah, seperti salad, buah potong, atau roti gandum dengan isian sehat, 
                agar Anda tetap bertenaga tanpa merasa terlalu kenyang atau mengantuk.
            """.trimIndent()
        ),
        ArticleData(
            id = "3",
            title = "7 Cara Menjaga Postur Tubuh Saat Bekerja",
            categories = listOf("Kesehatan Fisik", "Produktivitas"),
            readTime = "3 menit",
            content = """
                Duduk terlalu lama dengan posisi yang salah dapat menyebabkan nyeri punggung, leher, dan bahu.

                Pastikan posisi kursi, meja, dan layar monitor sejajar dengan pandangan mata. 
                Usahakan telapak kaki menapak lantai, bahu rileks, dan punggung tersandar dengan baik.

                Luangkan waktu setiap 30–60 menit untuk berdiri, menggerakkan leher, lengan, dan punggung. 
                Kebiasaan kecil ini dapat membantu mencegah keluhan muskuloskeletal dalam jangka panjang.
            """.trimIndent()
        )
    )
}
