package com.example.rejournal.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CleaningServices
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.DirectionsRun
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.FamilyRestroom
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.Label
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MonitorHeart
import androidx.compose.material.icons.filled.MovieFilter
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Nightlight
import androidx.compose.material.icons.filled.Pets
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.SportsSoccer
import androidx.compose.material.icons.filled.Spa
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.filled.Work
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.ui.graphics.vector.ImageVector

object ActivityIcons {

    val palette: List<Pair<String, ImageVector>> = listOf(
        "fitness" to Icons.Filled.FitnessCenter,
        "run" to Icons.Filled.DirectionsRun,
        "bike" to Icons.Filled.DirectionsBike,
        "soccer" to Icons.Filled.SportsSoccer,
        "family" to Icons.Filled.FamilyRestroom,
        "friends" to Icons.Filled.Groups,
        "pet" to Icons.Filled.Pets,
        "work" to Icons.Filled.Work,
        "school" to Icons.Filled.School,
        "book" to Icons.Filled.MenuBook,
        "reading" to Icons.Filled.Book,
        "park" to Icons.Filled.Park,
        "sun" to Icons.Filled.WbSunny,
        "spa" to Icons.Filled.Spa,
        "meditate" to Icons.Filled.SelfImprovement,
        "sleep" to Icons.Filled.Nightlight,
        "food" to Icons.Filled.Restaurant,
        "coffee" to Icons.Filled.LocalCafe,
        "water" to Icons.Filled.Water,
        "cleaning" to Icons.Filled.CleaningServices,
        "shopping" to Icons.Filled.ShoppingCart,
        "music" to Icons.Filled.MusicNote,
        "headphones" to Icons.Filled.Headphones,
        "movie" to Icons.Filled.MovieFilter,
        "games" to Icons.Filled.SportsEsports,
        "art" to Icons.Filled.Brush,
        "phone" to Icons.Filled.Phone,
        "health" to Icons.Filled.MonitorHeart,
        "face" to Icons.Filled.Face,
        "memory" to Icons.Filled.Memory,
        "photo" to Icons.Filled.PhotoCamera
    )

    private val defaultTagIcons: Map<String, ImageVector> = mapOf(
        "Exercise" to Icons.Filled.FitnessCenter,
        "Family" to Icons.Filled.FamilyRestroom,
        "Friends" to Icons.Filled.Groups,
        "Work" to Icons.Filled.Work,
        "Reading" to Icons.Filled.MenuBook,
        "Outdoors" to Icons.Filled.Park,
        "Relaxing" to Icons.Filled.SelfImprovement,
        "Eating well" to Icons.Filled.Restaurant,
        "Chores" to Icons.Filled.CleaningServices
    )

    fun byId(id: String?): ImageVector? = palette.find { it.first == id }?.second


    fun resolve(tagName: String, customIconId: String?): ImageVector {
        byId(customIconId)?.let { return it }
        defaultTagIcons[tagName]?.let { return it }
        return Icons.Filled.Label
    }
}