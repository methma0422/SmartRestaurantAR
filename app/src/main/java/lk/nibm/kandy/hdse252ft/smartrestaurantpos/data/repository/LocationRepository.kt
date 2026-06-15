package lk.nibm.kandy.hdse252ft.smartrestaurantpos.data.repository

import android.annotation.SuppressLint
import android.location.Location
import com.google.android.gms.location.FusedLocationProviderClient
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class LocationRepository @Inject constructor(
    private val fusedLocationClient: FusedLocationProviderClient
) {
    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(): Location? {
        return try {
            // Task.await() requires 'kotlinx-coroutines-play-services' which is included in play-services-location
            fusedLocationClient.lastLocation.await()
        } catch (e: Exception) {
            null
        }
    }
}
