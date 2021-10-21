package com.qty.appviewer.view

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.TextView

@SuppressLint("AppCompatCustomView")
class MarqueeTextView(
    context: Context,
    attrs: AttributeSet?,
    defStyle: Int = 0
): TextView(context, attrs, defStyle) {

    constructor(context: Context): this(context, null, 0)

    constructor(context: Context, attrs: AttributeSet?): this(context, attrs, 0)

    override fun isFocused(): Boolean {
        return true
    }

}