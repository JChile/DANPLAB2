package com.example.myapplicationdanp
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import java.util.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()
            NavGraph(navController = navController)
        }
    }
}

@Composable
fun NavGraph (navController: NavHostController){
    val attends = remember { mutableStateListOf<Attendee>() }
    NavHost(
        navController = navController,
        startDestination = Screens.List.route)
    {
        composable(route = Screens.List.route){
            list(navController, attends)
        }
        composable(route = Screens.Data.route){
            register(navController, attends, -1)
        }
        composable(route = "${Screens.Edit.route}/{id}") { backStackEntry  ->
            val id = backStackEntry.arguments?.getString("id")?.toIntOrNull()
            register(navController, attends, id ?: -1)
        }
    }
}

@Composable
fun list(navController: NavHostController, attends: MutableList<Attendee>) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally,

    ){
        Button(onClick = {
            navController.navigate("data_screen")

        },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Icon",
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text("ADD")
            }
        }
        LazyColumn(
            contentPadding = PaddingValues(all = 20.dp),
            verticalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            items(attends){ item ->
                listItemRow(
                    item = item.fullName,
                    onDelete = { attends.remove(item)},
                    onEdit = { navController.navigate("${Screens.Edit.route}/${item.id}") }
                )
            }
        }
    }
}

@Composable
fun listItemRow(item: String, onDelete: () -> Unit, onEdit: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(shape = MaterialTheme.shapes.small)
            .background(color = Color.LightGray)
            .padding(horizontal = 16.dp, vertical = 18.dp)
    ) {
        Row() {
            Text(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                text = item,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )
            IconButton(
                onClick = onEdit,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit"
                )
            }
            Spacer(modifier = Modifier.width(width = 15.dp))
            IconButton(
                onClick = onDelete,
                modifier = Modifier.size(24.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete"
                )
            }
        }
    }
}
class TextFieldState(){
    var text: String by mutableStateOf("")
}

@Composable
fun register(navController: NavHostController, attends: MutableList<Attendee>, id: Int) {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(20.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {

        var valueName = ""
        var valuePhone = ""
        var valueEmail = ""
        var valueAmount = ""
        var blood = remember { TextFieldState() }
        var date = remember { TextFieldState() }

        if(id != -1){
            val existingAttendee = attends.find { it.id == id }
            if(existingAttendee != null){
                valueName= existingAttendee.fullName
                valuePhone= existingAttendee.phone
                valueEmail= existingAttendee.email
                valueAmount= existingAttendee.amountPaid
                blood.text = existingAttendee.bloodType
                date.text =existingAttendee.registrationDate
            }

       }

        var fullName by remember { mutableStateOf(valueName) }
        var phone by remember { mutableStateOf(valuePhone) }
        var email by remember { mutableStateOf(valueEmail) }
        var amount by remember { mutableStateOf(valueAmount) }

        Text(
            text = if (id != -1) "EDIT FORM" else "REGISTER FORM",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        OutlinedTextField(
            value = fullName,
            onValueChange = { fullName = it },
            label = { Text(text = "Full name")},
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Person Icon"
                )
            }
        )
        dropDownMenu(blood)
        showDatePicker(date, LocalContext.current)

        OutlinedTextField(
            value = phone,
            onValueChange = { phone = it },
            label = { Text(text = "Phone")},
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Phone,
                    contentDescription = "Phone Icon"
                )
            }
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text(text = "Email")},
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email) ,
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Email,
                    contentDescription = "Phone Icon"
                )
            }
        )

        OutlinedTextField(
            value = amount.toString(),
            onValueChange = { amount = it },
            label = { Text(text = "Amount")},
            modifier = Modifier.fillMaxWidth(),
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            trailingIcon = {
                Icon(
                    imageVector = Icons.Filled.Star,
                    contentDescription = "Money Icon"
                )
            }
        )

        Button(
            onClick = {
                if(id == -1){
                    val newAttendee = Attendee(
                        id = attends.size + 1,
                        fullName = fullName,
                        registrationDate = date.text,
                        bloodType = blood.text,
                        phone = phone,
                        email = email,
                        amountPaid = amount
                    )
                    attends.add(newAttendee)
                    navController.navigate("list_screen")

                } else {
                    val existingAttendee = attends.find { it.id == id }
                    if(existingAttendee != null) {
                        existingAttendee.fullName = fullName
                        existingAttendee.email = email
                        existingAttendee.phone = phone
                        existingAttendee.amountPaid = amount
                        existingAttendee.bloodType = blood.text
                        existingAttendee.registrationDate = date.text
                        navController.navigate("list_screen")
                    }
                }
            },
            modifier = Modifier
                .padding(top = 8.dp)
                .fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                if(id != -1 ) {
                    Icon(
                        imageVector = Icons.Default.Edit,
                        contentDescription = "Save Icon",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("SAVE")
                } else {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Close Icon",
                        modifier = Modifier.padding(end = 4.dp)
                    )
                    Text("REGISTER")
                }
            }
        }

        Button(onClick = {
            navController.navigate("list_screen")
        },
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Close Icon",
                    modifier = Modifier.padding(end = 4.dp)
                )
                Text("CANCEL")
            }
        }


    }
}



@Composable
fun dropDownMenu(blood : TextFieldState = remember { TextFieldState() }) {
    var expanded by remember { mutableStateOf(false) }
    val suggestions = listOf("A", "B", "AB", "O")
    var textfieldSize by remember { mutableStateOf(Size.Zero)}
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column () {
        OutlinedTextField(
            value = blood.text,
            readOnly = true,
            onValueChange = { blood.text = it },
            modifier = Modifier
                .fillMaxWidth()
                .onGloballyPositioned { coordinates ->
                    textfieldSize = coordinates.size.toSize()
                },
            label = {Text("Blood type")},
            trailingIcon = {
                Icon(
                    imageVector = icon,
                    contentDescription = "contentDescription",
                    Modifier.clickable { expanded = !expanded }
                )
            }

        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current){textfieldSize.width.toDp()})
        ) {
            suggestions.forEach { label ->
                DropdownMenuItem(onClick = {
                    blood.text = label
                    expanded = false
                }) {
                    Text(text = label)
                }
            }
        }
    }

}



@Composable
fun showDatePicker(date : TextFieldState = remember { TextFieldState() }, context: Context){
    val year: Int
    val month: Int
    val day: Int

    val calendar = Calendar.getInstance()
    year = calendar.get(Calendar.YEAR)
    month = calendar.get(Calendar.MONTH)
    day = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val datePickerDialog = DatePickerDialog(
        context,
        {_: DatePicker, year: Int, month: Int, dayOfMonth: Int ->
            date.text = "$dayOfMonth/$month/$year"
        }, year, month, day
    )

    OutlinedTextField(
        value = date.text,
        readOnly = true,
        onValueChange = { date.text = it },
        modifier = Modifier
            .fillMaxWidth(),
        label = {Text("Inscription date")},

        trailingIcon = {
            Icon(
                imageVector = Icons.Filled.DateRange,
                contentDescription = "contentDescription",
                Modifier.clickable { datePickerDialog.show() }
            )
        }

        )

}
