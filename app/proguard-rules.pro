# Reglas para el recorte de código (R8) en la versión release.
# Room y WorkManager crean algunas clases por su nombre; estas reglas evitan que R8 las borre o renombre.

-keep class * extends androidx.room.RoomDatabase
-keep class * extends androidx.work.ListenableWorker {
    public <init>(android.content.Context, androidx.work.WorkerParameters);
}
