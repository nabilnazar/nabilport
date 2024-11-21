package com.example.techsmithsample.presentation.ui

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.rounded.Face
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import android.util.Log
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext



@Composable
fun ContactScreen(userName: Map<String, String>) {

    val ctx = LocalContext.current

    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(16.dp)
    ) {

        userName.forEach {
            item() {

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentHeight()
                        .padding(vertical = 25.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(Icons.Rounded.Face, contentDescription = "contact_image")
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = it.key)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(text = it.value)
                    Spacer(modifier = Modifier.width(8.dp))
                    FloatingActionButton(
                        onClick = { callTheNum(it.value,ctx) },
                    ) {
                        Icon(Icons.Filled.Call, "call action button.")
                    }

                }
            }

        }
    }
}

fun callTheNum(phoneNumber: String, ctx: Context) {

    Log.d("nonparsedItem", phoneNumber)

//    val u = Uri.parse("tel:" + phoneNumber)
//    Log.d("parsedItem", u.toString())


    // Create the intent and set the data for the
    // intent as the phone number.
   // val i = Intent(Intent.ACTION_DIAL, phoneNumber)
    try {

        // Launch the Phone app's dialer with a phone
        // number to dial a call.
        ctx.startActivity(i)
    } catch (s: SecurityException) {

        // show() method display the toast with
        // exception message.
        Toast.makeText(ctx, "An error occurred", Toast.LENGTH_LONG)
            .show()
    }

}
