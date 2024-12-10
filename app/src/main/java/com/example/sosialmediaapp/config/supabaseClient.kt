package com.example.supabaseapp

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage


val supabase = createSupabaseClient(
    supabaseUrl = "https://knkgrcdvcevpkocsqfzf.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Imtua2dyY2R2Y2V2cGtvY3NxZnpmIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzM2NjcxOTAsImV4cCI6MjA0OTI0MzE5MH0.mrwC0UhdYsDMy2dufc8lGPKfBwGR3CcH5iGkDXqrasM"
) {

    install(Auth) // Untuk autentikasi
    install(Postgrest) // Untuk operasi database
    install(Storage)
}