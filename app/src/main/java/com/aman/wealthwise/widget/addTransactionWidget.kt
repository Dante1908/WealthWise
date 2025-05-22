package com.aman.wealthwise.widget

import android.annotation.SuppressLint
import android.content.Context
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.Image
import androidx.glance.ImageProvider
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.cornerRadius
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.size
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import com.aman.wealthwise.MainActivity
import com.aman.wealthwise.R
import com.aman.wealthwise.ui.theme.Blue40
import com.aman.wealthwise.ui.theme.Blue80

class AddTransactionWidget : GlanceAppWidget() {
    @SuppressLint("RestrictedApi")
    override suspend fun provideGlance(context: Context, id: GlanceId) {
        provideContent {
            Box(modifier = GlanceModifier.fillMaxSize().background(Blue40).cornerRadius(16.dp).padding(12.dp)) {
                Column(modifier = GlanceModifier.fillMaxSize(), horizontalAlignment = Alignment.CenterHorizontally) {
                    Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Image(provider = ImageProvider(R.drawable.wallet), contentDescription = "Wallet Icon", modifier = GlanceModifier.size(24.dp))
                        Text(text = "WealthWise", style = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Bold,color = ColorProvider(Color.White)), modifier = GlanceModifier.padding(start = 8.dp).defaultWeight())
                    }
                    Spacer(modifier = GlanceModifier.height(10.dp))
                    Box(modifier = GlanceModifier.fillMaxSize().background(Blue80).cornerRadius(12.dp).padding(12.dp)
                            .clickable(
                                actionStartActivity<MainActivity>()
                            )
                    ) {
                        Row(modifier = GlanceModifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            Text(text = "Add Transaction", style = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Bold), modifier = GlanceModifier.defaultWeight())
                            Image(provider = ImageProvider(R.drawable.baseline_add_24), contentDescription = "Income Trend", modifier = GlanceModifier.size(24.dp))
                        }
                    }
                }
            }
        }
    }
}