package by.android.academy.minsk.fastfinger

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import by.android.academy.minsk.fastfinger.game.GameFragment

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, GameFragment.newInstance())
                .commitNow()
        }
    }
}
