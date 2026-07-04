package com.example.api

import com.example.BuildConfig
import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

@JsonClass(generateAdapter = true)
data class Part(val text: String? = null)

@JsonClass(generateAdapter = true)
data class Content(val parts: List<Part>)

@JsonClass(generateAdapter = true)
data class GenerationConfig(
    val temperature: Float? = null,
    val topP: Float? = null,
    val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class GenerateContentRequest(
    val contents: List<Content>,
    val generationConfig: GenerationConfig? = null,
    val systemInstruction: Content? = null
)

@JsonClass(generateAdapter = true)
data class Candidate(val content: Content?)

@JsonClass(generateAdapter = true)
data class GenerateContentResponse(val candidates: List<Candidate>?)

interface GeminiApiService {
    @POST("v1beta/models/gemini-3.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body request: GenerateContentRequest
    ): GenerateContentResponse
}

object GeminiClient {
    private const val BASE_URL = "https://generativelanguage.googleapis.com/"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    val service: GeminiApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(MoshiConverterFactory.create())
            .build()
            .create(GeminiApiService::class.java)
    }

    suspend fun generateToast(characterName: String, topic: String, stage: String): String {
        val apiKey = BuildConfig.GEMINI_API_KEY
        if (apiKey.isEmpty() || apiKey == "MY_GEMINI_API_KEY") {
            return getDefaultToast(characterName, topic, stage)
        }

        val prompt = """
            შენ ხარ ქართული სუფრის მონაწილე პერსონაჟი: '$characterName'.
            დაწერე სასაცილო, იუმორისტული, პათეტიკური და ტრადიციული ქართული სადღეგრძელო თემაზე: '$topic'.
            სუფრის მიმდინარე ეტაპია: '$stage'.
            სადღეგრძელო უნდა იყოს წმინდა ქართულ სუფრული სტილით, გაჯერებული სასაცილო შედარებებით და ხუმრობებით. 
            დაწერე მაქსიმუმ 3-4 წინადადება, ძალიან გრძელი არ იყოს. ბოლოში დაამატე სიტყვა "გაუმარჯოს!".
            უპასუხე მხოლოდ ქართულ ენაზე, პირდაპირ სადღეგრძელოს ტექსტით, ყოველგვარი შესავლის გარეშე.
        """.trimIndent()

        val request = GenerateContentRequest(
            contents = listOf(Content(parts = listOf(Part(text = prompt)))),
            generationConfig = GenerationConfig(temperature = 0.85f),
            systemInstruction = Content(parts = listOf(Part(text = "შენ ხარ ქართული იუმორისტული სადღეგრძელოების გენერატორი.")))
        )

        return try {
            val response = service.generateContent(apiKey, request)
            response.candidates?.firstOrNull()?.content?.parts?.firstOrNull()?.text 
                ?: getDefaultToast(characterName, topic, stage)
        } catch (e: Exception) {
            e.printStackTrace()
            getDefaultToast(characterName, topic, stage)
        }
    }

    private fun getDefaultToast(characterName: String, topic: String, stage: String): String {
        return when {
            topic.lowercase().contains("პროგრამისტ") || topic.lowercase().contains("კოდ") -> {
                "ჩვენს პროგრამისტებს გაუმარჯოს, ღამეებს რომ ათენებენ და კოდის წერაში სულს ძვრენენ! როგორც კოდში ერთი მძიმის გამოტოვება აქცევს სისტემას, ისე ჩვენს ცხოვრებაში მეგობრობის გამოკლება არ ყოფილიყოს! გაუმარჯოს გამართულ კოდს და კარგ ხაშს დილით!"
            }
            topic.lowercase().contains("მწვად") || topic.lowercase().contains("ჭამ") -> {
                "ამ ცხელ მწვადს და კარგ მასპინძელს გაუმარჯოს! მწვადივით აშიშხინებულიყოს ჩვენი მტრების გული და ჩვენს ოჯახებში სულ შამფურის ტრიალი ყოფილიყოს! გაუმარჯოს!"
            }
            topic.lowercase().contains("სიყვარულ") -> {
                "სიყვარულს გაუმარჯოს, იმ გრძნობას, ადამიანს კაციდან გიჟად რომ აქცევს და გიჟს – პოეტად! ჩვენს გულებში სიყვარულის ცეცხლი არასოდეს ჩამქრალიყოს და სიყვარულით მთვრალები ვყოფილიყავით და არა მარტო ამ საფერავით! გაუმარჯოს!"
            }
            else -> {
                "ამ ლამაზ დღეს და ჩვენს შეკრებას გაუმარჯოს! თემაზე '$topic' ბევრი თქმულა, მაგრამ მე ვიტყვი: ცხოვრებაში სულ სიხარული, მეგობრობა და პატივისცემა გვქონოდეს! წინაპრების დანატოვარი სუფრა და ტრადიცია არ შეგველახოს. გაუმარჯოს!"
            }
        }
    }
}
