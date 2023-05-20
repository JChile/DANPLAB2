package com.example.myapplicationdanp

sealed class Screens(val route: String) {
    object List: Screens("list_screen")
    object Data: Screens("data_screen")
    object Edit: Screens("edit_screen")

}
