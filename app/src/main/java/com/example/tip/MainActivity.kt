package com.example.tip

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.tip.components.InputField
import com.example.tip.ui.theme.TipTheme
import com.example.tip.widgets.RoundIconButton
import org.intellij.lang.annotations.JdkConstants.HorizontalAlignment
import androidx.compose.material3.Slider
import androidx.compose.ui.text.font.Font
import com.example.tip.util.calculateTotalPerPerson
import com.example.tip.util.calculateTotalTip

class MainActivity : ComponentActivity() {
    @SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {

            MyApp {
                //TopHeader()
                MainContent()

            }
        }
    }
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MyApp(content: @Composable () -> Unit) {

    TipTheme {
        Scaffold(modifier = Modifier.fillMaxSize()) {

             content()

        }
    }

}


@Preview
@Composable
fun TopHeader(totalPerPerson: Double = 0.0){

    Surface(modifier = Modifier
        //.padding(top = 9.dp)
        .fillMaxWidth()
        .height(220.dp)
        .padding(start = 19.dp, top = 63.dp, end = 19.dp)
        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp)))
//        ,
//        color = Color(0xFFE9D7F7)
       ) {

        Column(modifier = Modifier
            .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

            ) {

            var total = "%.2f".format(totalPerPerson)

            Text(text = "Total Per Person", fontSize = 30.sp)
            Text(text = "$$total",fontSize = 30.sp, fontWeight = FontWeight.ExtraBold)


        }
    }

}



@Preview
@Composable
fun MainContent() {

   Column {


        BillForm {
//                billAmt ->
//            Log.d("AMT", "MainContent: $billAmt")

        }

    }
}


@Composable
fun BillForm(modifier: Modifier = Modifier,
             onValChange: (String) -> Unit){

    val totalBillState = remember{
        mutableStateOf("")
    }

    val validState = remember(totalBillState.value) {
        totalBillState.value.trim().isNotEmpty()
    }

    val keyboardController = LocalSoftwareKeyboardController.current



    val sliderPositionState = remember {
        mutableStateOf(0f)
    }

    val tipPercentage = (sliderPositionState.value * 100).toInt()

    val splitByValue = remember {
        mutableStateOf(1)
    }

    val range = IntRange(start = 1, endInclusive = 100)

    val tipAmountState = remember{
        mutableStateOf(0.0)
    }

    val totalPerPersonState = remember {
        mutableStateOf(0.0)
    }

    TopHeader(totalPerPersonState.value)


    Spacer(modifier = Modifier.height(10.dp))

    Surface(modifier = Modifier
        .fillMaxWidth()
        .padding(start = 9.dp, top = 3.dp, end = 9.dp)

        .clip(shape = RoundedCornerShape(corner = CornerSize(12.dp)))
//        ,
//        border = BorderStroke(width = 5.dp, color = Color.LightGray)
//        ,
//        color = Color(0xFFE5D5F7)
    ){

        Column() {

            InputField(valueState = totalBillState,
                labelId = "Enter Bill",
                enabled = true,
                isSingleLine = true,
                onAction = KeyboardActions {
                    if (!validState) return@KeyboardActions

                    //Todo - onvaluechanged

                    keyboardController?.hide()
                }
            )

            if (validState) {


                Row(
                    modifier = Modifier.padding(3.dp),
                    horizontalArrangement = Arrangement.Start
                ) {
                    Text(
                        text = "Split",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 1.dp)

                    )

                    Spacer(modifier = Modifier.width(120.dp))

                    Row(
                        modifier = Modifier.padding(horizontal = 3.dp),
                        horizontalArrangement = Arrangement.End
                    ) {

                        RoundIconButton(
                            imageVector = Icons.Default.Remove,
                            onClick = {
                                if (splitByValue.value > 1) {
                                    splitByValue.value -= 1
                                } else {
                                    splitByValue.value = 1

                                }

                                val newTipPercentage = (sliderPositionState.value * 100).toInt()
                                tipAmountState.value = calculateTotalTip(
                                    totalBillState.value.toDouble(),
                                    newTipPercentage
                                )

                                totalPerPersonState.value =
                                    calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByValue.value,
                                        tipPercentage = newTipPercentage
                                    )
                            })


                        Text(
                            text = "${splitByValue.value}",
                            fontSize = 20.sp,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 9.dp, end = 9.dp)
                        )


                        RoundIconButton(
                            imageVector = Icons.Default.Add,
                            onClick = {
                                if (splitByValue.value < range.last) {
                                    splitByValue.value += 1
                                }

                                val newTipPercentage = (sliderPositionState.value * 100).toInt()
                                tipAmountState.value = calculateTotalTip(
                                    totalBillState.value.toDouble(),
                                    newTipPercentage
                                )

                                totalPerPersonState.value =
                                    calculateTotalPerPerson(
                                        totalBill = totalBillState.value.toDouble(),
                                        splitBy = splitByValue.value,
                                        tipPercentage = newTipPercentage
                                    )
                            })

                    }


                }


                Row(
                    modifier = Modifier
                        .padding(
                            horizontal = 3.dp,
                            vertical = 12.dp
                        )
                ) {
                    Text(
                        text = "Tip",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )

                    Spacer(modifier = Modifier.width(200.dp))

                    Text(
                        text = "$${tipAmountState.value}",
                        fontSize = 20.sp,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                    )

                }

                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {


                    Text(text = "$tipPercentage %",
                        fontSize = 20.sp,)

                    Spacer(modifier = Modifier.height(14.dp))

                    // Slider


                    Slider(
                        value = sliderPositionState.value,
                        onValueChange = { newVal ->
                            sliderPositionState.value = newVal
                            val newTipPercentage = (sliderPositionState.value * 100).toInt()
                            tipAmountState.value =
                                calculateTotalTip(totalBillState.value.toDouble(), newTipPercentage)

                            totalPerPersonState.value =
                                calculateTotalPerPerson(
                                    totalBill = totalBillState.value.toDouble(),
                                    splitBy = splitByValue.value,
                                    tipPercentage = newTipPercentage
                                )

                        },
                        modifier = Modifier.padding(
                            start = 16.dp,
                            end = 18.dp
                        )
                    )


                }
            }

            else{
                Box() {}
            }
        }

    }

}



