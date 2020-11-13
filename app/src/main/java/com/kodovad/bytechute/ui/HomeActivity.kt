package com.kodovad.bytechute.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.lifecycle.ViewModelProvider
import com.kodovad.bytechute.R
import com.kodovad.bytechute.viewmodel.BitChuteViewModel

class HomeActivity : AppCompatActivity() {

    private val bitChuteViewModel : BitChuteViewModel by lazy {
        ViewModelProvider(this).get(BitChuteViewModel::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

    }

}