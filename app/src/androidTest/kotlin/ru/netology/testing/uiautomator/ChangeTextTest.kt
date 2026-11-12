
package ru.netology.testing.uiautomator

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.uiautomator.By
import androidx.test.uiautomator.UiDevice
import androidx.test.uiautomator.UiSelector
import androidx.test.uiautomator.Until
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

const val SETTINGS_PACKAGE = "com.android.settings"
const val MODEL_PACKAGE = "ru.netology.testing.uiautomator"

const val TIMEOUT = 5000L

@RunWith(AndroidJUnit4::class)
class ChangeTextTest {

    private lateinit var device: UiDevice
    private val textToSet = "Netology"

    private fun waitForPackage(packageName: String) {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        context.startActivity(intent)
        device.wait(Until.hasObject(By.pkg(packageName)), TIMEOUT)
    }

    @Before
    fun beforeEachTest() {
        device = UiDevice.getInstance(
            InstrumentationRegistry.getInstrumentation()
        )

        device.pressHome()

        val launcherPackage = device.launcherPackageName
        device.wait(Until.hasObject(By.pkg(launcherPackage)), TIMEOUT)
    }

    @Test
    fun testInternetSettings() {
        waitForPackage(SETTINGS_PACKAGE)

        device.findObject(
            UiSelector().resourceId("android:id/title").instance(0)
        ).click()
    }

    @Test
    fun testChangeText() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        device.findObject(By.res(packageName, "userInput")).text = textToSet
        device.findObject(By.res(packageName, "buttonChange")).click()

        val result = device.findObject(
            By.res(packageName, "textToBeChanged")
        ).text

        assertEquals(textToSet, result)
    }

    @Test
    fun testEmptyText() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        val textView = device.wait(
            Until.findObject(By.res(packageName, "textToBeChanged")),
            TIMEOUT
        ) ?: throw AssertionError("TextView не найден")

        val initialText = textView.text

        val input = device.findObject(
            By.res(packageName, "userInput")
        )

        val button = device.findObject(
            By.res(packageName, "buttonChange")
        )

        // Проверяем пустую строку
        input.text = ""
        button.click()

        assertEquals(initialText, textView.text)

        // Проверяем строку из пробелов
        input.text = "   "
        button.click()

        assertEquals(initialText, textView.text)
    }

    @Test
    fun testOpenNewActivity() {
        val packageName = MODEL_PACKAGE
        waitForPackage(packageName)

        val textToSet = "Проверка новой Activity"

        device.findObject(
            By.res(packageName, "userInput")
        ).text = textToSet

        device.findObject(
            By.res(packageName, "buttonActivity")
        ).click()

        // Ожидаем появления TextView второй Activity
        val activityOpened = device.wait(
            Until.hasObject(By.res(packageName, "text")),
            TIMEOUT
        )

        assertTrue("Вторая Activity не открылась", activityOpened)

        val result = device.findObject(
            By.res(packageName, "text")
        ).text

        assertEquals(textToSet, result)
    }
}




