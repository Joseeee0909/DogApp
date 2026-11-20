package com.dogapp.view.components

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.FrameLayout
import com.dogapp.databinding.ViewCurvedToolbarBinding

/**
 * Componente de Toolbar Curva reutilizable para toda la aplicación.
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

    fun showBackButton(onClick: () -> Unit) {
        binding.btnBack.visibility = VISIBLE
        binding.btnBack.setOnClickListener { onClick() }
    }

    fun hideBackButton() {
        binding.btnBack.visibility = GONE
    }
}
