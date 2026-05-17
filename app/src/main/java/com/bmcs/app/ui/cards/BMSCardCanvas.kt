package com.bmcs.app.ui.cards

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.FilterQuality
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.sp
import android.graphics.BitmapFactory
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

// Standard Pokemon-style card ratio (63mm × 88mm)
private const val CARD_RATIO = 0.714f

@Composable
fun BMSCard(
    cardData: BMSCardData,
    modifier: Modifier = Modifier
) {
    val textMeasurer = rememberTextMeasurer()
    val context = LocalContext.current

    // Stable random index (1-5) per card id — rerolled only when the card changes.
    val localIndex = remember(cardData.id) { (1..5).random() }

    // Decode the asset JPEG directly via AssetManager on a background thread.
    // Coil's AsyncImagePainter only starts loading once it's actually drawn into
    // a layout — since we just read its bitmap and never draw the painter itself,
    // the load never fires. Decoding directly bypasses that entirely.
    var imageBitmap by remember { mutableStateOf<ImageBitmap?>(null) }
    LaunchedEffect(localIndex) {
        imageBitmap = withContext(Dispatchers.IO) {
            runCatching {
                context.assets.open("test-images/$localIndex.jpeg").use { stream ->
                    BitmapFactory.decodeStream(stream)?.asImageBitmap()
                }
            }.getOrNull()
        }
    }

    Canvas(modifier = modifier.aspectRatio(CARD_RATIO)) {
        drawBMSCard(cardData, imageBitmap, textMeasurer)
    }
}

