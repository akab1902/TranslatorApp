package com.example.homeworkfragments.ui

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.example.homeworkfragments.R
import com.google.firebase.auth.FirebaseAuth

class AccountFragment : Fragment(R.layout.fragment_account) {

    private lateinit var mAuth: FirebaseAuth

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        val user = mAuth.currentUser

        if (user != null){
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.profileContainer, ProfileFragment())
            transaction?.disallowAddToBackStack()
            transaction?.commit()
        } else {
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.profileContainer, SignInFragment())
            transaction?.disallowAddToBackStack()
            transaction?.commit()
        }
    }
}