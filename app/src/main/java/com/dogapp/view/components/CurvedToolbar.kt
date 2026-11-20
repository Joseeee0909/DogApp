package com.dogapp.view.components

import android.content.Context
import android.content.res.ColorStateList
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.dogapp.databinding.ViewCurvedToolbarBinding

/**
 * Componente de Toolbar Curva reutilizable con opciones de configuración
 * de título, color de fondo, botón atrás y controles/acciones secundarios.
 */
class CurvedToolbar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr) {

    val binding: ViewCurvedToolbarBinding =
        ViewCurvedToolbarBinding.inflate(LayoutInflater.from(context), this, true)

    fun setTitle(title: String) {
        binding.tvTitle.text = title
    }

    fun setToolbarBackgroundColor(@ColorRes colorRes: Int) {
        val color = ContextCompat.getColor(context, colorRes)
        binding.containerToolbar.backgroundTintList = ColorStateList.valueOf(color)
    }

    fun showBackButton(onClick: () -> Unit) {
        binding.btnBack.visibility = VISIBLE
        binding.btnBack.setOnClickListener { onClick() }
    }

    fun hideBackButton() {
        binding.btnBack.visibility = GONE
    }

    fun setActionIcon(@DrawableRes iconRes: Int, onClick: () -> Unit) {
        binding.btnAction.setImageResource(iconRes)
        binding.btnAction.visibility = VISIBLE
        binding.btnAction.setOnClickListener { onClick() }
    }

    fun hideActionIcon() {
        binding.btnAction.visibility = GONE
    }
}