private fun DrawScope.drawBMSCard(
    card: BMSCardData,
    imageBitmap: ImageBitmap?,
    tm: androidx.compose.ui.text.TextMeasurer
) {
    val w = size.width
    val h = size.height
    val r = card.rarity

    // ── 1. Outer border ──────────────────────────────────────────────────────
    val outerRadius = w * 0.06f
    drawRoundRect(
        brush = Brush.linearGradient(
            listOf(r.borderColor, Color(0xFFFFEF58), r.borderColor),
            Offset.Zero,
            Offset(w, h)
        ),
        cornerRadius = CornerRadius(outerRadius)
    )

    // ── 2. Inner card body ───────────────────────────────────────────────────
    val bp = w * 0.028f
    drawRoundRect(
        brush = Brush.radialGradient(
            listOf(r.cardColorLight, r.cardColor),
            center = Offset(w * 0.5f, h * 0.22f),
            radius = w * 0.9f
        ),
        topLeft = Offset(bp, bp),
        size = Size(w - bp * 2, h - bp * 2),
        cornerRadius = CornerRadius(outerRadius - bp)
    )

    // ── 3. Badge gem (top-left) ──────────────────────────────────────────────
    val bx = bp + w * 0.098f
    val by = bp + h * 0.063f
    val br = w * 0.080f

    // Outer gold ring
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color(0xFFFFE082), Color(0xFFB8860B)),
            Offset(bx, by), br
        ),
        radius = br,
        center = Offset(bx, by)
    )
    // Inner silver gem
    val ibr = br * 0.72f
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color(0xFFF0F0F0), Color(0xFFAEAEAE), Color(0xFF5E5E5E)),
            Offset(bx - ibr * 0.22f, by - ibr * 0.22f),
            ibr
        ),
        radius = ibr,
        center = Offset(bx, by)
    )
    // Gem specular highlight
    drawCircle(
        color = Color.White.copy(alpha = 0.42f),
        radius = ibr * 0.30f,
        center = Offset(bx - ibr * 0.20f, by - ibr * 0.26f)
    )

    // ── 4. Rarity label + small icon circle (top-right) ──────────────────────
    val smallR = w * 0.046f
    val scX = w - bp - smallR - w * 0.014f
    val scY = bp + smallR + h * 0.008f

    val rarityLayout = tm.measure(
        text = r.displayLabel,
        style = TextStyle(
            fontSize = (w * 0.038f / density).sp,
            fontWeight = FontWeight.Bold,
            fontStyle = FontStyle.Italic,
            color = Color(0xFF1A1A1A)
        )
    )
    drawText(
        rarityLayout,
        topLeft = Offset(
            scX - smallR - rarityLayout.size.width - w * 0.012f,
            bp + h * 0.013f
        )
    )

    // Small dark circle (ant icon placeholder)
    drawCircle(
        brush = Brush.radialGradient(
            listOf(Color(0xFF795548), Color(0xFF3E2723)),
            Offset(scX, scY), smallR
        ),
        radius = smallR,
        center = Offset(scX, scY)
    )
    drawCircle(
        color = Color.White.copy(alpha = 0.22f),
        radius = smallR * 0.38f,
        center = Offset(scX - smallR * 0.18f, scY - smallR * 0.20f)
    )

    // ── 5. Card title (between badge and rarity label) ───────────────────────
    val titleLayout = tm.measure(
        text = card.title,
        style = TextStyle(
            fontSize = (w * 0.052f / density).sp,
            fontWeight = FontWeight.ExtraBold,
            color = Color(0xFF1A1A1A)
        ),
        constraints = Constraints(maxWidth = (w * 0.50f).toInt()),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1
    )
    drawText(
        titleLayout,
        topLeft = Offset(bx + br + w * 0.018f, bp + h * 0.012f)
    )

    // ── 6. Image frame ───────────────────────────────────────────────────────
    val imgL = bp + w * 0.040f
    val imgT = bp + h * 0.093f
    val imgR = w - bp - w * 0.040f
    val imgB = h * 0.548f
    val imgW = imgR - imgL
    val imgH = imgB - imgT
    val imgCR = w * 0.012f
    val frameBW = w * 0.018f

    // Gold border frame
    drawRoundRect(
        brush = Brush.linearGradient(
            listOf(Color(0xFFFFF176), Color(0xFFC8960A), Color(0xFFFFF176)),
            Offset(imgL, imgT),
            Offset(imgR, imgB)
        ),
        topLeft = Offset(imgL - frameBW, imgT - frameBW),
        size = Size(imgW + frameBW * 2, imgH + frameBW * 2),
        cornerRadius = CornerRadius(imgCR + frameBW),
        style = Stroke(frameBW)
    )

    // White artwork background
    drawRoundRect(
        color = Color.White,
        topLeft = Offset(imgL, imgT),
        size = Size(imgW, imgH),
        cornerRadius = CornerRadius(imgCR)
    )

    // Card artwork image
    if (imageBitmap != null) {
        val artworkClip = Path().apply {
            addRoundRect(RoundRect(Rect(imgL, imgT, imgR, imgB), CornerRadius(imgCR)))
        }
        clipPath(artworkClip) {
            drawImage(
                image = imageBitmap,
                dstOffset = IntOffset(imgL.toInt(), imgT.toInt()),
                dstSize = IntSize(imgW.toInt(), imgH.toInt()),
                filterQuality = FilterQuality.Medium
            )
        }
    }

    // ── 7. Gold separator bar (tapered ribbon) ───────────────────────────────
    val barT = imgB + h * 0.007f
    val barH = h * 0.026f
    val barPath = Path().apply {
        moveTo(imgL, barT)
        lineTo(imgR - w * 0.055f, barT)
        lineTo(imgR, barT + barH * 0.5f)
        lineTo(imgR - w * 0.055f, barT + barH)
        lineTo(imgL, barT + barH)
        close()
    }
    drawPath(
        barPath,
        brush = Brush.horizontalGradient(
            listOf(Color(0xFFFFF176), Color(0xFFD4A017), Color(0xFFFFF176), Color(0xFFCC900A)),
            startX = imgL,
            endX = imgR
        )
    )

    // ── 8. Description text ──────────────────────────────────────────────────
    val descT = barT + barH + h * 0.012f
    val descPad = bp + w * 0.05f
    val descLayout = tm.measure(
        text = card.description,
        style = TextStyle(
            fontSize = (w * 0.036f / density).sp,
            color = Color(0xFF1A1A1A),
            lineHeight = (w * 0.05f / density).sp
        ),
        constraints = Constraints(maxWidth = (w - descPad * 2).toInt()),
        overflow = TextOverflow.Ellipsis,
        maxLines = 5
    )
    drawText(descLayout, topLeft = Offset(descPad, descT))

    // ── 9. Points earned row ─────────────────────────────────────────────────
    val divL = bp + w * 0.01f
    val divR = w - bp - w * 0.01f
    val line1 = h * 0.776f
    val line2 = h * 0.823f

    drawLine(Color(0xFF1A1A1A), Offset(divL, line1), Offset(divR, line1), 1.5f)
    drawLine(Color(0xFF1A1A1A), Offset(divL, line2), Offset(divR, line2), 1.5f)

    val midDiv = (line1 + line2) / 2f

    val ptsLabel = tm.measure(
        text = "puntos base",
        style = TextStyle(
            fontSize = (w * 0.028f / density).sp,
            color = Color(0xFF1A1A1A).copy(alpha = 0.65f)
        )
    )
    val ptsValue = tm.measure(
        text = "${card.points} pts",
        style = TextStyle(
            fontSize = (w * 0.036f / density).sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF1A1A1A)
        )
    )
    drawText(ptsLabel, topLeft = Offset(divL + w * 0.020f, midDiv - ptsLabel.size.height / 2f))
    drawText(ptsValue, topLeft = Offset(divR - ptsValue.size.width - w * 0.020f, midDiv - ptsValue.size.height / 2f))

    // ── 10. Leaf decorations ─────────────────────────────────────────────────
    val leafTop = line2 + h * 0.011f
    val leafH = h * 0.029f
    val leafW = w * 0.160f
    drawLeaf(divL + w * 0.036f, leafTop, leafW, leafH, r.cardColor)
    drawLeaf(w / 2f - leafW / 2f, leafTop, leafW, leafH, r.cardColor)

    // ── 11. Bottom text box (rarity indicator) ───────────────────────────────
    val boxT = h * 0.878f
    val boxB = h * 0.944f
    val boxL = bp + w * 0.016f
    val boxRx = w - bp - w * 0.016f
    val boxCR = w * 0.008f

    drawRoundRect(
        color = Color.White.copy(alpha = 0.60f),
        topLeft = Offset(boxL, boxT),
        size = Size(boxRx - boxL, boxB - boxT),
        cornerRadius = CornerRadius(boxCR)
    )
    drawRoundRect(
        color = Color(0xFF333333),
        topLeft = Offset(boxL, boxT),
        size = Size(boxRx - boxL, boxB - boxT),
        cornerRadius = CornerRadius(boxCR),
        style = Stroke(1.5f)
    )
    val rarityNameLayout = tm.measure(
        text = r.name.lowercase().replaceFirstChar { it.uppercase() },
        style = TextStyle(
            fontSize = (w * 0.032f / density).sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFF1A1A1A)
        )
    )
    drawText(
        rarityNameLayout,
        topLeft = Offset(
            w / 2f - rarityNameLayout.size.width / 2f,
            boxT + (boxB - boxT - rarityNameLayout.size.height) / 2f
        )
    )

    // ── 12. Copyright text ───────────────────────────────────────────────────
    val cpLayout = tm.measure(
        text = "©BMCS - Banco Mercantil Santa Cruz",
        style = TextStyle(
            fontSize = (w * 0.026f / density).sp,
            color = Color(0xFF1A1A1A).copy(alpha = 0.60f),
            textAlign = TextAlign.Center
        )
    )
    drawText(
        cpLayout,
        topLeft = Offset(
            w / 2f - cpLayout.size.width / 2f,
            h - bp - cpLayout.size.height - h * 0.003f
        )
    )

    // ── 13. Sparkle (bottom-right corner decoration) ─────────────────────────
    drawSparkle(
        center = Offset(w - bp - w * 0.050f, h - bp - h * 0.034f),
        size = w * 0.052f,
        color = Color(0xFFFFD700)
    )
}

