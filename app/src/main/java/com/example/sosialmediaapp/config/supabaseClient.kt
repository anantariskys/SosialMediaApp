package com.example.supabaseapp

import io.github.jan.supabase.auth.Auth
import io.github.jan.supabase.createSupabaseClient
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.storage.Storage


val supabase = createSupabaseClient(
    supabaseUrl = "https://yyvuyecllzzyjvpvrbga.supabase.co",
    supabaseKey = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6Inl5dnV5ZWNsbHp6eWp2cHZyYmdhIiwicm9sZSI6ImFub24iLCJpYXQiOjE3MzI1MTExMjcsImV4cCI6MjA0ODA4NzEyN30.H7lnC8z0iK6-o9xY0lRuVQxIqt2DW0VHa7VzWnYiGDU"
) {

    install(Auth) // Untuk autentikasi
    install(Postgrest) // Untuk operasi database
    install(Storage)
}