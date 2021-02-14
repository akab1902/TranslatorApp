package com.example.homeworkfragments.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.homeworkfragments.App
import com.example.homeworkfragments.Executors
import com.example.homeworkfragments.MainActivity
import com.example.homeworkfragments.R
import com.example.homeworkfragments.models.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.fragment_profile.*


class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private lateinit var mAuth: FirebaseAuth
    private val database = Firebase.database
    private val executors = Executors()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mAuth = FirebaseAuth.getInstance()
        val currentUser = mAuth.currentUser

        userNameTextView.text = currentUser?.displayName
        userEmailTextView.text = currentUser?.email
        getWordsCount()
        //Log.d("word count", getWordsCount())
        Glide.with(this).load(currentUser?.photoUrl).into(userAvatarImageView);

        signOutButton.setOnClickListener{
            mAuth.signOut()
            val transaction = activity?.supportFragmentManager?.beginTransaction()
            transaction?.replace(R.id.profileContainer, SignInFragment())
            transaction?.disallowAddToBackStack()
            transaction?.commit()

            //(activity as MainActivity).stopChronometer()
        }

//        readFirebaseDatabase()
//        writeFirebaseDatabase()
    }

    private fun readFirebaseDatabase(){
        val reference = database.getReference("user").child(userNameTextView.toString())
        reference.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
                Log.d("user onCancelled", error.message)
            }

            override fun onDataChange(snapshot: DataSnapshot) {
                getWordsCount()
                var startTime = snapshot.child("time").getValue()
                timeTextView.text = startTime.toString()
//                var userList = snapshot.getValue<List<HashMap<String, Any>>>()
//                val convertedUser = userList?.map{
//                    return@map UserConverter().convert(it)
//                }
//                convertedUser
                //Log.d("users ", convertedUser.toString())
            }
        })
    }

    private fun writeFirebaseDatabase(){
        val reference = database.getReference("user")
        reference.child("0").setValue(User("User123", "email123", 123, 123))
    }

    private fun getWordsCount(){
        var eng = 0
        var lat = 0
        executors.diskIO().execute{
            eng = (activity?.applicationContext as App).db.translationDao().getCount() / 2
            lat = (activity?.applicationContext as App).db.latinDao().getCount() / 2
        }
        executors.mainThread().execute {
            wordsCountTextView.text = (eng+lat).toString()
        }
    }
}