private fun DrawScope.drawLeaf(
    left: Float, top: Float,
    lw: Float, lh: Float,
    color: Color
) {
    val path = Path().apply {
        moveTo(left, top + lh / 2)
        cubicTo(left + lw * 0.15f, top, left + lw * 0.85f, top, left + lw, top + lh / 2)
        cubicTo(left + lw * 0.85f, top + lh, left + lw * 0.15f, top + lh, left, top + lh / 2)
        close()
    }
    drawPath(path, color = color.copy(alpha = 0.88f))
    // Leaf vein
    drawLine(
        color = Color.Black.copy(alpha = 0.30f),
        start = Offset(left + lw * 0.18f, top + lh / 2),
        end = Offset(left + lw * 0.84f, top + lh / 2),
        strokeWidth = 1.2f
    )
}

private fun DrawScope.drawSparkle(center: Offset, size: Float, color: Color) {
    val half = size / 2f
    val thin = size * 0.11f
    val path = Path().apply {
        moveTo(center.x, center.y - half)
        lineTo(center.x + thin, center.y - thin)
        lineTo(center.x + half, center.y)
        lineTo(center.x + thin, center.y + thin)
        lineTo(center.x, center.y + half)
        lineTo(center.x - thin, center.y + thin)
        lineTo(center.x - half, center.y)
        lineTo(center.x - thin, center.y - thin)
        close()
    }
    drawPath(path, color = color)
}
