package com.rgbat.zeechat

import android.app.ProgressDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.squareup.okhttp.internal.Util
import kotlinx.android.synthetic.main.activity_sign_up.*
import kotlinx.android.synthetic.main.activity_signin.*

class SignUpActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        signin_link_btn .setOnClickListener {
            startActivity(Intent(this,SigninActivity::class.java))
        }

        signup_btn.setOnClickListener {


            createAccount()
        }

    }

    private fun createAccount() {

        val fullName = fullname_signup.text.toString()
        val username = username_signup.text.toString()
        val email = email_signup.text.toString()
        val password = password_signup.text.toString()


        when{


            TextUtils.isEmpty(fullName)->Toast.makeText( this,"full name is require",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(username)->Toast.makeText( this,"User Name name is require",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(email)->Toast.makeText( this,"Email is require",Toast.LENGTH_LONG).show()
            TextUtils.isEmpty(password)->Toast.makeText( this,"password is require",Toast.LENGTH_LONG).show()

            else ->{

                val progressDialog = ProgressDialog(this)
                progressDialog.setTitle("SignUp")
                progressDialog.setMessage("Please Wait....")
                progressDialog.setCanceledOnTouchOutside(false)
                progressDialog.show()


                val mAuth: FirebaseAuth =FirebaseAuth.getInstance()

                mAuth.createUserWithEmailAndPassword(email,password)
                    .addOnCompleteListener { task ->

                        if (task.isSuccessful){


                            saveUserInfo(fullName,username,email,progressDialog)

                        }
                        else{
                            val message = task.exception!!.toString()
                            Toast.makeText( this,"Error $message",Toast.LENGTH_LONG).show()
                            mAuth.signOut()
                            progressDialog.dismiss()
                        }
                    }



            }
        }

    }

    private fun saveUserInfo(fullName: String, username: String, email: String,progressDialog:ProgressDialog) {

        val  currentUserID = FirebaseAuth.getInstance().currentUser!!.uid
        val userRef:DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users")
        val userMap = HashMap<String,Any>()
        userMap["uid"] = currentUserID
        userMap["fullname"] = fullName.toLowerCase()
        userMap["username"] = username.toLowerCase()
        userMap["email"] = email
        userMap["biography"] = "Im using ZeeChat"
        userMap["image"] = "https://firebasestorage.googleapis.com/v0/b/zeechat-e9b18.appspot.com/o/defaullt%20Images%2Fprro.png?alt=media&token=71714106-f24f-4ca5-bf27-7c89f0aba36f"


        userRef.child(currentUserID).setValue(userMap)
            .addOnCompleteListener { task ->
                if (task.isSuccessful){

                    progressDialog.dismiss()
                    Toast.makeText( this,"Account has been Created Successfully",Toast.LENGTH_LONG).show()



                        FirebaseDatabase.getInstance().reference.child("Follow").child(currentUserID)
                            .child("Following").child(currentUserID)
                            .setValue(true)

                    val intent = Intent(this,MainActivity::class.java)
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    startActivity(intent)

                }
                else{
                    val message = task.exception!!.toString()
                    Toast.makeText( this,"Error $message",Toast.LENGTH_LONG).show()
                    FirebaseAuth.getInstance().signOut()
                    progressDialog.dismiss()
                }
            }

    }
}
