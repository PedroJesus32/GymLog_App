package pt.pc.gymlog.ui.components.onboarding

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import pt.pc.gymlog.viewmodel.FocusAreaUi

@Composable
fun BodyMapVisual(selected: Set<FocusAreaUi>) {
    val highlightColor = Color(0xFF6B91FF)
    val baseColor = Color(0xFFC8C8C8)
    val lineColor = Color.White
    val lineWidth = 2f

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().height(420.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Canvas(modifier = Modifier.weight(1f).fillMaxHeight().padding(4.dp)) {
                drawFrontBody(selected, highlightColor, baseColor, lineColor, lineWidth)
            }
            Canvas(modifier = Modifier.weight(1f).fillMaxHeight().padding(4.dp)) {
                drawBackBody(selected, highlightColor, baseColor, lineColor, lineWidth)
            }
        }
        Text(
            text = "Mapa Corporal",
            style = MaterialTheme.typography.bodyMedium,
            color = Color.Gray,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}

private fun DrawScope.drawFrontBody(
    selected: Set<FocusAreaUi>,
    highlightColor: Color,
    baseColor: Color,
    lineColor: Color,
    lw: Float
) {
    val w = size.width
    val h = size.height
    val cx = w * 0.5f
    
    fun active(vararg areas: FocusAreaUi) = areas.any { it in selected } || FocusAreaUi.FULL_BODY in selected
    fun color(vararg areas: FocusAreaUi) = if (active(*areas)) highlightColor else baseColor
    
    // HEAD
    drawCircle(baseColor, w * 0.11f, Offset(cx, h * 0.08f))
    drawCircle(lineColor, w * 0.11f, Offset(cx, h * 0.08f), style = Stroke(lw))
    
    // NECK
    val neck = path {
        moveTo(cx - w * 0.04f, h * 0.14f)
        lineTo(cx + w * 0.04f, h * 0.14f)
        lineTo(cx + w * 0.05f, h * 0.17f)
        lineTo(cx - w * 0.05f, h * 0.17f)
        close()
    }
    drawPath(neck, baseColor)
    drawPath(neck, lineColor, style = Stroke(lw))
    
    // SHOULDERS
    val sColor = color(FocusAreaUi.SHOULDERS)
    listOf(-1f, 1f).forEach { side ->
        val shoulder = path {
            val s = side
            moveTo(cx + s * w * 0.05f, h * 0.17f)
            lineTo(cx + s * w * 0.12f, h * 0.18f)
            lineTo(cx + s * w * 0.18f, h * 0.22f)
            lineTo(cx + s * w * 0.19f, h * 0.28f)
            lineTo(cx + s * w * 0.16f, h * 0.31f)
            lineTo(cx + s * w * 0.11f, h * 0.29f)
            close()
        }
        drawPath(shoulder, sColor)
        drawPath(shoulder, lineColor, style = Stroke(lw))
    }
    
    // CHEST
    val cColor = color(FocusAreaUi.CHEST)
    listOf(-1f, 1f).forEach { side ->
        val s = side
        val pec = path {
            moveTo(cx, h * 0.18f)
            lineTo(cx + s * w * 0.05f, h * 0.18f)
            lineTo(cx + s * w * 0.11f, h * 0.22f)
            lineTo(cx + s * w * 0.11f, h * 0.29f)
            lineTo(cx + s * w * 0.06f, h * 0.31f)
            lineTo(cx, h * 0.31f)
            close()
        }
        drawPath(pec, cColor)
        drawPath(pec, lineColor, style = Stroke(lw))
    }
    
    // ABS
    val aColor = color(FocusAreaUi.ABS)
    val abs = path {
        moveTo(cx - w * 0.09f, h * 0.31f)
        lineTo(cx + w * 0.09f, h * 0.31f)
        lineTo(cx + w * 0.10f, h * 0.46f)
        lineTo(cx - w * 0.10f, h * 0.46f)
        close()
    }
    drawPath(abs, aColor)
    drawPath(abs, lineColor, style = Stroke(lw))
    
    // Abs lines
    drawLine(lineColor, Offset(cx, h * 0.31f), Offset(cx, h * 0.46f), lw)
    listOf(0.355f, 0.385f, 0.415f).forEach { y ->
        drawLine(lineColor, Offset(cx - w * 0.09f, h * y), Offset(cx + w * 0.09f, h * y), lw)
    }
    
    // Obliques
    listOf(-1f, 1f).forEach { side ->
        val s = side
        val oblique = path {
            moveTo(cx + s * w * 0.09f, h * 0.31f)
            lineTo(cx + s * w * 0.13f, h * 0.33f)
            lineTo(cx + s * w * 0.14f, h * 0.44f)
            lineTo(cx + s * w * 0.10f, h * 0.46f)
            close()
        }
        drawPath(oblique, aColor)
        drawPath(oblique, lineColor, style = Stroke(lw))
    }
    
    // ARMS
    val armColor = color(FocusAreaUi.ARMS)
    listOf(-1f, 1f).forEach { side ->
        val s = side
        // Bicep
        val bicep = path {
            moveTo(cx + s * w * 0.16f, h * 0.31f)
            lineTo(cx + s * w * 0.19f, h * 0.28f)
            lineTo(cx + s * w * 0.22f, h * 0.33f)
            lineTo(cx + s * w * 0.22f, h * 0.41f)
            lineTo(cx + s * w * 0.19f, h * 0.43f)
            lineTo(cx + s * w * 0.16f, h * 0.40f)
            close()
        }
        drawPath(bicep, armColor)
        drawPath(bicep, lineColor, style = Stroke(lw))
        
        // Forearm
        val forearm = path {
            moveTo(cx + s * w * 0.19f, h * 0.43f)
            lineTo(cx + s * w * 0.22f, h * 0.41f)
            lineTo(cx + s * w * 0.21f, h * 0.54f)
            lineTo(cx + s * w * 0.18f, h * 0.56f)
            lineTo(cx + s * w * 0.16f, h * 0.56f)
            lineTo(cx + s * w * 0.16f, h * 0.52f)
            close()
        }
        drawPath(forearm, armColor)
        drawPath(forearm, lineColor, style = Stroke(lw))
    }
    
    // HIPS
    val hipColor = color(FocusAreaUi.GLUTES)
    val hips = path {
        moveTo(cx - w * 0.10f, h * 0.46f)
        lineTo(cx + w * 0.10f, h * 0.46f)
        lineTo(cx + w * 0.12f, h * 0.52f)
        lineTo(cx + w * 0.09f, h * 0.58f)
        lineTo(cx, h * 0.59f)
        lineTo(cx - w * 0.09f, h * 0.58f)
        lineTo(cx - w * 0.12f, h * 0.52f)
        close()
    }
    drawPath(hips, hipColor)
    drawPath(hips, lineColor, style = Stroke(lw))
    drawLine(lineColor, Offset(cx, h * 0.46f), Offset(cx, h * 0.59f), lw)
    
    // LEGS
    val legColor = color(FocusAreaUi.LEGS)
    listOf(-1f, 1f).forEach { side ->
        val s = side
        // Quad outer
        val qOuter = path {
            moveTo(cx + s * w * 0.12f, h * 0.52f)
            lineTo(cx + s * w * 0.16f, h * 0.55f)
            lineTo(cx + s * w * 0.17f, h * 0.73f)
            lineTo(cx + s * w * 0.14f, h * 0.74f)
            lineTo(cx + s * w * 0.11f, h * 0.68f)
            close()
        }
        drawPath(qOuter, legColor)
        drawPath(qOuter, lineColor, style = Stroke(lw))
        
        // Quad middle
        val qMid = path {
            moveTo(cx + s * w * 0.11f, h * 0.52f)
            lineTo(cx + s * w * 0.11f, h * 0.68f)
            lineTo(cx + s * w * 0.08f, h * 0.74f)
            lineTo(cx + s * w * 0.09f, h * 0.58f)
            close()
        }
        drawPath(qMid, legColor)
        drawPath(qMid, lineColor, style = Stroke(lw))
        
        // Quad inner
        val qInner = path {
            moveTo(cx + s * w * 0.09f, h * 0.58f)
            lineTo(cx + s * w * 0.05f, h * 0.60f)
            lineTo(cx + s * w * 0.04f, h * 0.74f)
            lineTo(cx + s * w * 0.08f, h * 0.74f)
            close()
        }
        drawPath(qInner, legColor)
        drawPath(qInner, lineColor, style = Stroke(lw))
        
        // Calf
        val calf = path {
            moveTo(cx + s * w * 0.14f, h * 0.74f)
            lineTo(cx + s * w * 0.17f, h * 0.73f)
            lineTo(cx + s * w * 0.15f, h * 0.85f)
            lineTo(cx + s * w * 0.12f, h * 0.87f)
            lineTo(cx + s * w * 0.11f, h * 0.82f)
            close()
        }
        drawPath(calf, legColor)
        drawPath(calf, lineColor, style = Stroke(lw))
        
        val calfInner = path {
            moveTo(cx + s * w * 0.08f, h * 0.74f)
            lineTo(cx + s * w * 0.11f, h * 0.74f)
            lineTo(cx + s * w * 0.11f, h * 0.82f)
            lineTo(cx + s * w * 0.09f, h * 0.87f)
            lineTo(cx + s * w * 0.07f, h * 0.85f)
            close()
        }
        drawPath(calfInner, legColor)
        drawPath(calfInner, lineColor, style = Stroke(lw))
    }
    
    // FEET
    listOf(-0.11f, 0.11f).forEach { x ->
        drawCircle(baseColor, w * 0.025f, Offset(cx + w * x, h * 0.91f))
        drawCircle(lineColor, w * 0.025f, Offset(cx + w * x, h * 0.91f), style = Stroke(lw))
    }
}

private fun DrawScope.drawBackBody(
    selected: Set<FocusAreaUi>,
    highlightColor: Color,
    baseColor: Color,
    lineColor: Color,
    lw: Float
) {
    val w = size.width
    val h = size.height
    val cx = w * 0.5f
    
    fun active(vararg areas: FocusAreaUi) = areas.any { it in selected } || FocusAreaUi.FULL_BODY in selected
    fun color(vararg areas: FocusAreaUi) = if (active(*areas)) highlightColor else baseColor
    
    // HEAD
    drawCircle(baseColor, w * 0.11f, Offset(cx, h * 0.08f))
    drawCircle(lineColor, w * 0.11f, Offset(cx, h * 0.08f), style = Stroke(lw))
    
    // NECK
    val neck = path {
        moveTo(cx - w * 0.04f, h * 0.14f)
        lineTo(cx + w * 0.04f, h * 0.14f)
        lineTo(cx + w * 0.04f, h * 0.17f)
        lineTo(cx - w * 0.04f, h * 0.17f)
        close()
    }
    drawPath(neck, baseColor)
    drawPath(neck, lineColor, style = Stroke(lw))
    
    // TRAPS
    val sColor = color(FocusAreaUi.SHOULDERS, FocusAreaUi.BACK)
    val traps = path {
        moveTo(cx, h * 0.15f)
        lineTo(cx - w * 0.11f, h * 0.17f)
        lineTo(cx - w * 0.13f, h * 0.24f)
        lineTo(cx - w * 0.09f, h * 0.27f)
        lineTo(cx, h * 0.25f)
        lineTo(cx + w * 0.09f, h * 0.27f)
        lineTo(cx + w * 0.13f, h * 0.24f)
        lineTo(cx + w * 0.11f, h * 0.17f)
        close()
    }
    drawPath(traps, sColor)
    drawPath(traps, lineColor, style = Stroke(lw))
    drawLine(lineColor, Offset(cx, h * 0.15f), Offset(cx, h * 0.25f), lw)
    
    // DELTOIDS
    listOf(-1f, 1f).forEach { side ->
        val s = side
        val delt = path {
            moveTo(cx + s * w * 0.11f, h * 0.17f)
            lineTo(cx + s * w * 0.16f, h * 0.21f)
            lineTo(cx + s * w * 0.18f, h * 0.27f)
            lineTo(cx + s * w * 0.15f, h * 0.31f)
            lineTo(cx + s * w * 0.13f, h * 0.24f)
            close()
        }
        drawPath(delt, sColor)
        drawPath(delt, lineColor, style = Stroke(lw))
    }
    
    // BACK MUSCLES
    val bColor = color(FocusAreaUi.BACK)
    val spine = path {
        moveTo(cx - w * 0.04f, h * 0.25f)
        lineTo(cx + w * 0.04f, h * 0.25f)
        lineTo(cx + w * 0.05f, h * 0.54f)
        lineTo(cx - w * 0.05f, h * 0.54f)
        close()
    }
    drawPath(spine, bColor)
    drawPath(spine, lineColor, style = Stroke(lw))
    drawLine(lineColor, Offset(cx, h * 0.25f), Offset(cx, h * 0.54f), lw)
    
    // Spine segments
    for (i in 1..5) {
        val y = h * (0.25f + i * 0.05f)
        drawLine(lineColor, Offset(cx - w * 0.04f, y), Offset(cx + w * 0.04f, y), lw * 0.6f)
    }
    
    // LATS
    listOf(-1f, 1f).forEach { side ->
        val s = side
        val lat = path {
            moveTo(cx + s * w * 0.09f, h * 0.27f)
            lineTo(cx + s * w * 0.15f, h * 0.31f)
            lineTo(cx + s * w * 0.14f, h * 0.46f)
            lineTo(cx + s * w * 0.09f, h * 0.48f)
            lineTo(cx + s * w * 0.05f, h * 0.40f)
            close()
        }
        drawPath(lat, bColor)
        drawPath(lat, lineColor, style = Stroke(lw))
    }
    
    // ARMS (TRICEPS)
    val armColor = color(FocusAreaUi.ARMS)
    listOf(-1f, 1f).forEach { side ->
        val s = side
        val tricep = path {
            moveTo(cx + s * w * 0.15f, h * 0.31f)
            lineTo(cx + s * w * 0.18f, h * 0.27f)
            lineTo(cx + s * w * 0.22f, h * 0.33f)
            lineTo(cx + s * w * 0.21f, h * 0.42f)
            lineTo(cx + s * w * 0.17f, h * 0.43f)
            close()
        }
        drawPath(tricep, armColor)
        drawPath(tricep, lineColor, style = Stroke(lw))
        
        val forearm = path {
            moveTo(cx + s * w * 0.17f, h * 0.43f)
            lineTo(cx + s * w * 0.21f, h * 0.42f)
            lineTo(cx + s * w * 0.20f, h * 0.54f)
            lineTo(cx + s * w * 0.17f, h * 0.56f)
            close()
        }
        drawPath(forearm, armColor)
        drawPath(forearm, lineColor, style = Stroke(lw))
    }
    
    // LOWER BACK
    val lowerBack = path {
        moveTo(cx - w * 0.09f, h * 0.48f)
        lineTo(cx + w * 0.09f, h * 0.48f)
        lineTo(cx + w * 0.11f, h * 0.54f)
        lineTo(cx - w * 0.11f, h * 0.54f)
        close()
    }
    drawPath(lowerBack, bColor)
    drawPath(lowerBack, lineColor, style = Stroke(lw))
    
    // GLUTES
    val gColor = color(FocusAreaUi.GLUTES)
    val glutes = path {
        moveTo(cx - w * 0.11f, h * 0.54f)
        lineTo(cx + w * 0.11f, h * 0.54f)
        lineTo(cx + w * 0.13f, h * 0.62f)
        lineTo(cx + w * 0.09f, h * 0.66f)
        lineTo(cx, h * 0.67f)
        lineTo(cx - w * 0.09f, h * 0.66f)
        lineTo(cx - w * 0.13f, h * 0.62f)
        close()
    }
    drawPath(glutes, gColor)
    drawPath(glutes, lineColor, style = Stroke(lw))
    drawLine(lineColor, Offset(cx, h * 0.54f), Offset(cx, h * 0.67f), lw)
    
    // HAMSTRINGS
    val legColor = color(FocusAreaUi.LEGS)
    listOf(-1f, 1f).forEach { side ->
        val s = side
        val hamOuter = path {
            moveTo(cx + s * w * 0.13f, h * 0.62f)
            lineTo(cx + s * w * 0.16f, h * 0.66f)
            lineTo(cx + s * w * 0.16f, h * 0.74f)
            lineTo(cx + s * w * 0.13f, h * 0.74f)
            lineTo(cx + s * w * 0.11f, h * 0.68f)
            close()
        }
        drawPath(hamOuter, legColor)
        drawPath(hamOuter, lineColor, style = Stroke(lw))
        
        val hamInner = path {
            moveTo(cx + s * w * 0.09f, h * 0.66f)
            lineTo(cx + s * w * 0.06f, h * 0.67f)
            lineTo(cx + s * w * 0.07f, h * 0.74f)
            lineTo(cx + s * w * 0.10f, h * 0.74f)
            lineTo(cx + s * w * 0.11f, h * 0.68f)
            close()
        }
        drawPath(hamInner, legColor)
        drawPath(hamInner, lineColor, style = Stroke(lw))
        
        // Calf
        val calf = path {
            moveTo(cx + s * w * 0.13f, h * 0.74f)
            lineTo(cx + s * w * 0.16f, h * 0.74f)
            lineTo(cx + s * w * 0.14f, h * 0.86f)
            lineTo(cx + s * w * 0.11f, h * 0.87f)
            close()
        }
        drawPath(calf, legColor)
        drawPath(calf, lineColor, style = Stroke(lw))
    }
    
    // FEET
    listOf(-0.13f, 0.13f).forEach { x ->
        drawCircle(baseColor, w * 0.025f, Offset(cx + w * x, h * 0.91f))
        drawCircle(lineColor, w * 0.025f, Offset(cx + w * x, h * 0.91f), style = Stroke(lw))
    }
}

private fun path(block: Path.() -> Unit) = Path().apply(block)
