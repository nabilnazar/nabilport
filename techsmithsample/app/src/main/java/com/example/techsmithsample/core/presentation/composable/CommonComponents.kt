package com.example.techsmithsample.core.presentation.composable


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.techsmithsample.R
import com.example.techsmithsample.presentation.ui.theme.montserratFamily


@Composable
fun CartButton(
    modifier: Modifier = Modifier,
    quantity: Int?,
    onValueChange: (Int) -> Unit
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        if (quantity == 0) {
            OutlinedButton(
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                onClick = {
                    onValueChange(quantity + 1)
                }) {
                Text(
                    stringResource(id = R.string.str_add_to_cart),
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    fontFamily = montserratFamily,
                    fontWeight = FontWeight.SemiBold
                )
            }
        } else {
            Row {
                OutlinedIconButton(
                    modifier = Modifier.defaultMinSize(0.dp, 0.dp),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                    content = {
                        Icon(
                            painterResource(id = R.drawable.ic_minus),
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            contentDescription = stringResource(id = R.string.str_minus)
                        )
                    },
                    onClick = {
                        if ((quantity ?: 0) > 1) {
                            onValueChange(quantity!! - 1)
                        } else {
                            onValueChange(0)
                        }
                    }
                )
            }
            Text(
                quantity.toString(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                fontFamily = montserratFamily,
                modifier = Modifier
                    .width(40.dp)
            )
            OutlinedIconButton(
                modifier = Modifier.defaultMinSize(0.dp, 0.dp),
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onPrimaryContainer),
                content = {
                    Icon(
                        painterResource(id = R.drawable.ic_plus),
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        contentDescription = stringResource(id = R.string.str_plus)
                    )
                },
                onClick = {
                    onValueChange((quantity ?: 0) + 1)
                }
            )
        }
    }
}

@Composable
fun DrawErrorMessage(
    message: String,
    modifier: Modifier = Modifier,
    onClickRetry: () -> Unit
) {
    Row(
        modifier = modifier.padding(10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = message,
            fontWeight = FontWeight.SemiBold,
            fontFamily = montserratFamily,
            color = MaterialTheme.colorScheme.error,
            modifier = Modifier.weight(1f),
            maxLines = 2
        )
        OutlinedButton(onClick = onClickRetry) {
            Text(
                text = stringResource(id = R.string.strRetry),
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                fontFamily = montserratFamily,
            )
        }
    }
}


@Composable
fun DrawBadgedBox(
    badgeCount: Int,
    painter: Painter,
    onClick: () -> Unit
) {
    @Composable
    fun DrawIcon() {
        Icon(
            painter,
            modifier = Modifier
                .padding(vertical = 2.dp)
                .clickable { onClick() },
            tint = MaterialTheme.colorScheme.onPrimaryContainer,
            contentDescription = null
        )
    }
    if (badgeCount > 0) {
        BadgedBox(
            modifier = Modifier.padding(8.dp),
            badge = {
                Badge { Text("$badgeCount") }
            }) {
            DrawIcon()
        }
    } else {
        DrawIcon()
    }
}


