package com.rgbat.zeechat

import Adapter.CommentsAdapter
import Model.Comments
import Model.User
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_account_settings.*
import kotlinx.android.synthetic.main.activity_comments2.*
import java.util.*
import kotlin.collections.HashMap

class CommentsActivity2 : AppCompatActivity() {

    private var postid = ""
    private var publisherid = ""
    private var firebaseUser:FirebaseUser?= null
    private var commentsAdapter:CommentsAdapter? = null
    private var commentList:MutableList<Comments>?= null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comments2)
        val intent = intent
        postid = intent.getStringExtra("postid").toString()
        publisherid = intent.getStringExtra("publiserid").toString()

        firebaseUser = FirebaseAuth.getInstance().currentUser

        val recyclerView:RecyclerView
        recyclerView = findViewById(R.id.recyclerView_comments)
        val linearLayoutManager=LinearLayoutManager(this)
        linearLayoutManager.reverseLayout = true
        recyclerView.layoutManager = linearLayoutManager

        userInfo()


        post_comment.setOnClickListener (View.OnClickListener {

            if (add_comment!!.text.toString() == ""){

                Toast.makeText(this,"Please Write Comment First",Toast.LENGTH_LONG).show()
            }
            else{


         //// to write Commens
       addComment()

            }
        })

    }

    private fun addComment(){


        val commentRef = FirebaseDatabase.getInstance().reference.child("Comments")
            .child(postid!!)

        val commentMap = HashMap<String,Any>()

        commentMap["comment"] = add_comment!!.text.toString()
        commentMap["publisher"] = firebaseUser!!.uid
        commentRef.push().setValue(commentMap)

        ////to meake editText clear to add new Comment
        add_comment!!.text .clear()



    }

    private fun userInfo() {

        val userRef = FirebaseDatabase.getInstance().getReference().child("Users")
            .child(firebaseUser!!.uid)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

//                if (context != null){
//                    return
//                }
                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile)
                        .into(profile_image_comment)

                }

            }

            override fun onCancelled(error: DatabaseError) {

            }


        })
    }

}