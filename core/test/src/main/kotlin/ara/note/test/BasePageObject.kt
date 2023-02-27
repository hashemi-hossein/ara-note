package ara.note.test

import android.content.Context
import androidx.test.core.app.ApplicationProvider

abstract class BasePageObject {
    abstract fun setUp()

    private val context = ApplicationProvider.getApplicationContext<Context>()
    fun getString(resId: Int) = context.getString(resId)
}
